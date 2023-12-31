package com.postermaker.flyerdesigner.creator.imageloader;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import com.postermaker.flyerdesigner.creator.activity.Poster_IntegerVersionSignature;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_AppConstants;
import com.postermaker.flyerdesigner.creator.imageloader.Poster_Progress_Module.UIonProgressModuleListener;

public class Poster_Quick_Glide_IMG_Loader1 {

    private ImageView mImageView;
    private ProgressBar mProgressBar;

    public Poster_Quick_Glide_IMG_Loader1(ImageView imageView, ProgressBar progressBar) {
        this.mImageView = imageView;
        this.mProgressBar = progressBar;
    }

    private void onConnecting() {
        ProgressBar progressBar = mProgressBar;
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void loadImageFromStr(final String str, RequestOptions requestOptions) {
        if (str != null && requestOptions != null) {
            onConnecting();
            Poster_Progress_Module.expect(str, new UIonProgressModuleListener() {
                public float getGranualityPercentage() {
                    return 1.0f;
                }

                public void onProgress(long j, long j2) {
                    if (mProgressBar != null) {
                        mProgressBar.setProgress((int) ((j * 100) / j2));
                    }
                }
            });

            Glide.with(mImageView.getContext()).load(str).transition(DrawableTransitionOptions.withCrossFade())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .signature(new Poster_IntegerVersionSignature(Poster_AppConstants.getAPPVersionInfo())))
                    .listener(new RequestListener<Drawable>() {
                        public boolean onLoadFailed(@Nullable GlideException glideException, Object obj, Target<Drawable> target, boolean z) {
                            Poster_Progress_Module.forget(str);
                            Poster_Quick_Glide_IMG_Loader1.this.onFinished();
                            return false;
                        }

                        public boolean onResourceReady(Drawable drawable, Object obj, Target<Drawable> target, DataSource dataSource, boolean z) {
                            Poster_Progress_Module.forget(str);
                            Poster_Quick_Glide_IMG_Loader1.this.onFinished();
                            return false;
                        }
                    }).into(mImageView);
        }
    }


    private void onFinished() {
        ProgressBar progressBar = this.mProgressBar;
        if (progressBar != null && this.mImageView != null) {
            progressBar.setVisibility(View.GONE);
            this.mImageView.setVisibility(View.VISIBLE);
        }
    }
}
