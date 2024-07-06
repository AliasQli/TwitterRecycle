package com.recycle.twitter.data

import kotlin.reflect.KProperty

class Cache<T>(cacheSet: MutableCollection<Cache<*>>? = null, private val initialize: () -> T) {
    private var value = initialize()

    init {
        cacheSet?.add(this)
    }

    fun refresh() {
        value = initialize()
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
}
