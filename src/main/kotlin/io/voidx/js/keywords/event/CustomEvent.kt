package io.voidx.js.keywords.event

import io.voidx.js.data.randomString
import io.voidx.js.keywords.JsValue
import io.voidx.js.keywords.Keyword
import io.voidx.js.keywords.variable.Const
import io.voidx.js.JavaScript

interface JsEvent : Keyword
data class CustomEvent(val eventName: JsValue<*>, val parent: JavaScript) : JsEvent {

    val variable = Const(name = String.randomString(5), value = "new Event($eventName)", parent = parent)
    override var jsReturn: String = variable.render()

    companion object {
        private val defaultEvents: Map<Events, DefaultEvent> by lazy {
            Events.entries.associateWith { DefaultEvent(it.name.lowercase()) }.toMap()
        }

        fun getEvent(events: Events): DefaultEvent {
            return defaultEvents[events]!!
        }
    }

    override fun render(): String {
        return jsReturn
    }
}

data class DefaultEvent(val eventName: String) : JsEvent {
    override var jsReturn: String = "\"$eventName\""

    override fun render(): String {
        return jsReturn
    }
}

fun JavaScript.customEvent(name: JsValue<*>): CustomEvent {
    val event = CustomEvent(name, this)
    children.add(event)
    return event
}
