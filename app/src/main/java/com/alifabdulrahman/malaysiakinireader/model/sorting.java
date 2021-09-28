package com.alifabdulrahman.malaysiakinireader.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class sorting {
    private Context context;
    ArrayList<ArticleData>articleData;

    public  sorting(Context context){
        this.context = context;
    }

    //Sort by oldest by comparing the Date object in ArticleData
    public ArrayList<ArticleData> sortByOldest(ArrayList<ArticleData> toSort){
        Collections.sort(toSort, new Comparator<ArticleData>() {
            @Override
            public int compare(ArticleData o1, ArticleData o2) {
                return o1.getPublishDate().compareTo(o2.getPublishDate());
            }
        });

        return toSort;
    }

    //Sort by latest by comparing the Date object in ArticleData
    public ArrayList<ArticleData> sortByLatest(ArrayList<ArticleData> toSort){
        toSort = sortByOldest(toSort);
        Collections.reverse(toSort);

        return toSort;
    }
}
