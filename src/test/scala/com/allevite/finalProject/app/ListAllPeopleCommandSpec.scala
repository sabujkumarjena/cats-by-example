package com.allevite.finalProject.app


import cats.implicits.*
import com.allevite.finalProject.FpFinalSpec
import com.allevite.part4_FinalProject.app.Syntax.*
import com.allevite.finalProject.fakes.FakeEnv
import com.allevite.part4_FinalProject.app.{AppState, ListAllPeopleCommand}

class ListAllPeopleCommandSpec extends FpFinalSpec {
  test("List all people writes people name to console") {
    val env: FakeEnv = new FakeEnv {
       var linesToRead: List[String] = Nil
    }
    forAll { (initialAppState: AppState) =>
      ListAllPeopleCommand
        .execute()
        .unsafeRunAppS(env, initialAppState) eqv Right(initialAppState)
      initialAppState.personState.personByName.keySet.forall(name =>
        env.linesWritten.contains(name)
      )
    }
  }
}

 