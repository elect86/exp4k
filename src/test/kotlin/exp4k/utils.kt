package exp4k

import io.kotest.matchers.shouldBe

//fun Token.shouldBeOperator(symbol: String, numArgs: Int, precedence: Precedence) {
//    this as Token.Operator
//    numArgs shouldBe operator.arity
//    symbol shouldBe operator.symbol
//    precedence shouldBe operator.precedence
//}

infix fun Number.shouldBeInt(int: Int) {
    assert(this is Int)
    shouldBe(int)
}

infix fun Number.shouldBeDouble(double: Double) {
    assert(this is Double)
    shouldBe(double)
}

infix fun Token.shouldBeOperator(operator: Operator) {
    (this as Token.Operator).operator.apply {
        arity shouldBe operator.arity
        symbol shouldBe operator.symbol
        precedence shouldBe operator.precedence
    }
}