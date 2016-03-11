package com.paulrlutz.vocabmanager.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.paulrlutz.vocabmanager.R;
import com.paulrlutz.vocabmanager.fragments.SettingsFragment;


public class SettingsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        /**
         * Retrieves the boolean that determines if dark mode is on, and applies the appropriate theme.
         */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean darkTheme = prefs.getBoolean(getString(R.string.preference_dark_mode_on_key), false);

        if(darkTheme) {
            setTheme(R.style.AppTheme_NoActionBar);
        }
        else {
            setTheme(R.style.AppTheme_Light_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getFragmentManager().beginTransaction().replace(R.id.currentFragment, new SettingsFragment()).commit();
    }
}