package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.recycle.twitter.data.data

object ProtectedMediaHook : Hook() {
    override fun PackageParam.load() {
        val jsonAllowDownloadStatusClass =
            "com.twitter.model.json.media.JsonAllowDownloadStatus".toClass()
        val jsonMediaEntityClass = "com.twitter.model.json.core.JsonMediaEntity".toClass()
        val jsonMediaEntityMapperClass =
            "com.twitter.model.json.core.JsonMediaEntity\$\$JsonObjectMapper".toClass()

        jsonMediaEntityMapperClass.method {
            name = "parse"
        }.hook {
            after {
                if (!data.prefs.unprotectMedia) return@after

                val status = jsonMediaEntityClass.field {
                    type = jsonAllowDownloadStatusClass
                }.get(result)

                if (status.any() == null) {
                    status.set(jsonAllowDownloadStatusClass.constructor().get().call())
                }

                jsonAllowDownloadStatusClass.field {
                    type = BooleanType
                }.get(status.any()).set(true)
            }
        }
    }
}