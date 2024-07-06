package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.BooleanClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.recycle.twitter.data.config
import de.robv.android.xposed.XposedHelpers

/**
 * Disable promoted tweets
 */
object JsonApiTweetHook : Hook() {
    private const val followingKey = "following"
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
            }.give()!!.returnType // xm0.a implements tdu.a
        val obfUserClass =
            "com.twitter.android.widget.UserPreference".toClass().field().give()!!.type // rvu
        val obfUserConverterClass =
            "com.twitter.api.model.json.core.b\$a".toClass()
        val restJsonTwitterUser = "com.twitter.api.model.json.core.RestJsonTwitterUser".toClass()

        // Pass RestJsonTwitterUser.v (following) all the way up

        obfUserConverterClass.method {
            paramCount = 1
        }.hook {
            after {
                val following =
                    restJsonTwitterUser.field {
                        type = BooleanClass
                        name { it.length == 1 && it.isOnlyLowercase() }
                        order()
                    }.get(args(0).any()).cast<Boolean>() ?: return@after
                XposedHelpers.setAdditionalInstanceField(result, followingKey, following)
            }
        }

        obfUserClass.constructor {
            paramCount = 1
        }.hook {
            after {
                val following =
                    XposedHelpers.getAdditionalInstanceField(args(0).any(), followingKey)
                        ?: return@after
                XposedHelpers.setAdditionalInstanceField(instance, followingKey, following)
            }
        }

        jsonApiTweetMapperClass.method {
            name = "parse"
        }.hook {
            after {
                if (!config.blockRetweets) return@after
                result ?: return@after

                val legacy = jsonApiTweetClass.field {
                    type = jsonGraphQlLegacyApiTweetClass
                }.get(result).any() ?: return@after

                val retweetedStatusResult = jsonGraphQlLegacyApiTweetClass.field {
                    type { it != BooleanType && it != IntType }
                }.get(legacy).any() ?: return@after

                // retweetedStatusResult: tdu.a
                if (retweetedStatusResult::class.java != obfJsonApiTweetA) return@after

                val user = obfJsonApiTweetA.field {
                    type = obfUserClass
                }.get(retweetedStatusResult).any() ?: return@after

                val following =
                    XposedHelpers.getAdditionalInstanceField(user, followingKey) as Boolean?
                        ?: return@after

                if (following) {
                    val id = obfUserClass.field {
                        type = LongType
                        name { it.length == 1 && it.isOnlyLowercase() }
                    }.get(user).long().toString()

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