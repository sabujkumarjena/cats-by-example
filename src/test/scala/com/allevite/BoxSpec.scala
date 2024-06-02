package com.allevite

import cats.*
import cats.implicits.*
import cats.kernel.laws.discipline.EqTests
import cats.laws.discipline.MonadTests
import com.allevite.part2_WellKnownTypeclass.chapt11_Testing.Box
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.prop.Configuration
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks  //for foAll
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

class BoxSpec extends AnyFunSuite with Configuration with FunSuiteDiscipline with ScalaCheckDrivenPropertyChecks:
  val genInt: Gen[Int] = Gen.choose(1, 10)
  val genInt2: Gen[Int] = Gen.oneOf(1, 5, 10)
  val genString: Gen[String] = Gen.alphaNumStr
  val genString2: Gen[String] = Gen.numStr
  val genTuple: Gen[(Int, String)] =
    for
      i <- genInt
      s <- genString
    yield (i, s)
  val arbInt: Arbitrary[Int] = Arbitrary(genInt)

  given arbBoxA[A](using arbA: Arbitrary[A]): Arbitrary[Box[A]] =
    Arbitrary(arbA.arbitrary.map(Box(_)))

  given arbFun[A](using arbA: Arbitrary[A]): Arbitrary[A => A] =
    Arbitrary(arbA.arbitrary.map(a => (_:A) => a))

  checkAll("Eq[Box[Int]]", EqTests[Box[Int]].eqv)
  checkAll("Monad[Box]", MonadTests[Box].monad[Int, Int, Int])

  //writing our own test
  test("Boxing and unboxing should yield original value"){
//    given Arbitrary[Int] = arbInt //if u want to use your own Arbitrary
    forAll{(i: Int, s: String) =>
      assert(Box(i).value eqv i)
      assert(Box(s).value eqv s)
    }
  }

  test("Boxing and unboxing should yield original value ---2") :
    //    given Arbitrary[Int] = arbInt //if u want to use your own Arbitrary
    forAll : (i: Int, s: String) =>
      assert(Box(i).value eqv i)
      assert(Box(s).value eqv s)

  test("Boxing and unboxing should yield original value ---3"):
    //    given Arbitrary[Int] = arbInt //if u want to use your own Arbitrary
    forAll(genInt2, genString2): (i: Int, s: String) =>    //if u want to pass ur own generator
      assert(Box(i).value eqv i)
      assert(Box(s).value eqv s)


