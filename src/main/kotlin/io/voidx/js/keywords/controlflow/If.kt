package io.voidx.js.keywords.controlflow

import io.voidx.js.keywords.Keyword
import io.voidx.js.Function
import io.voidx.js.FunctionVariable
import io.voidx.js.JavaScript

data class If(
    val condition: String,
    val _body: JavaScript.(List<FunctionVariable<*>>) -> Unit
) : Function<Nothing>(
    name = "",
    arguments = emptyList(),
    body = _body
) {

    private fun renderBlock(statements: List<Keyword>): String {
        return statements.joinToString(";") {
            val rendered = it.render()
            if (!rendered.endsWith(";")) "$rendered;" else rendered
        }
    }

    override var jsReturn: String = "if($condition) {${renderBlock(children)}}"

    override fun render(): String {
        return jsReturn
    }

    fun ElseIf(condition: String, body: JavaScript.() -> Unit): If {
        children.clear()
        this.body()
        jsReturn += "else if ($condition) {${renderBlock(children)}}"
        return this
    }

    fun Else(body: JavaScript.() -> Unit): If {
        children.clear()
        this.body()
        jsReturn += "else {${renderBlock(children)}}"
        return this
    }
}

fun JavaScript.If(condition: String, body: JavaScript.(List<FunctionVariable<*>>) -> Unit): If {
    val conditional = If(
        condition = condition,
        _body = body
    )
    children.add(conditional)
    return conditional
}