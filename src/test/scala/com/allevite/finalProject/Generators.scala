package com.allevite.finalProject

import cats.data.{NonEmptyChain, State}
import cats.implicits._
import com.allevite.part4_FinalProject.app.AppState
import com.allevite.part4_FinalProject.app.Configuration.IsValid
import com.allevite.part4_FinalProject.common.IO
import com.allevite.part4_FinalProject.common.IO.{Done, FlatMap, More}
import com.allevite.part4_FinalProject.model._
import com.allevite.part4_FinalProject.service.ExpenseService.{ExpenseOp, ExpenseState}
import com.allevite.part4_FinalProject.service.PersonService.{PersonOp, PersonState}
import org.scalacheck.{Arbitrary, Gen}

trait Generators :

  /**
   * TODO #3a: implement an arbitrary of Person.
   *
   * You can use Person.unsafeCreate as long as you take
   * care of only producing valid values (check out
   * the constraints in Person.create) .
   */
  given personArb: Arbitrary[Person] =  Arbitrary:
    for
      n <- Gen.choose(1, 32)
      name <- Gen.stringOfN(n, Gen.alphaChar)
    yield Person.unsafeCreate(name)

  given moneyArb: Arbitrary[Money] = Arbitrary :
    Gen.choose(1, 1e9.toInt).map(Money.unsafeCreate)


  /**
   * TODO #3b: Use the provided arbitraries and the Expense.unsafeCreate method
   * to create an instance of Arbitrary[Expense]
   */
  given expenseArb(using
                          arbPerson: Arbitrary[Person],
                          arbMoney: Arbitrary[Money]
                         ): Arbitrary[Expense] =
  Arbitrary:
    for
      person <- arbPerson.arbitrary
      money <- arbMoney.arbitrary
      participants <- Gen.nonEmptyListOf(arbPerson.arbitrary)
    yield Expense.unsafeCreate(person, money, participants)

  given payeeDebtArb: Arbitrary[DebtByPayee] = Arbitrary :
    Gen
      .listOf(expenseArb.arbitrary)
      .map(_.map(DebtByPayee.fromExpense).combineAll)


  given payerDebtArb: Arbitrary[DebtByPayer] = Arbitrary :
    Gen
      .listOf(expenseArb.arbitrary)
      .map(_.map(DebtByPayer.fromExpense).combineAll)


  given functionArb[A](using arbA: Arbitrary[A]): Arbitrary[A => A] =
    Arbitrary :
      arbA.arbitrary.map(a => (_: A) => a)


  given personStateArb(using
                              personArb: Arbitrary[Person]
                             ): Arbitrary[PersonState] =
    Arbitrary :
      Gen
        .mapOf[String, Person](personArb.arbitrary.map(p => (p.name, p)))
        .map(PersonState.apply)


  given ioArb[A](using arbA: Arbitrary[A]): Arbitrary[IO[A]] =
    val doneGen: Gen[Done[A]] = arbA.arbitrary.map(Done.apply)
    val moreGen: Gen[More[A]] = arbA.arbitrary.map(a => More(() => Done(a)))
    val flatMapGen: Gen[FlatMap[A, A]] =
      for {
        fa <- Gen.oneOf(doneGen, moreGen)
        ta <- Gen.oneOf(doneGen, moreGen)
      } yield FlatMap(ta, (_: A) => fa)

    Arbitrary(Gen.oneOf(doneGen, moreGen, flatMapGen))


  given expenseStateArb(using
                               expenseArb: Arbitrary[Expense]
                              ): Arbitrary[ExpenseState] =
    Arbitrary :
      Gen.listOf(expenseArb.arbitrary).map(ExpenseState.apply)


  given appStateArb(using
                           arbPersonState: Arbitrary[PersonState],
                           arbExpenseState: Arbitrary[ExpenseState]
                          ): Arbitrary[AppState] =
    Arbitrary :
      for
        ps <- arbPersonState.arbitrary
        es <- arbExpenseState.arbitrary
      yield AppState(es, ps)


  /**
   * TODO #3c: implement an arbitrary of PersonOp[A].
   *
   * One possible implementation is to create a State
   * whose run function ignores the current state and just
   * sets the state and value to random values.
   */
  given personOpArb[A](using
                              arbA: Arbitrary[A],
                              arbPersonState: Arbitrary[PersonState]
                             ): Arbitrary[PersonOp[A]] =
  Arbitrary :
    for
      a <- arbA.arbitrary
      ps <- arbPersonState.arbitrary
    yield State((_: PersonState) => (ps, a))


  given isValidArb[A](using
                             arbA: Arbitrary[A]
                            ): Arbitrary[IsValid[A]] =
    val validGen: Gen[IsValid[A]] = arbA.arbitrary.map(_.validNec[String])
    val invalidGen: Gen[IsValid[A]] = Gen
      .nonEmptyListOf(Arbitrary.arbitrary[String])
      .map(xs => NonEmptyChain.fromSeq(xs).get.invalid[A])
    Arbitrary(Gen.oneOf(validGen, invalidGen))


  given expenseOpGen[A](using
                               arbA: Arbitrary[A],
                               expenseStateArb: Arbitrary[ExpenseState]
                              ): Arbitrary[ExpenseOp[A]] =
    Arbitrary :
      for
        a <- arbA.arbitrary
        es <- expenseStateArb.arbitrary
      yield State((_: ExpenseState) => (es, a))

