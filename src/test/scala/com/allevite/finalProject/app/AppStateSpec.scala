package com.allevite.finalProject.app

import cats.implicits.*
import cats.kernel.laws.discipline.EqTests
import com.allevite.finalProject.FpFinalSpec
import com.allevite.part4_FinalProject.app.AppState

class AppStateSpec extends FpFinalSpec {
  checkAll("Eq[AppState]", EqTests[AppState].eqv)
}