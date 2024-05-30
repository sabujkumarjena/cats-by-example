import java.io.FileOutputStream
import java.nio.ByteBuffer
import scala.util.Using
/** Typeclass with capability to encode any type to array of bytes */
trait ByteEncoder[A] :
  /** encode any object of tpe A to array of bytes
   *
   *  @param a of type [[A]]
   *  @return {{{Array[Byte]}}}
   */
  def encode(a: A): Array[Byte]

/** An interface to write to a Channel */
trait Channel :
  /** writes to the channel
   * 
   * @param obj an instance of type [[A]]
   * @param enc an encoder of type [[A]]
   */
  def write[A](obj: A, enc: ByteEncoder[A]): Unit

/** A file channel with capability to write to file */
object FileChannel extends Channel :
  override def write[A](obj: A, enc: ByteEncoder[A]): Unit =
    val bytes: Array[Byte] = enc.encode(obj)

    Using(new FileOutputStream("test")) { os =>
      os.write(bytes)
      os.flush()
    }

/** ByteEncoder for [[Int]] type */
object IntByteEncoder extends ByteEncoder[Int] :
  override def encode(n: Int): Array[Byte] =
    val bb = ByteBuffer.allocate(4)
    bb.putInt(n)
    bb.array()


FileChannel.write[Int](5, IntByteEncoder)
/** ByteEncoder for [[String]] */
object StringByteEncoder extends ByteEncoder[String] :
  override def encode(s: String): Array[Byte] =
    s.getBytes


FileChannel.write("hello", StringByteEncoder)