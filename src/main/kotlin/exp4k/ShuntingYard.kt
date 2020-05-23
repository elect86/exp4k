package exp4k

import java.util.*


/**
 * Shunting yard implementation to convert infix to reverse polish notation
 */
object ShuntingYard {
    /**
     * Convert a Set of tokens from infix to reverse polish notation
     *
     * @param expression             the expression to convert
     * @param userFunctions          the custom functions used
     * @param userOperators          the custom operators used
     * @param variableNames          the variable names used in the expression
     * @param implicitMultiplication set to false to turn off implicit multiplication
     * @return a [net.objecthunter.exp4j.tokenizer.Token] array containing the result
     */
    fun convertToRPN(
        expression: String,
        vararg parameters: Any,
        implicitMultiplication: Boolean = true
    ): Array<Token> {
        val stack = Stack<Token>()
        val output = ArrayList<Token>()
        val functions = mutableMapOf<String, Function>()
        val operators = mutableMapOf<String, Operator>()
        val variables = hashSetOf<String>()
        for(p in parameters) {
            when(p) {
                is Function -> functions[p.name] = p
                is String -> variables += p
                is Operator -> operators[p.symbol] = p
            }
        }
        val tokenizer = Tokenizer(expression, functions, operators, variables, implicitMultiplication)
        while (tokenizer.hasNext())
            when (val token = tokenizer.nextToken!!) {
                is Token.Number, is Token.Variable -> output += token
                is Token.Function -> stack += token
                is Token.ArgumentSeparator -> {
                    while (stack.isNotEmpty() && stack.peek() !is Token.OpenParentheses)
                        output += stack.pop()
                    if (stack.isEmpty() || stack.peek() !is Token.OpenParentheses)
                        throw IllegalArgumentException("Misplaced function separator ',' or mismatched parentheses");
//                    require(stack.isNotEmpty() || stack.peek() is Token.OpenParentheses) { "Misplaced function separator ',' or mismatched parentheses" }
                }
                is Token.Operator -> {
                    if (token.operator.arity == 0 && token.operator.postfix) // !3
                        require(output.isNotEmpty()) { "empty output, postfix operator with arity 0 misplaced" }
                    while (stack.isNotEmpty() && stack.peek() is Token.Operator) {
                        val other = stack.peek() as Token.Operator
                        if (token.operator.arity == 1 && other.operator.arity == 2)
                            break
                        else if ((token.operator.isLeftAssociative && token.operator.precedence <= other.operator.precedence)
                            || token.operator.precedence < other.operator.precedence
                        )
                            output += stack.pop()
                        else break
                    }
                    stack += token
                }
                Token.OpenParentheses -> stack += token
                Token.CloseParentheses -> {
                    while (stack.peek() !is Token.OpenParentheses)
                        output += stack.pop()
                    stack.pop()
                    if (stack.isNotEmpty() && stack.peek() is Token.Function)
                        output += stack.pop()
                }
            }
        while (stack.isNotEmpty()) {
            val t = stack.pop()
            require(t !is Token.CloseParentheses && t !is Token.OpenParentheses) { "Mismatched parentheses detected. Please check the expression" }
            output += t
        }
        return Array(output.size) { output[it] }
    }
}
