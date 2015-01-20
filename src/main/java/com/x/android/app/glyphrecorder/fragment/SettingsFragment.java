package com.x.android.app.glyphrecorder.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.x.android.app.glyphrecorder.R;

/**
 * Created by X on 15/1/19.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }
}
