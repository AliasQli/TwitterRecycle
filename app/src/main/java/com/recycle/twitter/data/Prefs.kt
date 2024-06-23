package com.recycle.twitter.data

import com.highcapable.yukihookapi.hook.type.java.BooleanClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge

const val BlockRetweetsKey = "block_retweets"
const val DisablePromotedTweetsKey = "disable_promoted_tweets"
const val DisableWhoToFollowKey = "disable_who_to_follow"
const val FollowingMarkKey = "following_mark"
const val FollowingMarkPrefixKey = "following_mark_prefix"

class Prefs(private val prefs: YukiHookPrefsBridge) {
    companion object {
        fun type(key: String): Class<*> {
            return when (key) {
                FollowingMarkPrefixKey -> StringClass
                BlockRetweetsKey, DisablePromotedTweetsKey,
                DisableWhoToFollowKey, FollowingMarkKey -> BooleanClass

                else -> throw Error("Unexpected key $key")
            }
        }

        fun defaultString(key: String): String {
            return when (key) {
                FollowingMarkPrefixKey -> "\uD83D\uDCCE"
                else -> throw Error("Unexpected string key $key")
            }
        }

        fun defaultBool(key: String): Boolean {
            return when (key) {
                BlockRetweetsKey, DisablePromotedTweetsKey,
                DisableWhoToFollowKey, FollowingMarkKey -> true

                else -> throw Error("Unexpected bool key $key")
            }
        }
    }

    val blockRetweets get() = prefs.getBoolean(BlockRetweetsKey, defaultBool(BlockRetweetsKey))
    val disablePromotedTweets
        get() = prefs.getBoolean(
            DisablePromotedTweetsKey, defaultBool(
                DisablePromotedTweetsKey
            )
        )
    val disableWhoToFollow
        get() = prefs.getBoolean(
            DisableWhoToFollowKey, defaultBool(
                DisableWhoToFollowKey
            )
        )
    val followingMarkEnabled
        get() = prefs.getBoolean(
            FollowingMarkKey,
            defaultBool(FollowingMarkKey)
        )
    val followingMarkPrefix
        get() = prefs.getString(
            FollowingMarkPrefixKey, defaultString(
                FollowingMarkPrefixKey
            )
        )

    override fun toString(): String {
        return "Pref: ${blockRetweets}, ${disablePromotedTweets}, ${disableWhoToFollow}, ${followingMarkEnabled}, $followingMarkPrefix"
    }
}