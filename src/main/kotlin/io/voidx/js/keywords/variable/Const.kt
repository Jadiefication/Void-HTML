package io.voidx.js.keywords.variable

import io.voidx.js.keywords.JsValue
import io.voidx.js.keywords.Keyword
import io.voidx.js.keywords.RawJs
import io.voidx.js.JavaScript

data class Const<T>(
    override val value: T?,
    override val name: String,
    val parent: JavaScript
) : Variable<T> {

    override var jsReturn: String = "const $name = ${
        if (value is Keyword) {
            value.render()
        } else {
            "$value"
        }
    }"

    operator fun plus(other: JsValue<T>) {
        parent.children.add(RawJs("$name + $other"))
    }

    operator fun minus(other: JsValue<T>) {
        parent.children.add(RawJs("$name - $other"))
    }

    operator fun times(other: JsValue<T>) {
        parent.children.add(RawJs("$name * $other"))
    }

    operator fun div(other: JsValue<T>) {
        parent.children.add(RawJs("$name / $other"))
    }

    operator fun rem(other: JsValue<T>) {
        parent.children.add(RawJs("$name % $other"))
    }

    override fun render(): String {
        return jsReturn
    }
}

inline fun <reified T> JavaScript.const(value: T, name: String): Const<T> {
    val constant = Const(
        value = value,
        name = name,
        parent = this
    )
    children.add(constant)
    return constant
}