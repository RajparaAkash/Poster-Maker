package com.postermaker.flyerdesigner.creator.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class Poster_TV_Customized extends TextView {
    public Poster_TV_Customized(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public Poster_TV_Customized(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        applyCustomFont(context);
    }

    public Poster_TV_Customized(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        setTypeface(Poster_GetFontTypeFace.getFontTypeface("font/Montserrat-Medium.ttf", context));
    }
}
