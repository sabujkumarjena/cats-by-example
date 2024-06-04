package com.allevite.finalProject.fakes

import cats.implicits._
import com.allevite.part4_FinalProject.model.Person
import com.allevite.part4_FinalProject.service.PersonService
import com.allevite.part4_FinalProject.service.PersonService.PersonOp

trait FakePersonService extends PersonService {
  var peopleAdded = List[Person]()
  var peopleSearched = 0

  override val personService: PService = new PService {
    override def findByName(name: String): PersonOp[Option[Person]] = {
      peopleSearched += 1
      Option(Person.unsafeCreate(name)).pure[PersonOp]
    }

    override def addPerson(person: Person): PersonOp[Unit] = {
      peopleAdded = person :: peopleAdded
      ().pure[PersonOp]
    }

    override def getAllPeople(): PersonOp[List[Person]] =
      List.empty[Person].pure[PersonOp]
  }
}
