package com.x.android.app.glyphrecorder.component;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;

import com.x.android.app.glyphrecorder.R;
import com.xi47.common.android.util.DeviceUtil;

/**
 * Created by X on 14/11/29.
 */
public class FontButton extends Button {

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/coda.ttf");
        setTypeface(font);
        DeviceUtil.getInstance().init(context);
        Point screenSize = DeviceUtil.getInstance().getDisPlayPoint();
        int miniSize = screenSize.x > screenSize.y ? screenSize.y : screenSize.x;
        int scaleRate = context.getResources().getInteger(R.integer.scale_rate_text_size);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, miniSize / scaleRate);
    }
}
