import kotlin.system.exitProcess

fun main() {
    println("Kotlin Expression Evaluator")
    println("Type 'exit!' to exit.")
    println()

    while (true) {
        val expr = input("> ")

        when {
            expr == "exit!" -> exitProcess(0)
            expr.isEmpty() || expr.isBlank() -> continue
        }

        try {
            val tokens = Lexer(expr).lex().getOrThrow()
            val ast = Parser(tokens).parse().getOrThrow()
            val res = Interpreter(ast).interpret().getOrThrow()

            println("< ${printNumber(res)}")
        } catch (e: Exception) {
            continue
        }
    }
}

fun printNumber(n: Double): String =
    if (n.toInt().toDouble() == n)
        n.toString().substringBefore('.')
    else
        n.toString()

fun input(prompt: String): String {
    print(prompt)
    return readln()
}

fun printError(position: Int, errorLength: Int, message: String): Nothing {
    print("  ${" ".repeat(position)}")
    println("${"^".repeat(errorLength)} $message")
    println()

    throw Exception()
}

fun internalError(message: String): Nothing {
    println("Internal error - $message")
    throw Exception()
}
