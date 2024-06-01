import cats.*
import cats.implicits.*

import scala.util.{Success, Try}

sealed trait MOption[+A]

object MOption :
  case class MSome[+A](a: A) extends MOption[A]
  case object MNone extends MOption[Nothing]

  given monadOption: Monad[MOption] = new Monad[MOption] :
    override def pure[A](x: A): MOption[A] = MSome(x)

    override def flatMap[A, B](fa: MOption[A])(f: A => MOption[B]): MOption[B] =
      fa match
        case MSome(a) => f(a)
        case MNone => MNone


    override def tailRecM[A, B](a: A)(f: A => MOption[Either[A, B]]): MOption[B] =
      f(a) match
        case MSome(Right(b)) => MSome(b)
        case MSome(Left(a)) => tailRecM(a)(f)
        case MNone => MNone


    override def map[A, B](fa: MOption[A])(f: A => B): MOption[B] =
      flatMap(fa)(a => pure(f(a)))

    override def flatten[A](ffa: MOption[MOption[A]]): MOption[A] =
      flatMap(ffa)(identity)


  def msome[A](a: A): MOption[A] = MSome(a)
  def mnone[A]: MOption[A] = MNone

import MOption._
Monad[MOption].pure(5)
Monad[MOption].flatMap(msome(3))(x => msome(x+1))
Monad[MOption].flatMap(mnone[Int])(x => msome(x+1))
Monad[MOption].flatMap(msome(4))(x => mnone)

msome(4).flatMap(x => msome(x * 2))

for 
  x <- msome(4)
  y <- mnone[Int]
yield x + y

msome(4).flatMap(x => msome(5).map(y => x + y))

val listMonad: Monad[List] = new Monad[List] {
  override def pure[A](x: A): List[A] = List(x)

  // pattern matching, recursion and map
  override def flatMap[A, B](fa: List[A])(f: A => List[B]): List[B] = 
    fa match 
      case (x :: xs) => f(x) ++ flatMap(xs)(f)
      case Nil => Nil
    

  override def tailRecM[A, B](a: A)(f: A => List[Either[A, B]]): List[B] = ???
}

listMonad.flatMap(List(1,2,3))(x => List(x + 1, x + 2))

Success(5).flatMap(x => Success(x + 1))

// fa.flatMap(pure) == fa
// pure(a).flatMap(f) == f(a)
val f: Int => Try[Int] = x => throw new Exception("boom")
Success(3).flatMap(f)
f(3)