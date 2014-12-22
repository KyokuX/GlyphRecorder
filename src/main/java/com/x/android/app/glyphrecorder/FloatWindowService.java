package com.x.android.app.glyphrecorder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.x.android.app.glyphrecorder.bean.Color;
import com.x.android.app.glyphrecorder.component.TouchTrackerView;

/**
 * Created by KyokuX on 14/11/15.
 */
public class FloatWindowService extends Service {

    private static final int MAX_TRACK = 5;
    private static final int[] COLOR_RANGE = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.PINK};

    private int mTriggerX = -1;
    private int mTriggerY = -1;

    private View mGlyphView = null;
    private View mTriggerView = null;
    private ViewGroup mMakerContainerView = null;
    private WindowManager mManager = null;
    private View mActionBar = null;

    private TouchTrackerView mTrackerView = null;
    private TouchTrackerView.OnTrackerListener mListener = new TouchTrackerView.OnTrackerListener() {

        @Override
        public void onTrackEnd(Path path, Color color) {
            addTrackerMaker(path, color);
        }
    };

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Tracker tracker = ((AnalyticsApplication) getApplication()).getTracker();
            switch (v.getId()) {
                case R.id.btn_translucent:
                    tracker.setScreenName("translucent");
                    tracker.send(new HitBuilders.AppViewBuilder().build());

                    showGlyph(true);
                    showTrigger();
                    hideActionBar();
                    break;

                case R.id.btn_close:
                    tracker.setScreenName("close");
                    tracker.send(new HitBuilders.AppViewBuilder().build());

                    mTrackerView.clear();
                    clearGlyphMakers();
                    hideGlyph();
                    showTrigger();
                    break;

                case R.id.btn_trigger:
                    tracker.setScreenName("trigger");
                    tracker.send(new HitBuilders.AppViewBuilder().build());

                    hideTrigger();
                    showGlyph(false);
                    showActionBar();
                    break;

                case R.id.btn_exit:
                    tracker.setScreenName("exit");
                    tracker.send(new HitBuilders.AppViewBuilder().build());

                    hideGlyph();
                    hideTrigger();
                    stopSelf();
                    break;

                default:
                    break;
            }
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("TCX", "down: " + event.getX() + ", " + event.getY());
                    break;

                case MotionEvent.ACTION_MOVE:
                    mTriggerX = (int)(event.getX());
                    mTriggerY = (int)(event.getY());
                    Log.d("TCX", "x: " + mTriggerX + " y: " + mTriggerY + " count:" + event.getPointerCount());
                    updateTriggerPosition();
                    break;

                case MotionEvent.ACTION_UP:
                    Log.d("TCX", "up: " + event.getX() + ", " + event.getY());
                    break;

                default:
                    break;
            }
            return false;
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mManager == null) {
            mManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            showTrigger();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }

    private void showActionBar() {
        if (mActionBar.getVisibility() != View.VISIBLE) {
            mActionBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideActionBar() {
        if (mActionBar.getVisibility() != View.INVISIBLE) {
            mActionBar.setVisibility(View.INVISIBLE);
        }
    }

    private void updateTriggerPosition() {
        WindowManager.LayoutParams params = getTriggerParams();
        params.x = mTriggerX;
        params.y = mTriggerY;
        mManager.updateViewLayout(mTriggerView, params);
    }

    private void clearGlyphMakers() {
        mMakerContainerView.removeAllViews();
    }

    private void addTrackerMaker(Path path, Color color) {
        View view = new ImageView(this);
        view.setBackgroundColor(color.rgb());
        int size = getScreenSize().widthPixels / 11;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.leftMargin = size;
        mMakerContainerView.addView(view, params);
    }

    private void showTrigger() {
        if (mTriggerView == null) {
            mTriggerView = View.inflate(this, R.layout.view_trigger, null);
            mTriggerView.setOnClickListener(mOnClickListener);
            // TODO
//            mTriggerView.setOnTouchListener(mOnTouchListener);
        }
        hideTrigger();
        WindowManager.LayoutParams params = getTriggerParams();
        if (mTriggerX < 0 || mTriggerY < 0) {
            mTriggerX = 0;
            mTriggerY = getScreenSize().heightPixels / 2;
        }
        params.x = mTriggerX;
        params.y = mTriggerY;
        mManager.addView(mTriggerView, params);
    }

    private void hideTrigger() {
        if (mTriggerView == null || mTriggerView.getParent() == null) {
            return;
        }
        mManager.removeView(mTriggerView);
    }

    private void showGlyph(boolean isTranslucent) {
        if (mGlyphView == null) {
            mGlyphView = View.inflate(this, R.layout.view_glyph, null);
            mGlyphView.findViewById(R.id.btn_translucent).setOnClickListener(mOnClickListener);
            mGlyphView.findViewById(R.id.btn_close).setOnClickListener(mOnClickListener);
            mGlyphView.findViewById(R.id.btn_exit).setOnClickListener(mOnClickListener);

            mTrackerView = (TouchTrackerView) mGlyphView.findViewById(R.id.view_touch_tracker);
            mTrackerView.setOnTrackListener(mListener);
            mTrackerView.setMaxTrack(MAX_TRACK);
            mTrackerView.setColorRange(COLOR_RANGE);

            mMakerContainerView = (ViewGroup) mGlyphView.findViewById(R.id.layout_glygh_container);
            mMakerContainerView.setMinimumHeight(getScreenSize().widthPixels / 11);

            mActionBar = mGlyphView.findViewById(R.id.layout_action_bar);
        }
        hideGlyph();
        mManager.addView(mGlyphView, getTrackerViewParam(isTranslucent ? WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE));
    }

    private void hideGlyph() {
        if (mGlyphView == null || mGlyphView.getParent() == null) {
            return;
        }
        mManager.removeView(mGlyphView);
    }

    private WindowManager.LayoutParams getTriggerParams() {
        WindowManager.LayoutParams params = getParams(WindowManager.LayoutParams.TYPE_PHONE);
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | params.flags;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        return params;
    }

    private DisplayMetrics getScreenSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        mManager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    private WindowManager.LayoutParams getTrackerViewParam(int type) {
        WindowManager.LayoutParams params = getParams(type);
        params.y = getScreenSize().heightPixels * 1 / 4;
        params.height = getScreenSize().heightPixels * 3 / 4;
        return params;
    }

    private WindowManager.LayoutParams getParams(int type) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        params.type = type;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.LEFT | Gravity.TOP; // TODO change to Gravity.START.
        return params;
    }
}
