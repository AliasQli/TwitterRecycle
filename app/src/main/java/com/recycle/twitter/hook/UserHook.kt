package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.recycle.twitter.data.Data

/**
 * Mark followed users
 */
object UserHook : Hook() {
    override fun PackageParam.load() {
        val legacyUserClassName =
            "com.twitter.api.model.json.core.GraphqlJsonTwitterUser\$JsonGraphQlLegacyTwitterUser"
        val legacyUserClass = legacyUserClassName.toClass()
        val legacyUserMapperClass = "$legacyUserClassName\$\$JsonObjectMapper".toClass()

        legacyUserMapperClass.method {
            name = "parse"
        }.hook {
            after {
                if (!Data.Prefs.followingMarkEnabled) return@after

                val id = legacyUserClass.field {
                    type = LongType
                    superClass()
                }.get(result).long()

                if (id == 0L) return@after

                if (Data.persistentUsers.contains(id.toString())) {
                    legacyUserClass.field {
                        type = StringClass
                        order()
                        superClass()
                    }.get(result).apply {
                        set(Data.Prefs.followingMarkPrefix + string())
                        YLog.debug("Marked user ${string()}#$id")
                    }
                }
            }
        }
    }
}
