package com.allevite.finalProject.fakes

import com.allevite.part4_FinalProject.app.LiveController
import com.allevite.part4_FinalProject.service.{LiveExpenseService, LivePersonService}

trait FakeEnv
  extends LiveExpenseService
    with LivePersonService
    with FakeConsole
    with LiveController
