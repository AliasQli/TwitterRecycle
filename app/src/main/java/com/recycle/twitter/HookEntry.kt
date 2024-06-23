package com.recycle.twitter

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.registerModuleAppActivities
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.recycle.twitter.data.Data
import com.recycle.twitter.hook.JsonHook
import com.recycle.twitter.hook.SettingsHook
import com.recycle.twitter.hook.UserHook
import com.tencent.mmkv.MMKV

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
                    onCreate {
                        injectModuleAppResources()
                        registerModuleAppActivities()
                        MMKV.initialize(this)
                        Data.init(this)
                    }
                }

                val hooks = arrayListOf(
                    JsonHook,
                    UserHook,
                    SettingsHook,
                )

                hooks.forEach { hook ->
                    hook.init(this)
                }
            }
        }
    }
}
