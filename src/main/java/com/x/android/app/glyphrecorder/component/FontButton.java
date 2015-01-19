package com.x.android.app.glyphrecorder.component;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

import com.xi47.common.android.util.DeviceUtil;

/**
 * Created by X on 14/11/29.
 */
public class FontButton extends Button {

    private static final int SCALE_RATE_TEXT_SIZE = 13;

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/coda.ttf");
        setTypeface(font);
        DeviceUtil.getInstance().init(context);
        Point screenSize = DeviceUtil.getInstance().getDisPlayPoint();
        int miniSize = screenSize.x > screenSize.y ? screenSize.y : screenSize.x;
        setTextSize(TypedValue.COMPLEX_UNIT_PX, miniSize / SCALE_RATE_TEXT_SIZE);
    }
}
