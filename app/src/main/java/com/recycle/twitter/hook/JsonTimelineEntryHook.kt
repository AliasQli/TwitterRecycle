package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.recycle.twitter.data.config

object JsonTimelineEntryHook : Hook() {
    private fun needRemove(entryId: String): Boolean {
        if (entryId.startsWith("superhero-") && config.disablePromotedTweets) return true
        if (entryId.startsWith("who-to-follow-") && config.disableWhoToFollow) return true
        if (entryId.startsWith("who-to-subscribe-") && config.disableWhoToFollow) return true
        if (entryId.startsWith("pinned-tweets-") && config.disablePinnedTweets) return true
        if (entryId.startsWith("bookmarked-tweet-") && config.disableBookmarkedTweets) return true
        if (entryId.startsWith("tweetdetailrelatedtweets-") && config.disableTweetDetailRelatedTweets) return true
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
                } else if (
                // Filter some uninteresting entryId
                    !entryId.startsWith("promoted-tweet-") &&
                    !entryId.startsWith("cursor-") &&
                    !entryId.startsWith("user-") &&
                    !entryId.startsWith("tweet-") &&
                    !entryId.startsWith("Guide-") &&
                    !entryId.startsWith("conversationthread-") &&
                    !entryId.startsWith("profile-conversation-") &&
                    !entryId.startsWith("community-to-join-") &&
                    !entryId.startsWith("cursor:")
                ) {
                    YLog.debug("entryId $entryId")
                }
            }
        }
    }
}