package com.allevite.part1_Typeclass.exercise


trait Eq[A]:
  // TODO #1: Define an 'eq' method that takes two A values as parameters, and returns a Boolean
  def eq(a: A, b: A): Boolean

object Eq:
  // TODO #2: Define the method 'apply' so we can summon instances from implicit scope
  def apply[A](using ev: Eq[A]): Eq[A] =ev
  // TODO #3: Define the method 'instance' so we can build instances of the Eq typeclass more easily.
  def instance[A](f: (A,A)=> Boolean) : Eq[A] = new Eq[A]:
    override def eq(a: A, b: A): Boolean = f(a,b)
  // TODO #4: Define an Eq instance for String
  given Eq[String] = new Eq[String]:
    override def eq(a: String, b: String): Boolean = a.compareTo(b) == 0
  // TODO #5: Define an Eq instance for Int
  given Eq[Int] = new Eq[Int]:
    override def eq(a: Int, b: Int): Boolean = a == b
  // TODO #6: Define an Eq instance for Person. Two persons are equal if both their names and ids are equal.
  given Eq[Person] = new Eq[Person]:
    override def eq(a: Person, b: Person): Boolean =
      Eq[String].eq(a.name, b.name) & Eq[Int].eq(a.id, b.id)
  // TODO #7: Provide a way to automatically derive instances for Eq[Option[A]] given that we have an implicit
  given optionEq[A](using ev: Eq[A]): Eq[Option[A]] = new Eq[Option[A]]:
    override def eq(a: Option[A], b: Option[A]): Boolean = (a,b) match
      case (None, None) => true
      case (Some(x), Some(y)) => ev.eq(x,y)
      case _ => false
  // TODO #8: Define an extension method 'eqTo' that enables the following syntax:
  //   "hello".eqTo("world")
  extension [A](a: A)
    def eqTo(b: A)(using Eq[A]):Boolean = Eq[A].eq(a, b)