package io.voidx.js.keywords.error

import io.voidx.js.Function
import io.voidx.js.FunctionVariable
import io.voidx.js.JavaScript

data class CatchFunction(
    val _body: JavaScript.(List<FunctionVariable<*>>) -> Unit,
    val errorName: String
) : Function<Nothing>(
    name = "",
    body = _body,
    arguments = listOf(errorName)
) {

    override var jsReturn: String = children.joinToString(";") { it.render() }

    override fun render(): String {
        return jsReturn
    }
}

data class Catch(
    val _body: CatchFunction
) : Function<Nothing>(
    name = "",
    body = _body._body
) {

    override fun render(): String {
        return "catch (${_body.errorName}) {${_body.render()}}"
    }
}