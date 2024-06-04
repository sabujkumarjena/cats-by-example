
import cats.data.Reader

val signReader: Reader[Int, String] = Reader(n => if (n > 0) "positive" else if (n < 0) "negative" else "zero")
signReader.run(0)

val parityReader: Reader[Int, String] = Reader(n => if(n % 2 == 0) "even" else "odd")
parityReader.run(1)

val descriptionReader: Reader[Int, String] =
  for
    sign <- signReader
    parity <- parityReader
  yield s"$sign and $parity"
descriptionReader.run(-2)

val addOneReader: Reader[Int, Int] =
  for
    env <- Reader(identity[Int]) //ask
  yield env + 1

/////////////////////
case class Person(id: Long, name: String, emailAddress: String)
case class Account(id: Long, ownerId: Long)

trait AccountRepository:
  def findAccountById(id: Long): Account

trait LiveAccountRepository extends AccountRepository:
  def findAccountById(id: Long): Account = Account(id,2)

trait PersonRepository:
  def findPersonById(id: Long): Person

trait LivePersonRepository extends PersonRepository:
  override def findPersonById(id: Long): Person = Person(2, "Sabuj", "semail")

def findNextAccount(id: Long): Reader[AccountRepository, Account] =
  for {
    repos <- Reader(identity[AccountRepository])
    account = repos.findAccountById(id + 1)
  } yield account

def findOwnerNameByAccountId(id: Long)=
  for {
    ar <- Reader(identity[AccountRepository])
    pr <- Reader(identity[PersonRepository])
    account = ar.findAccountById(id)
    owner = pr.findPersonById(account.ownerId)
  } yield owner.name

val deps: PersonRepository with AccountRepository = new LivePersonRepository  with LiveAccountRepository

findOwnerNameByAccountId(1).run(deps)

 