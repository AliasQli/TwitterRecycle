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

private inline fun <T> JSONObject.intoHelper(into: String, f: JSONObject.(String) -> T?): T? {
    val fields = into.split(".")
    val last = fields.size - 1
    val obj = fields.subList(0, last).fold(this as JSONObject?) { obj, s -> obj?.optJSONObject(s) }
    return obj?.f(fields[last])
}

fun JSONObject.intoJSONObject(into: String): JSONObject? =
    intoHelper(into, JSONObject::optJSONObject)

fun JSONObject.intoJSONArray(into: String): JSONArray? =
    intoHelper(into, JSONObject::optJSONArray)

fun JSONObject.intoString(into: String): String? =
    intoHelper(into, JSONObject::optString)

fun JSONObject.hasTypename(name: String): Boolean =
    optString("__typename").equals(name)

fun JSONObject.checkTypename(name: String): JSONObject? =
    if (hasTypename(name)) {
        this
    } else {
        null
    }
