package io.voidx.js.data

import io.voidx.js.keywords.variable.Const
import io.voidx.html.Element
import io.voidx.html.Fractal
import io.voidx.js.JavaScript
import io.voidx.js.keywords.JsValue
import io.voidx.js.keywords.Keyword
import io.voidx.js.keywords.RawJs
import java.util.*

class DataHolder(val js: JavaScript) : Keyword {

    override var jsReturn: String = ""

    fun initialize(baseValue: Any?): DataHolder {
        jsReturn = "ref($baseValue)"
        return this
    }

    override fun render(): String {
        return jsReturn
    }

    fun read(): Any {
        jsReturn += ".read()"
        return ""
    }

    fun write(value: JsValue<*>) {
        jsReturn += ".write($value)"
    }
}

fun Element.get(dataHolder: Const<DataHolder>): Fractal {
    val uuid = UUID.randomUUID().toString()
    val js = dataHolder.value!!.js
    this.attributes.add("v_datahold" to uuid)
    js.children.add(
        js.children.indexOf(dataHolder) + 1,
        RawJs("bindText(document.querySelector('[v_datahold=\"$uuid\"]'), ${dataHolder.name})")
    )
    return Fractal(text = "")
}