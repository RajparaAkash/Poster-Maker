package com.postermaker.flyerdesigner.creator.apiclass;

import com.postermaker.flyerdesigner.creator.poster_builder.Poster_BG_Poster;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Key_Poster;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_List_Poster_Category;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Datas;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Thumbnail;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_With_List;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Thumb_BG;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Poster_APIInterface {
    @FormUrlEncoded
    @POST("poster/background")
    Call<Poster_BG_Poster> getBackground(@Field("device") int i);

    @FormUrlEncoded
    @POST("poster/backgroundlatest")
    Call<Poster_Thumb_BG> getBackgrounds(@Field("device") int i);

    @FormUrlEncoded
    @POST("poster/category")
    Call<Poster_List_Poster_Category> getPosterCatList(@Field("key") String str, @Field("device") int i);

    @FormUrlEncoded
    @POST("poster/swiperCat")
    Call<Poster_With_List> getPosterCatListFull(@Field("key") String str, @Field("device") int i, @Field("cat_id") int i2, @Field("ratio") String str2);

    @FormUrlEncoded
    @POST("poster/poster")
    Call<Poster_Datas> getPosterDetails(@Field("key") String str, @Field("device") int i, @Field("cat_id") int i2, @Field("post_id") int i3);

    @FormUrlEncoded
    @POST("apps_key")
    Call<Poster_Key_Poster> getPosterKey(@Field("device") int i);

    @FormUrlEncoded
    @POST("poster/poster")
    Call<Poster_Thumbnail> getPosterThumbList(@Field("key") String str, @Field("device") int i, @Field("cat_id") int i2);

    @FormUrlEncoded
    @POST("poster/stickerlatest")
    Call<Poster_Thumb_BG> getSticker(@Field("device") int i);
}
