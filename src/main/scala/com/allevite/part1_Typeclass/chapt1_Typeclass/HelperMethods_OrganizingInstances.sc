import java.io.FileOutputStream
import scala.util.Using

trait ByteEncoder[A] :
  def encode(a: A): Array[Byte]


object ByteEncoder :
  given StringByteEncoder :ByteEncoder[String] =  new ByteEncoder[String] :
    override def encode(s: String): Array[Byte] =
      s.getBytes
  def apply[A](using ev: ByteEncoder[A]): ByteEncoder[A] = ev




trait Channel :
  def write[A](obj: A)(implicit enc: ByteEncoder[A]): Unit


object FileChannel extends Channel :
  override def write[A](obj: A)(using enc: ByteEncoder[A]): Unit =
    val bytes: Array[Byte] = enc.encode(obj)

    Using(new FileOutputStream("test")) { os =>
      os.write(bytes)
      os.flush()
    }


given Rot3StringByteEncoder: ByteEncoder[String] =  new ByteEncoder[String] :
  override def encode(s: String): Array[Byte] =
    s.getBytes.map(b => (b + 3).toByte)

case class Switch(isOn: Boolean)
object Switch :
  given SwitchByteEncoder:ByteEncoder[Switch] = new ByteEncoder[Switch] :
    // Should return an array of 1 byte:
    // - '1' if isOn is true
    // - '0' otherwise
    override def encode(s: Switch): Array[Byte] =
      Array(if(s.isOn) '1'.toByte else '0'.toByte)





FileChannel.write("hello")
FileChannel.write(Switch(true))


ByteEncoder.StringByteEncoder.encode("Sabuj") //inflexible
//Summon instances via apply method
ByteEncoder[String].encode("Sabuj")