package io.voidx.js.keywords

import io.voidx.js.Function
import io.voidx.js.FunctionVariable
import io.voidx.js.JavaScript

data class Lambda<T>(
    val _arguments: List<String>,
    val _body: JavaScript.(List<FunctionVariable<*>>) -> Unit,
) : Function<T>(
    name = "",
    body = _body,
    arguments = _arguments
) {
    override var jsReturn: String =
        "(${_arguments.joinToString(", ")}) => {${children.joinToString(";") { it.render() }}}"

    override fun render(): String {
        return jsReturn
    }

    override fun run(arguments: JsValue<*>): String {
        return "${render()}($arguments)"
    }
}

inline fun <reified T> JavaScript.lambda(
    noinline body: JavaScript.(List<FunctionVariable<*>>) -> Unit,
    arguments: List<String>
): Lambda<T> {
    val lambda = Lambda<T>(
        _body = body,
        _arguments = arguments
    )
    children.add(lambda)
    return lambda
}