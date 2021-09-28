package com.alifabdulrahman.malaysiakinireader.storage.substorage;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.alifabdulrahman.malaysiakinireader.storage.storage;

public class settings extends storage {

    private final String storageName = "settings";

    public settings(Context context) {
        super(context);
        this.sp = context.getSharedPreferences(storageName, Context.MODE_PRIVATE);
        this.editor = sp.edit();
    }

    //Save the user's order settings
    public void saveSettings(String newsType, boolean orderLatest){
        editor.putBoolean("order" + newsType, orderLatest);
        editor.apply();
    }

    //Load the user's order settings
    public boolean loadSettings(String newsType){
        return sp.getBoolean("order" + newsType, true);
    }

    public boolean checkFirstRun(AlertDialog.Builder startUp) {
        SharedPreferences sp = context.getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        boolean firstRun = context.getSharedPreferences("settings", MODE_PRIVATE).getBoolean("firstRun", true);
        return firstRun;
    }

    public void editFirstRun() {
        SharedPreferences sp = context.getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("firstRun", false);
        editor.apply();
    }

}
