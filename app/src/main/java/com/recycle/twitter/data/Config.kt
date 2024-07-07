package com.recycle.twitter.data

import android.content.Context
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import com.recycle.twitter.R

class Config(context: Context) {
    private val prefs = context.prefs().native()

    val customNavigationMenuKey = context.getString(R.string.custom_navigation_menu_key)
    val earlyAccessMenuKey = context.getString(R.string.early_access_menu_key)

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
    private val itemDisablePinnedTweets = PrefsData(
        context.getString(R.string.disable_pinned_tweets),
        context.resources.getBoolean(R.bool.disable_pinned_tweets_def)
    )
    private val itemDisableBookmarkedTweets = PrefsData(
        context.getString(R.string.disable_bookmarked_tweets),
        context.resources.getBoolean(R.bool.disable_bookmarked_tweets_def)
    )
    private val itemTweetDetailRelatedTweets = PrefsData(
        context.getString(R.string.disable_tweet_detail_related_tweets),
        context.resources.getBoolean(R.bool.disable_tweet_detail_related_tweets_def)
    )
    private val itemHideNewTweetsBanner = PrefsData(
        context.getString(R.string.hide_new_tweets_banner),
        context.resources.getBoolean(R.bool.hide_new_tweets_banner_def)
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

    private val allCache = mutableSetOf<Cache<*>>()
    fun refresh() = allCache.forEach(Cache<*>::refresh)

    val pretendPremium by Cache(allCache) { prefs.get(itemPretendPremium) }
    val enableUndoPost by Cache(allCache) { prefs.get(itemEnableUndoPost) }
    val blockRetweets by Cache(allCache) { prefs.get(itemBlockRetweets) }
    val disablePromotedTweets by Cache(allCache) { prefs.get(itemDisablePromotedTweets) }
    val disableWhoToFollow by Cache(allCache) { prefs.get(itemDisableWhoToFollow) }
    val disablePinnedTweets by Cache(allCache) { prefs.get(itemDisablePinnedTweets) }
    val disableBookmarkedTweets by Cache(allCache) { prefs.get(itemDisableBookmarkedTweets) }
    val disableTweetDetailRelatedTweets by Cache(allCache) { prefs.get(itemTweetDetailRelatedTweets) }
    val hideNewTweetsBanner by Cache(allCache) { prefs.get(itemHideNewTweetsBanner) }
    val disableRecommendedUsers by Cache(allCache) { prefs.get(itemDisableRecommendedUsers) }
    val disableMediaWarning by Cache(allCache) { prefs.get(itemDisableMediaWarning) }
    val unprotectMedia by Cache(allCache) { prefs.get(itemUnprotectMedia) }
    val followingMark by Cache(allCache) { prefs.get(itemFollowingMark) }
    val followingMarkPrefix by Cache(allCache) { prefs.get(itemFollowingMarkPrefix) }
}

lateinit var config: Config