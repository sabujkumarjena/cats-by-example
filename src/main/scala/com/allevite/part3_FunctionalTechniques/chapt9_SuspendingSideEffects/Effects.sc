import cats.*
import cats.implicits.*
import cats.data._

import scala.annotation.tailrec

trait IO[+A] :
  import IO.*

  def resume: Either[() => IO[A], A] = this match
    case Done(a) => Right(a)
    case More(thunk) => thunk().resume
    case FlatMap(t, f) => t match
      case Done(a2) => f(a2).resume
      case More(thunk2) => Left(() => FlatMap(thunk2(), f))
      case FlatMap(t2, f2) => FlatMap(t2, (x) => FlatMap(f2(x), f)).resume



  @tailrec
  final def run: A = resume match
    case Right(a) => a
    case Left(thunk) => thunk().run



object IO :
  case class Done[A](a: A) extends IO[A]
  case class More[A](f: () => IO[A]) extends IO[A]
  case class FlatMap[A, B](ta: IO[A], f: A => IO[B]) extends IO[B]

  def apply[A](a: => A): IO[A] = suspend(a)

  def suspend[A](a: => A): IO[A] =
    More(() => Done(a))

  implicit val ioMonad: Monad[IO] = new Monad[IO] :
    override def pure[A](x: A): IO[A] = Done(x)

    override def flatMap[A, B](fa: IO[A])(f: A => IO[B]): IO[B] = FlatMap(fa, f)

    override def tailRecM[A, B](a: A)(f: A => IO[Either[A, B]]) = ???



val x: IO[Unit] = IO.suspend(println("hello"))
val y: IO[Int] = IO :
  println("Computing my int...")
  5


val z: IO[Int] = IO :
  println("Computing my second int...")
  6


val result: IO[Int] =
  for
    a <- y
    b <- z
  yield a + b

result.run