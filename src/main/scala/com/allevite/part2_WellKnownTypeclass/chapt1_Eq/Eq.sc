import cats.*
import cats.implicits.*

case class Account(id: Long, number: String, balance: Double, owner: String)

object Account :
  given Eq[Account] = Eq.fromUniversalEquals //==
  object Instances:
    given byIdEq(using eqLong: Eq[Long]): Eq[Account] = Eq.instance[Account]((a1, a2) => eqLong.eqv(a1.id, a2.id))
    given byIdEq2(using eqLong: Eq[Long]): Eq[Account] = Eq.by(_.id)
    given byNumber(using eqString: Eq[String]): Eq[Account] = Eq.by(_.number)

val account1 = Account(1, "123-56", 1000, "Sabuj")
val account2 = Account(2, "123-56", 1500, "Deepak")

Eq[Account].eqv(account1, account2)
Account.Instances.byIdEq.eqv(account1, account2)
Account.Instances.byNumber.eqv(account1, account2)

given eqToUse:Eq[Account] = Account.Instances.byNumber

account1 === account2
