package exp4k

import exp4k.Operators.addition
import exp4k.Operators.factorial
import exp4k.Operators.multiplication
import exp4k.Operators.power
import exp4k.Operators.subtraction
import exp4k.Operators.unaryMinus
import exp4k.Operators.unaryPlus
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.math.ln

class TokenizerTest : StringSpec() {

    val Tokenizer.nextValue: Number
        get() = (nextToken as Token.Number).value

    fun Tokenizer.doesMatch(vararg args: Any) {
        for (arg in args)
            when (arg) {
                is Int -> nextValue shouldBeInt arg
                is Double -> nextValue shouldBeDouble arg
                is Operator -> nextToken!! shouldBeOperator arg
                is String -> when (val token = nextToken!!) {
                    is Token.Function -> {
                        val func = functions[arg]!!
                        token.function.name shouldBe func.name
                        token.function.arity shouldBe func.arity
                    }
                    is Token.Variable -> token.name shouldBe arg
                }
                is Function -> {
                    val func = (nextToken!! as Token.Function).function
                    func.name shouldBe arg.name
                    func.arity shouldBe arg.arity
                }
                else -> nextToken shouldBe arg
            }
        hasNext() shouldBe false
    }

    init {
        "Tokenization1" {
            Tokenizer("1.222331").doesMatch(1.222331)
        }

        "Tokenization2" {
            Tokenizer(".222331").doesMatch(.222331)
        }

        "Tokenization3" {
            Tokenizer("3e2").doesMatch(300.0)
        }

        "Tokenization4" {
            Tokenizer("3+1").doesMatch(3, addition, 1)
        }

        "Tokenization5" {
            Tokenizer("+3").doesMatch(unaryPlus, 3)
        }

        "Tokenization6" {
            Tokenizer("-3").doesMatch(unaryMinus, 3)
        }

        "Tokenization7" {
            Tokenizer("---++-3").doesMatch(
                unaryMinus, unaryMinus, unaryMinus, unaryPlus, unaryPlus, unaryMinus, 3
            )
        }

        "Tokenization8" {
            Tokenizer("---++-3.004").doesMatch(
                unaryMinus, unaryMinus, unaryMinus, unaryPlus, unaryPlus, unaryMinus, 3.004
            )
        }

        "Tokenization9" {
            Tokenizer("3+-1").doesMatch(3, addition, unaryMinus, 1)
        }

        "Tokenization10" {
            Tokenizer("3+-1-.32++2").doesMatch(
                3, addition, unaryMinus, 1, subtraction, 0.32, addition, unaryPlus, 2
            )
        }

        "Tokenization11" {
            Tokenizer("2+").doesMatch(2, addition)
        }

        "Tokenization12" {
            Tokenizer("ln(1)").doesMatch("ln", Token.OpenParentheses, 1, Token.CloseParentheses)
        }

        "Tokenization13" {
            Tokenizer("x").doesMatch("x")
        }

        "Tokenization14" {
            Tokenizer("2*x-ln(3)").doesMatch(
                2, multiplication, "x", subtraction, "ln", Token.OpenParentheses, 3, Token.CloseParentheses
            )
        }

        "Tokenization15" {
            Tokenizer("2*xlog+ln(3)", variableNames = setOf("xlog")).doesMatch(
                2, multiplication, "xlog", addition, "ln", Token.OpenParentheses, 3, Token.CloseParentheses
            )
        }

        "Tokenization16" {
            Tokenizer("2*x+-ln(3)").doesMatch(
                2, multiplication, "x", addition, unaryMinus, "ln", Token.OpenParentheses, 3, Token.CloseParentheses
            )
        }

        "Tokenization17" {
            Tokenizer("2 * x + -ln(3)").doesMatch(
                2, multiplication, "x", addition, unaryMinus, "ln", Token.OpenParentheses, 3, Token.CloseParentheses
            )
        }

        "Tokenization18" {
            val log2 = Function1<Double, Double>("log2") { a -> ln(a) / ln(2.0) }
            Tokenizer("log2(4)", userFunctions = mapOf("log2" to log2)).doesMatch(
                log2, Token.OpenParentheses, 4, Token.CloseParentheses
            )
        }

        "Tokenization19" {
            val avg = Function2<Int, Int, Int>("avg") { a, b -> (a + b) / 2 }
            Tokenizer("avg(1,2)", userFunctions = mapOf("avg" to avg)).doesMatch(
                avg, Token.OpenParentheses, 1, Token.ArgumentSeparator, 2, Token.CloseParentheses
            )
        }

        "Tokenization20" {
            Tokenizer("2!").doesMatch(2, factorial)
        }

        "Tokenization21" {
            Tokenizer("ln(x) - y * (sqrt(x^cos(y)))").doesMatch(
                "ln", Token.OpenParentheses, "x", Token.CloseParentheses, subtraction, "y", multiplication,
                Token.OpenParentheses, "sqrt", Token.OpenParentheses, "x", power, "cos", Token.OpenParentheses, "y",
                Token.CloseParentheses, Token.CloseParentheses, Token.CloseParentheses
            )
        }

        "Tokenization22" {
            Tokenizer("--2 * (-14)").doesMatch(
                unaryMinus, unaryMinus, 2, multiplication, Token.OpenParentheses, unaryMinus, 14, Token.CloseParentheses
            )
        }

        "Tokenization23" {
            Tokenizer("sincos(x)").doesMatch(
                "sin", "cos", Token.OpenParentheses, "x", Token.CloseParentheses
            )
        }
    }
}