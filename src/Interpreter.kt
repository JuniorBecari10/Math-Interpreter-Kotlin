private typealias Data = Double

class Interpreter(private val node: AstNode): NodeVisitor<Data> {
    fun interpret(): Result<Data> =
        try {
            Result.success(node.accept(this))
        }
        catch (e: Exception) {
            Result.failure(Exception())
        }

    override fun acceptLiteral(n: AstNode.Literal): Data =
        n.value

    override fun acceptBinary(n: AstNode.Binary): Data {
        val left = n.left.accept(this)
        val right = n.right.accept(this)

        if (n.operator.type == TokenType.Slash && right == 0.0)
            printError(n.left.position, n.length, "Cannot divide by zero")

        return when (n.operator.type) {
            TokenType.Plus -> left + right
            TokenType.Minus -> left - right
            TokenType.Star -> left * right
            TokenType.Slash -> left / right

            else -> internalError("Invalid operator for binary expression: '${n.operator.lexeme}'")
        }
    }

    override fun acceptUnary(n: AstNode.Unary): Data {
        val value = n.node.accept(this)

        return when (n.operator.type) {
            TokenType.Minus -> -value

            else -> internalError("Invalid operator for unary expression: '${n.operator.lexeme}'")
        }
    }

    override fun acceptGroup(n: AstNode.Group): Data =
        n.node.accept(this)
}
