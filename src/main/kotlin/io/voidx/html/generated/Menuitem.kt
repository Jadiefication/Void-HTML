package io.voidx.html.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.SelfClosingElement

fun Element.Menuitem(vararg attribute: Attribute): SelfClosingElement {
    val node = object : SelfClosingElement("menuitem") {}
    node.addAttributes(*attribute)
    children!!.add(node)
    return node
}
