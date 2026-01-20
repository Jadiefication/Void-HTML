package io.voidx.js.keywords.controlflow

import io.voidx.js.Function
import io.voidx.js.FunctionVariable
import io.voidx.js.JavaScript

data class While(
    val condition: String,
    val _body: JavaScript.(List<FunctionVariable<*>>) -> Unit
) : Function<Nothing>(
    name = "",
    arguments = emptyList(),
    body = _body
) {

    override var jsReturn: String = "while($condition) {${children.joinToString(";") { it.render() }}}"

    override fun render(): String {
        return jsReturn
    }
}

data class For(
    val condition: String,
    val _body: JavaScript.(List<FunctionVariable<*>>) -> Unit
) : Function<Nothing>(
    name = "",
    arguments = emptyList(),
    body = _body
) {

    override var jsReturn: String = "for($condition) {${children.joinToString(";") { it.render() }}}"

    override fun render(): String {
        return jsReturn
    }
}

fun JavaScript.While(condition: String, body: JavaScript.(List<FunctionVariable<*>>) -> Unit): While {
    val While = While(
        condition = condition,
        _body = body
    )
    children.add(While)
    return While
}

fun JavaScript.For(condition: String, body: JavaScript.(List<FunctionVariable<*>>) -> Unit): For {
    val For = For(
        condition = condition,
        _body = body
    )
    children.add(For)
    return For
}