package com.example.rmarkov.mapapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.SeekBarPreference

class SettingsActivity: AppCompatActivity(){

    companion object {
        fun createIntent(context: Context) = Intent(context, SettingsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(supportFragmentManager.findFragmentById(android.R.id.content) == null) {
            supportFragmentManager.beginTransaction().add(android.R.id.content, SettingsFragment()).commit()
        } else {
            supportFragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()
        }
    }

    class SettingsFragment: PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.settings)
            var radiusPreference = preferenceManager.findPreference(getString(R.string.preference_key_for_radius))

            radiusPreference?.let{
                (radiusPreference as SeekBarPreference).seekBarIncrement = 100
                radiusPreference.min = 100}
        }

        override fun onStart() {
            var radiusPreference = preferenceManager.findPreference(getString(R.string.preference_key_for_radius))

            radiusPreference?.let{
                (radiusPreference as SeekBarPreference).seekBarIncrement = 100
                radiusPreference.min = 100}
            super.onStart()
        }
    }
}