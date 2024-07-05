package com.recycle.twitter.hook

import com.highcapable.yukihookapi.hook.param.PackageParam
import com.recycle.twitter.data.data
import java.lang.reflect.Modifier

object PremiumHook : Hook() {
    override fun PackageParam.load() {
        val premiumCompanionClass = data.dexKit.findClass {
            findFirst = true
            matcher {
                usingStrings(
                    "feature/twitter_blue",
                    "feature/premium_basic",
                    "feature/twitter_blue_verified",
                    "feature/premium_plus"
                )
                fieldCount(0)
            }
        }.single()

        premiumCompanionClass.findMethod {
            matcher {
                usingStrings(
                    "feature/twitter_blue",
                    "feature/premium_basic",
                    "feature/twitter_blue_verified",
                    "feature/premium_plus"
                )
                returnType = "boolean"
                modifiers = Modifier.PUBLIC or Modifier.STATIC
            }
        }.single().getMethodInstance(appClassLoader!!).hook {
            after {
                if (data.prefs.pretendPremium) resultTrue()
            }
        }

        premiumCompanionClass.findMethod {
            matcher {
                paramTypes = listOf(null, "java.lang.String", null, "int")
                returnType = "boolean"
                modifiers = Modifier.PUBLIC or Modifier.STATIC
            }
        }.single().getMethodInstance(appClassLoader!!).hook {
            after {
                if (args(1).string() == "subscriptions_feature_1003" && data.prefs.enableUndoPost) resultTrue()
            }
        }

        data.dexKit.findMethod {
            matcher {
                name = "invoke"
                usingStrings("subscriptions_feature_1003")
                modifiers = Modifier.PUBLIC or Modifier.FINAL
            }
        }.single().getMethodInstance(appClassLoader!!).hook {
            after {
                if (data.prefs.enableUndoPost) resultTrue()
            }
        }
    }
}