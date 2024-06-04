import cats.*
import cats.implicits.*

val optionMonad: Monad[Option] = new Monad[Option] :
  override def pure[A](x: A): Option[A] = Some(x)

  override def flatMap[A, B](fa: Option[A])(f: A => Option[B]): Option[B] =
    fa match
      case Some(a) => f(a)
      case None => None


  override def tailRecM[A, B](a: A)(f: A => Option[Either[A, B]]): Option[B] =
    f(a) match
      case Some(Right(b)) => Some(b)
      case Some(Left(a)) => tailRecM(a)(f)
      case None => None


// while p is true keep applying f starting on initial. once p becomes false return the value
def iterateWhileM[A](initial: A)(f: A => Option[A])(p: A => Boolean): Option[A] =
  if p(initial) then f(initial).flatMap(a => iterateWhileM(a)(f)(p))
  else Some(initial)

iterateWhileM(1)(n => Some(n + 1))(_ < 5)
//iterateWhileM(1)(n => Some(n + 1))(_ < 1000000) //stack overflow

def iterateWhileMV2[A](initial: A)(f: A => Option[A])(p: A => Boolean): Option[A] =
  optionMonad.tailRecM(initial) {a =>
    if p(a) then f(a).map(x => Left(x))
    else Some(Right(a))
  }

iterateWhileMV2(1)(n => Some(n + 1))(_ < 1000000)  // Stack safe