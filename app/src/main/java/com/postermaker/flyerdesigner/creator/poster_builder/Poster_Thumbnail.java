package com.postermaker.flyerdesigner.creator.poster_builder;

import java.util.ArrayList;

public class Poster_Thumbnail {

    ArrayList<Poster_List_Poster_Thumb> data;

    String message, error;

    public String getPOSTERMessage() {
        return this.message;
    }

    public void setPOSTERMessage(String str) {
        this.message = str;
    }

    public String getPOSTERError() {
        return this.error;
    }

    public void setPOSTERError(String str) {
        this.error = str;
    }

    public ArrayList<Poster_List_Poster_Thumb> getPOSTERData() {
        return this.data;
    }

    public void setPOSTERData(ArrayList<Poster_List_Poster_Thumb> arrayList) {
        this.data = arrayList;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ClassPojo [message = ");
        stringBuilder.append(this.message);
        stringBuilder.append(", error = ");
        stringBuilder.append(this.error);
        stringBuilder.append(", data = ");
        stringBuilder.append(this.data);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
