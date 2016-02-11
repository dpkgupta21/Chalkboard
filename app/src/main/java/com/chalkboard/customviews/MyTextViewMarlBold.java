package com.chalkboard.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by deepak on 16/11/15.
 */
public class MyTextViewMarlBold extends TextView {

    public MyTextViewMarlBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewMarlBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewMarlBold(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/marlbold.ttf");
        setTypeface(tf);
        setTextSize(16);
    }

}


