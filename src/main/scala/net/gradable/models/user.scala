package net.gradable.models

import akka.http.scaladsl.model.DateTime

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
  import org.anormcypher._
  import org.anormcypher.CypherParser._
  import CypherParserExtensions._

  lazy val label  = "User"
  lazy val fields = Vector("email", "name", "hashedPassword", "createdAt")

  lazy val parser = {
    uuid("uuid")~str("email")~str("name")~str("hashedPassword")~dateTime("createdAt") map {
      case uuid~email~name~password~createdAt => WithId(Id(uuid), User(email, name, password, createdAt))
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
    ).on(extract(user): _*).execute
  }

  def update(user: User): Boolean = ???

  def delete(user: User): Boolean = {
    Cypher(
      """
        MATCH (n:User { email: {email} })
        DELETE n
      """
    ).on("email" -> user.email).execute
  }

  def findByEmail(email: String): Option[WithId[User]] = {
    Cypher(
      s"""
        MATCH (n:User { email: {email} })
        RETURN ${mkReturnStatement("n")}
      """
    ).on("email" -> email).as(parser.*).headOption
  }
}