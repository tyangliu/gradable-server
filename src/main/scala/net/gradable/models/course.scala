package net.gradable.models

case class Course(id: Int)

sealed trait CourseComponent { val id: Int }

case class Section(id: Int) extends CourseComponent
case class Entry(id: Int)   extends CourseComponent