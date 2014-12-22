package com.x.android.app.glyphrecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by KyokuX on 14/11/15.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        Tracker tracker = ((AnalyticsApplication) getApplication()).getTracker();
        tracker.setScreenName("start");
        tracker.send(new HitBuilders.AppViewBuilder().build());

        Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
        startService(intent);
        finish();
    }
}
