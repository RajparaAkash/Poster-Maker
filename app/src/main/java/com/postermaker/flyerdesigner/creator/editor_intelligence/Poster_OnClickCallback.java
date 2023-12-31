package com.postermaker.flyerdesigner.creator.editor_intelligence;

import android.app.Activity;

import java.util.ArrayList;

import com.postermaker.flyerdesigner.creator.poster_builder.Poster_BG_Image;

public interface Poster_OnClickCallback<T, P, A, V, Q> {
    void onClickCallBack(ArrayList<String> arrayList, ArrayList<Poster_BG_Image> arrayList2, String str, Activity activity, String str2);
}
