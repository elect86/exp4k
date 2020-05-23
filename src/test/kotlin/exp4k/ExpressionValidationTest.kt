package exp4k

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.*

class ExpressionValidationTest : StringSpec() {

    init {
        // valid scenarios
        "ValidateNumber" {
            Expression("1").isValidAndComplete() shouldBe true
        }

        "ValidateNumberPositive" {
            Expression("+1").isValidAndComplete() shouldBe true
        }

        "ValidateNumberNegative" {
            Expression("-1").isValidAndComplete() shouldBe true
        }

        "ValidateOperator" {
            Expression("x + 1 + 2").apply {
                isValid() shouldBe true
                isComplete() shouldBe false
            }
        }

        "ValidateFunction" {
            Expression("sin(x)").apply {
                isValid() shouldBe true
                isComplete() shouldBe false
            }
        }

        "ValidateFunctionPositive" {
            Expression("+sin(x)").apply {
                isValid() shouldBe true
                isComplete() shouldBe false
            }
        }

        "ValidateFunctionNegative" {
            Expression("-sin(x)").apply {
                isValid() shouldBe true
                isComplete() shouldBe false
            }
        }

        "ValidateFunctionAndOperator" {
            Expression("sin(x + 1 + 2)").apply {
                isValid() shouldBe true
                isComplete() shouldBe false
            }
        }

        // Dummy function with 2 arguments.
        val beta = Function2("beta") { a: Int, b: Int -> b - a }

        "ValidateFunctionWithTwoArguments" {
            Expression("beta(x, y)", beta).apply {
                isValid() shouldBe true
                isComplete() shouldBe false
            }
        }

        "ValidateFunctionWithTwoArgumentsAndOperator" {
            Expression("beta(x, y + 1)", beta).apply {
                isValid() shouldBe true
                isComplete() shouldBe false
            }
        }


        // Dummy function with 3 arguments.
        val gamma = Function3("gamma") { a: Int, b: Int, c: Int -> a * b / c }

        "ValidateFunctionWithThreeArguments" {
            Expression("gamma(x, y, z)", gamma).apply {
                isValid() shouldBe true
                isComplete() shouldBe false
            }
        }

        "ValidateFunctionWithThreeArgumentsAndOperator" {
            Expression("gamma(x, y, z + 1)", gamma).apply {
                isValid() shouldBe true
                isComplete() shouldBe false
            }
        }

        "ValidateFunctionWithTwoAndThreeArguments" {
            Expression("gamma(x, beta(y, h), z)", gamma, beta, "h").apply {
                isValid() shouldBe true
                isComplete() shouldBe false
            }
        }

        "ValidateFunctionWithTwoAndThreeArgumentsAndOperator" {
            Expression("gamma(x, beta(y, h), z + 1)", gamma, beta, "h").apply {
                isValid() shouldBe true
                isComplete() shouldBe false
            }
        }

        "ValidateFunctionWithTwoAndThreeArgumentsAndMultipleOperator" {
            Expression("gamma(x * 2 / 4, beta(y, h + 1 + 2), z + 1 + 2 + 3 + 4)", gamma, beta, "h").apply {
                isValid() shouldBe true
                isComplete() shouldBe false
            }
        }


        // Dummy function with 7 arguments.
        val eta = Function7("eta") { a: Int, b: Int, c: Int, d: Int, e: Int, f: Int, g: Int ->
            a + b + c + d + e + f + g
        }

        "ValidateFunctionWithSevenArguments" {
            Expression("eta(1, 2, 3, 4, 5, 6, 7)", eta).isValidAndComplete() shouldBe true
        }

        "Validate function with 7 Arguments and operator" {
            Expression("eta(1, 2, 3, 4, 5, 6, 7) * 2 * 3 * 4", eta).isValidAndComplete() shouldBe true
        }

        // invalid scenarios

        "ValidateInvalidFunction" {
            Expression("sin()").isValid() shouldBe false
        }

        "ValidateInvalidOperand" {
            Expression("1 + ").isValid() shouldBe false
        }

        "ValidateInvalidFunctionWithTooFewArguments" {
            Expression("beta(1)", beta).isValid() shouldBe false
        }

        "ValidateInvalidFunctionWithTooFewArgumentsAndOperands" {
            Expression("beta(1 + )", beta).isValid() shouldBe false
        }

        "ValidateInvalidFunctionWithManyArguments" {
            Expression("beta(1, 2, 3)", beta).isValid() shouldBe false
        }

        "ValidateInvalidOperator" {
            Expression("+").isValid() shouldBe false
        }

        "NoArgFunctionValidation" {
            val now = Function0("now") { Date().time }
            Expression("14*now()", now).isValid() shouldBe true
            Expression("now()", now).isValid() shouldBe true
            Expression("sin(now())", now).isValid() shouldBe true
            Expression("sin(now()) % 14", now).isValid() shouldBe true
        }
    }
}