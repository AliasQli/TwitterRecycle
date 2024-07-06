package com.recycle.twitter.data

import android.content.Context
import com.highcapable.yukihookapi.hook.factory.prefs
import com.recycle.twitter.getId
import org.luckypray.dexkit.DexKitBridge

class Data(context: Context, val dexKit: DexKitBridge) {
    private val yukiPrefs = context.prefs().native()
    val prefs: Prefs = Prefs(context, yukiPrefs)
    val getId = context::getId
}

lateinit var data: Data