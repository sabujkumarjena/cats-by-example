import cats.*
import cats.implicits.*

case class Account(id: Long, number: String, balance: Double, owner: String)

object Account :
  given orderById(using orderLong: Order[Long]): Order[Account] = Order.from((a1, a2) => orderLong.compare(a1.id, a2.id))

  object Instances :
    given orderByNumber: Order[Account] = Order.by(_.number)
    // provide an instance of Order[Account] that orders by balance
    given orderByBalance(using orderDouble: Order[Double]): Order[Account] = Order.by(_.balance)



def sort[A](list: List[A])(using orderA: Order[A]) =
  list.sorted(orderA.toOrdering)

val account1 = Account(1, "442-21", 3000, "Sabuj")
val account2 = Account(2, "442-21", 2500, "Deepako")
sort[Account](List(account1, account2))

account1 compare account2
account1 min account2
account1 max account2

given Order[Account] = Order.reverse(Account.orderById)
sort[Account](List(account1, account2))