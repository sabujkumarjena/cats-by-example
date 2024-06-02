import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.propspec.AnyPropSpec
import org.scalatest.refspec.RefSpec
import org.scalatest.wordspec.AnyWordSpec

object ScalaTestingStyles

//AnyFunSuit is a testing style in scalatest
class CalculatorSuit extends AnyFunSuite:

  val calculator = new Calculator

  test("multiplication by 0 should always be 0"):
    assert(calculator.multiply(1223, 0)  == 0)
    assert(calculator.multiply(-3423, 0)  == 0)
    assert(calculator.multiply(0, 0)  == 0)

  test("multiplication by 0 should always be 0 -- old with brace style") {
    assert(calculator.multiply(1223, 0) == 0)
    assert(calculator.multiply(-3423, 0) == 0)
    assert(calculator.multiply(0, 0) == 0)
  }

  test("dividing bu 0 should throw some math error"):
    assertThrows[ArithmeticException](calculator.divide(37455, 0))

//BDD
class CalculatorSpec extends AnyFunSpec:
  val calculator = new Calculator

  describe("multiplication"):
    it("should give back 0 if multiplying by 0"):
      assert(calculator.multiply(1223, 0) == 0)
      assert(calculator.multiply(-3423, 0) == 0)
      assert(calculator.multiply(0, 0) == 0)

  describe("multiplication --old style"):
    it("should give back 0 if multiplying by 0"):
      assert(calculator.multiply(1223, 0) == 0)
      assert(calculator.multiply(-3423, 0) == 0)
      assert(calculator.multiply(0, 0) == 0)

  describe("division"):
    it("should throw a math error if dividing by 0"):
      assertThrows[ArithmeticException](calculator.divide(37455, 0))

//BDD
class CalculatorWordSpec extends AnyWordSpec:
  val calculator = new Calculator

  "A calculator" should :
    "give back 0 if multiplying by 0" in :
      assert(calculator.multiply(1223, 0) == 0)
      assert(calculator.multiply(-3423, 0) == 0)
      assert(calculator.multiply(0, 0) == 0)

    "throw a math error if dividing by 0" in:
      assertThrows[ArithmeticException](calculator.divide(37455, 0))

//BDD
class CalculatorFreeSpec extends AnyFreeSpec:
  val calculator = new Calculator
  "A calculator" - { //Anything u want
    "give back 0 if multiplying by 0" in :
      assert(calculator.multiply(1223, 0) == 0)
      assert(calculator.multiply(-3423, 0) == 0)
      assert(calculator.multiply(0, 0) == 0)

    "throw a math error if dividing by 0" in :
      assertThrows[ArithmeticException](calculator.divide(37455, 0))

  }

// property-style checking
class CalculatorPropSpec extends AnyPropSpec:
  val calculator = new Calculator

  val multiplyByZeroExamples = List((1223, 0), (-3423, 0), (0, 0)  )

  property("Calculator multiply by 0 should be 0"):
    assert(multiplyByZeroExamples.forall:
      case(a,b) => calculator.multiply(a,b) == 0
    )

  property("Calculator divide by 0 should throw some math error"):
    assertThrows[ArithmeticException](calculator.divide(37455, 0))

// BDD style
class CalculatorRefSpec extends RefSpec:
  object `A calculator` :
    //test suit
    val calculator = new Calculator
    def `Calculator multiply by 0 should be 0`: Unit =
      assert(calculator.multiply(1223, 0) == 0)
      assert(calculator.multiply(-3423, 0) == 0)
      assert(calculator.multiply(0, 0) == 0)

    def `should throw a math error when dividing by 0`: Unit =
      assertThrows[ArithmeticException](calculator.divide(37455, 0))

class Calculator:
  def add(a: Int, b: Int):Int = a + b
  def substract(a: Int, b: Int):Int = a - b
  def multiply(a: Int, b: Int):Int = a * b
  def divide(a: Int, b: Int):Int = a / b