package com.rayse;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by patrickohalloran on 4/16/16.
 */

public class RayseTextView extends TextView {
    private final static String FONTS_ROOT = "fonts/";

    public RayseTextView(Context context) {
        super(context);
        init(context, null);
    }

    public RayseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RayseTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public RayseTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RayseTextView);
        String fontName = a.getString(R.styleable.RayseTextView_fontName);
        if (fontName != null) {
            if (fontName != null) {
                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), FONTS_ROOT + fontName);
                setTypeface(myTypeface);
            }
            a.recycle();
        } else {
            Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), FONTS_ROOT + "Raleway-Medium.ttf");
            setTypeface(myTypeface);
        }
    }

}
