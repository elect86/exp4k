package exp4k

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.math.cos
import kotlin.math.ln

class Examples : StringSpec() {

    init {
        "Evaluating an expression" {
            val e = Expression("3 * sin(y) - 2 / (x - 2)")
//            .variables("x", "y")
//                .build()
//                .setVariable("x", 2.3)
//                .setVariable("y", 3.14)
            val result = e(x = 2.3, y = 3.14)
            result shouldBe -6.66188870791721
        }

//        Evaluating an expression asynchronously -> coroutines

        "Implicit multiplication" {
            val result = Expression("2cos(xy)")(x = 0.5, y = 0.25)
            2.0 * cos(0.5 * 0.25) shouldBe result
        }

        "Numerical constants" {
            val expr = Expression("pi+π+e+φ")
            val expected = 2 * Math.PI + Math.E + 1.61803398874
            expr() shouldBe expected
        }

        "Scientific notation" {
            val expr = Expression("7.2973525698e-3")
            expr() shouldBe 0.0072973525698
        }

        "Custom functions" {
            val logb = Function2("logb") { a: Double, b: Double ->
                ln(a) / ln(b)
            }
            val exp = Expression("logb(8, 2)", logb)
            exp() shouldBe 3.0
        }

        "Custom functions 2" {
            val avg = Function4("avg") { a: Double, b: Double, c: Double, d: Double ->
                (a + b + c + d) / 4
            }
            val exp = Expression("avg(1,2,3,4)", avg)
            exp() shouldBe 2.5
        }

        "Custom operators" {
            val factorial = Operator("&", 1, true, Precedence.power + 1, postfix = true)
            { a: Int ->
                require(a >= 0) { "The operand of the factorial can not be less than zero" }
                var result = 1
                for (i in 1..a)
                    result *= i
                result
            }

            val exp = Expression("3&", factorial)
            exp() shouldBe 6
        }

        "Custom operators 2" {

            val gteq = Operator(">=", 2, true, Precedence.addition - 1) { a: Int, b: Int ->
                if(a >= b) 1 else 0
            }

            var e = Expression("1>=2", gteq)
            e() shouldBe 0
            e = Expression("2>=1", gteq)
            e() shouldBe 1
        }

        "Division by zero in operations and functions" {

            val reciprocal = Operator("$", 1, true, Precedence.division) { a: Double ->
                if (a == 0.0)
                    throw ArithmeticException("Division by zero!")
                1.0 / a
            }
            val e = Expression("0$", reciprocal)
            shouldThrow<ArithmeticException> { e() } // <- this call will throw an ArithmeticException
        }

        "Validation of expression" {
            val e = Expression("x")

            val errors = arrayListOf<String>()
            e.isValid(errors) shouldBe true
            errors.isEmpty() shouldBe true
            e.isComplete(errors) shouldBe false
            errors.size shouldBe 1
            errors[0] shouldBe "The variable 'x' has not been set"

            e["x"] = 1
            e.isValid() shouldBe true
            e.isComplete() shouldBe true
        }

        "Validation of expression 2" {
            val e = Expression("a", "a")

            e.isValid() shouldBe true
            e.isComplete() shouldBe false
        }
    }
}