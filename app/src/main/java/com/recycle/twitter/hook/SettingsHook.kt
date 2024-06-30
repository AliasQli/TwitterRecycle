package com.recycle.twitter.hook

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import com.highcapable.yukihookapi.hook.factory.allConstructors
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.recycle.twitter.data.Data
import com.recycle.twitter.getId
import com.recycle.twitter.ui.MainActivity

/**
 * Inject the module settings activity.
 * */
class SettingsHook(val data: Data) : Hook() {
    override fun PackageParam.load() {
        val logoId = data.context.getId("logo", "id")

        ImageView::class.java.allConstructors { _, constructor ->
            constructor.hook {
                after {
                    val view = instance as ImageView
                    if (view.id == logoId) {
                        view.setOnLongClickListener {
                            if (view.context is Activity) { // somehow it always is
                                val host = view.context as Activity
                                host.startActivity(Intent(host, MainActivity::class.java))
                            }
                            true
                        }
                    }
                }
            }
        }
    }
}
