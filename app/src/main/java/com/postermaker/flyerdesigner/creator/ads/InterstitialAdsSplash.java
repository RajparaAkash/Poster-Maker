package com.postermaker.flyerdesigner.creator.ads;

import android.app.Activity;

public class InterstitialAdsSplash {

    public static AdCloseListener adCloseListener;

    public void Show_Ads(Activity source_class, AdCloseListener adCloseListener) {
        MyDataSaved preference = new MyDataSaved(source_class);
        if (!preference.get_click_flag().equalsIgnoreCase("on")) {
            if (Constant.IS_TIME_INTERVAL) {
                InterstitialAdsSplashGoogle.ShowAd_Full(source_class, adCloseListener);
            } else {
                if (adCloseListener != null) {
                    adCloseListener.onAdClosed();
                }
            }
        } else {
            if (Constant.Front_Counter % Integer.parseInt(preference.get_ad_click()) == 0) {
                InterstitialAdsSplashGoogle.ShowAd_Full(source_class, adCloseListener);
            } else {
                Constant.Front_Counter++;
                if (adCloseListener != null) {
                    adCloseListener.onAdClosed();
                }
            }
        }
    }

    public interface AdCloseListener {
        void onAdClosed();
    }

}
