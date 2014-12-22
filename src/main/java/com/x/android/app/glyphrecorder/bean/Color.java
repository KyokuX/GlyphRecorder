package com.x.android.app.glyphrecorder.bean;

import java.util.Random;

/**
 * Created by KyokuX on 14/11/15.
 */
public final class Color {

    public static final int BLACK = 0xFF000000;
    public static final int RED = 0xFFFF0000;
    public static final int GREEN = 0xFF00FF00;
    public static final int BLUE = 0xFF0000FF;
    public static final int YELLOW = 0xFFFFFF00;
    public static final int PINK = 0xFFFF00FF;
    public static final int CYAN = 0xFF00FFFF;
    public static final int WHITE = 0xFFFFFFFF;

    private int mAlpha = 0xFF;
    private int mRed = 0x00;
    private int mGreen = 0x00;
    private int mBlue = 0x00;

    public Color(int color) {
        mAlpha = (color & 0xFF000000) >> 24;
        mRed = (color & 0xFF0000) >> 16;
        mGreen = (color & 0xFF00) >> 8;
        mBlue = (color & 0xFF);
    }

    public static final Color randomWithoutAlpha() {
        Random random = new Random();
        return new Color(random.nextInt(0xFF + 1), random.nextInt(0xFF + 1), random.nextInt(0xFF + 1));
    }

    public Color(int red, int green, int blue) {
        setRed(red);
        setGreen(green);
        setBlue(blue);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Color) {
            Color source = (Color) o;
            return source.mRed == mRed && source.mGreen == mGreen && source.mBlue == mBlue;
        }
        return super.equals(o);
    }

    /**
     * @return blue value from 0 to 255.
     */
    public int getBlue() {
        return mBlue;
    }

    public void setBlue(int blue) {
        if (isVaild(blue)) {
            mBlue = blue;
        }
    }

    /**
     * @return green value from 0 to 255.
     */
    public int getGreen() {
        return mGreen;
    }

    public void setGreen(int green) {
        if (isVaild(green)) {
            mGreen = green;
        }
    }

    /**
     * @return red value from 0 to 255.
     */
    public int getRed() {
        return mRed;
    }

    public void setRed(int red) {
        if (isVaild(red)) {
            mRed = red;
        }
    }

    /**
     * @return alpha value from 0 to 255.
     */
    public int getAlpha() {
        return mAlpha;
    }

    public void setAlpha(int alpha) {
        if (isVaild(alpha)) {
            mAlpha = alpha;
        }
    }

    public int rgb() {
        return argb(0xFF);
    }

    public int argb() {
        return argb(mAlpha);
    }

    private int argb(int alpha) {
        return (alpha << 24) | (mRed << 16) | (mGreen << 8) | mBlue;
    }

    private boolean isVaild(int value) {
        return value >= 0x00  && value <= 0xFF;
    }
}
