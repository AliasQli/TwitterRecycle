package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.recycle.twitter.data.config

object AdaptiveTrackSelectionHook : Hook() {
    override fun PackageParam.load() {
        val adaptiveTrackSelectionClass = dexKit.findClass {
            matcher {
                usingStrings("AdaptiveTrackSelection")
            }
        }.single().getInstance(appClassLoader!!)

        adaptiveTrackSelectionClass.method {
            param(LongType, LongType)
            returnType(IntType)
        }.hook {
            after {
                if (!config.useMinimumBitrate || config.minimumBitrate == null) return@after

                val getBitrate = { i: Int ->
                    val format = adaptiveTrackSelectionClass.method {
                        param(IntType)
                        returnType { it != IntType }
                        superClass(true)
                    }.get(instance).call(i)!!
                    format.javaClass.field {
                        name = "Z"
                    }.get(format).int()
                }
                var i = result as Int
                while (i >= 0) {
                    val b = getBitrate(i)
                    if (b >= config.minimumBitrate!!) {
                        result = i
                        return@after
                    }
                    i--
                }
                result = 0
            }
        }
    }
}