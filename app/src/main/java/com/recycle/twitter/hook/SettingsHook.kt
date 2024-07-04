package com.recycle.twitter.hook

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import com.highcapable.yukihookapi.hook.factory.allConstructors
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.recycle.twitter.data.data
import com.recycle.twitter.ui.SettingsActivity

/**
 * Inject the module settings activity.
 * */
object SettingsHook : Hook() {
    override fun PackageParam.load() {
        val logoId = data.getId("logo", "id")

        ImageView::class.java.allConstructors { _, constructor ->
            constructor.hook {
                after {
                    val view = instance as ImageView
                    if (view.id == logoId) {
                        view.setOnLongClickListener {
                            if (view.context is Activity) { // somehow it always is
                                val host = view.context as Activity
                                host.startActivity(Intent(host, SettingsActivity::class.java))
                            }
                            true
                        }
                    }
                }
            }
        }
    }
}
