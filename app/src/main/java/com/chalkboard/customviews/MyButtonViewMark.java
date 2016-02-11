package com.chalkboard.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class MyButtonViewMark extends Button {

    public MyButtonViewMark(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyButtonViewMark(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyButtonViewMark(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/mark.ttf");
        setTypeface(tf);
       
    }

}