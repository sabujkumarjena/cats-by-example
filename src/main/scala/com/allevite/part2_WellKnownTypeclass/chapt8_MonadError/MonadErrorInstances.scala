package com.allevite.part2_WellKnownTypeclass.chapt8_MonadError

import cats.*
import cats.implicits.*

import scala.util.{Failure, Success, Try}

// Either[E, *] :  * -> *
object MonadErrorInstances :
  given eitherME[E]: MonadError[[z] =>> Either[E, z], E] = new MonadError[[z] =>> Either[E, z], E] :
    override def raiseError[A](e: E): Either[E, A] =
      Left(e)

    override def handleErrorWith[A](fa: Either[E, A])(f: E => Either[E, A]): Either[E, A] =
      fa match
        case Right(a) => Right(a)
        case Left(e) => f(e)


    override def pure[A](x: A): Either[E, A] = Right(x)

    override def flatMap[A, B](fa: Either[E, A])(f: A => Either[E, B]): Either[E, B] =
      fa match
        case Right(a) => f(a)
        case Left(e) => Left(e)

    override def tailRecM[A, B](a: A)(f: A => Either[E, Either[A, B]]): Either[E, B] = ???


  given tryME: MonadError[Try, Throwable] = new MonadError[Try, Throwable] :
    override def raiseError[A](e: Throwable): Try[A] = Failure(e)

    override def handleErrorWith[A](fa: Try[A])(f: Throwable => Try[A]): Try[A] =
      fa match {
        case Success(a) => Success(a)
        case Failure(t) => f(t)
      }

    override def pure[A](x: A): Try[A] = Success(x)

    override def flatMap[A, B](fa: Try[A])(f: A => Try[B]): Try[B] =
      fa match
        case Success(a) => f(a)
        case Failure(e) => Failure(e)

    override def tailRecM[A, B](a: A)(f: A => Try[Either[A, B]]): Try[B] = ???

