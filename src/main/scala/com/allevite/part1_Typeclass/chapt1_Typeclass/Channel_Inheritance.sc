import java.io.FileOutputStream
import scala.util.Using



/** Gives the capability to encode into byte array when this is extended  */
trait ByteEncodable :
  def encode(): Array[Byte]

/** Gives the capability to write to Channel */
trait Channel :
  /** write any byte encodable object to Channel
   *
   * @param obj any byte encodable object
   * */
  def write(obj: ByteEncodable): Unit

/** FullName case class
 *
 * @constructor create a full name with firstName and lastName
 * @param firstName fist name
 * @param lastName  last name
 */
case class FullName(firstName: String, lastName: String) extends ByteEncodable :
  override def encode(): Array[Byte] =
    firstName.getBytes ++ lastName.getBytes

/** Gives capability to write any byte encodable object to file */
object FileChannel extends Channel :
  override def write(obj: ByteEncodable): Unit =
    val bytes: Array[Byte] = obj.encode()

    Using(new FileOutputStream("test")) { os =>
      os.write(bytes)
      os.flush()
    }

/** Advantages
 *
 *  Advantage:
 *  - unique responsibility
 *  - easy to test
 *  - unhandled type -> compile error
 *
 *  Disadvantage:
 *  - how to extend Int
 *   - only one implementation
 *  - overloaded interface
 */