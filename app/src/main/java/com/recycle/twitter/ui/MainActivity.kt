package com.recycle.twitter.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base.ModuleAppCompatActivity
import com.recycle.twitter.R
import com.recycle.twitter.data.Prefs

class MainActivity : ModuleAppCompatActivity() {
    override val moduleTheme: Int
        get() = R.style.Theme_TwitterRecycle

    class MFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            PreferenceManager.getDefaultSharedPreferences(requireActivity())
                .registerOnSharedPreferenceChangeListener(this)
            setPreferencesFromResource(R.xml.settings_screen, rootKey)
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            val ctx = context ?: return
            YLog.debug(Prefs(ctx.prefs().native()).toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.app_name)
        setContentView(R.layout.main)

        if (savedInstanceState == null) {
            val frag = MFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, frag)
                .commit()
        }
    }
}