package com.alifabdulrahman.malaysiakinireader.storage.substorage;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import com.alifabdulrahman.malaysiakinireader.storage.storage;

public class currentArticle extends storage {
    private final String storageName = "currentArticle";

    public currentArticle(Context context) {
        super(context);
        this.sp = context.getSharedPreferences("currentArticle", MODE_PRIVATE);
        this.editor = sp.edit();
    }

    public void saveReading(String wasReading, int index){
        editor.putString("wasReading", wasReading);
        editor.putInt("lastIndex3", index);

        editor.apply();
    }

    public void saveData(String link) {
        editor.putString("lastURL", link);
        editor.apply();
    }

    public String loadData(){
        return sp.getString("lastURL", "");
    }


    public String loadLastArticle(){return "no";}


    public String loadNewsType(){
        return sp.getString("lastNewsType", "");
    }

    public String loadNewsSectionURL(){
        return sp.getString("lastURL", "");
    }

}
