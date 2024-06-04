package com.allevite.finalProject.fakes

import cats.Monoid
import cats.implicits._
import com.allevite.part4_FinalProject.model.{DebtByPayer, Expense}
import com.allevite.part4_FinalProject.service.ExpenseService
import com.allevite.part4_FinalProject.service.ExpenseService.ExpenseOp

trait FakeExpenseService extends ExpenseService {

  var callsToAddExpense = 0
  var callsToComputeDebt = 0

  override val expenseService: EService = new EService {
    override def addExpense(expense: Expense): ExpenseOp[Expense] = {
      callsToAddExpense += 1
      expense.pure[ExpenseOp]
    }

    override def computeDebt(): ExpenseOp[DebtByPayer] = {
      callsToComputeDebt += 1
      Monoid[DebtByPayer].empty.pure[ExpenseOp]
    }
  }

}
