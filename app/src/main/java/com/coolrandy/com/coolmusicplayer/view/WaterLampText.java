package com.coolrandy.com.coolmusicplayer.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by admin on 2016/1/26.
 */
public class WaterLampText extends TextView {

    public WaterLampText(Context context) {
        super(context);
    }

    public WaterLampText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaterLampText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
}
