package com.allevite.finalProject.model

import cats.data.Validated.Valid
import cats.implicits.*
import cats.kernel.laws.discipline.{EqTests, MonoidTests, OrderTests}
import com.allevite.finalProject.FpFinalSpec
import com.allevite.part4_FinalProject.model.Money
import org.scalacheck.{Arbitrary, Gen}

class MoneySpec extends FpFinalSpec :
  test("unsafe create") :
    forAll : (c: Int) =>
      assert(Money.unsafeCreate(c).cents eqv c)

  test("create a valid money") :
    given positiveArb: Arbitrary[Double] =
      Arbitrary(Gen.choose(0, Int.MaxValue))
    forAll : (dls: Double) =>
      assert(
        Money.dollars(dls) eqv Valid(Money.unsafeCreate((dls * 100).toInt))
      )

  test("create invalid money with negative amount") :
    given positiveArb: Arbitrary[Double] =
      Arbitrary(Gen.choose(Int.MinValue, -1))
    forAll : (dls: Double) =>
      assert(Money.dollars(dls).isInvalid)

  test("dollars and cents relation") :
    forAll : (money: Money) =>
      assert((money.dollars * 100.0 - money.cents) < 0.001)

  test("divide by 0 yields None") :
    forAll : (money: Money) =>
      assert(money.divideBy(0) eqv None)

  test("cents of a division") :
    given posInt: Arbitrary[Int] = Arbitrary(Gen.posNum[Int])
    forAll : (money: Money, n: Int) =>
      assert(money.divideBy(n).map(_.cents) eqv Some(money.cents / n))



  // TODO #5: Add the missing Order typeclass tests
  checkAll("Eq[Money]", EqTests[Money].eqv)
  checkAll("Monoid[Money]", MonoidTests[Money].monoid)
