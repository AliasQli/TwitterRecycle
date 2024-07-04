package com.recycle.twitter.data

import android.content.Context
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import com.recycle.twitter.R

class Prefs(context: Context) {
    private val prefs = context.prefs().native()
    private val itemBlockRetweets = PrefsData(
        context.getString(R.string.block_retweets),
        context.resources.getBoolean(R.bool.block_retweets_def)
    )
    private val itemDisablePromotedTweets = PrefsData(
        context.getString(R.string.disable_promoted_tweets),
        context.resources.getBoolean(R.bool.disable_promoted_tweets_def)
    )
    private val itemDisableWhoToFollow = PrefsData(
        context.getString(R.string.disable_who_to_follow),
        context.resources.getBoolean(R.bool.disable_who_to_follow_def)
    )
    private val itemDisableMediaWarning = PrefsData(
        context.getString(R.string.disable_media_warning),
        context.resources.getBoolean(R.bool.disable_media_warning_def)
    )
    private val itemFollowingMark = PrefsData(
        context.getString(R.string.following_mark),
        context.resources.getBoolean(R.bool.following_mark_def)
    )
    private val itemFollowingMarkPrefix = PrefsData(
        context.getString(R.string.following_mark_prefix),
        context.resources.getString(R.string.following_mark_prefix_def)
    )

    val blockRetweets get() = prefs.get(itemBlockRetweets)
    val disablePromotedTweets get() = prefs.get(itemDisablePromotedTweets)
    val disableWhoToFollow get() = prefs.get(itemDisableWhoToFollow)
    val disableMediaWarning get() = prefs.get(itemDisableMediaWarning)
    val followingMark get() = prefs.get(itemFollowingMark)
    val followingMarkPrefix get() = prefs.get(itemFollowingMarkPrefix)
}