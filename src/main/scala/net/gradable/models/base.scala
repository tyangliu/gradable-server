package net.gradable.models

import akka.actor.Actor
import org.anormcypher._

trait BaseRepositoryProtocol[T] {
  case class  Create(model: T)
  case class  Update(model: T)
  case class  Delete(model: T)

  case object FindAll
  case class  FindBy(args: Seq[(String, Any)])
}

trait BaseRepository[T] extends Actor with BaseRepositoryProtocol[T] with BaseCypher[T] {
  def receive = {
    case Create(model)       => create(model)
    case Update(model)       => update(model)
    case Delete(model)       => delete(model)

    case FindAll             => findAll()
    case FindBy(args)        => findBy(args: _*)
  }
}

trait BaseCypher[T] {
  implicit val connection = Neo4jREST("localhost", 7474, "/db/data/", "neo4j", "password")

  implicit def label:  String
  implicit def fields: Seq[String]

  def create(model: T): Boolean
  def update(model: T): Boolean
  def delete(model: T): Boolean

  def findAll()(implicit label: String): Vector[T] = {
    val result = Cypher(
      s"""
        MATCH (n:$label)
        RETURN ${mkReturnStatement("n")}
      """
    )()

    mapResult(result)
  }

  def findBy(args: (String, Any)*)(implicit label: String): Seq[T] = {
    ???
  }

  protected def mkReturnStatement(node: String)(implicit fields: Seq[String]): String = {
    fields.map(field => s"$node.$field as $field").mkString(", ")
  }

  protected def extractFields(model: T): Vector[(String, Any)]
  protected def mapResult(rowStream: Stream[CypherResultRow]): Vector[T]
}