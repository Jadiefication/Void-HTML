package io.voidx.js.keywords.error

import io.voidx.js.Function
import io.voidx.js.FunctionVariable
import io.voidx.js.JavaScript

data class Finally(
    val _body: JavaScript.(List<FunctionVariable<*>>) -> Unit
) : Function<Nothing>(
    name = "",
    arguments = emptyList(),
    body = _body
) {

    override var jsReturn: String = "finally {${children.joinToString(";") { it.render() }}}"

    override fun render(): String {
        return jsReturn
    }
}