package exp4k

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.math.tan

class ExpressionTest : StringSpec() {

    init {
        "Expression, 1" {
            val tokens = arrayOf(
                Token.Number(3.0),
                Token.Number(2),
                Token.Operator(Operators.addition)
            )
            Expression(tokens)() shouldBeDouble 5.0
        }

        "Expression, 2" {
            val tokens = arrayOf(
                Token.Number(1.0),
                Token.Function(functions["ln"]!!)
            )
            Expression(tokens)() shouldBeDouble 0.0
        }

        "Get variable names, 1" {
            val tokens = arrayOf(
                Token.Variable("a"),
                Token.Variable("b"),
                Token.Operator(Operators.addition)
            )
            Expression(tokens).variableNames.size shouldBe 2
        }

        "Factorial" {
            Expression("2!+3!")() shouldBeInt 8
            Expression("2.0!+3.0!")() shouldBeDouble 8.0
            Expression("3!-2!")() shouldBeInt 4
            Expression("3!")() shouldBeInt 6
            Expression("3!!")() shouldBeInt 720
            Expression("4 + 3!")() shouldBeInt 10
            Expression("3! * 2")() shouldBeInt 12
            Expression("3!").apply {
                isValidAndComplete() shouldBe true
                invoke() shouldBeInt 6
            }
            Expression("3!!").apply {
                isValidAndComplete() shouldBe true
                invoke() shouldBeInt 720
            }
            Expression("4 + 3!").apply {
                isValidAndComplete() shouldBe true
                invoke() shouldBeInt 10
            }
            Expression("3! * 2").apply {
                isValidAndComplete() shouldBe true
                invoke() shouldBeInt 12
            }
            Expression("2 * 3!").apply {
                isValidAndComplete() shouldBe true
                invoke() shouldBeInt 12
            }
            Expression("4 + (3!)").apply {
                isValidAndComplete() shouldBe true
                invoke() shouldBeInt 10
            }
            Expression("4 + 3! + 2 * 6").apply {
                isValidAndComplete() shouldBe true
                invoke() shouldBeInt 22
            }
        }

        "Cotangent1" {
            Expression("cot(1.0)")() shouldBeDouble 1 / tan(1.0)
        }

        "InvalidCotangent1" {
            shouldThrow<ArithmeticException> { Expression("cot(0.0)")() }
                .message shouldBe "Division by zero in cotangent!"
        }

        "OperatorFactorial2" {
            shouldThrow<IllegalArgumentException> { Expression("$3")() }
        }

        "OperatorFactorial3" {
            shouldThrow<IllegalArgumentException> { Expression("!3")() }
        }

        "ClearVariables" {
            val exp = Expression("x + y")
            exp(x = 1, y = 2) shouldBeInt 3
            exp.clearVariables()

            shouldThrow<IllegalArgumentException> { exp() }
        }

        // If Expression should be threads safe this test must pass
//        fun evaluateFamily() {
//            val e: Expression = ExpressionBuilder("sin(x)")
//                .variable("x")
//                .build()
//            val executor: Executor = Executors.newFixedThreadPool(100)
//            for (i in 0..99999) {
//                executor.execute {
//                    val x = Math.random()
//                    e.setVariable("x", x)
//                    try {
//                        Thread.sleep(100)
//                    } catch (e1: InterruptedException) {
//                        e1.printStackTrace()
//                    }
//                    assertEquals(Math.sin(x), e.evaluate(), 0f)
//                }
//            }
//        }
    }
}