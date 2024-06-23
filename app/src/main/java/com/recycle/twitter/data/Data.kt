package com.recycle.twitter.data

import android.annotation.SuppressLint
import android.content.Context
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.log.YLog
import com.recycle.twitter.BuildConfig
import com.tencent.mmkv.MMKV
import kotlin.properties.Delegates

const val PersistentUsersKey = "following_users"

val kv by lazy {
    MMKV.mmkvWithID(BuildConfig.APPLICATION_ID)
}

@SuppressLint("StaticFieldLeak")
object Data {
    var volatileUsers: MutableSet<String> = mutableSetOf()
    lateinit var persistentUsers: MutableSet<String>
    var logoId by Delegates.notNull<Int>()
    lateinit var Prefs: Prefs

    fun init(context: Context) {
        persistentUsers = getPersistentUsersInternal()
        logoId = context.getId("logo", "id")
        Prefs = Prefs(context.prefs().native())
        YLog.debug(Prefs.toString())
    }

    @SuppressLint("DiscouragedApi")
    fun Context.getId(name: String, defType: String): Int {
        return resources.getIdentifier(
            name, defType, packageName
        )
    }

    private fun getPersistentUsersInternal(): MutableSet<String> =
        kv.getStringSet(PersistentUsersKey, mutableSetOf())!!

    fun flushPersistentUsers() {
        kv.putStringSet(PersistentUsersKey, persistentUsers)
    }
}