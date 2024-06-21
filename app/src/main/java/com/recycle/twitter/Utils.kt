package com.recycle.twitter

import org.json.JSONArray
import org.json.JSONObject

inline fun JSONArray.forEach(action: (JSONObject) -> Unit) {
    (0 until this.length()).forEach { i ->
        if (this[i] is JSONObject) {
            action(this[i] as JSONObject)
        }
    }
}

inline fun JSONArray.filter(action: (JSONObject) -> Boolean) {
    var i = 0
    while (i < this.length()) {
        if (action(this[i] as JSONObject)) {
            i++
        } else {
            this.remove(i)
        }
    }
}

