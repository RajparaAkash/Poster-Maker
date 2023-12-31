package com.postermaker.flyerdesigner.creator.app_utils;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.NonNull;

public class Poster_OwnTypefaceSpan extends MetricAffectingSpan {
    private final Typeface typeface;

    public Poster_OwnTypefaceSpan(Typeface typeface) {
        this.typeface = typeface;
    }

    public void updateDrawState(TextPaint textPaint) {
        apply(textPaint);
    }

    public void updateMeasureState(@NonNull TextPaint textPaint) {
        apply(textPaint);
    }

    private void apply(Paint paint) {
        paint.setFakeBoldText(true);
        paint.setTypeface(this.typeface);
    }
}
