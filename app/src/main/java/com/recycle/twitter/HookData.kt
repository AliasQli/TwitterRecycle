package com.recycle.twitter

import com.tencent.mmkv.MMKV

const val PersistentUsersKey = "following_users"
const val MMID = "com.recycle.twitter"

val kv by lazy {
    MMKV.mmkvWithID(MMID)
}

object HookData {
    var volatileUsers: MutableSet<String> = mutableSetOf()
    lateinit var persistentUsers: MutableSet<String>

    fun init() {
        persistentUsers = getPersistentUsersInternal()
    }

    private fun getPersistentUsersInternal(): MutableSet<String> =
        kv.getStringSet(PersistentUsersKey, mutableSetOf())!!

    fun flushPersistentUsers() {
        kv.putStringSet(PersistentUsersKey, persistentUsers)
    }
}