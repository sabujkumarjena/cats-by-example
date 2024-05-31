import java.nio.ByteBuffer

trait ByteEncoder[A] :
  def encode(a: A): Array[Byte]


object ByteEncoder :
  def apply[A](using ev: ByteEncoder[A]): ByteEncoder[A] = ev


given ByteEncoder[String] = new  ByteEncoder[String] :
  override def encode(a: String): Array[Byte] =
    a.getBytes()


given ByteEncoder[Int] = new  ByteEncoder[Int] :
  override def encode(n: Int): Array[Byte] =
    val bb = ByteBuffer.allocate(4)
    bb.putInt(n)
    bb.array()

given optionEncoder[A](using encA: ByteEncoder[A]): ByteEncoder[Option[A]] = new ByteEncoder[Option[A]] :
  override def encode(a: Option[A]): Array[Byte] =
    a match
      case Some(value) => encA.encode(value)
      case None => Array[Byte]()

ByteEncoder[String].encode("hello")
ByteEncoder[Int].encode(1000)
ByteEncoder[Option[String]].encode(Option("hello"))
ByteEncoder[Option[String]].encode(None)
ByteEncoder[Option[Int]].encode(Option(1000))
ByteEncoder[Option[Int]].encode(None)