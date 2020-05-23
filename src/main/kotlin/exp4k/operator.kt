package exp4k

import kotlin.math.pow

/** Class representing operators that can be used in an expression */
class Operator//<A : Number, R : Number>
/** Create a new operator for use in expressions */(
    /** Get the operator symbol */
    val symbol: String,
    /** Get the number of operands */
    val arity: Int,
    /** Check if the operator is left associative
     *  @return true os the operator is left associative, false otherwise     */
    val isLeftAssociative: Boolean,
    /** Check the precedence value for the operator */
    val precedence: Int,
    val postfix: Boolean = true,
    val func: Any
) {

    constructor(
        symbol: String,
        arity: Int,
        isLeftAssociative: Boolean,
        precedence: Precedence,
        postfix: Boolean = true,
        func: Any
    ) : this(symbol, arity, isLeftAssociative, precedence.i, postfix, func)

//    operator fun invoke() = function

    companion object {
        val allowed = charArrayOf(
            '+', '-', '*', '/', '%', '^', '!', '#', '§', '$', '&', ';',
            ':', '~', '<', '>', '|', '=', '÷', '√', '∛', '⌈', '⌊'
        )
    }
}

/*

http://mathforum.org/library/drmath/view/69058.html

1. Expressions within parentheses.  Expressions within nested
   parentheses are evaluated from inner to outer.
2. Prefix functions (such as sin, ln, ...).
3. Postfix functions (such as ! (factorial)).
4. Power (^) and square root.
5. Negation (-), multiplication, and division.
6. Addition and subtraction.
 */
enum class Precedence(val i: Int) {

    factorial(4),

    power(3),
    sqrt(3),

    unaryMinus(2),
    unaryPlus(2),

    multiplication(1),
    division(1),
    modulo(1),

    addition(0),
    subtraction(0);

    operator fun plus(int: Int) = i + int
}

object Operators {
    operator fun get(symbol: Char): Operator? = getBuiltinOperator(symbol, arity = 2)
    fun getBuiltinOperator(symbol: Char, arity: Int): Operator? = when (symbol) {
        '+' -> when {
            arity != 1 -> addition
            else -> unaryPlus
        }
        '-' -> when {
            arity != 1 -> subtraction
            else -> unaryMinus
        }
        '*' -> multiplication
        '÷', '/' -> division
        '^' -> power
        '%' -> modulo
        '!' -> factorial
        else -> null
    }

    val addition = Operator("+", 2, true, Precedence.addition) { a: Number, b: Number ->
        when {
            a is Float || a is Double || b is Float || b is Double -> a.toDouble() + b.toDouble()
            a is Long || b is Long -> a.toLong() + b.toLong()
            else -> a.toInt() + b.toInt()
        }
    }
    val unaryPlus = Operator("+", 1, false, Precedence.unaryPlus) { a: Number -> a }
    val subtraction = Operator("-", 2, true, Precedence.addition) { a: Number, b: Number ->
        when {
            a is Float || a is Double || b is Float || b is Double -> a.toDouble() - b.toDouble()
            a is Long || b is Long -> a.toLong() - b.toLong()
            else -> a.toInt() - b.toInt()
        }
    }
    val unaryMinus = Operator("-", 1, false, Precedence.unaryMinus) { a: Number ->
        when {
            a is Float || a is Double -> -a.toDouble()
            a is Long -> -a.toLong()
            else -> -a.toInt()
        }
    }
    val multiplication = Operator("*", 2, true, Precedence.multiplication) { a: Number, b: Number ->
        when {
            a is Float || a is Double || b is Float || b is Double -> a.toDouble() * b.toDouble()
            a is Long || b is Long -> a.toLong() * b.toLong()
            else -> a.toInt() * b.toInt()
        }
    }
    val division = Operator("/", 2, true, Precedence.division) { a: Number, b: Number ->
        when {
            a is Float || a is Double || b is Float || b is Double -> {
                val A = a.toDouble()
                val B = b.toDouble()
                if (B == 0.0) throw ArithmeticException("Division by zero!")
                else A / B
            }
            a is Long || b is Long -> {
                val A = a.toLong()
                val B = b.toLong()
                if (B == 0L) throw ArithmeticException("Division by zero!")
                else A / B
            }
            else -> {
                val A = a.toInt()
                val B = b.toInt()
                if (B == 0) throw ArithmeticException("Division by zero!")
                else A / B
            }
        }
    }
    val power = Operator("^", 2, false, Precedence.power) { a: Number, b: Number ->
        a.toDouble().pow(b.toDouble())
    }
    val modulo = Operator("%", 2, true, Precedence.modulo) { a: Number, b: Number ->
        when {
            a is Float || a is Double || b is Float || b is Double -> {
                val A = a.toDouble()
                val B = b.toDouble()
                if (B == 0.0) throw ArithmeticException("Division by zero!")
                else A % B
            }
            a is Long || b is Long -> {
                val A = a.toLong()
                val B = b.toLong()
                if (B == 0L) throw ArithmeticException("Division by zero!")
                else A % B
            }
            else -> {
                val A = a.toInt()
                val B = b.toInt()
                if (B == 0) throw ArithmeticException("Division by zero!")
                else A % B
            }
        }
    }
    val factorial = Operator("!", 0, true, Precedence.factorial) { a: Number ->
        when (a) {
            is Int -> {
                var result = 1
                for (i in 1..a)
                    result *= i
                result
            }
            is Double -> {
                var result = 1.0
                for (i in 1..a.toInt())
                    result *= i
                result
            }
            else -> error("invalid type")
        }
    }
}

val defaultVariableNames = arrayListOf("x", "y", "z", "w")