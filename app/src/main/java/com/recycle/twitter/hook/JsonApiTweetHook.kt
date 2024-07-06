package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.recycle.twitter.data.data

/**
 * Disable promoted tweets
 */
object JsonApiTweetHook : Hook() {
    override fun PackageParam.load() {
        val jsonApiTweetClass =
            "com.twitter.api.model.json.core.JsonApiTweet".toClass()
        val jsonApiTweetMapperClass =
            "com.twitter.api.model.json.core.JsonApiTweet\$\$JsonObjectMapper".toClass()
        val jsonGraphQlLegacyApiTweetClass =
            "com.twitter.api.model.json.core.JsonApiTweet\$JsonGraphQlLegacyApiTweet".toClass()
        val obfJsonApiTweetA =
            "com.twitter.api.model.json.core.BaseJsonApiTweet".toClass().method {
                modifiers { isAbstract }
            }.give()!!.returnType
        val obfUserClass =
            "com.twitter.android.widget.UserPreference".toClass().field().give()!!.type

        jsonApiTweetMapperClass.method {
            name = "parse"
        }.hook {
            after {
                if (!data.prefs.blockRetweets) return@after
                result ?: return@after

                val legacy = jsonApiTweetClass.field {
                    type = jsonGraphQlLegacyApiTweetClass
                }.get(result).any() ?: return@after

                val retweetedStatusResult = jsonGraphQlLegacyApiTweetClass.field {
                    type { it != BooleanType && it != IntType }
                }.get(legacy).any() ?: return@after

                val user = obfJsonApiTweetA.field {
                    type = obfUserClass
                }.get(retweetedStatusResult).any()

                val id = obfUserClass.field {
                    type = LongType
                    name { it.length == 1 && it.isOnlyLowercase() }
                }.get(user).long().toString()

                if (data.persistentUsers.contains(id)) {
                    val strings = obfUserClass.field {
                        type = StringClass
                        order()
                    }.all(user)

                    val screenName = strings[0].string()
                    val name = strings[1].string()
                    YLog.debug("Filter retweet from $name@$screenName#$id")
                    result = null
                }
            }
        }
    }
}