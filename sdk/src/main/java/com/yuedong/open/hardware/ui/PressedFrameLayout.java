package com.yuedong.open.hardware.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by virl on 15/6/15.
 */
public class PressedFrameLayout extends FrameLayout {
    public PressedFrameLayout(Context context) {
        super(context);
    }
    public PressedFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public PressedFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PressedFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        dispatchSetPressed(pressed);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        int count = getChildCount();
        for(int i=0; i!=count; ++i) {
            getChildAt(i).setEnabled(enabled);
        }
    }
}
