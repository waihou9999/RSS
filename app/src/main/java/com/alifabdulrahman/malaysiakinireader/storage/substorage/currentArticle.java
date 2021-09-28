package com.alifabdulrahman.malaysiakinireader.storage.substorage;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.alifabdulrahman.malaysiakinireader.Activity.Enter.ArticleList.ArticleListingActivity;
import com.alifabdulrahman.malaysiakinireader.Activity.Enter.ArticleView.ArticleViewActivity;
import com.alifabdulrahman.malaysiakinireader.Activity.Enter.NewsSectionActivity;
import com.alifabdulrahman.malaysiakinireader.Activity.MainActivity.sectionManager;
import com.alifabdulrahman.malaysiakinireader.model.NewsSectionData;
import com.alifabdulrahman.malaysiakinireader.storage.storage;

import java.util.ArrayList;

public class currentArticle extends storage {
    private final String storageName = "currentArticle";

    public currentArticle(Context context) {
        super(context);
        this.sp = context.getSharedPreferences("currentArticle", MODE_PRIVATE);
        this.editor = sp.edit();
    }

    @Override
    public void saveData() {

    }


    public String loadLastArticle(){return "no";}


    public String loadNewsType(){
        return sp.getString("lastNewsType", "");
    }

    public String loadNewsSectionURL(){
        return sp.getString("lastURL", "");
    }

}
