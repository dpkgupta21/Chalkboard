package com.chalkboard.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class MyButtonViewMarlBold extends Button {

    public MyButtonViewMarlBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyButtonViewMarlBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyButtonViewMarlBold(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/marlbold.ttf");
        setTypeface(tf);
       
    }

}