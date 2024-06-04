package com.allevite.finalProject.app


import cats.implicits.*
import com.allevite.finalProject.FpFinalSpec
import com.allevite.finalProject.fakes.FakeEnv
import com.allevite.part4_FinalProject.app.{AddPersonCommand, AppState}
import com.allevite.part4_FinalProject.app.Syntax.*
import com.allevite.part4_FinalProject.model.Person

class AddPersonCommandSpec extends FpFinalSpec {
  test("Add person command reads data and adds a person to state") {
    forAll { (initialAppState: AppState) =>
      val name = "Leandro"
      val env = new FakeEnv {
        var linesToRead: List[String] = List(name)
      }

      assert(
        AddPersonCommand
          .execute()
          .unsafeRunAppS(env, initialAppState)
          .map(_.personState.personByName.get(name))
          eqv Right(Some(Person.unsafeCreate(name)))
      )
    }
  }

  test("Trying to add an invalid person yields error") {
    forAll { (initialAppState: AppState) =>
      val name = ""
      val env = new FakeEnv {
        var linesToRead: List[String] = List(name)
      }

      assert(
        AddPersonCommand
          .execute()
          .unsafeRunAppS(env, initialAppState)
          .isLeft
      )
    }
  }
}

 