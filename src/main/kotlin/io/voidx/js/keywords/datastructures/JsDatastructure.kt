package io.voidx.js.keywords.datastructures

import io.voidx.js.data.randomString
import io.voidx.js.FunctionVariable
import io.voidx.js.JavaScript
import io.voidx.js.keywords.JsValue
import io.voidx.js.keywords.Keyword
import io.voidx.js.keywords.Lambda
import io.voidx.js.keywords.Reference
import io.voidx.js.keywords.refer

interface JsDatastructure : Keyword {

    fun initialize(): JsDatastructure

    fun forEach(body: JavaScript.(List<FunctionVariable<*>>) -> Unit): Reference<JsDatastructure> {
        jsReturn += ".forEach(${
            Lambda<Nothing>(
                _arguments = listOf(String.randomString(4)),
                _body = body
            ).render()
        })"
        return this.refer()
    }

    fun forEach(body: JsValue<JavaScript.(List<FunctionVariable<*>>) -> Unit>): Reference<JsDatastructure> {
        jsReturn += ".forEach($body)"
        return this.refer()
    }
}