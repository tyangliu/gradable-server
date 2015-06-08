package net.gradable.models

import akka.http.util.DateTime
import org.anormcypher._

case class User(email: String, name: String, school: Option[String], created: DateTime, hashedPassword: String)

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
  implicit lazy val fields = Vector("email", "name", "school", "created", "hashedPassword")

  def create(user: User): Boolean = {
    Cypher(
      """
        CREATE (n:User {
          email: {email},
          name: {name},
          school: {school},
          created: {created},
          hashedPassword: {hashedPassword}
        })
      """
    ).on(extractFields(user): _*).execute()
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
    val result = Cypher(
      s"""
        MATCH (n:User { email: {email} })
        RETURN ${mkReturnStatement("n")}
      """
    ).on("email" -> email)()

    mapResult(result).headOption
  }

  protected def extractFields(user: User): Vector[(String, Any)] = {
    val User(email, name, school, created, hashedPassword) = user
    Vector(
      "email"          -> email,
      "name"           -> name,
      "school"         -> school.getOrElse(""),
      "created"        -> created.clicks,
      "hashedPassword" -> hashedPassword
    )
  }

  protected def mapResult(rowStream: Stream[CypherResultRow]): Vector[User] = {
    rowStream.map(row => User(
      row[String]         ("email"),
      row[String]         ("name"),
      row[Option[String]] ("school"),
      DateTime(row[Long]  ("created")),
      row[String]         ("hashedPassword")
    )).toVector
  }
}