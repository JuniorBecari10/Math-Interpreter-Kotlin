class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parse(): Result<AstNode> =
        try {
            Result.success(this.expr(0))
        } catch (e: Exception) {
            Result.failure(Exception())
        }

    private fun expr(precedence: Int): AstNode = when (precedence) {
        0 -> this.binary(precedence, TokenType.Plus, TokenType.Minus)
        1 -> this.binary(precedence, TokenType.Star, TokenType.Slash)
        2 -> this.unary(precedence, TokenType.Minus)
        3 -> this.group(precedence)
        4 -> this.literal()

        else -> internalError("Invalid precedence: $precedence")
    }

    private fun binary(precedence: Int, vararg operators: TokenType): AstNode {
        var left = this.expr(precedence + 1)

        if (this.match(*operators)) {
            val operator = this.advance()!!
            val right = this.expr(precedence + 1)

            left = AstNode.Binary(
                left.position,
                (right.position + right.length) - left.position,
                left,
                right,
                operator
            )
        }

        return left
    }

    private fun unary(precedence: Int, vararg operators: TokenType): AstNode {
        if (this.match(*operators)) {
            val operator = this.advance()!!
            val expr = this.expr(precedence + 1)

            return AstNode.Unary(expr.position, (expr.position + expr.length) - operator.position, expr, operator)
        }

        return this.expr(precedence + 1)
    }

    private fun group(precedence: Int): AstNode {
        if (this.match(TokenType.LParen)) {
            val pos = this.advance()!!.position
            val expr = this.expr(0)
            val prev = this.expect(TokenType.RParen)

            return AstNode.Group(pos, (prev.position + prev.lexeme.length) - pos, expr)
        }

        return this.expr(precedence + 1)
    }

    private fun literal(): AstNode {
        val token = this.expect(TokenType.Number)

        return AstNode.Literal(
            token.position,
            token.lexeme.length,
            token.lexeme.toDouble()
        )
    }

    // ---

    private fun match(vararg types: TokenType): Boolean =
        !this.isAtEnd() && this.current()!!.type in types

    private fun expect(vararg types: TokenType): Token =
        if (!this.match(*types) || this.isAtEnd()) {
            printError(
                this.current()?.position ?: (tokens.last().position + tokens.last().lexeme.length),
                this.current()?.lexeme?.length ?: 1,

                if (this.isAtEnd())
                    "Unexpected end of line. ${this.prettyPrintTypes(*types)} ${if (types.size == 1) "was" else "were"} expected instead"
                else
                    "Unexpected token: '${this.current()!!.lexeme}'. ${this.prettyPrintTypes(*types)} ${if (types.size == 1) "was" else "were"} expected instead")
        }

        else
            this.advance()!!

    private fun prettyPrintTypes(vararg types: TokenType) =
        types.joinToString(", ") { it.type }

    private fun advance(): Token? =
        this.tokens.getOrNull(this.current++)

    private fun current(offset: Int = 0): Token? =
        this.tokens.getOrNull(this.current + offset)

    private fun isAtEnd(offset: Int = 0): Boolean =
        this.current + offset < 0 ||
        this.current + offset >= this.tokens.size
}
