package com.chalkboard.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by deepak on 16/11/15.
 */
public class MyTextViewMark extends TextView {

    public MyTextViewMark(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewMark(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewMark(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/mark.ttf");
        setTypeface(tf);
    }

}


