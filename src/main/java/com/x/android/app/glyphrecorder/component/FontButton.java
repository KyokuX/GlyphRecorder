package com.x.android.app.glyphrecorder.component;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by X on 14/11/29.
 */
public class FontButton extends Button {

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/coda.ttf");
        setTypeface(font);
    }
}
