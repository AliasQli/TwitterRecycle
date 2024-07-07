package com.recycle.twitter.ui

import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base.ModuleAppCompatActivity
import com.recycle.twitter.R
import com.recycle.twitter.data.config

class SettingsActivity : ModuleAppCompatActivity() {
    override val moduleTheme: Int
        get() = R.style.Theme_TwitterRecycle

    class MFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            PreferenceManager.getDefaultSharedPreferences(requireActivity())
                .registerOnSharedPreferenceChangeListener(this)
            setPreferencesFromResource(R.xml.settings_screen, rootKey)
        }

        override fun onDisplayPreferenceDialog(preference: Preference) {
            val ctx = context ?: return
            when (preference.key) {
                config.customNavigationMenuKey -> {
                    ctx.startActivity(
                        // Use class name because classloader doesn't work here
                        Intent()
                            .setComponent(
                                ComponentName(
                                    ctx,
                                    "com.twitter.feature.subscriptions.settings.extras.ExtrasSettingsActivity"
                                )
                            )
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )

                }

                config.earlyAccessMenuKey -> {
                    ctx.startActivity(
                        Intent()
                            .setComponent(
                                ComponentName(
                                    ctx,
                                    "com.twitter.feature.twitterblue.settings.tabcustomization.TabCustomizationActivity"
                                )
                            )
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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