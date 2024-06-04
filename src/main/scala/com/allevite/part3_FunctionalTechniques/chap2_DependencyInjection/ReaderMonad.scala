package com.allevite.part3_FunctionalTechniques.chap2_DependencyInjection

import cats.data.Reader

//Reader[A, B] is wrapper around a function
object ReaderMonad extends App:

  trait DbResultSet

  trait SqlDecoder[A] :
    def fromResultSet(result: DbResultSet): A

  object SqlDecoder :
    def apply[A](using ev: SqlDecoder[A]): SqlDecoder[A] = ev

    def instance[A](f: DbResultSet => A): SqlDecoder[A] = new SqlDecoder[A] {
      override def fromResultSet(result: DbResultSet): A = f(result)
    }


  case class Account(id: Long, balance: Double) :
    def deposit(amount: Double): Account =
      copy(balance = balance + amount)

    def withdraw(amount: Double): Account =
      copy(balance = balance - amount)


  object Account :
    given sqlDecoder: SqlDecoder[Account] = SqlDecoder.instance(rs => Account(1, 2000))

  case class Query(query: String) :
    def withLongParam(p: Long): Query = this



  case class Statement(statement: String) :
    def withLongParam(l: Long): Statement = this

    def withDoubleParam(d: Double): Statement = this


  trait DbClient :
    def executeStatement(sql: Statement): Boolean

    def executeQuery[A: SqlDecoder](sql: Query): A


  trait AccountService :
    def save(account: Account): Reader[DbClient, Boolean]

    def delete(id: Long): Reader[DbClient, Boolean]

    def findById(id: Long): Reader[DbClient, Account]

    def transferFunds(sourceId: Long, destId: Long, amount: Double): Reader[DbClient, Boolean]

    def update(id: Long, upd: Account => Account): Reader[DbClient, Boolean]

  object LiveAccountService extends AccountService:
    def save(account: Account): Reader[DbClient, Boolean] = ???

    def delete(id: Long): Reader[DbClient, Boolean] = ???

    def findById(id: Long): Reader[DbClient, Account] = ???

    def transferFunds(sourceId: Long, destId: Long, amount: Double): Reader[DbClient, Boolean] =
      for
        sourceAccount <- findById(sourceId)
        destAccount <- findById(destId)
        sourceSuccessful <- save(sourceAccount.withdraw(amount))
        destSuccessful <- save(destAccount.deposit(amount))
      yield sourceSuccessful && destSuccessful

    def update(id: Long, upd: Account => Account): Reader[DbClient, Boolean] =
      for
        account <- findById(id)
        success <- save(upd(account))
      yield success

  val signReader: Reader[Int, String] = Reader(n => if (n > 0) "positive" else if (n < 0) "negative" else "zero")
  println(signReader.run(0))
  //print("Reader Monad")