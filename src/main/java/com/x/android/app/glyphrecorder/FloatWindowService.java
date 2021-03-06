package com.x.android.app.glyphrecorder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.x.android.app.glyphrecorder.bean.Color;
import com.x.android.app.glyphrecorder.component.TouchTrackerView;
import com.x.android.app.glyphrecorder.util.Constants;
import com.xi47.common.android.content.PreferencesUtil;

/**
 * Created by KyokuX on 14/11/15.
 */
public class FloatWindowService extends Service {

    private static final int MAX_TRACK = 5;

    private View mGlyphView = null;
    private View mTriggerView = null;
    private ViewGroup mMakerContainerView = null;
    private WindowManager mManager = null;
    private View mActionBar = null;
    private View mSettingView = null;

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
                    hideButtons();
                    break;

                case R.id.btn_close:
                    tracker.setScreenName("close");
                    tracker.send(new HitBuilders.AppViewBuilder().build());

                    mTrackerView.clear();
                    clearGlyphMakers();
                    hideGlyph();
                    showTrigger();
                    break;

                case R.id.btn_exit:
                    tracker.setScreenName("exit");
                    tracker.send(new HitBuilders.AppViewBuilder().build());

                    exit();
                    break;

                case R.id.btn_settings:
                    tracker.setScreenName("setting");
                    tracker.send(new HitBuilders.AppViewBuilder().build());

                    exit();
                    PreferencesUtil.getInstance().put(Constants.PREFERENCE_NAME, Constants.PREFERENCE_SETTINGS_SHOWN, false);
                    Intent intent = new Intent(FloatWindowService.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;

                default:
                    break;
            }
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mGestureDetector.onTouchEvent(event);
        }
    };

    private GestureDetector mGestureDetector = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mManager == null) {
            mManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

            mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

                float mLastX = -1f;
                float mLastY = -1f;

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    updateTriggerPosition((int)e2.getRawX(), (int)e2.getRawY());
                    return super.onScroll(e1, e2, distanceX, distanceY);
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float xDistance = e2.getX() - e1.getX();
                    float yDistance = e2.getY() - e1.getY();
                    WindowManager.LayoutParams params = createTriggerParams();
                    if (Math.abs(yDistance) > Math.abs(xDistance)) {
                        if (Math.abs(velocityY) > ViewConfiguration.get(FloatWindowService.this).getScaledMaximumFlingVelocity() / 4) {
                            updateTriggerPosition(params.x, yDistance > 0 ? getScreenSize().heightPixels : 0);
                        }
                    } else {
                        if (Math.abs(velocityX) > ViewConfiguration.get(FloatWindowService.this).getScaledMaximumFlingVelocity() / 4) {
                            updateTriggerPosition(xDistance > 0 ? getScreenSize().widthPixels : 0, params.y);
                        }
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    Tracker tracker = ((AnalyticsApplication) getApplication()).getTracker();
                    tracker.setScreenName("trigger");
                    tracker.send(new HitBuilders.AppViewBuilder().build());

                    hideTrigger();
                    showGlyph(false);
                    showButtons();
                    return super.onSingleTapConfirmed(e);
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    mLastX = e.getX();
                    mLastY = e.getY();
                    return super.onDown(e);
                }
            });

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

    private void exit() {
        hideGlyph();
        hideTrigger();
        stopSelf();
    }

    private void showButtons() {
        // Avoid IllegalStateException on some devices.
        // has already been added to the window manager.
        try {
            if (mActionBar.getVisibility() != View.VISIBLE) {
                mActionBar.setVisibility(View.VISIBLE);
                mSettingView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideButtons() {
        if (mActionBar.getVisibility() != View.INVISIBLE) {
            mActionBar.setVisibility(View.INVISIBLE);
            mSettingView.setVisibility(View.INVISIBLE);
        }
    }

    private void updateTriggerPosition(int x, int y) {
        WindowManager.LayoutParams params = createTriggerParams();
        params.x = x;
        params.y = y;
        PreferencesUtil.getInstance().put(Constants.PREFERENCE_NAME, Constants.PREFERENCE_TRIGGER_X, x);
        PreferencesUtil.getInstance().put(Constants.PREFERENCE_NAME, Constants.PREFERENCE_TRIGGER_Y, y);
        mManager.updateViewLayout(mTriggerView, params);
    }

    private void clearGlyphMakers() {
        mMakerContainerView.removeAllViews();
    }

    private void addTrackerMaker(Path path, Color color) {
        View view = new ImageView(this);
        view.setBackgroundColor(color.argb());
        int size = mMakerContainerView.getWidth() / 11;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.leftMargin = size;
        mMakerContainerView.addView(view, params);
    }

    private void showTrigger() {
        if (mTriggerView == null) {
            mTriggerView = View.inflate(this, R.layout.view_trigger, null);
            mTriggerView.setOnTouchListener(mOnTouchListener);
        }
        hideTrigger();
        mManager.addView(mTriggerView, createTriggerParams());
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
            mSettingView = mGlyphView.findViewById(R.id.btn_settings);
            mSettingView.setOnClickListener(mOnClickListener);

            mTrackerView = (TouchTrackerView) mGlyphView.findViewById(R.id.view_touch_tracker);
            mTrackerView.setOnTrackListener(mListener);
            mTrackerView.setMaxTrack(MAX_TRACK);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            int[] colors = new int[]{preferences.getInt("color_line_1st", Color.randomWithoutAlpha().rgb()), preferences.getInt("color_line_2nd", Color.randomWithoutAlpha().rgb()), preferences.getInt("color_line_3rd", Color.randomWithoutAlpha().rgb()), preferences.getInt("color_line_4th", Color.randomWithoutAlpha().rgb()), preferences.getInt("color_line_5th", Color.randomWithoutAlpha().rgb())};
            mTrackerView.setColorRange(colors);

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

    private WindowManager.LayoutParams createTriggerParams() {
        WindowManager.LayoutParams params = createParams(WindowManager.LayoutParams.TYPE_PHONE);
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | params.flags;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.x = PreferencesUtil.getInstance().get(Constants.PREFERENCE_NAME, Constants.PREFERENCE_TRIGGER_X, 0);
        params.y = PreferencesUtil.getInstance().get(Constants.PREFERENCE_NAME, Constants.PREFERENCE_TRIGGER_Y, getScreenSize().heightPixels / 2);
        return params;
    }

    private DisplayMetrics getScreenSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        mManager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    private WindowManager.LayoutParams getTrackerViewParam(int type) {
        WindowManager.LayoutParams params = createParams(type);
        params.y = getScreenSize().heightPixels * 1 / 4;
        params.height = getScreenSize().heightPixels * 3 / 4;
        return params;
    }

    private WindowManager.LayoutParams createParams(int type) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        params.type = type;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.START | Gravity.TOP;
        return params;
    }
}
