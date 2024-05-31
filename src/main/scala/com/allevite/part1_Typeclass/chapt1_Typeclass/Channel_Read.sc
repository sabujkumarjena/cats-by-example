import scala.util.Try

/*
1. Add apply and instance to ByteDecoder

2. Write an instance of ByteDecoder[String] (declare it as implicit val or implicit object)

3. Use the instance to decode the following array: Array(98, 105, 101, 110, 32, 58, 41)
   Extra points: use apply (point 1)

*/
trait ByteDecoder[A] :
  def decode(bytes: Array[Byte]): Option[A]


object ByteDecoder :
  def apply[A](implicit ev: ByteDecoder[A]): ByteDecoder[A] = ev
  def instance[A](f: Array[Byte] => Option[A]): ByteDecoder[A] = new ByteDecoder[A] :
    override def decode(bytes: Array[Byte]): Option[A] = f(bytes)



given stringByteDecoder: ByteDecoder[String]  =  new ByteDecoder[String] :
  override def decode(bytes: Array[Byte]): Option[String] =
    Try(new String(bytes)).toOption


val a: Array[Byte] = Array(97, 98, 99, 100 ,101, 102, 110, 104)

ByteDecoder[String].decode(a)