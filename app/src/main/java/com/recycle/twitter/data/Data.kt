package com.recycle.twitter.data

import android.content.Context
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import com.recycle.twitter.R
import com.recycle.twitter.getId
import org.luckypray.dexkit.DexKitBridge

class Data(context: Context, val dexKit: DexKitBridge) {
    private val yukiPrefs = context.prefs().native()
    val prefs: Prefs = Prefs(context, yukiPrefs)
    val getId = context::getId

    private val itemPersistentUsers = PrefsData(
        context.getString(R.string.persistent_users_key),
        setOf<String>()
    )

    var volatileUsers = mutableSetOf<String>()
    var persistentUsers = yukiPrefs.get(itemPersistentUsers).toMutableSet()

    fun flushPersistentUsers() {
        yukiPrefs.edit {
            put(itemPersistentUsers, persistentUsers)
        }
    }
}

lateinit var data: Data