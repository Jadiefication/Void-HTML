package io.voidx.js.keywords.datastructures

import io.voidx.js.keywords.JsValue
import io.voidx.js.keywords.Keyword

data class ObjectDestructure(
    val pattern: String
) : JsValue<Keyword>, Keyword {

    override var jsReturn: String = "{ $pattern }"

    override fun render(): String {
        return jsReturn
    }

    override fun toJs(): String {
        return render()
    }
}

data class ArrayDestructure(
    val pattern: String
) : JsValue<Keyword>, Keyword {

    override var jsReturn: String = "[ $pattern ]"

    override fun render(): String {
        return jsReturn
    }

    override fun toJs(): String {
        return render()
    }
}