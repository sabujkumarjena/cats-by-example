import cats.*
import cats.implicits.*
import scala.util.*


given tryMonad: Monad[Try] = new Monad[Try]:
  override def pure[A](x: A) = Success(x)

  override def flatMap[A, B](fa: Try[A])(f: A => Try[B]) =
    fa match
      case Success(a) => f(a)
      case Failure(e) => Failure(e)

  override def tailRecM[A, B](a: A)(f: A => Try[Either[A, B]]) = ???

tryMonad.pure(10)
tryMonad.pure(10).flatMap(i => tryMonad.pure( i + 1))
tryMonad.pure(10).flatMap(i => Failure(new Exception("boom")))
tryMonad.pure(10).flatMap(i => Failure(new Exception("boom")).flatMap(j => Failure(new Exception("boom2"))))

// pure(x).flatMap(f) === f(x)

val f: Int => Try[Int] = i => Success(i + 1)
Success(10).flatMap(f)
f(10)
val g: Int => Try[Int] = i => throw new Exception("oops")
Success(10).flatMap(g)
g(10)
