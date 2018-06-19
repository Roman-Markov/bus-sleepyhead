package com.example.rmarkov.mapapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.SeekBarPreference
import android.widget.Toast
import com.example.rmarkov.mapapp.location.LocationStatusHolder
import com.example.rmarkov.mapapp.utils.getAppComponent
import javax.inject.Inject

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

    class SettingsFragment: PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

        @Inject
        lateinit var locationStatusHolder: LocationStatusHolder

        override fun onCreate(savedInstanceState: Bundle?) {
            activity?.getAppComponent()?.inject(this)
            super.onCreate(savedInstanceState)
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.settings)
            val radiusPreference = preferenceManager.findPreference(
                    getString(R.string.preference_key_for_radius)) as SeekBarPreference

            radiusPreference.seekBarIncrement = 200
            radiusPreference.min = 200
            radiusPreference.onPreferenceChangeListener = this
        }

        override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
           if (!locationStatusHolder.validateNewRadius((newValue as Int).toDouble())){
               Toast.makeText(activity, R.string.radius_too_big, Toast.LENGTH_SHORT).show()
               return false
           }
            return true
        }
    }
}