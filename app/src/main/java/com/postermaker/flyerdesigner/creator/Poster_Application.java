package com.postermaker.flyerdesigner.creator;

import android.content.Context;
import android.os.StrictMode;
import android.os.StrictMode.VmPolicy.Builder;
import android.text.TextUtils;

import androidx.multidex.MultiDexApplication;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_AppConstants;

public class Poster_Application extends MultiDexApplication {

    private static final String TAG = "Application";
    private static Poster_Application mInstance;
    private RequestQueue mRequestQueue;

    public static synchronized Poster_Application getInstance() {
        Poster_Application application;
        synchronized (Poster_Application.class) {
            synchronized (Poster_Application.class) {
                application = mInstance;
            }
        }
        return application;
    }

    public RequestQueue getRequestQueue() {
        if (this.mRequestQueue == null) {
            this.mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return this.mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String str) {
        Object str2 = null;
        if (TextUtils.isEmpty((CharSequence) str2)) {
            str2 = TAG;
        }
        request.setTag(str2);
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(request);
    }

    public <T> void addToRequestQueue(Request<T> request) {
        request.setTag(TAG);
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        getRequestQueue().add(request);
    }

    public void cancelPendingRequests(Object obj) {
        RequestQueue requestQueue = this.mRequestQueue;
        if (requestQueue != null) {
            requestQueue.cancelAll(obj);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        Poster_AppConstants.BASE_URL_POSTER = "https://postermaker.letsappbuilder.com/api/";
        Poster_AppConstants.BASE_URL_STICKER = "https://postermaker.letsappbuilder.com/";
        Poster_AppConstants.BASE_URL_BG = "https://postermaker.letsappbuilder.com/uploads/";
        Poster_AppConstants.BASE_URL = "http://threemartians.com/poster1/Resources/Poster.php";
        Poster_AppConstants.stickerURL = "";
        Poster_AppConstants.fURL = "";
        Poster_AppConstants.bgURL = "";

        StrictMode.setVmPolicy(new Builder().build());
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }
}
