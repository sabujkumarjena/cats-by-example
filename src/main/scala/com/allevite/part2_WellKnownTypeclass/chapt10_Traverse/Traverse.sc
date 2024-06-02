import cats.*
import cats.data.*
import cats.implicits.*


trait MList[+A]
object MList :
  case class MCons[+A](hd: A, tl: MList[A]) extends MList[A]
  case object MNil extends MList[Nothing]

  def apply[A](elems: A*) =
    elems.foldRight(mnil[A])((a, b) => mcons(a, b))


  def mnil[A]: MList[A] = MNil
  def mcons[A](hd: A, tl: MList[A]): MList[A] = MCons(hd, tl)
  // 1. Write a functor instance for MList
  // 2. Implement traverse in terms of sequence and using the functor

  given listFuntor :Functor[MList] = new Functor[MList]:
    override def map[A, B](fa: MList[A])(f: A => B) =
      fa match
        case MNil => MNil
        case MCons(h,t) => MCons(f(h), map(t)(f))

  given traverseMList: Traverse[MList] = new Traverse[MList] :
    override def traverse[G[_]: Applicative, A, B](fa: MList[A])(f: A => G[B]): G[MList[B]] =
      fa match
        case MNil => Applicative[G].pure(MNil)
        case MCons(h, t) => (f(h), traverse(t)(f)).mapN(MCons.apply)

//      sequence(fa.map(f))

    override def foldLeft[A, B](fa: MList[A], b: B)(f: (B, A) => B): B = fa match
      case MNil => b
      case MCons(h, t) => foldLeft(t, f(b, h))(f)


    override def foldRight[A, B](fa: MList[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = fa match
      case MNil => lb
      case MCons(h, t) => foldRight(t, f(h, lb))(f)


    override def sequence[G[_]: Applicative, A](fga: MList[G[A]]): G[MList[A]] =
      traverse(fga)(identity)


import MList._

Traverse[MList].traverse(MList(1, 2, 3)) { i =>
  if(i % 2 == 0) Option("even") else Option("odd")
}

Traverse[MList].sequence(MList(Some(1), Option(2), Some(3)))
Traverse[MList].sequence(MList(Some(1), Option(2), None, Some(3)))

given  optionTraverse: Traverse[Option] = new Traverse[Option]:
  override def traverse[G[_] : Applicative, A, B](fa: Option[A])(f: A => G[B]): G[Option[B]]=
    fa match
      case  None => Applicative[G].pure(None)
      case Some(a) => f(a).map(Option(_))

  override def foldLeft[A, B](fa: Option[A], b: B)(f: (B, A) => B):B =
    fa match
      case None => b
      case Some(a) => f(b,a)

  override def foldRight[A, B](fa: Option[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]):Eval[B] =
    fa match
      case None => lb
      case Some(a) => f(a, lb)

optionTraverse.traverse(Some(5))(x => List( x +1, x + 2)) //List(Some(6), Some(7))
optionTraverse.traverse[List, Int, Int](None)(x => List( x +1, x + 2)) //List(None)

type MapString[A] = Map[String, A]
implicit val traverseMap: Traverse[MapString] = new Traverse[MapString] {
  override def traverse[G[_], A, B](fa: MapString[A])(f: A => G[B])(implicit G: Applicative[G]): G[MapString[B]] = ???


  override def foldLeft[A, B](fa: MapString[A], b: B)(f: (B, A) => B): B = ???

  override def foldRight[A, B](fa: MapString[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = ???
}