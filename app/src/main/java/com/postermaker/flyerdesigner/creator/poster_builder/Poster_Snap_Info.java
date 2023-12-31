package com.postermaker.flyerdesigner.creator.poster_builder;

import java.util.ArrayList;

public class Poster_Snap_Info {

    ArrayList<Poster_BG_Image> BG_Images;

    int mGravity;
    String mText;

    public Poster_Snap_Info(int i, String str, ArrayList<Poster_BG_Image> arrayList) {
        this.mGravity = i;
        this.mText = str;
        this.BG_Images = arrayList;
    }

    public String getText() {
        return this.mText;
    }

    public int getGravity() {
        return this.mGravity;
    }

    public ArrayList<Poster_BG_Image> getBG_Images() {
        return this.BG_Images;
    }
}
