package com.postermaker.flyerdesigner.creator.apiclass;

import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_AppConstants;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class Poster_ApiClientFeedBack {
    public static final String BASE_URL = Poster_AppConstants.BASE_URL_BG;
    private static Retrofit retrofit1 = null;

    public static Retrofit getClient() {
        if (retrofit1 == null) {
            retrofit1 = new Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit1;
    }
}
