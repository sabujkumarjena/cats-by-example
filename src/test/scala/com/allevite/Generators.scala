package com.allevite

import com.allevite.part2_WellKnownTypeclass.chapt12_Excercise.Get
import org.scalacheck.{Arbitrary, Gen}

trait Generators:
  /**
   * TODO 10
   * Instance of Arbitrary for functions A => A.
   * No constraints on what the function does.
   * Simple solutions should be enough.
   */
  given arbFun[A](using arbA: Arbitrary[A]): Arbitrary[A => A] =
    Arbitrary(arbA.arbitrary.map(a => (_: A) => a))

  /**
   * TODO 11
   * Instance of Arbitrary for the Get monad.
   * Any solution should suffice.
   * Some optional constraints if you want to spice things up:
   * - allow for both successful and failed return values to be produced
   * - when running the Get successfully, the remaining bytes should be
   * a suffix of the bytes passed as argument to the run function; that is,
   * you should simulate actual 'consumption' of the bytes.
   */
  given arbGet[A](using arbA: Arbitrary[A]): Arbitrary[Get[A]] =
    //Arbitrary(arbA.arbitrary.map(a => Get(l => Right(l,a)))) // simple solution
    val successful: Gen[Get[A]] =
      for 
        pctToDrop <- Gen.choose(0, 100)
        a <- arbA.arbitrary
      yield Get(bytes => Right((bytes.drop(bytes.length * pctToDrop / 100), a)))

    val failed: Gen[Get[A]] = Gen.alphaNumStr.map(s => Get(_ => Left(s)))

    Arbitrary(Gen.oneOf(successful, failed))
