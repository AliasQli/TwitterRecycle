package com.recycle.twitter

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.registerModuleAppActivities
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.recycle.twitter.data.Data
import com.recycle.twitter.hook.JsonHook
import com.recycle.twitter.hook.JsonTimelineTweetHook
import com.recycle.twitter.hook.JsonTimelineUserHook
import com.recycle.twitter.hook.MarkUserHook
import com.recycle.twitter.hook.SensitiveMediaHook
import com.recycle.twitter.hook.SettingsHook

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
                            val data = Data(this)

                            val hooks = arrayListOf(
                                ::JsonHook,
                                ::MarkUserHook,
                                ::JsonTimelineUserHook,
                                ::JsonTimelineTweetHook,
                                ::SensitiveMediaHook,
                                ::SettingsHook,
                            )

                            hooks.forEach { hook ->
                                hook(data).init(this@loadApp)
                            }
                        }
                    }
                }
            }
        }
    }
}
