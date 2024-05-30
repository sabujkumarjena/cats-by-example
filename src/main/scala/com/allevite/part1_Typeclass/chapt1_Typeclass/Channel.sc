import java.io.FileOutputStream
import java.nio.ByteBuffer
import scala.util.Using

/** A generic channel to write any object.
 *
 *  Advantage: Simple Interface
 *  Disadvantage:
 *  - Unhandled type -> throw exception
 *  -  focusing on two responsibilities -
 *        i. getting the bytes
 *        i.  writing bytes
 */

trait Channel:
  def write(obj: Any): Unit

/** A file channel with capability of write any object to file
 *
 *  @param obj  any object
 */

object FileChannel extends Channel:
  override def write(obj: Any): Unit =
    val bytes: Array[Byte] = obj match
      case n: Int =>
        val bb = ByteBuffer.allocate(4)
        bb.putInt(n)
        bb.array()

      case s: String => s.getBytes
      case _ => throw new Exception("unhandled")

    Using(new FileOutputStream("cats-by-example/test")) {os =>
      os.write(bytes)
      os.flush()
    }

FileChannel.write("hello world")
