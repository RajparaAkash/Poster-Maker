package com.postermaker.flyerdesigner.creator.poster_builder;

import java.util.ArrayList;

public class Poster_Main_BG_Image {

     ArrayList<Poster_BG_Image> category_list;

     String category_name;
     int category_id;

    public Poster_Main_BG_Image(int i, String str, ArrayList<Poster_BG_Image> arrayList) {
        this.category_id = i;
        this.category_name = str;
        this.category_list = arrayList;
    }

    public int getCategory_id() {
        return this.category_id;
    }

    public void setCategory_id(int i) {
        this.category_id = i;
    }

    public String getCategory_name() {
        return this.category_name;
    }

    public void setCategory_name(String str) {
        this.category_name = str;
    }

    public ArrayList<Poster_BG_Image> getCategory_list() {
        return this.category_list;
    }

    public void setCategory_list(ArrayList<Poster_BG_Image> arrayList) {
        this.category_list = arrayList;
    }
}
