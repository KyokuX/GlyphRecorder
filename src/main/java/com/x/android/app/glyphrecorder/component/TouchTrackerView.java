package com.x.android.app.glyphrecorder.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.x.android.app.glyphrecorder.bean.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KyokuX on 14/11/15.
 */
public class TouchTrackerView extends View {

    private int mTrackCount = 0;
    private int mMaxTrack = 0;
    private List<Color> mUsedColors = null;
    private int[] mColors = null;

    private Paint mPaint = null;
    private Path mCurrentPath = null;
    private Bitmap mBitmap = null;

    private OnTrackerListener mListener = null;

    public TouchTrackerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(10);// TODO Move this to settings.
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setDither(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        mUsedColors = new ArrayList<Color>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        } else {
            canvas.drawColor(android.graphics.Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mMaxTrack > 0 && mTrackCount >= mMaxTrack) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPaintColor();
                mCurrentPath = new Path();
                mCurrentPath.moveTo(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                mCurrentPath.lineTo(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                mTrackCount++;
                if (mListener != null && mUsedColors != null && mUsedColors.size() > 0) {
                    mListener.onTrackEnd(mCurrentPath, mUsedColors.get(mUsedColors.size() - 1));
                }
                break;

            default:
                break;
        }
        recordBitmap();
        invalidate();
        return true;
    }

    public void setColorRange(int[] colors) {
        mColors = colors;
    }

    public void setOnTrackListener(OnTrackerListener listener) {
        mListener = listener;
    }

    public void clear() {
        mBitmap = null;
        mTrackCount = 0;
        invalidate();
    }

    public void setMaxTrack(int maxTrack) {
        mMaxTrack = maxTrack;
    }

    private void setColorRandom() {
        Color color = getNewColor();
        mPaint.setARGB(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
    }

    private boolean setColorByColor() {
        if (mColors == null || mColors.length < 1) {
            return false;
        }
        int index = mTrackCount % mColors.length;
        Color color = new Color(mColors[index]);
        mPaint.setARGB(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
        mUsedColors.add(new Color(mColors[index]));
        return true;
    }

    private void setPaintColor() {
        boolean success = setColorByColor();
        if (!success) {
            setColorRandom();
        }
    }

    private Color getNewColor() {
        Color color = Color.randomWithoutAlpha();
        if (mUsedColors.size() < 1) {
            mUsedColors.add(color);
            return color;
        }
        boolean isNewColor = false;
        while (!isNewColor) {
            for (Color c : mUsedColors) {
                if (c.equals(color)) {
                    isNewColor = false;
                    color = Color.randomWithoutAlpha();
                    break;
                } else {
                    isNewColor = true;
                }
            }
        }
        mUsedColors.add(color);
        return color;
    }

    private void recordBitmap() {
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(mBitmap);
        canvas.drawPath(mCurrentPath, mPaint);
    }

    public interface OnTrackerListener {

        public void onTrackEnd(Path path, Color color);
    }
}
