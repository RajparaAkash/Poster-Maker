package com.postermaker.flyerdesigner.creator.ads;

import com.google.android.gms.ads.MobileAds;
import com.postermaker.flyerdesigner.creator.Poster_Application;

public class MyApplication extends Poster_Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(
                this,
                initializationStatus -> {
                });
        new AppOpenManager(this);

    }
}
