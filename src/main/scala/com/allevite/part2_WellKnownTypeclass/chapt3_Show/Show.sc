import cats.*
import cats.implicits.*

case class Account(id: Long, number: String, balance: Double, owner: String)

object Account :
  implicit val toStringShow: Show[Account] = Show.fromToString

  object Instances :
    given byOwnerAndBalance: Show[Account] = Show.show { account =>
      s"${account.owner} - $$${account.balance}"
    }

    // Write an instance of show which will output something like 'This account belongs to Leandro'
    given prettyByOwner: Show[Account] = Show.show { account =>
      s"This account belongs to ${account.owner}"
    }
  


val sabuj = Account(1, "123-45", 2000, "Sabuj")
Account.toStringShow.show(sabuj)
Account.Instances.byOwnerAndBalance.show(sabuj)
Account.Instances.prettyByOwner.show(sabuj)

import Account.Instances.prettyByOwner
sabuj.show