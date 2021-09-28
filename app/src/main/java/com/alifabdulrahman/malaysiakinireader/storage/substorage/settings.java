package com.alifabdulrahman.malaysiakinireader.storage.substorage;

import android.content.Context;

import com.alifabdulrahman.malaysiakinireader.storage.storage;

public class settings extends storage {

    private final String storageName = "settings";

    public settings(Context context) {
        super(context);
        this.sp = context.getSharedPreferences(storageName, Context.MODE_PRIVATE);
        this.editor = sp.edit();
    }

    //Save the user's order settings
    private void saveSettings(String newsType, boolean orderLatest){
        editor.putBoolean("order" + newsType, orderLatest);
        editor.apply();
    }

    //Load the user's order settings
    public boolean loadSettings(String newsType){
        return sp.getBoolean("order" + newsType, true);
    }


}
