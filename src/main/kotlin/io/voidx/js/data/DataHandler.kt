package io.voidx.js.data

import io.voidx.js.keywords.HTMLElement
import io.voidx.js.keywords.JsValue
import io.voidx.js.keywords.Keyword
import io.voidx.js.keywords.variable.Const
import io.voidx.js.keywords.variable.const
import io.voidx.js.JavaScript

class DataHandler : Keyword {

    override var jsReturn: String = "bind"
    override fun render(): String {
        return jsReturn
    }

    fun text(element: JsValue<HTMLElement>, ref: JsValue<DataHolder>) {
        jsReturn += "Text($element, $ref)"
    }
}

fun JavaScript.setData(value: JsValue<*>, name: String): Const<DataHolder> {
    return const(
        name = name,
        value = DataHolder(this).initialize(value)
    )
}

val String.Companion.letters
    get() = mutableMapOf<Int, MutableSet<String>>()

fun String.Companion.randomString(length: Int): String {
    val chars = ('a'..'z') + ('A'..'Z')
    var string = (1..length)
        .map { chars.random() }
        .joinToString("")
    if (letters[length]?.contains(string) == true) {
        string = randomString(length)
    } else {
        letters[length]?.add(string)
    }
    return string
}