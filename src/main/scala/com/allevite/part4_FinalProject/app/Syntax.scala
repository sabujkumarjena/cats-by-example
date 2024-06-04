package com.allevite.part4_FinalProject.app

import cats.data.*
import cats.implicits.*
import com.allevite.part4_FinalProject.app.Configuration.*
import com.allevite.part4_FinalProject.common.IO
import com.allevite.part4_FinalProject.service.ExpenseService.ExpenseOp
import com.allevite.part4_FinalProject.service.PersonService.PersonOp

import scala.annotation.targetName

/**
 * Extension methods for different types in the application.
 */
object Syntax :

  extension [A](fa: IO[A])
    def toAppOp: AppOp[A] =
      val attemptIO: IO[Either[Error, A]] = fa.attempt.map(_.leftMap(_.getMessage))
      val errorOr: ErrorOr[A] = EitherT(attemptIO)
      val st: St[A] = StateT.liftF(errorOr)
      ReaderT.liftF(st)

  extension [A](fa: PersonOp[A])
    @targetName("f_string")
    def toAppOp: AppOp[A] =
      val st: St[A] = StateT { appState =>
        val (faS, faA) = fa.run(appState.personState).value
        (appState.copy(personState = faS), faA).pure[ErrorOr]
      }
      ReaderT.liftF(st)

  extension [A](fa: IsValid[A])
    def toAppOp: AppOp[A] =
      val mergedValidations: Either[Error, A] = fa.toEither.leftMap { errors =>
        s"""Errors: ${errors.mkString_("[", ", ", "]")}"""
      }
      val errorOr: ErrorOr[A] = EitherT.fromEither(mergedValidations)
      val st: St[A] = StateT.liftF(errorOr)
      ReaderT.liftF(st)
  
  extension [A](fa: ExpenseOp[A])
    def toAppOp: AppOp[A] =
      val st: St[A] = StateT { appState =>
        val (faS, faA) = fa.run(appState.expenseState).value
        (appState.copy(expenseState = faS), faA).pure[ErrorOr]
      }
      ReaderT.liftF(st)

  extension [A](fa: AppOp[A])
    def unsafeRunApp(
                      environment: Environment,
                      initialState: AppState
                    ): Either[Error, (AppState, A)] =
      fa.run(environment).run(initialState).value.run

    /**
     * Similar to unsafeRunApp but we only return the state.
     */
    def unsafeRunAppS(
                       environment: Environment,
                       initialState: AppState
                     ): Either[Error, AppState] =
      unsafeRunApp(environment, initialState).map(_._1)


  