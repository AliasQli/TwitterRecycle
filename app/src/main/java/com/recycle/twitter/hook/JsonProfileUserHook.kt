package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.ArrayListClass
import com.recycle.twitter.data.data

object JsonProfileUserHook : Hook() {
    override fun PackageParam.load() {
        if (!data.prefs.disableRecommendedUsers) return

        val jsonProfileRecommendationModuleResponseClass =
            "com.twitter.model.json.people.JsonProfileRecommendationModuleResponse".toClass()
        val jsonProfileRecommendationModuleResponseMapperClass =
            "com.twitter.model.json.people.JsonProfileRecommendationModuleResponse\$\$JsonObjectMapper".toClass()

        jsonProfileRecommendationModuleResponseMapperClass.method {
            name = "parse"
        }.hook {
            after {
                result ?: return@after
                val users = jsonProfileRecommendationModuleResponseClass.field {
                    type = ArrayListClass
                }.get(result)
                if (users.cast<ArrayList<*>>()!!.isNotEmpty()) {
                    users.setNull()
                }
            }
        }
    }
}