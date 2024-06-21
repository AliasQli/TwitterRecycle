package com.recycle.twitter

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.InputStreamClass
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.lang.StringBuilder

fun JSONObject.jsonCheckTypename(name: String): Boolean =
    optString("__typename").equals(name)

private fun JSONObject.jsonGetInstructions(): JSONArray? =
    optJSONObject("data")?.optJSONObject("timeline_response")?.optJSONObject("timeline")
        ?.optJSONArray("instructions")

private fun JSONObject.jsonGetUserInstructions(): JSONArray? =
    optJSONObject("data")?.optJSONObject("user")?.optJSONObject("timeline_response")
        ?.optJSONObject("timeline")?.optJSONArray("instructions")

object JsonHook : Hook() {
    private fun JSONObject.timelineEntryNeedRetain(): Boolean {
        if (optString("entryId").startsWith("tweet-")) {
            val entryContent = optJSONObject("content")

            if (entryContent?.jsonCheckTypename("TimelineTimelineItem") == true) {
                val itemContent = entryContent.optJSONObject("content")

                if (itemContent?.jsonCheckTypename("TimelineTweet") == true) {
                    val tweet = itemContent.optJSONObject("tweetResult")?.optJSONObject("result")

                    if (tweet?.jsonCheckTypename("Tweet") == true) {
                        val retweeted = tweet.optJSONObject("legacy")?.optJSONObject("retweeted_status_result")?.optJSONObject("result")

                        if (retweeted?.jsonCheckTypename("Tweet") == true) {
                            val user = retweeted.optJSONObject("core")?.optJSONObject("user_result")?.optJSONObject("result")

                            if (user?.jsonCheckTypename("User") == true) {
                                val restId = user.optString("rest_id")
                                val followed = HookData.persistentUsers.contains(restId)

                                val legacy = user.optJSONObject("legacy")
                                val screenName = legacy?.optString("screen_name")
                                val name = legacy?.optString("name")
                                if (followed) {
                                    YLog.info("Filter retweet from $name@$screenName#$restId")
                                } else {
                                    YLog.debug("Don't filter retweet from $name@$screenName#$restId")
                                }
                                return !followed
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    private fun processJson(json: JSONObject) {
        val timelineInstructions = json.jsonGetInstructions()

        if (timelineInstructions != null) {
            YLog.info("Processing main page timeline")

            timelineInstructions.forEach { instruction ->
                if (instruction.jsonCheckTypename("TimelineAddEntries")) {
                    instruction.optJSONArray("entries")?.filter { entry ->
                        entry.timelineEntryNeedRetain()
                    }
                }
            }
            return
        }

        val userInstructions = json.jsonGetUserInstructions()

        if (userInstructions != null) {
            YLog.info("Processing user timeline")

            var isTerminate = false

            userInstructions.forEach { instruction ->
                if (instruction.jsonCheckTypename("TimelineAddEntries")) {
                    var n = 0
                    instruction.optJSONArray("entries")?.forEach { entry ->
                        val entryId = entry.optString("entryId")
                        if (entryId.startsWith("user-")) {
                            val restId = entryId.split("-")[1]
                            HookData.volatileUsers.add(restId)
                            HookData.persistentUsers.add(restId)
                            n++
                        }
                    }
                    YLog.info("Got $n users")
                } else if (instruction.jsonCheckTypename("TimelineTerminateTimeline")) {
                    if (instruction.optString("direction").equals("Bottom")) {
                        isTerminate = true
                    }
                }
            }

            if (isTerminate) {
                HookData.persistentUsers = HookData.volatileUsers
                HookData.volatileUsers = mutableSetOf()

                YLog.info("User timeline termination: following ${HookData.persistentUsers.size} users")
                val b = StringBuilder()
                HookData.persistentUsers.forEach{
                    b.append(it).append(",")
                }
                YLog.debug(b.toString())
            }
            HookData.flushPersistentUsers()
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
                val reader = BufferedReader(inputStream.reader())
                var content: String
                try {
                    reader.use { r ->
                        content = r.readText()
                    }
                } catch (_: IOException) {
                    args[0] = object : InputStream() {
                        override fun read(): Int {
                            return -1
                        }
                    }
                    return@before
                }

                try {
                    val json = JSONObject(content)
                    processJson(json)
                    content = json.toString()
                } catch (_: JSONException) {
                } catch (e: Throwable) {
                    YLog.info("json hook failed to parse JSONObject", e)
                    YLog.info(content)
                }

                args[0] = content.byteInputStream()
            }
        }
    }
}