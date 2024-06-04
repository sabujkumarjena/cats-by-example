package com.allevite.part4_FinalProject

import com.allevite.part4_FinalProject.app.Configuration._
import com.allevite.part4_FinalProject.app.Syntax._
import com.allevite.part4_FinalProject.app.{App, AppState}

object Main {
  def main(args: Array[String]): Unit = {
    App
      .run()
      .unsafeRunApp(liveEnv, AppState.empty)
  }
}
