package exp4k

operator fun <T> StringBuilder.plusAssign(any: T) {
    append(any)
}

val String.toNumber: Number
    get() = when (val long = toLongOrNull()) {
        null -> toDouble()
        else -> when {
            long <= Integer.MAX_VALUE && long >= Integer.MIN_VALUE -> long.toInt()
            else -> long
        }
    }