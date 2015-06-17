package net.gradable.models

import akka.actor.Actor
import io.jvm.uuid._

case class Id[T](uuid: UUID)
case class WithId[T](id: Id[T], model: T)

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

    case FindAll             => findAll
    case FindBy(args)        => findBy(args: _*)
  }
}

trait BaseCypher[T] {
  import org.anormcypher._

  implicit val connection = Neo4jREST("localhost", 7474, "/db/data/", "neo4j", "password")

  def label:  String
  def fields: Seq[String]

  def parser: CypherRowParser[WithId[T]]
  def extract(model: T): Vector[(String, Any)]

  def mkReturnStatement(node: String): String = {
    ("uuid" +: fields).map(field => s"$node.$field as $field").mkString(", ")
  }

  def create(model: T): Boolean
  def update(model: T): Boolean
  def delete(model: T): Boolean

  def findAll: Vector[WithId[T]] = {
    Cypher(
      s"""
        MATCH (n:$label)
        RETURN ${mkReturnStatement("n")}
      """
    ).as(parser.*).toVector
  }

  def findBy(args: (String, Any)*): Vector[WithId[T]] = {
    ???
  }
}