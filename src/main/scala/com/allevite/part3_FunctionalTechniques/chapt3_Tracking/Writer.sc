import cats.*
import cats.data.*
import cats.implicits.*

// case class WriterT[F[_], L, V](run: F[(L, V)])

object Tracked :
  type Tracked[A] = Writer[List[String], A]

  given trackedShow[A: Show]: Show[Tracked[A]] = Show.show : ta =>
    val (log: List[String], a: A) = ta.run
    (log ++ List(a.show)).mkString("\n")



import Tracked.*

case class Client(id: Long, name: String, age: Int)
object Client :
  def makeRaw(id: Long, name: String, age: Int): Tracked[Client] =
    Client(id, name, age).writer(List("Creating client..."))


case class Product(id: Long, name: String, unitPrice: Double)
object Product :
  def makeRaw(id: Long, name: String, unitPrice: Double): Tracked[Product] =
    Product(id, name, unitPrice).writer(List("Creating product..."))


case class ShoppingCartItem(quantity: Int, product: Product) :
  def total: Double = quantity * product.unitPrice


object ShoppingCartItem :
  given shoppingCartItemShow: Show[ShoppingCartItem] =
    Show.show(item => s"${item.quantity} x ${item.product.name}")

  def makeRaw(quantity: Int, productId: Long, productName: String, productUnitPrice: Double): Tracked[ShoppingCartItem] =
    for
      _ <- List("Creating shopping cart item").tell
      product <- Product.makeRaw(productId, productName, productUnitPrice)
    yield ShoppingCartItem(quantity, product)

case class ShoppingCart(client: Client, items: List[ShoppingCartItem]) :
  def total: Double = items.map(_.total).sum


object ShoppingCart :
  given scShow: Show[ShoppingCart] = Show.fromToString

  def makeRaw(
               clientId: Long,
               clientName: String,
               clientAge: Int,
               items: List[(Int, Long, String, Double)]): Tracked[ShoppingCart] =
    for
      _ <- List("Creating shopping cart").tell
      client <- Client.makeRaw(clientId, clientName, clientAge)
      scitems <- items.traverse { case (q, pid, pname, pprice) => ShoppingCartItem.makeRaw(q, pid, pname, pprice) }
    yield ShoppingCart(client, scitems)




sealed trait Discount :
  val name: String
  def applies(client: Client, shoppingCartItem: ShoppingCartItem): Boolean
  def getDiscountedAmount(shoppingCartItem: ShoppingCartItem): Double

  def calculateDiscount(
                         client: Client,
                         shoppingCartItem:
                         ShoppingCartItem
                       ): Tracked[Double] =
    if applies(client, shoppingCartItem) then
      getDiscountedAmount(shoppingCartItem)
        .writer(List(s"Applied discount: $name"))
    else
      0d.pure[Tracked] //Applicative[Tracked].pure(0.d)



object Discount :
  object MoreThanFiveUnitsDiscount extends Discount :
    override val name = "10% discount on 5 units or more"

    override def applies(client: Client, shoppingCartItem: ShoppingCartItem): Boolean =
      shoppingCartItem.quantity > 5

    override def getDiscountedAmount(shoppingCartItem: ShoppingCartItem): Double =
      shoppingCartItem.total * 0.1


  object ElderlyDiscount extends Discount :
    override val name = "20% discount for people 65 or older"

    override def applies(client: Client, shoppingCartItem: ShoppingCartItem): Boolean =
      client.age > 65

    override def getDiscountedAmount(shoppingCartItem: ShoppingCartItem): Double =
      shoppingCartItem.total * 0.2


  val allDiscounts: List[Discount] = List(MoreThanFiveUnitsDiscount, ElderlyDiscount)


def calculateTotalDiscount(shoppingCart: ShoppingCart, discounts: List[Discount]): Tracked[Double] =
  //  (shoppingCart.items, discounts).mapN { (item, discount) =>
  //    discount.calculateDiscount(shoppingCart.client, item)
  //  }.combineAll
  (shoppingCart.items, discounts)
    .tupled
    .foldMap { case (i, d) => d.calculateDiscount(shoppingCart.client, i) }

//  (shoppingCart.items, discounts)
//    .tupled
//    .traverse { case (i, d) => d.calculateDiscount(shoppingCart.client, i) }
//    .map(_.sum)


def calculateTotal(shoppingCart: ShoppingCart): Tracked[Double] =
  calculateTotalDiscount(shoppingCart, Discount.allDiscounts)
    .map(a => shoppingCart.total - a)


val client = Client(1, "Sabuj", 70)
val milk = Product(1, "milk", 15.0)
val eggs = Product(1, "eggs", 25.0)
val items = List(
  ShoppingCartItem(15, milk),
  ShoppingCartItem(30, eggs)
)
val shoppingCart = ShoppingCart(client, items)
Show[Tracked[Double]].show(calculateTotalDiscount(shoppingCart, Discount.allDiscounts))
Show[Tracked[Double]].show(calculateTotal(shoppingCart))

val sc = ShoppingCart.makeRaw(
  1,
  "Sabuj",
  70,
  List((1, 3, "eggs", 15), (4, 8, "milk", 30))
)

Show[Tracked[ShoppingCart]].show(sc)

8.pure[Tracked] //WriterT((List(),8))
List("hello word").tell //WriterT((List(hello word),()))
10.writer(List("Hi")) //WriterT((List(Hi),10))
val x = Writer(List("hello"), 9) //WriterT((List(hello),9))
x.reset // WriterT((List(),9)) //(List(hello),9)
x.listen  // WriterT((List(hello),(9,List(hello))))
x.value //9
x.run // (List(hello),9)
x.map(_+1)
x.flatMap(i => (i + 2).writer(List(" increamented by 2"))) //WriterT((List(hello,  increamented by 2),10))
val y = 20.writer(List("world"))
(x,y).mapN(_+_)  //WriterT((List(hello, world),29))

x |+| y //WriterT((List(hello, world),29))

List(x,y).combineAll // WriterT((List(hello, world),29))
