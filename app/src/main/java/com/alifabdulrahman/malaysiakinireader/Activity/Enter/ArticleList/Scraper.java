package com.alifabdulrahman.malaysiakinireader.Activity.Enter.ArticleList;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.alifabdulrahman.malaysiakinireader.model.ArticleData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Scraper extends AsyncTask<String, Void, ArrayList<ArticleData>> {
    private ArrayList<ArticleData> articleDatas = new ArrayList<>(); // current
    private ArrayList<ArticleData> articleDatas2 = new ArrayList<>(); // stores all read articles, only clears when reset
    private String newsSectionURL;
    //Create progress dialog when getting contents and prevent user from doing anything else.
    private ProgressDialog dialog;

    public Scraper(ArticleListingActivity thisActivity){
        dialog = new ProgressDialog(thisActivity);
        dialog.setCancelable(true);
    }

    @Override
    public ArrayList<ArticleData> doInBackground(String... strings) {
        ArrayList<ArticleData> newArticles = new ArrayList<>();

        //Scrap the titles, links and dates from url.
        try {
            Document doc = Jsoup.connect(newsSectionURL).get();
            Elements items = doc.select("item");
            Elements titles = items.select("title");
            Elements links = items.select("link");

            //Add link to the articleData
            for(Element link : links){
                newArticles.add(new ArticleData(link.text()));
            }

            //Add title to the articleData
            int i = 0;
            for (Element title : titles) {
                newArticles.get(i).setTitle(title.text());
                i++;
            }


        } catch (Exception e) {
            Log.d("Error: ", e.getMessage());
        }
        return newArticles;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Getting contents. Please wait...");
        dialog.show();
    }
}
