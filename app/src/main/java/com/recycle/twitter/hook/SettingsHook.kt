package com.recycle.twitter.hook

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
