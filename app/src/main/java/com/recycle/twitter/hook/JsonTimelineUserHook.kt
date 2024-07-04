package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.recycle.twitter.data.Data

/**
 * Disable "who to follow"
 */
class JsonTimelineUserHook(val data: Data) : Hook() {
    override fun PackageParam.load() {

        val jsonTimelineUserClass = "com.twitter.model.json.timeline.urt.JsonTimelineUser".toClass()
        val jsonTimelineUserMapperClass =
            "com.twitter.model.json.timeline.urt.JsonTimelineUser\$\$JsonObjectMapper".toClass()
        val jsonPromotedContentUrtClass =
            "com.twitter.model.json.timeline.urt.JsonPromotedContentUrt".toClass()
        val jsonUserResultsClass =
            "com.twitter.model.json.core.JsonUserResults".toClass().field().give()!!.type

        jsonTimelineUserMapperClass.method {
            name = "parse"
            returnType = jsonTimelineUserClass
        }.hook {
            after {
                if (!data.prefs.disableWhoToFollow) return@after
                result ?: return@after
                jsonTimelineUserClass.apply {
                    field {
                        type = jsonPromotedContentUrtClass
                    }.get(result).any() ?: return@after
                    field {
                        type = jsonUserResultsClass
                    }.get(result).set(null)
                }
                YLog.info("Removed promoted user")
            }
        }
    }
}