# CATS BY EXAMPLES 
# 1. Introduction 

----

### Agenda 
- Channel
- Implicits & Helper methods
- Laws
- Automatic instance derivation
- Syntax

```scala
trait Channel:
  def write(obj: Any): Unit
```
Advantage:
- Simple Interface
Disadvantages:
- unhandled type -> throw exception (match error)
- two responsibilities 
    - getting the bytes
    - writing the bytes
```scala
trait Channel:
  def write(obj: ByteEncodable): Unit

trait ByteEncodable:
  def encode: Array[Byte]
```
Advantage:
- unique responsibility
- easy to test
- unhandled type -> compile error

Disadvantage:
- how to extend Int
- only one implementation
- overloaded interface

## Typeclass 
```scala
trait Channel:
  def write[A](obj: A, enc: ByteEncoder[A]): Unit

trait ByteEncoder[A]:
  def encode(a: A): Array[Byte]
```
Advantage:
- can be instanced by any type
- cleaner interface
- several implementations possible

## Typeclass: Helper Methods 

summons the instance in given scope
```scala
object ByteEncoder:
  def apply[A]( using enc: ByteEncoder[A])): ByteEncoder[A] = enc
```
helps create instances
```scala
object ByteEncoder:
  def instance[A] (f: A => Array[Byte]): ByteEncoder[A]
```
Adding **read** method to the **Channel**
```scala
trait Channel:
  def write[A](obj: A)(using enc: ByteEncoder[A]): Unit
  def read[A]()(using dec: ByteDecoder[A]): A

trait ByteDecoder[A]:
  def decode(bytes: Array[Byte]): Option[A]
```
### Typeclass ByteCodec
```scala
trait ByteCodec[A] extends ByteEncoder with ByteDecoder
```
### Laws
**Property:** Encoding and then decoding a value should return the original value unchanged.

```scala
def isomorphism(a: A)(using codec: ByteCodec[A]): Boolean =
  codec.decode(codec.encode(a)) == Some(a)
```
### Syntax
Using the typeclass
```scala
ByteEncoder[Int].encode(5)
```
using syntax
```scala
5.encode
```
we can have the nice syntax using **extension** method
```scala
extension [A](a: A)
  def encode(using enc: ByteEncoder[A]): Array[Byte] = 
    enc.encode(a)
```
### SUMMARY

- Typeclasses enable us to:
  - Extend types outside of our control (Int, String..)
  - Add functionality without modifying the interface
  - Use certain part of functionality when we need it (ad-hoc polymorphism)
  - Provide several implementations of the functionality for the same type
- By using **given** and some helper methods, we can make typeclasses easier to use
- Typeclasses usually have **laws**
- Laws make for excellent test cases

# 2. Wellknown Typeclasses

---

## 1. Eq

```scala
trait Eq[A]:
  /** Returns true if x and y are equivalent, false otherwise */
  def eqv(x: A, y: A): Boolean
```

## 2. Order

```scala
trait Order[A]:
  /** Result of comparing x with y. 
   * Returns an Int whose sign is:
   * - negative iff x < y 
   * - zero iff x = y 
   * - positive iff x > y */
  def compare(x: A, y: A): Int
```
## 3. Show

```scala
trait Show[A]:
  def show(a: A): String
```
## 4. Monoid

