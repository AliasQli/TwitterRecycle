package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.param.PackageParam
import com.recycle.twitter.data.data
import java.lang.reflect.Modifier

object PremiumHook : Hook() {
    override fun PackageParam.load() {
        data.dexKit.findMethod {
            findFirst = true
            matcher {
                usingStrings(
                    "feature/twitter_blue",
                    "feature/premium_basic",
                    "feature/twitter_blue_verified",
                    "feature/premium_plus"
                )
                modifiers = Modifier.PUBLIC or Modifier.STATIC
                returnType = "boolean"
            }
        }.single().getMethodInstance(appClassLoader!!).hook {
            after {
                if (data.prefs.pretendPremium) {
                    result = true
                }
            }
        }
    }
}