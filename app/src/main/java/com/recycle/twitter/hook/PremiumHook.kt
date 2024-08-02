package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.StringArrayClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.recycle.twitter.data.config

// subscriptions_enabled
// subscriptions_feature_1009 overall, controls 1011 and 1003 ok
// subscriptions_feature_1011 1080p_video ok
// subscriptions_feature_1003 undo_tweet ok
// subscriptions_feature_1002 bookmark_folders
// subscriptions_feature_1014 longer_tweets
// subscriptions_feature_1001 app_icon_settings
// subscriptions_feature_1008 tab_customization ok
// subscriptions_feature_1005 read_mode ok
// subscriptions_feature_1007 top_articles
// subscriptions_feature_labs_1002 longer_video
// subscriptions_feature_labs_1004 ?
object PremiumHook : Hook() {
    private const val overallSubscription = "subscriptions_feature_1009"
    private const val video1080pSubscription = "subscriptions_feature_1011"
    private const val undoTweetSubscription = "subscriptions_feature_1003"
    private const val readerModeSubscription = "subscriptions_feature_1005"

    override fun PackageParam.load() {
        val bookmarkTimelineRetainedGraphInterface =
            "com.twitter.app.bookmarks.di.retained.BookmarkTimelineRetainedGraph".toClass()
        val premiumClass =
            bookmarkTimelineRetainedGraphInterface.method().give()!!.returnType // hxr
        val premiumCompanionClass = premiumClass.field { name = "Companion" }.give()!!.type // hxr.a

        premiumCompanionClass.method {
            param(StringArrayClass, VagueType)
            returnType = BooleanType
        }.hook {
            replaceAny {
                val b = config.pretendPremium
                        && args(0).cast<Array<String>>()!!.contains("feature/premium_basic")
                b || callOriginal() as Boolean
            }
        }

        premiumCompanionClass.method {
            param(StringClass, VagueType, VagueType)
            returnType = BooleanType
        }.hook {
            replaceAny {
                when (args(0).cast<String>()!!) {
                    overallSubscription, video1080pSubscription -> true
                    undoTweetSubscription -> config.enableUndoPost
                    readerModeSubscription -> config.enableReaderMode
                    else -> false
                } || callOriginal() as Boolean
            }
        }
    }
}