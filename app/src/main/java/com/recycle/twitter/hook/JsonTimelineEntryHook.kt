package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.recycle.twitter.data.data

object JsonTimelineEntryHook : Hook() {
    private fun needRemove(entryId: String): Boolean {
        if (entryId.startsWith("superhero-") && data.prefs.disablePromotedTweets) return true
        if (entryId.startsWith("who-to-follow-") && data.prefs.disableWhoToFollow) return true
        if (entryId.startsWith("pinned-tweets-") && data.prefs.disablePinnedTweets) return true
        return false
    }

    override fun PackageParam.load() {
        val jsonTimelineEntryClass =
            "com.twitter.model.json.timeline.urt.JsonTimelineEntry".toClass()
        val jsonTimelineEntryMapperClass =
            "com.twitter.model.json.timeline.urt.JsonTimelineEntry\$\$JsonObjectMapper".toClass()

        jsonTimelineEntryMapperClass.method {
            name = "parse"
        }.hook {
            after {
                result ?: return@after
                val entryId = jsonTimelineEntryClass.field {
                    type = StringClass
                }.get(result).string()

                if (needRemove(entryId)) {
                    jsonTimelineEntryClass.field {
                        type {
                            it != StringClass && it != LongType
                        }
                    }.get(result).setNull()
                    YLog.info("Removed timeline entry $entryId")
                }
            }
        }
    }
}