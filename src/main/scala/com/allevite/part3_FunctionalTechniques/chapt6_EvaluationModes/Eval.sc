import cats.*
import cats.implicits.*
import cats.data.*

Eval.now {
  println("Calculating...")
  5
}

val x = Eval.later {
  println("Calculating...")
  5
}
x.value
x.value

val y = Eval.always {
  println("Calculating...")
  5
}

y.value
y.value

val z = Eval.defer {
  println("Calculating...")
  Eval.now(5)
}
z.value
z.value

val a = Eval.later(5)
a.flatMap(i => Eval.now(i + 2)).value

object X {
  def isEven(n: Int): Eval[Boolean] =
    if(n == 0) Eval.now(true)
    else Eval.defer(isOdd(n-1))

  def isOdd(n: Int): Eval[Boolean] =
    if(n == 0) Eval.now(false)
    else Eval.defer(isEven(n-1))

  def fact(n: Int): Eval[Int] =
    if(n == 0) Eval.now(1)
    else Eval.defer(fact(n-1)).map(_ * n)
}

import X._
isEven(100000).value
fact(10).value

case class Stream[+A](head: A, tail: Eval[Stream[A]]) {
  def take(n: Int): Eval[List[A]] =
    if(n == 0) Eval.now(Nil)
    else tail.flatMap(sa => sa.take(n-1)).map(rest => head :: rest)
}

object Stream {
  def iterate[A](initial: A)(f: A => A): Stream[A] =
    Stream(initial, Eval.later(iterate(f(initial))(f)))

  implicit val streamFunctor: Functor[Stream] = new Functor[Stream] {
    override def map[A, B](fa: Stream[A])(f: A => B): Stream[B] =
      Stream(f(fa.head), fa.tail.map(sa => map(sa)(f)))
  }
}
import Stream._
val nats = iterate(1)(_ + 1)
nats.take(100000).value
val evens = nats.map(_ * 2)
evens.take(100000).value