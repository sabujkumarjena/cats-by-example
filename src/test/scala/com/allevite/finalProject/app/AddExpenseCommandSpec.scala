package com.allevite.finalProject.app

import cats.implicits.*
import com.allevite.finalProject.FpFinalSpec
import com.allevite.part4_FinalProject.app.Syntax.*
import com.allevite.finalProject.fakes.FakeEnv
import com.allevite.part4_FinalProject.app.{AddExpenseCommand, AppState}
import com.allevite.part4_FinalProject.model.{Expense, Money, Person}
import com.allevite.part4_FinalProject.service.PersonService.PersonState



class AddExpenseCommandSpec extends FpFinalSpec {
  test("Add expense command reads data and adds a expense") {
    forAll { (s: AppState) =>
      val initialAppState = s.copy(personState =
        PersonState(
          Map(
            "Leandro" -> Person.unsafeCreate("Leandro"),
            "Martin" -> Person.unsafeCreate("Martin"),
            "Susan" -> Person.unsafeCreate("Susan")
          )
        )
      )
      val env = new FakeEnv {
        var linesToRead: List[String] = List(
          "Leandro", // The payer
          "2000.00", // The amount
          "Martin", // The first participant
          "Susan", // The second participant
          "END" // No more participants
        )
      }
      val expense = Expense.unsafeCreate(
        Person.unsafeCreate("Leandro"),
        Money.unsafeCreate(200000),
        List(Person.unsafeCreate("Martin"), Person.unsafeCreate("Susan"))
      )
      assert(
        AddExpenseCommand
          .execute()
          .unsafeRunAppS(env, initialAppState)
          .map(_.expenseState.expenses) eqv Right(
          expense :: s.expenseState.expenses
        )
      )
    }
  }

  test("Trying to add invalid expense with no participants yields error") {
    forAll { (s: AppState) =>
      val initialAppState = s.copy(personState =
        PersonState(
          Map(
            "Leandro" -> Person.unsafeCreate("Leandro"),
            "Martin" -> Person.unsafeCreate("Martin"),
            "Susan" -> Person.unsafeCreate("Susan")
          )
        )
      )
      val env = new FakeEnv {
        var linesToRead: List[String] = List(
          "Leandro", // The payer
          "2000.00", // The amount
          "END" // No more participants
        )
      }
      val expense = Expense.unsafeCreate(
        Person.unsafeCreate("Leandro"),
        Money.unsafeCreate(200000),
        List(Person.unsafeCreate("Martin"), Person.unsafeCreate("Susan"))
      )
      assert(
        AddExpenseCommand
          .execute()
          .unsafeRunAppS(env, initialAppState)
          .isLeft
      )
    }
  }
}

