package exp4k

import kotlin.math.*


/** A class representing a Function which can be used in an expression */
abstract class Function constructor(val name: String) {
    init {
        require(isValidName(name)) { "The function name '$name' is invalid" }
    }

    abstract val arity: Int

    companion object {
        fun isValidName(name: String): Boolean {
            if (name.isEmpty())
                return false
            for (i in name.indices) {
                val c = name[i]
                if (c.isLetter() || c == '_')
                    continue
                else if (c.isDigit() && i > 0)
                    continue
                return false
            }
            return true
        }
    }
}

class Function0<R : Number>(name: String, val func: () -> R) : Function(name) {
    override val arity: Int
        get() = 0

    operator fun invoke(): R = func()
}

class Function1<A : Number, R : Number>(name: String, val func: (a: A) -> R) : Function(name) {
    override val arity: Int
        get() = 1

    operator fun invoke(a: A): R = func(a)
}

class Function2<A : Number, B : Number, R : Number>(name: String, val func: (a: A, b: B) -> R) : Function(name) {
    override val arity: Int
        get() = 2

    operator fun invoke(a: A, b: B): R = func(a, b)
}

class Function3<A : Number, B : Number, C : Number, R : Number>(name: String, val func: (a: A, b: B, c: C) -> R) :
    Function(name) {
    override val arity: Int
        get() = 3

    operator fun invoke(a: A, b: B, c: C): R = func(a, b, c)
}

class Function4<A : Number, B : Number, C : Number, D : Number, R : Number>(
    name: String,
    val func: (a: A, b: B, c: C, d: D) -> R
) : Function(name) {
    override val arity: Int
        get() = 4

    operator fun invoke(a: A, b: B, c: C, d: D): R = func(a, b, c, d)
}

class Function5<A : Number, B : Number, C : Number, D : Number, E : Number, R : Number>(
    name: String,
    val func: (a: A, b: B, c: C, d: D, e: E) -> R
) : Function(name) {
    override val arity: Int
        get() = 5

    operator fun invoke(a: A, b: B, c: C, d: D, e: E): R = func(a, b, c, d, e)
}

class Function6<A : Number, B : Number, C : Number, D : Number, E : Number, F : Number, R : Number>(
    name: String,
    val func: (a: A, b: B, c: C, d: D, e: E, f: F) -> R
) : Function(name) {
    override val arity: Int
        get() = 6

    operator fun invoke(a: A, b: B, c: C, d: D, e: E, f: F): R = func(a, b, c, d, e, f)
}

class Function7<A : Number, B : Number, C : Number, D : Number, E : Number, F : Number, G : Number, R : Number>(
    name: String,
    val func: (a: A, b: B, c: C, d: D, e: E, f: F, g: G) -> R
) : Function(name) {
    override val arity: Int
        get() = 7

    operator fun invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G): R = func(a, b, c, d, e, f, g)
}

class Function8<A : Number, B : Number, C : Number, D : Number, E : Number, F : Number, G : Number, H : Number, R : Number>(
    name: String,
    val func: (a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H) -> R
) : Function(name) {
    override val arity: Int
        get() = 8

    operator fun invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H): R = func(a, b, c, d, e, f, g, h)
}

class Function9<A : Number, B : Number, C : Number, D : Number, E : Number, F : Number, G : Number, H : Number, I : Number, R : Number>(
    name: String,
    val func: (a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I) -> R
) : Function(name) {
    override val arity: Int
        get() = 9

    operator fun invoke(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I): R = func(a, b, c, d, e, f, g, h, i)
}

/** Class representing the builtin functions available for use in expressions */
object Functions {

    val map = mutableMapOf(
        "sin" to Function1("sin") { a: Double -> sin(a) },
        "cos" to Function1("cos") { a: Double -> cos(a) },
        "tan" to Function1("tan") { a: Double -> tan(a) },
        "cot" to Function1("cot") { a: Double ->
            val tan = tan(a)
            if (tan == 0.0)
                throw ArithmeticException("Division by zero in cotangent!")
            1.0 / tan
        },
        "ln" to Function1("ln") { a: Double -> ln(a) },
        "log2" to Function1("log2") { a: Double -> log2(a) },
        "log10" to Function1("log10") { a: Double -> log10(a) },
        "ln1p" to Function1("log2") { a: Double -> ln1p(a) },
        "abs" to Function1("abs") { a: Double -> abs(a) },
        "acos" to Function1("acos") { a: Double -> acos(a) },
        "asin" to Function1("asin") { a: Double -> asin(a) },
        "atan" to Function1("atan") { a: Double -> atan(a) },
        "cbrt" to Function1("cbrt") { a: Double -> Math.cbrt(a) },
        "floor" to Function1("floor") { a: Double -> floor(a) },
        "sinh" to Function1("sinh") { a: Double -> sinh(a) },
        "sqrt" to Function1("sqrt") { a: Double -> sqrt(a) },
        "tanh" to Function1("tanh") { a: Double -> tanh(a) },
        "cosh" to Function1("cosh") { a: Double -> cosh(a) },
        "ceil" to Function1("ceil") { a: Double -> ceil(a) },
        "pow" to Function2("pow") { a: Double, b: Double -> a.pow(b) },
        "exp" to Function1("exp") { a: Double -> exp(a) },
        "expm1" to Function1("expm1") { a: Double -> expm1(a) },
        "sign" to Function1("sign") { a: Double -> sign(a) },
        "csc" to Function1("csc") { a: Double ->
            val sin = sin(a)
            if (sin == 0.0)
                throw ArithmeticException("Division by zero in cosecant!")
            1.0 / sin
        },
        "sec" to Function1("sec") { a: Double ->
            val cos = cos(a)
            if (cos == 0.0)
                throw ArithmeticException("Division by zero in secant!")
            1.0 / cos
        },
        "csch" to Function1("csch") { a: Double ->
            when (a) {
                0.0 -> 0.0 //this would throw an ArithmeticException later as sinh(0) = 0
                else -> 1.0 / sinh(a)
            }
        },
        "sech" to Function1("sech") { a: Double -> 1.0 / cosh(a) },
        "coth" to Function1("coth") { a: Double -> cosh(a) / sinh(a) },
        "logb" to Function2("logb") { a: Double, b: Double -> ln(b) / ln(a) },
        "toRadian" to Function1("toradian") { a: Double -> Math.toRadians(a) },
        "toDegree" to Function1("toDegree") { a: Double -> Math.toDegrees(a) }
    )

    operator fun get(index: String): Function? = map[index]
}