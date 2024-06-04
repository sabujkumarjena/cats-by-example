package com.allevite.finalProject.common

import cats.*
import cats.implicits.*
import cats.laws.discipline.{MonadErrorTests, MonadTests}
import com.allevite.finalProject.Generators
import com.allevite.part4_FinalProject.common.IO
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.prop.Configuration
import org.typelevel.discipline.scalatest.FunSuiteDiscipline
import com.allevite.part4_FinalProject.common.IO.*

import scala.util.{Failure, Try}

class IOSpec
  extends AnyFunSuite
    with Configuration
    with FunSuiteDiscipline
    with Generators :

  given eqIO[A: Eq]: Eq[IO[A]] =
    Eq.instance((io1, io2) => io1.run eqv io2.run)

  given eqThrowable: Eq[Throwable] =
    Eq.instance((t1, t2) => t1.getMessage == t2.getMessage)

  test("Running a suspended exception throws") :
    val e = new Exception("boom")
    val ioa: IO[Int] = IO.suspend(throw e)
    Try(ioa.run) eqv Failure(e)


  test("Attempting and running a suspended exception yields a Left") :
    val e = new Exception("boom")
    val ioa: IO[Int] = IO.suspend(throw e)
    ioa.attempt.run eqv Left(e)


  test("Attempting and running an IO that throws yields a Left") :
    val e = new Exception("boom")
    val ioa: IO[Int] = IO.raiseError(e)
    ioa.attempt.run eqv Left(e)


  checkAll("MonadError[IO]", MonadErrorTests[IO, Throwable].monad[Int, Int, Int])