# CATS BY EXAMPLES #
# 1. Introduction #

----

### Agenda ###
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

## Typeclass ##
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

## Typeclass: Helper Methods ##

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
