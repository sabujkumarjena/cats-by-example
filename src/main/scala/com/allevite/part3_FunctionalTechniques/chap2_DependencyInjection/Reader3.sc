
import cats.data.Reader

val signReader: Reader[Int, String] = Reader(n => if(n > 0) "positive" else if(n < 0) "negative" else "zero")
signReader.run(0)

val parityReader: Reader[Int, String] = Reader(n => if(n % 2 == 0) "even" else "odd")
parityReader.run(1)

val descriptionReader: Reader[Int, String] =
  for {
    sign <- signReader
    parity <- parityReader
  } yield s"$sign and $parity"
descriptionReader.run(-2)

val addOneReader: Reader[Int, Int] =
  for {
    env <- Reader(identity[Int])
  } yield env + 1

case class Person(id: Long, name: String, emailAddress: String)
case class Account(id: Long, ownerId: Long)

trait AccountRepository {
  val accountRepository: AccountService

  trait AccountService {
    def findAccountById(id: Long): Account
    def saveAccount(account: Account): Unit
  }
}

trait LiveAccountRepository extends AccountRepository {
  override val accountRepository: AccountService = new AccountService {
    override def findAccountById(id: Long): Account = Account(id, 2)

    override def saveAccount(account: Account): Unit = ()
  }
}

trait PersonRepository {
  val personRepository: PersonService

  trait PersonService {
    def findPersonById(id: Long): Person
  }
}

trait LivePersonRepository extends PersonRepository {
  override val personRepository: PersonService = new PersonService {
    override def findPersonById(id: Long): Person = Person(2, "leandro", "leandro@mail.com")
  }
}

def findNextAccount(id: Long): Reader[AccountRepository, Account] =
  for {
    accountRepository <- Reader(identity[AccountRepository])
    account = accountRepository.accountRepository.findAccountById(id + 1)
  } yield account

def findOwnerNameByAccountId(id: Long): Reader[PersonRepository with AccountRepository, String] =
  for {
    accountModule <- Reader(identity[AccountRepository])
    personModule <- Reader(identity[PersonRepository])
    account = accountModule.accountRepository.findAccountById(id)
    owner = personModule.personRepository.findPersonById(account.ownerId)
  } yield owner.name

trait EmailRepository {
  val emailService: EmailService

  trait EmailService {
    def sendEmail(address: String, text: String): Unit
  }
}

trait LiveEmailRepository extends EmailRepository {
  override val emailService: EmailService = new EmailService {
    override def sendEmail(address: String, text: String): Unit = ()
  }
}

type Env = PersonRepository with AccountRepository with EmailRepository
val liveEnv: Env = new LivePersonRepository with LiveAccountRepository with LiveEmailRepository {}

findOwnerNameByAccountId(1).run(liveEnv)

def openAccount(accountId: Long, owner: Long): Reader[PersonRepository with AccountRepository with EmailRepository, Account] =
  for {
    accountRepository <- Reader(identity[AccountRepository])
    emailService <- Reader(identity[EmailRepository])
    personRepository <- Reader(identity[PersonRepository])
    account = Account(accountId, owner)
    _ = accountRepository.accountRepository.saveAccount(account)
    person = personRepository.personRepository.findPersonById(account.ownerId)
    _ = emailService.emailService.sendEmail(person.emailAddress, "You have a new account!")
  } yield account

openAccount(1, 1).run(liveEnv)

