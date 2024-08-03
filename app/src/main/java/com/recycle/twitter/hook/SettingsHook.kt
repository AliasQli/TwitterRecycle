package com.recycle.twitter.hook

import android.content.Intent
import android.widget.ImageView
import com.highcapable.yukihookapi.hook.factory.allConstructors
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.recycle.twitter.ui.SettingsActivity

/**
 * Inject the module settings activity.
 * */
object SettingsHook : Hook() {
    // Classloader doesn't work in SettingsActivity
    lateinit var undoTweetSettingsActivityClass: Class<*>
    lateinit var dataSettingsActivityClass: Class<*>
    override fun PackageParam.load() {
        val logoId = getId("logo", "id")
        undoTweetSettingsActivityClass =
            "com.twitter.feature.subscriptions.settings.undotweet.UndoTweetSettingsActivity".toClass()
        dataSettingsActivityClass = "com.twitter.app.settings.DataSettingsActivity".toClass()
        ImageView::class.java.allConstructors { _, constructor ->
            constructor.hook {
                after {
                    val view = instance as ImageView
                    if (view.id == logoId) {
                        view.setOnLongClickListener {
                            view.context.startActivity(
                                Intent(
                                    view.context,
                                    SettingsActivity::class.java
                                )
                            )
                            true
                        }
                    }
                }
            }
        }
    }
}
