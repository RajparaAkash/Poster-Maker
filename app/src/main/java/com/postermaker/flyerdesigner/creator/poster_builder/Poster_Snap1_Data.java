package com.postermaker.flyerdesigner.creator.poster_builder;

import java.util.ArrayList;

public class Poster_Snap1_Data {
     int cat_id;
     int mGravity;
     String mText;
     ArrayList<Poster_Full_Poster_Thumb> fullPosterThumbs;
     String ratio;

    public String getRatio() {
        return this.ratio;
    }

    public void setRatio(String str) {
        this.ratio = str;
    }

    public Poster_Snap1_Data(int i, String str, ArrayList<Poster_Full_Poster_Thumb> arrayList, int i2, String str2) {
        this.mGravity = i;
        this.mText = str;
        this.fullPosterThumbs = arrayList;
        this.cat_id = i2;
        this.ratio = str2;
    }

    public String getText() {
        return this.mText;
    }

    public int getGravity() {
        return this.mGravity;
    }

    public ArrayList<Poster_Full_Poster_Thumb> getFullPosterThumbs() {
        return this.fullPosterThumbs;
    }

    public int getCat_id() {
        return this.cat_id;
    }
}
