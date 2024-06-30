package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.recycle.twitter.data.Data

class SensitiveMediaHook(val data: Data) : Hook() {
    override fun PackageParam.load() {
        if (!data.prefs.disableMediaWarning) return

        val jsonSensitiveMediaWarningClass =
            "com.twitter.model.json.core.JsonSensitiveMediaWarning".toClass()
        val jsonSensitiveMediaWarningMapperClass =
            "com.twitter.model.json.core.JsonSensitiveMediaWarning\$\$JsonObjectMapper".toClass()

        jsonSensitiveMediaWarningMapperClass.method {
            name = "parse"
        }.hook {
            after {
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