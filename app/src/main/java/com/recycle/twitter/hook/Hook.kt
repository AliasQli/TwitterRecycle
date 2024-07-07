package com.recycle.twitter.hook

import android.annotation.SuppressLint
import android.content.Context
import com.highcapable.yukihookapi.hook.param.PackageParam

abstract class Hook {
    companion object {
        lateinit var getId: (String, String) -> Int
        @SuppressLint("DiscouragedApi")
        fun init(context: Context) {
            getId = { name: String, defType: String ->
                context.resources.getIdentifier(
                    name, defType, context.packageName
                )
            }
        }
    }

    abstract fun PackageParam.load()
    fun init(packageParam: PackageParam) = packageParam.load()
}