package io.voidx.js.keywords.error

import io.voidx.js.keywords.Reference
import io.voidx.js.keywords.refer
import io.voidx.js.Function
import io.voidx.js.FunctionVariable
import io.voidx.js.JavaScript

data class Try(
    val _body: JavaScript.(List<FunctionVariable<*>>) -> Unit
) : Function<Nothing>(
    name = "",
    body = _body
) {

    override var jsReturn: String = "try {${children.joinToString(";") { it.render() }}}"

    fun catch(catchClause: Catch): Try {
        jsReturn += catchClause.render()
        return this
    }

    fun finally(finally: Finally): Reference<Try> {
        jsReturn += finally.render()
        return this.refer()
    }

    override fun render(): String {
        return jsReturn
    }
}

fun JavaScript.Try(body: JavaScript.(List<FunctionVariable<*>>) -> Unit): Try {
    val Try = Try(
        _body = body
    )
    children.add(Try)
    return Try
}