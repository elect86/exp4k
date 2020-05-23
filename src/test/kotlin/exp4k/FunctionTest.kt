package exp4k

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FunctionTest : StringSpec() {

    init {
        "FunctionNameEmpty" {
            shouldThrow<IllegalArgumentException> {
                Function0("") { 0 }
            }
        }

        "FunctionNameZeroArgs" {
            val f = Function0("foo") { 0 }
            f() shouldBeInt 0
        }

        "IllegalFunctionName1" {
            shouldThrow<IllegalArgumentException> {
                Function0("1foo") { 0 }
            }
        }

        "IllegalFunctionName2" {
            shouldThrow<IllegalArgumentException> {
                Function0("_&oo") { 0 }
            }
        }

        "IllegalFunctionName3" {
            shouldThrow<IllegalArgumentException> {
                Function0("o+o") { 0 }
            }
        }

        "CheckFunctionNames" {
            Function.isValidName("log") shouldBe true
            Function.isValidName("sin") shouldBe true
            Function.isValidName("abz") shouldBe true
            Function.isValidName("alongfunctionnamecanhappen") shouldBe true
            Function.isValidName("_log") shouldBe true
            Function.isValidName("__blah") shouldBe true
            Function.isValidName("foox") shouldBe true
            Function.isValidName("aZ") shouldBe true
            Function.isValidName("Za") shouldBe true
            Function.isValidName("ZZaa") shouldBe true
            Function.isValidName("_") shouldBe true
            Function.isValidName("log2") shouldBe true
            Function.isValidName("lo32g2") shouldBe true
            Function.isValidName("_o45g2") shouldBe true

            Function.isValidName("&") shouldBe false
            Function.isValidName("_+log") shouldBe false
            Function.isValidName("_k&l") shouldBe false
            Function.isValidName("k&l") shouldBe false
            Function.isValidName("+log") shouldBe false
            Function.isValidName("fo-o") shouldBe false
            Function.isValidName("log+") shouldBe false
            Function.isValidName("perc%") shouldBe false
            Function.isValidName("del\$a") shouldBe false
        }
    }
}