package com.recycle.twitter

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.registerModuleAppActivities
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.recycle.twitter.data.Data
import com.recycle.twitter.data.data
import com.recycle.twitter.hook.BannerHook
import com.recycle.twitter.hook.JsonApiTweetHook
import com.recycle.twitter.hook.JsonProfileUserHook
import com.recycle.twitter.hook.JsonTimelineEntryHook
import com.recycle.twitter.hook.JsonTimelineTweetHook
import com.recycle.twitter.hook.MarkUserHook
import com.recycle.twitter.hook.PremiumHook
import com.recycle.twitter.hook.ProtectedMediaHook
import com.recycle.twitter.hook.SensitiveMediaHook
import com.recycle.twitter.hook.SettingsHook
import org.luckypray.dexkit.DexKitBridge

@InjectYukiHookWithXposed
object HookEntry : IYukiHookXposedInit {
    override fun onInit() {
        YukiHookAPI.configs {
            debugLog {
                tag = "TwitterRecycle"
                isEnable = true
                isRecord = false
                elements(TAG, PRIORITY, PACKAGE_NAME, USER_ID)
            }
            isDebug = false
            isEnableModuleAppResourcesCache = true
            isEnableHookSharedPreferences = false
            isEnableDataChannel = true
        }
    }

    override fun onHook() {
        YukiHookAPI.encase {
            loadApp {
                if (packageName.startsWith("com.google") || packageName == BuildConfig.APPLICATION_ID) return@loadApp

                onAppLifecycle {
                    attachBaseContext { baseContext, hasCalledSuper ->
                        if (hasCalledSuper) return@attachBaseContext
                        baseContext.apply {
                            injectModuleAppResources()
                            registerModuleAppActivities()

                            System.loadLibrary("dexkit")
                            DexKitBridge.create(baseContext.applicationInfo.sourceDir)
                                .use { dexKit ->
                                    data = Data(this, dexKit)

                                    val hooks = arrayListOf(
                                        JsonApiTweetHook,
                                        MarkUserHook,
                                        JsonTimelineEntryHook,
                                        JsonTimelineTweetHook,
                                        JsonProfileUserHook,
                                        SensitiveMediaHook,
                                        ProtectedMediaHook,
                                        PremiumHook,
                                        BannerHook,
                                        SettingsHook,
                                    )

                                    hooks.forEach { hook ->
                                        hook.init(this@loadApp)
                                    }
                                }
                        }
                    }
                }
            }
        }
    }
}
