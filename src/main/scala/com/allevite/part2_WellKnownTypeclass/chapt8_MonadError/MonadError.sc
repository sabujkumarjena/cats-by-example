import cats.*
import cats.implicits.*

import java.io.IOException
import scala.util.{Failure, Success, Try}

trait HttpMethod
case object GET extends HttpMethod
case class HttpRequest(method: HttpMethod, url: String)
case class HttpResponse(status: Int)

def doRequest(req: HttpRequest): HttpResponse =
  if(math.random() < 0.9) throw new IOException("boom!")
  else HttpResponse(200)

def executeRequest(req: HttpRequest): Option[HttpResponse] =
  try {
    Some(doRequest(req))
  } catch {
    case _: Exception => None
  }

def executeRequest2(req: HttpRequest): Either[String, HttpResponse] =
  try {
    Right(doRequest(req))
  } catch {
    case _: Exception => Left("Sorry :(")
  }

def executeRequest3(req: HttpRequest): Try[HttpResponse] =
  try {
    Success(doRequest(req))
  } catch {
    case e: Exception => Failure(e)
  }

executeRequest3(HttpRequest(GET, "ww.eeexample.com"))

val optionME: MonadError[Option, Unit] = new MonadError[Option, Unit] :
  override def raiseError[A](e: Unit): Option[A] = None

  override def handleErrorWith[A](fa: Option[A])(f: Unit => Option[A]): Option[A] =
    fa.orElse(f(()))

  override def pure[A](x: A): Option[A] = Some(x)

  override def flatMap[A, B](fa: Option[A])(f: A => Option[B]): Option[B] = fa.flatMap(f)

  override def tailRecM[A, B](a: A)(f: A => Option[Either[A, B]]) = ???

import com.allevite.part2_WellKnownTypeclass.chapt8_MonadError.MonadErrorInstances.*
def executeRequestME[F[_], E](request: HttpRequest)(f: Exception => E)(using ME: MonadError[F, E]): F[HttpResponse] =
  try {
    ME.pure(doRequest(request))
  } catch {
    case e: Exception => ME.raiseError(f(e))
  }

executeRequestME[Try, Throwable](HttpRequest(GET, "www.eeexample.com"))((e: Exception) => e)
type ErrorOr[A] = Either[String, A]
executeRequestME[Option, Unit](HttpRequest(GET, "www.eeexample.com"))((e: Exception) => ())
executeRequestME[ErrorOr, String](HttpRequest(GET, "wwww.eeexample.com"))((e: Exception) => e.getMessage)

MonadError[ErrorOr, String].ensure(Right(6))("oh noes")(_ % 2 == 1)
MonadError[ErrorOr, String].attempt(Left("oops")) //attempt always succeed
MonadError[ErrorOr, String].attempt(Right(6))
MonadError[Try, Throwable].attempt(Failure(new Exception("oops")))