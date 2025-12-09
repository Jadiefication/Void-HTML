package io.voidx.html.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.SelfClosingElement

fun Element.Source(vararg attribute: Attribute): SelfClosingElement {
    val node = object : SelfClosingElement("source") {}
    node.addAttributes(*attribute)
    children!!.add(node)
    return node
}
