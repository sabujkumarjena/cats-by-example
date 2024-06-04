package com.allevite.finalProject.app


import cats.implicits.*
import com.allevite.finalProject.FpFinalSpec
import com.allevite.part4_FinalProject.app.Syntax.*
import com.allevite.finalProject.fakes.FakeEnv
import com.allevite.part4_FinalProject.app.{AppState, ExitCommand}

class ExitCommandSpec extends FpFinalSpec {
  test("Exit command does not alter state") {
    val env: FakeEnv = new FakeEnv {
      var linesToRead: List[String] = Nil
    }
    forAll { (initialAppState: AppState) =>
      assert(
        ExitCommand
          .execute()
          .unsafeRunAppS(env, initialAppState)
          eqv Right(initialAppState)
      )
    }
  }
}

 