package com.paulrlutz.vocabmanager.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.paulrlutz.vocabmanager.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
    }
}