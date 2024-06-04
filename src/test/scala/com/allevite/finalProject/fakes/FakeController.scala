package com.allevite.finalProject.fakes

import com.allevite.part4_FinalProject.app.{Command, Controller}

trait FakeController extends Controller {
  val commands: Map[Int, Command] = Map.empty

  override val controller: Service = new Service {
    override def getCommandByNumber(number: Int): Option[Command] =
      commands.get(number)

    override def getAllCommands: Array[Command] = commands.values.toArray
  }
}