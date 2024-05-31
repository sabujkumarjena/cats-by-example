import java.nio.ByteBuffer

trait ByteEncoder[A] :
  def encode(a: A): Array[Byte]


given ByteEncoder[Int] = new  ByteEncoder[Int] :
  override def encode(n: Int): Array[Byte] =
    val bb = ByteBuffer.allocate(4)
    bb.putInt(n)
    bb.array()


given ByteEncoder[String] = new ByteEncoder[String] :
  override def encode(a: String): Array[Byte] =
    a.getBytes

//syntax
extension [A](a: A)
  def encode(using  ByteEncoder[A]): Array[Byte] =
    summon[ByteEncoder[A]].encode(a)


5.encode
"hello world".encode

trait ByteDecoder[A] :
  def decode(bytes: Array[Byte]): Option[A]


given ByteDecoder[Int]= new ByteDecoder[Int] :
  override def decode(bytes: Array[Byte]): Option[Int] =
    if bytes.length != 4 then None
    else
      val bb = ByteBuffer.allocate(4)
      bb.put(bytes)
      bb.flip()
      Some(bb.getInt())

