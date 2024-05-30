trait ByteEncoder[A]:
  def encode(a: A): Array[Byte]


object ByteEncoder :
//  given stingByteEncoder: ByteEncoder[String] =  new ByteEncoder[String] :
//    override def encode(s: String): Array[Byte] =
//      s.getBytes

//  given ByteEncoder[String] = new ByteEncoder[String]:
//    override def encode(s: String): Array[Byte] =
//      s.getBytes

//  given ByteEncoder[String] with
//    override def encode(s: String): Array[Byte] =
//      s.getBytes

//  given stingByteEncoder: ByteEncoder[String] = (s: String) => s.getBytes

//  given ByteEncoder[String] = (s: String) => s.getBytes



  def apply[A](implicit ev: ByteEncoder[A]): ByteEncoder[A] = ev


given Rot3StringByteEncoder : ByteEncoder[String] = new ByteEncoder[String] :
  override def encode(s: String): Array[Byte] =
    s.getBytes.map(b => (b + 3).toByte)


ByteEncoder[String].encode("hello")