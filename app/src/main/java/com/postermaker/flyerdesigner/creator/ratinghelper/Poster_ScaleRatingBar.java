package com.postermaker.flyerdesigner.creator.ratinghelper;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;

import com.postermaker.flyerdesigner.creator.R;


public class Poster_ScaleRatingBar extends Poster_BaseRatingBar {

    private static Handler sUiHandler = new Handler();

    public Poster_ScaleRatingBar(Context context) {
        super(context);
    }

    public Poster_ScaleRatingBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Poster_ScaleRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void emptyRatingBar() {
        // Need to remove all previous runnable to prevent emptyRatingBar and fillRatingBar out of sync
        sUiHandler.removeCallbacksAndMessages(null);

        int delay = 0;
        for (final Poster_PartialView view : mPartialViews) {
            sUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setEmpty();
                }
            }, delay += 5);
        }
    }

    @Override
    protected void fillRatingBar(final float rating) {
        // Need to remove all previous runnable to prevent emptyRatingBar and fillRatingBar out of sync
        sUiHandler.removeCallbacksAndMessages(null);

        int delay = 0;

        for (final Poster_PartialView partialView : mPartialViews) {
            final int ratingViewId = partialView.getId();
            final double maxIntOfRating = Math.ceil(rating);

            if (ratingViewId > maxIntOfRating) {
                partialView.setEmpty();
                continue;
            }

            sUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ratingViewId == maxIntOfRating) {
                        partialView.setPartialFilled(rating);
                    } else {
                        partialView.setFilled();
                    }

                    if (ratingViewId == rating) {
                        Animation scaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.poster_scale_up);
                        Animation scaleDown = AnimationUtils.loadAnimation(getContext(), R.anim.poster_scale_down);
                        partialView.startAnimation(scaleUp);
                        partialView.startAnimation(scaleDown);
                    }

                }
            }, delay += 15);
        }
    }
}

