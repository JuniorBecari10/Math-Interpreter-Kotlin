data class Token(
    val type: TokenType,
    val lexeme: String,
    val position: Int
)

enum class TokenType(val type: String) {
    Number("Number"),

    Plus("'+'"),
    Minus("'-'"),
    Star("'*'"),
    Slash("'/'"),

    LParen("'('"),
    RParen("')'")
}
