package com.recycle.twitter.hook

import android.view.View
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.recycle.twitter.data.data

object BannerHook : Hook() {
    override fun PackageParam.load() {
        val bannerId = data.getId("banner", "id")

        View::class.java.method {
            name = "setVisibility"
        }.hook {
            before {
                if (!data.prefs.hideNewTweetsBanner) return@before

                val view = instance as View
                val visibility = args(0).int()
                if (view.id == bannerId && visibility == View.VISIBLE) {
                    args(0).set(View.GONE)
                    YLog.debug("Hide banner")
                }
            }
        }
    }
}