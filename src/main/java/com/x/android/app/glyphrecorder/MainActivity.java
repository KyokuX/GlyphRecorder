package com.x.android.app.glyphrecorder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.x.android.app.glyphrecorder.fragment.SettingsFragment;
import com.x.android.app.glyphrecorder.util.Constants;
import com.xi47.common.android.content.PreferencesUtil;

/**
 * Created by KyokuX on 14/11/15.
 */
public class MainActivity extends FragmentActivity {

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_start:
                    startTrigger();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new SettingsFragment();
        try {
            fragmentTransaction.replace(R.id.layout_container, fragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // http://developer.android.com/reference/android/app/FragmentTransaction.html#commitAllowingStateLoss()
        // change to commitAllowingStateLoss() avoid IllegalStateException: Can not perform this action after onSaveInstanceState problem.
        fragmentTransaction.commitAllowingStateLoss();

        Crashlytics.start(this);

        Tracker tracker = ((AnalyticsApplication) getApplication()).getTracker();
        tracker.setScreenName("start");
        tracker.send(new HitBuilders.AppViewBuilder().build());

        PreferencesUtil.getInstance().initPreference(this, Constants.PREFERENCE_NAME);
        boolean isSettingsShown = PreferencesUtil.getInstance().get(Constants.PREFERENCE_NAME, Constants.PREFERENCE_SETTINGS_SHOWN, false);

        findViewById(R.id.btn_start).setOnClickListener(mOnClickListener);

        if (isSettingsShown) {
            startTrigger();
        } else {
            PreferencesUtil.getInstance().put(Constants.PREFERENCE_NAME, Constants.PREFERENCE_SETTINGS_SHOWN, true);
        }
    }

    private void startTrigger() {
        Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
        startService(intent);
        finish();
    }
}
