import scala.annotation.tailrec

@tailrec
def fact(n:Int, acc: Int = 1): Int =
  if n ==0 then acc
  else fact(n-1, n* acc)

fact(15)

object W:
  def isEven(n: Int): Boolean =
    if n == 0 then true else isOdd(n-1)
  def isOdd(n: Int): Boolean=
    if n==0 then false else isEven(n-1)

//W.isEven(100000)// stack overflow

trait Trampoline[+A]
object Trampoline:
  case class Done[A](a: A) extends Trampoline[A]
  case class More[A](f: () => Trampoline[A]) extends Trampoline[A]
  case class FlatMap[A, B](ta: Trampoline[A], f: A => Trampoline[B]) extends Trampoline[B]

  @tailrec
  def resume[A](ta: Trampoline[A]): Either[() => Trampoline[A],A] = ta match
    case Done(a) => Right(a)
    case More(thunk) => resume(thunk())
    case FlatMap(t,f) => t match
      case Done(a) => resume(f(a))
      case More(thunk) => Left(() => FlatMap(thunk(), f))
      case FlatMap(t2, f2) => resume(FlatMap(t2, (x)=> FlatMap(f2(x),f))) ///use right associativity of flatmap
  // FlatMap(FlatMap(t2, f2), f) ---> FlatMap(t2, x => FlatMap(f2(x), f))

  @tailrec
  def run[A](ta:Trampoline[A]): A = resume(ta) match
    case Right(a) => a
    case Left(thunk) => run(thunk())

object X:
  import Trampoline.*
  def isEven(n: Int): Trampoline[Boolean] =
    if n == 0 then Done(true) else More(() => isOdd(n-1))
  def isOdd(n: Int): Trampoline[Boolean]=
    if n==0 then Done(false) else More(() => isEven(n-1))

Trampoline.run(X.isEven(100011010))

object Y:
  def flatMap[A,B](as: List[A])(f: A => List[B]):List[B] =
    as match
      case Nil => Nil
      case h :: t => f(h) ::: flatMap(t)(f)

//Y.flatMap((1 to 100000).toList)(i => List(i, i+1)) // stack overflow

import Trampoline.*

def flatMap[A,B](as: List[A])(f: A => List[B]): Trampoline[List[B]] =
  as match
    case Nil => Done(Nil)
    case h :: t => More(() =>
    FlatMap(flatMap(t)(f), (lb) => Done(f(h) ::: lb))
    )

run(flatMap((1 to 100000).toList)(i => List(i, i+1)))