package com.recycle.twitter.data

import android.content.Context
import com.recycle.twitter.BuildConfig
import com.recycle.twitter.R
import com.tencent.mmkv.MMKV

class Data(var context: Context) {
    init {
        MMKV.initialize(context)
    }

    private val kv = MMKV.mmkvWithID(BuildConfig.APPLICATION_ID)
    private val persistentUsersKey = context.getString(R.string.persistent_users_key)
    var volatileUsers = mutableSetOf<String>()
    var persistentUsers: MutableSet<String> = kv.getStringSet(persistentUsersKey, mutableSetOf())!!
    var prefs = Prefs(context)

    fun flushPersistentUsers() {
        kv.putStringSet(persistentUsersKey, persistentUsers)
    }
}