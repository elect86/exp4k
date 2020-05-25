package exp4k

sealed class Token {
    object ArgumentSeparator : Token()
    object CloseParentheses : Token()
    class Function(val function: exp4k.Function) : Token()
    class Number(val value: kotlin.Number) : Token() {
        constructor(expression: CharArray, offset: Int, len: Int) : this(String(expression, offset, len).toNumber)
    }

    object OpenParentheses : Token()
    class Operator(val operator: exp4k.Operator) : Token() {
        constructor(symbol: Char) : this(Operators[symbol]!!)
    }

    class Variable(val name: String) : Token()
}

class Tokenizer(
    expression: String,
    val userFunctions: Map<String, Function> = emptyMap(),
    val userOperators: Map<String, Operator> = emptyMap(),
    val variableNames: Set<String> = emptySet(),
    val implicitMultiplication: Boolean = true
) {
    private val exp = expression.trim().toCharArray()
    private var pos = 0
    private var lastToken: Token? = null

    operator fun hasNext(): Boolean = exp.size > pos

    val nextToken: Token?
        get() {
            var ch = exp[pos]
            while (ch.isWhitespace()) {
                ch = exp[++pos]
            }
            return when {
                ch.isDigit() || ch == '.' -> when {
                    multValid -> insertMultiplication()
                    else -> ch.parseNumberToken()
                }
                ch.isArgumentSeparator -> parseArgumentSeparatorToken()
                ch.isOpenParentheses -> when {
                    multValid -> insertMultiplication()
                    else -> parseParentheses(true)
                }
                ch.isCloseParentheses -> parseParentheses(false)
                ch in Operator.allowed -> parseOperatorToken(ch)
                ch.isLetter() || ch == '_' -> when { // parse the name which can be a setVariable or a function
                    multValid -> insertMultiplication()
                    else -> parseFunctionOrVariable()
                }
                else -> throw IllegalArgumentException("Unable to parse char '$ch' (Code:${ch.toInt()}) at [$pos]")
            }
        }

    val multValid: Boolean
        get() = implicitMultiplication && (lastToken is Token.Variable || lastToken is Token.CloseParentheses || lastToken is Token.Number)

    // insert an implicit multiplication token
    fun insertMultiplication(): Token = Token.Operator('*').also { lastToken = it }

    private fun parseArgumentSeparatorToken(): Token? {
        pos++
        lastToken = Token.ArgumentSeparator
        return lastToken
    }

    private val Char.isArgumentSeparator: Boolean get() = equals(',')

    private fun parseParentheses(open: Boolean): Token? {
        lastToken = if (open) Token.OpenParentheses else Token.CloseParentheses
        pos++
        return lastToken
    }

    private val Char.isOpenParentheses: Boolean
        get() = equals('(') || equals('{') || equals('[')

    private val Char.isCloseParentheses: Boolean
        get() = equals(')') || equals('}') || equals(']')

    private fun parseFunctionOrVariable(): Token {
        val offset = pos
        var testPos: Int
        var lastValidLen = 1
        var lastValidToken: Token? = null
        var len = 1
        if (isEndOfExpression(offset))
            pos++
        testPos = offset + len - 1
        while (!isEndOfExpression(testPos) && exp[testPos].isVariableOrFunction) {
            val name = String(exp, offset, len)
            if (name in variableNames || name in defaultVariableNames) {
                lastValidLen = len
                lastValidToken = Token.Variable(name)
            } else
                getFunction(name)?.let {
                    lastValidLen = len
                    lastValidToken = Token.Function(it)
                }
            len++
            testPos = offset + len - 1
        }
        if (lastValidToken == null)
            throw UnknownFunctionOrVariableException(String(exp), pos, len)
        pos += lastValidLen
        lastToken = lastValidToken
        return lastToken!!
    }

    private fun getFunction(name: String): Function? = userFunctions[name] ?: functions[name]

    private fun parseOperatorToken(firstChar: Char): Token? {
        val offset = pos
        var len = 1
        val symbol = StringBuilder()
        var lastValid: Operator? = null
        symbol += firstChar
        while (!isEndOfExpression(offset + len) && exp[offset + len] in Operator.allowed)
            symbol += exp[offset + len++]
        while (symbol.isNotEmpty()) {
            val op = getOperator(symbol.toString())
            if (op == null) {
                symbol.setLength(symbol.length - 1)
            } else {
                lastValid = op
                break
            }
        }
        pos += symbol.length
        require(lastValid != null) { "Operator is unknown for token." }
        lastToken = Token.Operator(lastValid)
        return lastToken
    }

    private fun getOperator(symbol: String): Operator? =
        userOperators[symbol] ?: when (symbol.length) {
            1 -> Operators.getBuiltinOperator(symbol[0], getArgCount())
            else -> null
        }

    fun getArgCount(): Int = when (lastToken) {
        null -> 1
        is Token.OpenParentheses, is Token.ArgumentSeparator -> 1
        else -> {
            val lastOp = (lastToken as? Token.Operator)?.operator
            when {
                lastOp != null && (lastOp.arity == 2 || lastOp.arity == 1 && !lastOp.isLeftAssociative) -> 1
                else -> 2
            }
        }
    }

    private fun Char.parseNumberToken(): Token? {
        val offset = pos
        var len = 1
        pos++
        if (isEndOfExpression(offset + len)) {
            lastToken = Token.Number(toString().toNumber)
            return lastToken
        }
        while (!isEndOfExpression(offset + len) &&
            isNumeric(exp[offset + len], exp[offset + len - 1] == 'e' || exp[offset + len - 1] == 'E')
        ) {
            len++
            pos++
        }
        // check if the e is at the end
        if (exp[offset + len - 1] == 'e' || exp[offset + len - 1] == 'E') {
            // since the e is at the end it's not part of the number and a rollback is necessary
            len--
            pos--
        }
        lastToken = Token.Number(exp, offset, len)
        return lastToken
    }

    private fun isEndOfExpression(offset: Int): Boolean = exp.size <= offset
    val Char.isVariableOrFunction: Boolean
        get() = isLetterOrDigit() || equals('_') || equals('.')

    private fun isNumeric(ch: Char, lastCharE: Boolean): Boolean =
        ch.isDigit() || ch == '.' || ch == 'e' || ch == 'E' || lastCharE && (ch == '-' || ch == '+')
}


/** This exception is being thrown whenever [Tokenizer] finds unknown function or variable.
 *  @author Bartosz Firyn (sarxos) */
class UnknownFunctionOrVariableException(expression: String, position: Int, length: Int) :
    IllegalArgumentException() {
    val token = expression.substring(position, length)
    override val message = "Unknown function or variable '$token' at pos $position in expression '$expression'"
}

