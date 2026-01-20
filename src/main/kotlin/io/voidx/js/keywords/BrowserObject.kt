package io.voidx.js.keywords

import io.voidx.js.keywords.event.Event
import io.voidx.js.keywords.event.EventFunction
import io.voidx.js.keywords.event.JsEvent
import io.voidx.js.keywords.event.exception.FunctionNotVariableException
import io.voidx.js.FunctionVariable
import io.voidx.js.JavaScript

interface BrowserObject : Keyword {

    fun on(_eventType: JsValue<JsEvent>, _function: JsValue<EventFunction>): Reference<BrowserObject> {
        val event = Event(
            eventType = _eventType,
            function = _function
        )
        jsReturn += event.render()
        return this.refer()
    }

    fun on(_eventType: List<JsValue<JsEvent>>, _function: JsValue<EventFunction>): List<Reference<BrowserObject>> {
        return _eventType.map {
            return@map on(it, _function)
        }
    }

    fun off(event: JsValue<Event>): Reference<BrowserObject> {
        val eventValue = when (event) {
            is DirectValue<Event> -> event.value
            is VariableValue<Event> -> event.variable.value!!
            else -> throw UnsupportedOperationException("Cannot use a js function")
        }
        try {
            jsReturn += RawJs(
                operation = ".removeEventListener($event, ${eventValue.variable.name}${if (!eventValue.useCapture) ", true" else ""})"
            ).render()
        } catch (e: Exception) {
            throw FunctionNotVariableException(event = eventValue)
        }
        return this.refer()
    }

    fun off(events: List<JsValue<Event>>): Reference<BrowserObject> {
        events.forEach {
            off(it)
        }
        return this.refer()
    }

    fun on(
        _eventType: JsValue<JsEvent>,
        _function: JavaScript.(List<FunctionVariable<*>>) -> Unit
    ): Reference<BrowserObject> {
        val event = Event(
            eventType = _eventType,
            body = _function
        )
        jsReturn += event.render()
        return this.refer()
    }

    fun on(
        _eventType: List<JsValue<JsEvent>>,
        _function: JavaScript.(List<FunctionVariable<*>>) -> Unit
    ): List<Reference<BrowserObject>> {
        return _eventType.map {
            return@map on(it, _function)
        }
    }
}