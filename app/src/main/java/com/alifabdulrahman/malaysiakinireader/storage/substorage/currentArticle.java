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

    public void saveReading(boolean wasReading){
        editor.putBoolean("wasReading", wasReading);
        editor.apply();
    }

    public void saveIndex(int index){
        editor.putInt("index", index);
        editor.apply();
    }

    public int loadIndex(){
        return sp.getInt("index", 0);
    }

    public boolean loadReading(){
        return sp.getBoolean("wasReading", false);
    }

    public void saveData(String link) {
        editor.putString("lastURL", link);
        editor.apply();
    }

    public String loadData(){
        return sp.getString("lastURL", "");
    }

    public String loadNewsType(){
        return sp.getString("lastNewsType", "");
    }

    public String loadNewsSectionURL(){
        return sp.getString("lastURL", "");
    }

    public void saveNewsSectionURL(String newsSection){
        editor.putString("lastURL", newsSection);
        editor.apply();
    }

    public String loadLastArticle(){
            return sp.getString("lastURL", "");
    }


    public boolean startTSS(){return sp.getBoolean("startTSS", false);}

    public void setTTS(boolean tss){editor.putBoolean("startTSS", tss);}
}
