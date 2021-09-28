package com.alifabdulrahman.malaysiakinireader.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.alifabdulrahman.malaysiakinireader.Activity.MainActivity.sectionManager;
import com.alifabdulrahman.malaysiakinireader.model.NewsSectionData;

import java.util.ArrayList;

public abstract class storage {
    protected String storageName;
    protected SharedPreferences sp;
    protected SharedPreferences.Editor editor;
    protected Context context;
    protected sectionManager sectionManager;

    public storage(Context context){
        this.context = context;
        sectionManager = new sectionManager(context);
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public void saveData() {

    }
}
