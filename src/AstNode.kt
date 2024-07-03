sealed class AstNode(val position: Int, val length: Int) {
    fun <T> accept(visitor: NodeVisitor<T>) = when (this) {
        is Binary -> visitor.acceptBinary(this)
        is Group -> visitor.acceptGroup(this)
        is Literal -> visitor.acceptLiteral(this)
        is Unary -> visitor.acceptUnary(this)
    }

    data class Literal(
        val pos: Int,
        val len: Int,

        val value: Double
    ): AstNode(pos, len)

    data class Binary(
        val pos: Int,
        val len: Int,

        val left: AstNode,
        val right: AstNode,
        val operator: Token
    ): AstNode(pos, len)

    data class Unary(
        val pos: Int,
        val len: Int,

        val node: AstNode,
        val operator: Token
    ): AstNode(pos, len)

    data class Group(
        val pos: Int,
        val len: Int,

        val node: AstNode
    ): AstNode(pos, len)
}

interface NodeVisitor<T> {
    fun acceptLiteral(n: AstNode.Literal): T
    fun acceptBinary(n: AstNode.Binary): T
    fun acceptUnary(n: AstNode.Unary): T
    fun acceptGroup(n: AstNode.Group): T
}
