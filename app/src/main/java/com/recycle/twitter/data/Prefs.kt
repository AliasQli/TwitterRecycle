package com.recycle.twitter.data

import android.content.Context
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import com.recycle.twitter.R

class Prefs(context: Context, private val prefs: YukiHookPrefsBridge) {
    val extrasMenuKey = context.getString(R.string.extras_menu_key)
    val undoPostMenuKey = context.getString(R.string.undo_post_menu_key)

    private val itemPretendPremium = PrefsData(
        context.getString(R.string.pretend_premium),
        context.resources.getBoolean(R.bool.pretend_premium_def)
    )
    private val itemEnableUndoPost = PrefsData(
        context.getString(R.string.enable_undo_post),
        context.resources.getBoolean(R.bool.enable_undo_post_def)
    )
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
    private val itemDisableRecommendedUsers = PrefsData(
        context.getString(R.string.disable_recommended_users),
        context.resources.getBoolean(R.bool.disable_recommended_users_def)
    )
    private val itemDisableMediaWarning = PrefsData(
        context.getString(R.string.disable_media_warning),
        context.resources.getBoolean(R.bool.disable_media_warning_def)
    )
    private val itemUnprotectMedia = PrefsData(
        context.getString(R.string.unprotect_media),
        context.resources.getBoolean(R.bool.unprotect_media_def)
    )
    private val itemFollowingMark = PrefsData(
        context.getString(R.string.following_mark),
        context.resources.getBoolean(R.bool.following_mark_def)
    )
    private val itemFollowingMarkPrefix = PrefsData(
        context.getString(R.string.following_mark_prefix),
        context.resources.getString(R.string.following_mark_prefix_def)
    )

    // Don't modify; it's exposed just for convenience
    var pretendPremium = prefs.get(itemPretendPremium)
    var enableUndoPost = prefs.get(itemEnableUndoPost)
    var blockRetweets = prefs.get(itemBlockRetweets)
    var disablePromotedTweets = prefs.get(itemDisablePromotedTweets)
    var disableWhoToFollow = prefs.get(itemDisableWhoToFollow)
    var disableMediaWarning = prefs.get(itemDisableMediaWarning)
    var disableRecommendedUsers = prefs.get(itemDisableRecommendedUsers)
    var unprotectMedia = prefs.get(itemUnprotectMedia)
    var followingMark = prefs.get(itemFollowingMark)
    var followingMarkPrefix = prefs.get(itemFollowingMarkPrefix)

    fun refresh() {
        pretendPremium = prefs.get(itemPretendPremium)
        enableUndoPost = prefs.get(itemEnableUndoPost)
        blockRetweets = prefs.get(itemBlockRetweets)
        disablePromotedTweets = prefs.get(itemDisablePromotedTweets)
        disableWhoToFollow = prefs.get(itemDisableWhoToFollow)
        disableRecommendedUsers = prefs.get(itemDisableRecommendedUsers)
        disableMediaWarning = prefs.get(itemDisableMediaWarning)
        unprotectMedia = prefs.get(itemUnprotectMedia)
        followingMark = prefs.get(itemFollowingMark)
        followingMarkPrefix = prefs.get(itemFollowingMarkPrefix)
    }
}