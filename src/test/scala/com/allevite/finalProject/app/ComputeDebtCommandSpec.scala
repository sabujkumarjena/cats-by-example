package com.allevite.finalProject.app


import com.allevite.finalProject.FpFinalSpec
import com.allevite.part4_FinalProject.app.{AppState, ComputeDebtCommand}
import com.allevite.part4_FinalProject.app.Syntax.*
import com.allevite.finalProject.fakes.FakeEnv

class ComputeDebtCommandSpec extends FpFinalSpec {
  test("compute debt does not crash") {
    val env: FakeEnv = new FakeEnv {
      var linesToRead: List[String] = Nil
    }
    forAll { (initialState: AppState) =>
      ComputeDebtCommand.execute().unsafeRunApp(env, initialState).isRight
    }

  }
}
