// The hook method is from
// https://github.com/Dr-TSNG/TwiFucker/blob/master/app/src/main/java/icu/nullptr/twifucker/hook/JsonHook.kt

package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.InputStreamClass
import com.recycle.twitter.checkTypename
import com.recycle.twitter.data.Data
import com.recycle.twitter.filter
import com.recycle.twitter.forEach
import com.recycle.twitter.hasTypename
import com.recycle.twitter.intoJSONArray
import com.recycle.twitter.intoJSONObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

/**
 * Hook JSON creation.
 */
object JsonHook : Hook() {

    private const val timelineInstructionsAt = "data.timeline_response.timeline.instructions"
    private const val userInstructionsAt = "data.user.timeline_response.timeline.instructions"
    private const val timelineInstructionsFilter =
        "{\"data\":{\"timeline_response\":{\"timeline\":{\"instructions\":["
    private const val userInstructionsFilter = "{\"data\":{\"user\":{\"timeline_response\":"
    private fun JSONObject.timelineEntryNeedRetain(): Boolean {
        val entryId = optString("entryId")

        if (entryId.startsWith("tweet-") && Data.Prefs.blockRetweets) {
            optJSONObject("content")
                ?.checkTypename("TimelineTimelineItem")?.optJSONObject("content")
                ?.checkTypename("TimelineTweet")?.intoJSONObject("tweetResult.result")
                ?.checkTypename("Tweet")?.intoJSONObject("legacy.retweeted_status_result.result")
                ?.checkTypename("Tweet")?.intoJSONObject("core.user_result.result")
                ?.checkTypename("User")?.apply {
                    val restId = optString("rest_id")
                    val followed = Data.persistentUsers.contains(restId)

                    val legacy = optJSONObject("legacy")
                    val screenName = legacy?.optString("screen_name")
                    val name = legacy?.optString("name")
                    if (followed) {
                        YLog.info("Filter retweet from $name@$screenName#$restId")
                    } else {
                        YLog.debug("Don't filter retweet from $name@$screenName#$restId")
                    }
                    return !followed
                }
        } else if (entryId.startsWith("promoted-tweet-") && Data.Prefs.disablePromotedTweets) {
            YLog.debug("Filtered promoted tweet")
            return false
        } else if (entryId.startsWith("who-to-follow-") && Data.Prefs.disableWhoToFollow) {
            YLog.debug("Filtered who to follow")
            return false
        }
        return true
    }

    private fun processTimelineInstructions(timelineInstructions: JSONArray) {
        timelineInstructions.forEach { instruction ->
            if (instruction.hasTypename("TimelineAddEntries")) {
                instruction.optJSONArray("entries")?.filter { entry ->
                    entry.timelineEntryNeedRetain()
                }
            }
        }
    }

    private fun processUserInstructions(userInstructions: JSONArray) {
        var isTerminate = false

        userInstructions.forEach { instruction ->
            if (instruction.hasTypename("TimelineAddEntries")) {
                var n = 0
                instruction.optJSONArray("entries")?.forEach { entry ->
                    val entryId = entry.optString("entryId")
                    if (entryId.startsWith("user-")) {
                        val restId = entryId.split("-")[1]
                        Data.volatileUsers.add(restId)
                        Data.persistentUsers.add(restId)
                        n++
                    }
                }
                YLog.info("Got $n users")
            } else if (instruction.hasTypename("TimelineTerminateTimeline")) {
                if (instruction.optString("direction").equals("Bottom")) {
                    isTerminate = true
                }
            }
        }

        if (isTerminate) {
            Data.persistentUsers = Data.volatileUsers
            Data.volatileUsers = mutableSetOf()

            YLog.info("User timeline termination: following ${Data.persistentUsers.size} users")
            val b = StringBuilder()
            Data.persistentUsers.forEach {
                b.append(it).append(",")
            }
            YLog.debug(b.toString())
        }
        Data.flushPersistentUsers()
    }

    private fun processJson(json: JSONObject) {
        val timelineInstructions = json.intoJSONArray(timelineInstructionsAt)

        if (timelineInstructions != null) {
            YLog.info("Processing main page timeline")

            processTimelineInstructions(timelineInstructions)
            return
        }

        val userInstructions = json.intoJSONArray(userInstructionsAt)

        if (userInstructions != null) {
            YLog.info("Processing user timeline")

            processUserInstructions(userInstructions)
            return
        }
    }

    override fun PackageParam.load() {
        val loganSquare = "com.bluelinelabs.logansquare.LoganSquare".toClass()
        val jsonFactory = loganSquare.field {
            name = "JSON_FACTORY"
        }.give()!!.type

        jsonFactory.method {
            param(InputStreamClass)
        }.hook {
            before {
                val inputStream = args[0] as InputStream
                val reader = inputStream.bufferedReader()
                var content: String
                try {
                    reader.use { r ->
                        content = r.readText()
                    }
                    ByteArrayInputStream("".toByteArray())
                } catch (_: IOException) {
                    args[0] = object : InputStream() {
                        override fun read(): Int {
                            return -1
                        }
                    }
                    return@before
                }

                if (content.startsWith(timelineInstructionsFilter) || content.startsWith(
                        userInstructionsFilter
                    )
                ) {
                    try {
                        val json = JSONObject(content)
                        processJson(json)
                        content = json.toString()
                    } catch (_: JSONException) {
                    } catch (e: Throwable) {
                        YLog.info("json hook failed to parse JSONObject", e)
                        YLog.info(content)
                    }
                }

                args[0] = content.byteInputStream()
            }
        }
    }
}