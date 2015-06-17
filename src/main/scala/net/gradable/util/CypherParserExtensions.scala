package net.gradable.util

object CypherParserExtensions {
  import akka.http.scaladsl.model.DateTime
  import io.jvm.uuid._
  import org.anormcypher.CypherParser._
  import org.anormcypher._

  def dateTime(columnName: String): CypherRowParser[DateTime] = get[DateTime](columnName)(implicitly[Column[DateTime]])
  def uuid(columnName: String): CypherRowParser[UUID] = get[UUID](columnName)(implicitly[Column[UUID]])

  implicit def rowToDateTime: Column[DateTime] = Column.nonNull[DateTime] {
    (value, meta) => value match {
      case bd: BigDecimal => Right(DateTime(bd.toLong))
      case x => Left(TypeDoesNotMatch(s"Cannot convert $x:${x.getClass} to DateTime for column ${meta.column}"))
    }
  }

  implicit def rowToUUID: Column[UUID] = Column.nonNull[UUID] {
    (value, meta) => value match {
      case s: String => Right(UUID(s))
      case x => Left(TypeDoesNotMatch(s"Cannot convert $x:${x.getClass} to UUID for column ${meta.column}"))
    }
  }
}