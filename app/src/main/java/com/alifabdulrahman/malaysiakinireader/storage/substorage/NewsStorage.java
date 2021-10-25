package com.alifabdulrahman.malaysiakinireader.storage.substorage;

import android.content.Context;
import android.content.SharedPreferences;

import com.alifabdulrahman.malaysiakinireader.model.ArticleData;
import com.alifabdulrahman.malaysiakinireader.storage.storage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.alifabdulrahman.malaysiakinireader.model.sorting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class NewsStorage extends storage {
    private final String storageName = "NewsStorage";
    private ArrayList<ArticleData>articleDatas;
    private ArrayList<ArticleData>articleDatas2;
    private String newsType;
    private String newsType2 = "a";
    private settings settings = new settings(context);
    private newsSectionStorage newsSectionStorage = new newsSectionStorage(context);
    private boolean orderLatest;
    private sorting sorting;

    public NewsStorage(Context context, String newsType) {
        super(context);
        this.sp = context.getSharedPreferences(storageName, Context.MODE_PRIVATE);
        this.editor = sp.edit();
        this.newsType = newsType;
    }

    public boolean isOrderLatest() {
        return orderLatest;
    }

    //Load data of articles
    public ArrayList<ArticleData> loadData() {
        Gson gson = new Gson();
        Gson xson = new Gson();
        String json = sp.getString(newsType, null);
        String yson = sp.getString(newsType2, null);
        Type dataType = new TypeToken<ArrayList<ArticleData>>() {
        }.getType();
        articleDatas = gson.fromJson(json, dataType);
        articleDatas2 = xson.fromJson(yson, dataType);
        //articleDatas = articleDatas2;
        //System.out.println("dataloaded");

        orderLatest = settings.loadSettings(newsType);

        if (!orderLatest && (articleDatas != null || (!Objects.requireNonNull(articleDatas).isEmpty()))) {
            Collections.sort(articleDatas, new Comparator<ArticleData>() {
                @Override
                public int compare(ArticleData o1, ArticleData o2) {
                    return o1.getPublishDate().compareTo(o2.getPublishDate());
                }
            });
        }

        if (articleDatas == null) {
            articleDatas = new ArrayList<>();
        }

        return articleDatas;
    }

    //Save data of articles retrieved
    public void saveData(ArrayList<ArticleData>articleDatas) {
        //SharedPreferences xp = getSharedPreferences("ReadNews", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //SharedPreferences.Editor editor2 = xp.edit();
        Gson gson = new Gson();
        Gson xson = new Gson();
        String json;
        String yson;

        //save in latest order
        ArrayList<ArticleData> toSaveInOrder = new ArrayList<>(articleDatas);
        ArrayList<ArticleData> toSaveInOrder2;
        if (articleDatas2 != null) {
            toSaveInOrder2 = new ArrayList<>(articleDatas2);
            yson = xson.toJson(toSaveInOrder2);
            editor.putString(newsType2, yson);
            editor.apply();
        }

        sorting = new sorting(context);
        toSaveInOrder = sorting.sortByLatest(toSaveInOrder);
        json = gson.toJson(toSaveInOrder);
        editor.putString(newsType, json);

        editor.apply();
    }

    /*
    public ArrayList<ArticleData>loadArt1(){
        return articleDatas;
    }

     */

    public ArrayList<ArticleData>loadArt2(){
        return articleDatas2;
    }
}
