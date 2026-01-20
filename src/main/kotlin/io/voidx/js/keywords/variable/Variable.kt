package io.voidx.js.keywords.variable

import io.voidx.js.keywords.Keyword

interface Variable<T> : Keyword {
    val name: String
    val value: T?
}