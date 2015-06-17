package net.gradable.models

import akka.http.scaladsl.model.DateTime

case class Term(id: Int, name: String, start: Option[DateTime], end: Option[DateTime], created: DateTime)