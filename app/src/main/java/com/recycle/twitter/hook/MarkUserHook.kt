package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.BooleanClass
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.recycle.twitter.data.data

/**
 * Mark followed users
 */
object MarkUserHook : Hook() {
    override fun PackageParam.load() {
        val legacyUserMapperClass =
            "com.twitter.api.model.json.core.GraphqlJsonTwitterUser\$JsonGraphQlLegacyTwitterUser\$\$JsonObjectMapper".toClass()
        val restJsonTwitterUser = "com.twitter.api.model.json.core.RestJsonTwitterUser".toClass()

        legacyUserMapperClass.method {
            name = "parse"
        }.hook {
            after {
                if (!data.prefs.followingMark) return@after

                val following = restJsonTwitterUser.field {
                    type = BooleanClass
                    name { it.length == 1 && it.isOnlyLowercase() }
                    order()
                }.get(result).cast<Boolean>() ?: return@after

                if (following) {
                    val id = restJsonTwitterUser.field {
                        type = LongType
                    }.get(result).long()

                    if (id == 0L) return@after

                    restJsonTwitterUser.field {
                        type = StringClass
                        order()
                    }.get(result).apply {
                        set("${data.prefs.followingMarkPrefix}${string()}")
                        YLog.debug("Marked user ${string()}#$id")
                    }
                }
            }
        }
    }
}
