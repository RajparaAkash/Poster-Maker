package com.postermaker.flyerdesigner.creator.listener;

import android.net.Uri;

import java.util.ArrayList;

import com.postermaker.flyerdesigner.creator.poster_builder.Poster_BG_Image;

public interface Poster_On_Data_Snap_Listener {
    void onSnapFilter(int i, int i2, String str, String str2);

    void onSnapFilter(Uri uri, int i, int i2, int i3, int i4, boolean z);

    void onSnapFilter(ArrayList<Poster_BG_Image> arrayList, int i, String str);
}
