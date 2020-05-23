package exp4k

import exp4k.Operators.addition
import exp4k.Operators.factorial
import exp4k.Operators.multiplication
import exp4k.Operators.power
import exp4k.Operators.unaryMinus
import exp4k.Operators.unaryPlus
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ShuntingYardTest : StringSpec() {

    fun Array<Token>.doesMatch(vararg any: Any) {
        for (i in indices)
            when (val t = get(i)) {
                is Token.Number -> when (val v = t.value) {
                    is Int -> v shouldBeInt (any[i] as Int)
                    is Double -> v shouldBeDouble (any[i] as Double)
                }
                is Token.Operator -> t shouldBeOperator any[i] as Operator
                is Token.Variable -> t.name shouldBe any[i] as String
                else -> error("")
            }
    }

    init {
        "ShuntingYard1" {
            ShuntingYard.convertToRPN("2+3").doesMatch(2, 3, addition)
        }
        "ShuntingYard2" {
            ShuntingYard.convertToRPN("3*x").doesMatch(3, "x", multiplication)
        }
        "ShuntingYard3" {
            ShuntingYard.convertToRPN("-3").doesMatch(3, unaryMinus)
        }
        "ShuntingYard4" {
            ShuntingYard.convertToRPN("-2^2").doesMatch(2, 2, power, unaryMinus)
        }
        "ShuntingYard5" {
            ShuntingYard.convertToRPN("2^-2").doesMatch(2, 2, unaryMinus, power)
        }
        "ShuntingYard6" {
            ShuntingYard.convertToRPN("2^---+2").doesMatch(
                2, 2, unaryPlus, unaryMinus, unaryMinus, unaryMinus, power
            )
        }
        "ShuntingYard7" {
            ShuntingYard.convertToRPN("2^-2!").doesMatch(2, 2, factorial, unaryMinus, power)
        }
        "ShuntingYard8" {
            ShuntingYard.convertToRPN("-3^2").doesMatch(3, 2, power, unaryMinus)
        }
        "ShuntingYard9" {
            val reciprocal = Operator("$", 1, true, Precedence.division) { a: Int ->
                if (a == 0)
                    throw ArithmeticException("Division by zero!")
                1.0 / a.toDouble()
            }
            ShuntingYard.convertToRPN("1$", reciprocal).doesMatch(
                1, reciprocal
            )
        }
    }
}