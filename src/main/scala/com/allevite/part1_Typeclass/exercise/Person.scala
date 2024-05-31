package com.allevite.part1_Typeclass.exercise

case class Person(name: String, id: Int)

object Peron:
  import Eq.*
  given Eq[Person] = new Eq[Person]:
    override def eq(a: Person, b: Person): Boolean = a.name.eqTo(b.name)

  val eqp: Eq[Person] = new Eq[Person]:
    override def eq(a: Person, b: Person): Boolean = a.id.eqTo(b.id)