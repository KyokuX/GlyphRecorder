package com.x.android.app.glyphrecorder;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by X on 14/12/2.
 */
public class AnalyticsApplication extends Application {

    Tracker mTracker = null;

    @Override
    public void onCreate() {
        super.onCreate();

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.enableAutoActivityReports(this);
        mTracker = analytics.newTracker(R.xml.global_tracker);
        mTracker.enableAdvertisingIdCollection(true);
        mTracker.enableAutoActivityTracking(true);
        mTracker.enableExceptionReporting(true);
    }

    public Tracker getTracker() {
        return mTracker;
    }
}
