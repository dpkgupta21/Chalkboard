package com.chalkboard.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class MyEditTextViewMark extends EditText {

    public MyEditTextViewMark(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyEditTextViewMark(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyEditTextViewMark(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/mark.ttf");
        setTypeface(tf);
    }

}
