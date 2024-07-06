package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.recycle.twitter.data.config

/**
 * Make media insensitive
 */
object SensitiveMediaHook : Hook() {
    override fun PackageParam.load() {
        val jsonSensitiveMediaWarningClass =
            "com.twitter.model.json.core.JsonSensitiveMediaWarning".toClass()
        val jsonSensitiveMediaWarningMapperClass =
            "com.twitter.model.json.core.JsonSensitiveMediaWarning\$\$JsonObjectMapper".toClass()

        jsonSensitiveMediaWarningMapperClass.method {
            name = "parse"
        }.hook {
            after {
                if (!config.disableMediaWarning) return@after

                result ?: return@after
                jsonSensitiveMediaWarningClass.field {
                    type = BooleanType
                }.all(result).forEach {
                    it.set(false)
                }
            }
        }
    }
}