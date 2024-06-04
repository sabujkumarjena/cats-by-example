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

```scala
trait Monoid[A] extends Semigroup[A]:
  def combine(x: A, y: A): A // from semigroup
  def empty: A
```
Monoid Laws
```scala
given s: Monoid[A]
def semigroupAssociative(x: A, y: A, z: A): IsIq[A] =
  s.combine(s.combine(x,y), z) <->
          s.combine(x, s.combine(y, z))
def leftIdentity(x: A): IsEq[A] =
  s.combine(s.empty, x) <-> x
def rightIdentity(x:A): IsEq[A] =
  s.combine(x, s.empty) <-> x
```
**Higher Kinded Types**
```scala
*  ordinary types / no type parameters / e.g. String, Int, Double
* -> *  type constructor/ one type parameter / e.g List, Option, Set
* -> ->* -> *  type constructor/ two type parameter / e.g Map, Either
(* -> *) -> *    type constructor/ one type parameter (of kind * -> *) / e.g Functor

```
**Examples**
```scala
Int: *
List: * -> *
List[Int]: *  //apply Int as an argument
Map: * -> * -> *
Map[String, *]: * -> *  //partial application  //kind projector
Map[String, Int]: *

// Types of kind * -> *, * -> * -> * , etc. are called higher kinded types (HKT)
```

## 5. Functor
```scala
trait Functor[F[_]]:
  def map[A,B](fa: F[A])(f: A => B): F[B]

//Laws
def covariantIdentity[A](fa: F[A]): IsEq[F[A]] =
  fa.map(identity) <-> fa
  
def covariantComposition[A,B,C]( fa: F[A], f: A => B, g: B => C): IsEq[F[C]] =
  fa.map(f).map(g) <-> fa.map(f.andThen(g))
```
## 6. Applicative
```scala
trait Applicative[F[_]] extends Apply[F]: //extends Functor
  def pure[A](x: A): F[A] // wraps a value in F
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]  // applies a function in the F context

//Laws
def applicativeIdentity[A](fa: F[A]): IsEq[F[A]] =
  F.pure((a:A) => a).ap(fa) <-> fa
/** pure(id) is the identity function in the F context ( where ap is function application*/ 

def applicativeHomorphism[A,B](a: A, f: A => B): IsEq[F[B]] =
  F.pure(f).ap(F.pure(a)) <-> F.pure(f(a))

/**
 *      A     pure      F[A]
 *      |-------------->|
 *      |               |
 *    F |               | ap(pure(f))
 *      |               |
 *      v-------------->v
 *      B     pure      F[B]
 *      
  */
def applicativeInterchange[A,B](a: A, ff: F[A => B]): IsEq[F[B]] =
  ff.ap(F.pure(a)) <-> F.pure((f: A => B) => f(a)).ap (ff)
  
def applicativeComposition[A,B,C](fa: F[A], fab: F[A => B], fbc: F[B => C]): IsEq[F[C]] =
  val compose: (B => C) =>(A => B) => (A => C) = _.compose
  F.pure(compose).ap(fbc).ap(fab).ap(fa) <-> fbc.ap(fab.ap(fa))
```

## 7. Monad

```scala
trait Monad[F[_]] extends ...:
  def pure[A](x: A): F[A] // from Applicative
  def flatMap[A,B](fa: F[A])(f: A => F[B]): F[B] // from FlatMap
  def tailRecM[A,B](a: A)(f: A => F[Either[A,B]]): F[B]  //for stack safety
```
Laws
```scala
def monadLeftIdentity[A, B](a: A, f: A => F[B]): IsEq[F[B]] =
  F.pure(a).flatMap(f) <-> f(a)
  
def monadRightIdentity[A](fa: F[A]): isEq[F[A]] =
  fa.flatMap(F.pure) <-> fa
  
def flatMapAssociativity[A,B,C]( fa: F[A], f: A => F[B], g: B => F[C]): IsEq[F[C]] =
  fa.flatMap(f).flatMap(g) <->
          fa.flatMap(a => f(a).flatMap(g))
```

## 8. MonadError
```scala
trait MonadError[E, A]:
  def raiseError[A](e: E): F[A]  //from ApplicativeError
  def handleErrorWith[A](fa: F[A])(f: E => F[A]): F[A]  //from ApplicativeError
  def pure[A](x: A): F[A]
  def flatMp[A, B](fa: F[A])(f: A => F[B]): F[B]
  def tailRecM[A, B](a: A)(f: A => F[Either[A, B]]): F[B]
```
Laws
```scala
def monadErrorLeftZero[A, B](e: E, f:  => F[B]): IsEq[F[B]]=
  F.flatMap(F.raiseError[A](e))(f) <-> F.raiseError[B](e) //fail fast
```
## 9. Foldable

```scala
trait Foldable[F[_]]:
  def foldLeft[A, B](fa: F[A], b: B)(f: (B, A) => B): B
  def foldRight[A, B](fa: F[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B]

  def foldMap[A,B](fa: F[A])(f: A => B)(using M: Monoid[B]): B =
    foldLeft(fa)(M.empty)((b, a) => M.combine(b, f(a)))
  /** foldMap[Int, String](List(1,2,3))(_.show) === "123"*
```
Lawa
```scala
def leftFoldConsistentWithFoldMap[A, B](fa: F[A], f: A => B)(using M: Monoid[B]): IsEq[B] =
  fa.foldMap(f) <-> fa.foldLeft(M.empty){ (b,a) =>
    b |+| f(a)
  }

def rightFoldConsistentWIthFoldMap[A,B]( fa: F[A], f: A => B)( using M: Monoid[B]): IsEq[B] =
  fa.foldMap(f) <-> fa.foldRight(Later(M.empty))((a,lb) => lb.map(f(a) |+| _).value)
```
## 10. Traverse

```scala
trait Traverse[F[_]]...:
  def traverse[G[_]:Applicative, A,B](fa: F[A])(f: A => G[B]): G[F[B]]
  def foldLeft[A, B](fa: F[A], b: B)(f: (B,A) => B): B
  def foldRight[A,B](fa: F[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B]
```
Laws
```scala
def traverseIdentity[A, B](fa: F[A], f: A => B): IsEq[F[B]] =
  fa.traverse[Id, B](f) <-> F.map(fa)(f)
def traverseSequentialComposition[A, B, C, M[_], N[_]]
  (fa: F[A], f: A => M[B], g: B => N[C])(using N: Applicative[N], M: Applicative[M]): IsEq[Nested[M, N, F[C]]] = {
  val lhs = Nested(M.map(fa.traverse(f))(fb => fb.traverse(g)))
  val rhs = fa.traverse[Nested[M,N, *], C](a => Nested(M.map(f(a))(g)))
  lhs <-> rhs
} 
//Nested is used to compose M[_] and N[_]
Nested(fa.traverse(f).map(fb => fb.traverse(g)))
<-> fa.traverse(a => Nested(f(a).map(g)))
```

---

# 3 Functional Techniques

---
## 1. Validations
```scala
// E is for error accumulator and must have a semi group instant
// A for computed value
trait Validated[+E, +A]  // it ia a Applicative

final case class Valid[+A](a: A) extends Validated[Nothing, A]
final case class Invalid[+E](e: E) extends Validated[E, Nothing]

```
## 2. Dependency Injection
```scala
// Monad[[c] =>> Reader[c, A]]
Reader[C,A] // C is the environment or config and A is the read or computed value
```
## 3. Tracking - Writer Monad
```scala
/** Calculate a value while we accumulate data in a log */
Writer[W, A]
//W - type of the log, must have semigroup instance so wwe can combine it
//A - computed value
```
```scala
type Tracked[A] = Writer[List[String], A]
```

## 4. State Management vai State Monad
```scala
// Monad[[s] =>> State[s, A]]
class State[S, A](val run: S => (S,A))  // initail state => (final state, return value)

object State:
  def get[S]: State[S,S] //reads the state
  def set[S](s: S): State[S, Unit]  //writes the state
  def modify[S](f: S => S): State[S, Unit] //Update the state with function
```

## 5. Trampolines (Stack-safety)
- **function calls** creates stack-frames in call stack
- Recursive functions
- Tail recursive function
  - tail call optimisation (TCO)
```scala
@tailrec
def fact(n:Int, acc: Int = 1): Int =
  if n ==0 then acc
  else fact(n-1, n* acc)
```
### Mutually recursive call
```scala
def isEven(n: Int): Boolean =
    if n == 0 then true else isOdd(n-1)
  def isOdd(n: Int): Boolean=
    if n==0 then false else isEven(n-1)
```
Solved by Trampoline

```scala
trait Trampoline[+A]
object Trampoline:
  case class Done[A](a: A) extends Trampoline[A]
  case class More[A](f: () => Trampoline[A]) extends Trampoline[A]
  case class FlatMap[A, B](ta: Trampoline[A], f: A => Trampoline[B]) extends Trampoline[B]

  @tailrec
  def resume[A](ta: Trampoline[A]): Either[() => Trampoline[A],A] = ta match
    case Done(a) => Right(a)
    case More(thunk) => resume(thunk())
    case FlatMap(t,f) => t match
      case Done(a) => resume(f(a))
      case More(thunk) => Left(() => FlatMap(thunk(), f))
      case FlatMap(t2, f2) => resume(FlatMap(t2, (x)=> FlatMap(f2(x),f)))
  // FlatMap(FlatMap(t2, f2), f) ---> FlatMap(t2, x => FlatMap(f2(x), f))

  @tailrec
  def run[A](ta:Trampoline[A]): A = resume(ta) match
    case Right(a) => a
    case Left(thunk) => run(thunk())
```

```scala
def isEven(n: Int): Trampoline[Boolean] =
    if n == 0 then Done(true) else More(() => isOdd(n-1))
  def isOdd(n: Int): Trampoline[Boolean]=
    if n==0 then Done(false) else More(() => isEven(n-1))
```

## 6. Evaluation Modes - Eval Monad
```scala
trait Eval[+A]:
  def value: A
  def map[B](f: A => B): Eval[B] // stack safe // uses trampoline internally
  def flatMap[B](f: A => Eval[B]): Eval[B] // // stack safe // uses trampoline internally

object Eval:
  def now[A](a: A): Eval[A] // esger - no memo - val
  def later[A](a :=> A): Eval[A] //lazy - memo - lazy val
  def always[A](a: => A): Eval[A] // lazy - no memo - def
  def defer[A](a: => Eval[A]): Eval[A] // defers the computation of an Eval
```
## 7. TailRecM

## 8. Monad Transformer
```scala
ReaderT[F,A,B] // wrapper for A => F[B]
```
```scala
type AccountOp[A] = ReaderT[ErrorOr, AccountRepo, A]
                          //monad,   dependency,  value
```

## 9. Suspending Side effect

## Summary
- Perform validations that accumulate errors via **Validated**
- Declare dependencies via the **Reader monad** for easier composition
- Track information via the **Writer monad**
- Manage state in pure manner via the **State monad**
- Use different evaluation modes (especially the lazy mode) via the **Eval monad**
- Define rich interfaces by stacking effects, and using transformers to facilitate the composition
- Suspend effects to preserve referential transparency in programs