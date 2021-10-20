package com.alifabdulrahman.malaysiakinireader.storage.substorage;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import com.alifabdulrahman.malaysiakinireader.storage.storage;

public class currentRSS extends storage {
    private final String storageName = "currentRSS";

    public currentRSS(Context context) {
        super(context);
        this.sp = context.getSharedPreferences(storageName, MODE_PRIVATE);
        this.editor = sp.edit();
    }

    public void saveData(String link) {
        editor.putString("allLink", link);
        editor.apply();
    }

    public String loadData(){
        return sp.getString("allLink", "");
    }

    public void clearData(){
        editor.clear();
    }
}
