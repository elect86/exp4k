package exp4k

import java.util.*

class Expression
internal constructor(
    private val tokens: Array<Token>,
    private val variables: MutableMap<String, Number> = mutableMapOf(),
    private val userFunctionNames: Set<String> = emptySet()
) {

    /**
     * Creates a new expression that is a copy of the existing one.
     *
     * @param exp the expression to copy
     */
    constructor(exp: Expression) : this(
        exp.tokens.copyOf(),
        exp.variables.toMutableMap(),
        HashSet(exp.userFunctionNames)
    )

    constructor(exp: String, vararg parameters: Any) :
            this(ShuntingYard.convertToRPN(exp, *parameters))

    init {
        addConstants()
    }

    fun addConstants() {
        variables["pi"] = Math.PI
        variables["π"] = Math.PI
        variables["φ"] = 1.61803398874
        variables["e"] = Math.E
    }

    fun setVariable(name: String, value: Double): Expression {
        checkVariableName(name)
        variables[name] = value
        return this
    }

    private fun checkVariableName(name: String) {
        require(name !in userFunctionNames && Functions[name] == null) {
            "The variable name '$name' is invalid. Since there exists a function with the same name"
        }
    }

    fun setVariables(variables: Map<String, Double>): Expression {
        for ((key, value) in variables)
            setVariable(key, value)
        return this
    }

    fun clearVariables() {
        x = null
        y = null
        z = null
        w = null
        variables.clear()
        addConstants()
    }

    val variableNames: List<String>
        get() = tokens.filterIsInstance<Token.Variable>().map { it.name }

    fun isValidAndComplete(errors: ArrayList<String>? = null): Boolean {
        val valid = isValid(errors)
        return isComplete(errors) && valid // to avoid code skipping in case valid is false
    }

    fun isValid(errors: ArrayList<String>? = null): Boolean {

        /* Check if the number of operands, functions and operators match.
           The idea is to increment a counter for operands and decrease it for operators.
           When a function occurs the number of available arguments has to be greater
           than or equals to the function's expected number of arguments.
           The count has to be larger than 1 at all times and exactly 1 after all tokens
           have been processed */
        var count = 0
        for (tok in tokens) {
            when (tok) {
                is Token.Number, is Token.Variable -> count++
                is Token.Function -> {
                    val func = tok.function
                    val argsNum = func.arity
                    if (argsNum > count)
                        errors?.add("Not enough arguments for '${func.name}'")
                    if (argsNum > 1)
                        count -= argsNum - 1
                    else if (argsNum == 0)
                        count++ // see https://github.com/fasseg/exp4j/issues/59
                }
                is Token.Operator -> if (tok.operator.arity == 2) count--
            }
            if (count < 1) {
                errors?.add("Too many operators")
                return false
            }
        }
        return when {
            count > 1 -> {
                errors?.add("Too many operands")
                false
            }
            else -> true
        }
    }

    /** check that all vars have a value set */
    fun isComplete(errors: ArrayList<String>? = null): Boolean {
        tokens.filterIsInstance<Token.Variable>().forEach {
            val name = it.name
            if (name !in variables || (name == "x" && x == null) || (name == "y" && y == null)
                || (name == "z" && z == null) || (name == "w" && w == null)
            ) {
                errors?.add("The variable '$name' has not been set")
                return false
            }
        }
        return true
    }

    //fun evaluateAsync(executor: ExecutorService): Future<Double> = executor.submit<Double> { evaluate() }

    operator fun invoke(x: Number? = null, y: Number? = null, z: Number? = null, w: Number? = null): Number {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
        val stack = Stack<Number>()
        for (t in tokens)
            when (t) {
                is Token.Number -> stack += t.value
                is Token.Variable -> {
                    val name: String = t.name
                    val value = when (name) {
                        "x" -> x
                        "y" -> y
                        "z" -> z
                        "w" -> w
                        else -> variables[name]
                    } ?: throw IllegalArgumentException("No value has been set for the variable '$name'.")
                    stack += value
                }
                is Token.Operator -> {
                    require(stack.size >= t.operator.arity) { "Invalid number of operands available for '${t.operator.symbol}' operator" }
                    if (t.operator.arity == 2) {
                        // pop the operands and push the result of the operation
                        val func = t.operator.func as (Number, Number) -> Number
                        val b = stack.pop()
                        stack += func(stack.pop(), b)
                    } else if (t.operator.arity == 1 || (t.operator.postfix && t.operator.arity == 0)) {
                        // pop the operand and push the result of the operation
                        val func = t.operator.func as (Number) -> Number
                        stack += func(stack.pop())
                    }
                }
                is Token.Function -> {
                    val arity = t.function.arity
                    require(stack.size >= arity) { "Invalid arity number for '${t.function.name}' function" }
                    // collect the arguments from the stack
                    stack += when (val f = t.function) {
                        is Function0<*> -> f()
                        is Function1<*, *> -> (f as Function1<Number, Number>)(stack.pop())
                        is Function2<*, *, *> -> (f as Function2<Number, Number, Number>)(stack.pop(), stack.pop())
                        is Function3<*, *, *, *> -> (f as Function3<Number, Number, Number, Number>)(stack.pop(), stack.pop(), stack.pop())
                        is Function4<*, *, *, *, *> -> (f as Function4<Number, Number, Number, Number, Number>)(stack.pop(), stack.pop(), stack.pop(), stack.pop())
                        is Function5<*, *, *, *, *, *> -> (f as Function5<Number, Number, Number, Number, Number, Number>)(stack.pop(), stack.pop(), stack.pop(), stack.pop(), stack.pop())
                        is Function6<*, *, *, *, *, *, *> -> (f as Function6<Number, Number, Number, Number, Number, Number, Number>)(stack.pop(), stack.pop(), stack.pop(), stack.pop(), stack.pop(), stack.pop())
                        is Function7<*, *, *, *, *, *, *, *> -> (f as Function7<Number, Number, Number, Number, Number, Number, Number, Number>)(stack.pop(), stack.pop(), stack.pop(), stack.pop(), stack.pop(), stack.pop(), stack.pop())
                        is Function8<*, *, *, *, *, *, *, *, *> -> (f as Function8<Number, Number, Number, Number, Number, Number, Number, Number, Number>)(stack.pop(), stack.pop(), stack.pop(), stack.pop(), stack.pop(), stack.pop(), stack.pop(), stack.pop())
                        is Function9<*, *, *, *, *, *, *, *, *, *> -> (f as Function9<Number, Number, Number, Number, Number, Number, Number, Number, Number, Number>)(stack.pop(), stack.pop(), stack.pop(), stack.pop(), stack.pop(), stack.pop(), stack.pop(), stack.pop(), stack.pop())
                        else -> error("invalid")
                    }
                }
            }
        require(stack.size <= 1) { "Invalid number of items on the output queue. Might be caused by an invalid number of arguments for a function." }
        return stack.pop()
    }

    // builtin variables
    var x: Number? = null
    var y: Number? = null
    var z: Number? = null
    var w: Number? = null
}