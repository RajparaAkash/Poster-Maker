package com.postermaker.flyerdesigner.creator.poster_builder;

public class Poster_Key_Poster {

    private String key;

    public String getKey() {
        return this.key;
    }

    public void setKey(String str) {
        this.key = str;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ClassPojo [key = ");
        stringBuilder.append(this.key);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

}
