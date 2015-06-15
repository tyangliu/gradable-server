package net.gradable.models

import akka.http.util.DateTime
import org.anormcypher._
import org.anormcypher.CypherParser._

case class User(email: String, name: String, hashedPassword: String, createdAt: DateTime)

object UserRepositoryProtocol {
  case class FindByEmail(email: String)
}

class UserRepository extends BaseRepository[User] with UserCypher {
  import UserRepositoryProtocol._

  override def receive = super.receive orElse {
    case FindByEmail(email) => findByEmail(email)
  }
}

trait UserCypher extends BaseCypher[User] {
  implicit lazy val label  = "User"
  implicit lazy val fields = Vector("email", "name", "createdAt", "hashedPassword")

  lazy val parser = {
    str("email")~str("name")~str("hashedPassword")~long("createdAt") map {
      case email~name~password~createdAt => User(email, name, password, DateTime(createdAt))
    }
  }

  def extract(user: User): Vector[(String, Any)] = {
    val User(email, name, hashedPassword, createdAt) = user
    Vector("email" -> email, "name" -> name, "hashedPassword" -> hashedPassword, "createdAt" -> createdAt.clicks)
  }

  def create(user: User): Boolean = {
    Cypher(
      """
        CREATE (n:User { email: {email}, name: {name}, hashedPassword: {hashedPassword}, createdAt: {createdAt} })
      """
    ).on(extract(user): _*).execute()
  }

  def update(user: User): Boolean = ???

  def delete(user: User): Boolean = {
    Cypher(
      """
        MATCH (n:User { email: {email} })
        DELETE n
      """
    ).on("email" -> user.email).execute()
  }

  def findByEmail(email: String): Option[User] = {
    Cypher(
      s"""
        MATCH (n:User { email: {email} })
        RETURN ${mkReturnStatement("n")}
      """
    ).on("email" -> email).as(parser.*).headOption
  }
}