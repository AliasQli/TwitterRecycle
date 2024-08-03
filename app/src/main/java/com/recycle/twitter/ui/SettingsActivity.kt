package com.recycle.twitter.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.Preference.SummaryProvider
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base.ModuleAppCompatActivity
import com.recycle.twitter.R
import com.recycle.twitter.data.config
import com.recycle.twitter.hook.SettingsHook


class SettingsActivity : ModuleAppCompatActivity() {
    override val moduleTheme: Int
        get() = R.style.Theme_TwitterRecycle

    class MFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            PreferenceManager.getDefaultSharedPreferences(requireActivity())
                .registerOnSharedPreferenceChangeListener(this)
            setPreferencesFromResource(R.xml.settings_screen, rootKey)

            val minimumBitratePreference =
                preferenceManager.findPreference<Preference>(requireContext().getString(R.string.minimum_bitrate)) as EditTextPreference
            minimumBitratePreference.summaryProvider = SummaryProvider<Preference> {
                if (config.minimumBitrate != null) {
                    config.minimumBitrate.toString()
                } else {
                    "Error: Can't parse string \"${minimumBitratePreference.text}\""
                }
            }
        }

        override fun onDisplayPreferenceDialog(preference: Preference) {
            val ctx = context ?: return
            when (preference.key) {
                config.undoTweetMenuKey -> {
                    ctx.startActivity(
                        Intent(
                            ctx,
                            SettingsHook.undoTweetSettingsActivityClass
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }

                config.video1080pMenuKey -> {
                    ctx.startActivity(
                        Intent(
                            ctx,
                            SettingsHook.dataSettingsActivityClass
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }

                else -> super.onDisplayPreferenceDialog(preference)
            }
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            config.refresh()
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