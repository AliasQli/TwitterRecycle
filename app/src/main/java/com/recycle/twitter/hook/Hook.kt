package com.recycle.twitter.hook

import android.annotation.SuppressLint
import android.content.Context
import com.highcapable.yukihookapi.hook.param.PackageParam
import org.luckypray.dexkit.DexKitBridge

abstract class Hook {
    companion object {
        lateinit var getId: (String, String) -> Int
        lateinit var dexKit: DexKitBridge

        @SuppressLint("DiscouragedApi")
        fun init(context: Context, kit: DexKitBridge) {
            getId = { name: String, defType: String ->
                context.resources.getIdentifier(
                    name, defType, context.packageName
                )
            }
            dexKit = kit
        }
    }

    abstract fun PackageParam.load()
    fun init(packageParam: PackageParam) = packageParam.load()
}