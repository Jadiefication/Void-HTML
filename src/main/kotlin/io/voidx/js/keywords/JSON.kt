package io.voidx.js.keywords

import io.voidx.js.keywords.datastructures.JsDatastructure
import io.voidx.js.keywords.datastructures.JsObject
import io.voidx.js.FunctionVariable
import io.voidx.js.JavaScript

class JSON : Keyword {

    override var jsReturn: String = "JSON"
    override fun render(): String {
        return jsReturn
    }

    fun isRawJSON(value: JsValue<*>): Boolean {
        jsReturn += ".isRawJSON($value)"
        return true
    }

    fun parse(
        value: JsValue<String>,
        call: (JsDatastructure) -> Unit,
        reviver: (JavaScript.(List<FunctionVariable<*>>) -> Unit)? = null
    ): Reference<JSON> {
        jsReturn += ".parse($value${if (reviver != null) ", $reviver" else ""})"
        return applyMethods(call, JsObject(emptyMap()), this)
    }

    fun rawJSON(string: JsValue<String>, call: (JsObject) -> Unit): Reference<JSON> {
        jsReturn += ".rawJSON($string)"
        return applyMethods(call, JsObject(emptyMap()), this)
    }

    fun stringify(
        jsObject: JsObject,
        replacer: (JavaScript.(List<FunctionVariable<*>>) -> Unit)? = null,
        space: JsValue<*>? = null
    ): String {
        jsReturn += ".stringify($jsObject${if (replacer != null) ", $replacer" else ""}${if (space != null) ", $space" else ""})"
        return ""
    }
}

fun JavaScript.isRawJSON(value: JsValue<*>): Boolean {
    val json = JSON()
    children.add(json)
    return json.isRawJSON(value)
}

fun JavaScript.parse(
    value: JsValue<String>,
    call: (JsDatastructure) -> Unit,
    reviver: (JavaScript.(List<FunctionVariable<*>>) -> Unit)? = null
): Reference<JSON> {
    val json = JSON()
    children.add(json)
    return json.parse(value, call, reviver)
}

fun JavaScript.rawJSON(string: JsValue<String>, call: (JsObject) -> Unit): Reference<JSON> {
    val json = JSON()
    children.add(json)
    return json.rawJSON(string, call)
}

fun JavaScript.stringify(
    jsObject: JsObject,
    replacer: (JavaScript.(List<FunctionVariable<*>>) -> Unit)? = null,
    space: JsValue<*>? = null
): String {
    val json = JSON()
    children.add(json)
    return json.stringify(jsObject, replacer, space)
}