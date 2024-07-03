class Lexer(private val chars: String) {
    private var start = 0
    private var current = 0

    private val tokens: MutableList<Token> = mutableListOf()

    fun lex(): Result<List<Token>> {
        var hadError = false

        while (true) {
            try {
                val token = this.token() ?: break
                this.tokens.add(token)
            }
            catch (e: Exception) {
                hadError = true
            }
        }

        return if (hadError)
            Result.failure(Exception())
        else
            Result.success(this.tokens)
    }

    private fun token(): Token? {
        while (this.current() == ' ')
            this.advance()

        start = current

        return when (this.advance()) {
            '+' -> newToken(TokenType.Plus)
            '-' -> newToken(TokenType.Minus)
            '*' -> newToken(TokenType.Star)
            '/' -> newToken(TokenType.Slash)

            '(' -> newToken(TokenType.LParen)
            ')' -> newToken(TokenType.RParen)

            else ->
                if (this.current(-1).isDigit())
                    this.number()
                else if (this.current(-1) == 0.toChar())
                    null
                else
                    printError(this.start, 1, "Unknown token: '${this.current(-1)}'")
        }
    }

    private fun number(): Token {
        while (this.current().isDigit())
            this.advance()

        if (this.current() == '.' && this.current(1).isDigit()) {
            do this.advance()
            while (this.current().isDigit())
        }

        return this.newToken(TokenType.Number)
    }

    private fun advance(): Char {
        val c = this.current()
        this.current++

        return c
    }

    private fun current(offset: Int = 0): Char =
        this.chars.getOrNull(this.current + offset) ?: 0.toChar()

    private fun lexeme(): String =
        this.chars.substring(this.start, this.current)

    private fun newToken(type: TokenType): Token =
        Token(type, this.lexeme(), this.start)
}
