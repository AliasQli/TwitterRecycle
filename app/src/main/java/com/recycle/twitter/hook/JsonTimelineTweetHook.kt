package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.recycle.twitter.data.Data

/**
 * Disable promoted tweets
 */
class JsonTimelineTweetHook(val data: Data) : Hook() {
    override fun PackageParam.load() {
        val jsonTimelineTweetClass =
            "com.twitter.model.json.timeline.urt.JsonTimelineTweet".toClass()
        val jsonTimelineTweetMapperClass =
            "com.twitter.model.json.timeline.urt.JsonTimelineTweet\$\$JsonObjectMapper".toClass()
        val jsonTweetResultsClass =
            "com.twitter.model.json.core.JsonTweetResults".toClass().field().give()!!.type
        val jsonPromotedContentUrtClass =
            "com.twitter.model.json.timeline.urt.JsonPromotedContentUrt".toClass()

        jsonTimelineTweetMapperClass.method {
            name = "parse"
            returnType = jsonTimelineTweetClass
        }.hook {
            after {
                if (!data.prefs.disablePromotedTweets) return@after

                result ?: return@after
                jsonTimelineTweetClass.apply {
                    field {
                        type = jsonPromotedContentUrtClass
                    }.get(result).any() ?: return@after
                    field {
                        type = jsonTweetResultsClass
                    }.get(result).set(null)
                    field {
                        type = StringClass
                        order()
                    }.get(result).set(null)
                    YLog.info("Removed promoted tweet")
                }
            }
        }
    }
}