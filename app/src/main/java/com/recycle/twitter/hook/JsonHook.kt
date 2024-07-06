// The hook method is from
// https://github.com/Dr-TSNG/TwiFucker/blob/master/app/src/main/java/icu/nullptr/twifucker/hook/JsonHook.kt

package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.InputStreamClass
import com.recycle.twitter.data.data
import com.recycle.twitter.forEach
import com.recycle.twitter.intoJSONArray
import com.recycle.twitter.typename
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

/**
 * Hook JSON creation.
 * In detail, this handles:
 * - Parse "following" page
 * - Handle (un)follow actions
 * - Filter retweets from followed users
 */
object JsonHook : Hook() {
    private const val userInstructionsAt = "data.user.timeline_response.timeline.instructions"
    private const val userInstructionsFilter = "{\"data\":{\"user\":{\"timeline_response\":"

    enum class FollowingState {
        Nothing,
        InFollowingPage,
        GettingFollowing,
    }

    private var followingState = FollowingState.Nothing

    private fun processUserInstructions(userInstructions: JSONArray) {
        var isTerminate = false

        userInstructions.forEach { instruction ->
            when (instruction.typename) {
                "TimelineTerminateTimeline" -> {
                    when (instruction.optString("direction")) {
                        "Top" -> when (followingState) {
                            FollowingState.InFollowingPage -> {
                                followingState = FollowingState.GettingFollowing
                                data.volatileUsers = mutableSetOf()
                                YLog.info("User timeline start")
                            }

                            FollowingState.GettingFollowing -> {
                                followingState = FollowingState.Nothing
                                YLog.info("Stop getting followed users")
                            }

                            FollowingState.Nothing -> {}
                        }

                        "Bottom" -> {
                            if (followingState != FollowingState.GettingFollowing) return
                            isTerminate = true
                        }
                    }
                }

                "TimelineAddEntries" -> {
                    if (followingState != FollowingState.GettingFollowing) return
                    var n = 0
                    instruction.optJSONArray("entries")?.forEach { entry ->
                        val entryId = entry.optString("entryId")
                        if (entryId.startsWith("user-")) {
                            val restId = entryId.split("-")[1]
                            data.volatileUsers.add(restId)
                            data.persistentUsers.add(restId)
                            n++
                        }
                    }
                    YLog.info("Got $n users")
                }
            }
        }

        if (isTerminate) {
            followingState = FollowingState.Nothing
            data.persistentUsers = data.volatileUsers
            data.volatileUsers = mutableSetOf()

            YLog.info("User timeline termination: following ${data.persistentUsers.size} users")
            val b = StringBuilder()
            data.persistentUsers.forEach {
                b.append(it).append(",")
            }
            YLog.debug(b.toString())
        }
        data.flushPersistentUsers()
    }

    private fun processUserFollowResponse(json: JSONObject) {
        val id = json.getString("id_str")
        val following = json.getBoolean("following")
        val screenName = json.optString("screen_name")
        val name = json.optString("name")

        if (!following) { // it is reversed (when following a user they're not followed yet, vice versa)
            data.volatileUsers.add(id) // if following from user list
            data.persistentUsers.add(id)
            YLog.info("Followed user $name@$screenName#$id")
        } else {
            data.volatileUsers.remove(id)
            data.persistentUsers.remove(id)
            YLog.info("Unfollowed user $name@$screenName#$id")
        }

        data.flushPersistentUsers()
    }

    private fun processJson(json: JSONObject) {
        val userInstructions = json.intoJSONArray(userInstructionsAt)

        if (userInstructions != null) {
            YLog.info("Processing user timeline")

            processUserInstructions(userInstructions)
            return
        }

        if (json.has("id_str") && json.has("name") && json.has("screen_name") && json.has("following")) {
            processUserFollowResponse(json)
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

                if (
                    content.startsWith(userInstructionsFilter) ||
                    content.startsWith("{\"id\":")
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

        val followingTimelineActivity =
            "com.twitter.users.following.FollowingTimelineActivity".toClass()
        followingTimelineActivity.constructor().hookAll {
            before {
                followingState = FollowingState.InFollowingPage
                YLog.info("into FollowingTimelineActivity")
            }
        }
    }
}