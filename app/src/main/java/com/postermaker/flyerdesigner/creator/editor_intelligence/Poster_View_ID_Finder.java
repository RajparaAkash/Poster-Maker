package com.postermaker.flyerdesigner.creator.editor_intelligence;

import android.annotation.SuppressLint;
import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

public class Poster_View_ID_Finder {

    private static final AtomicInteger sNext_VIEW_ID_GeneratedId = new AtomicInteger(1);

    @SuppressLint({"NewApi"})
    public static int generateViewId() {
        return View.generateViewId();
    }
}
