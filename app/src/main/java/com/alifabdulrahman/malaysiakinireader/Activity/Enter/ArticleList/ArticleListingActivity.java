package com.alifabdulrahman.malaysiakinireader.Activity.Enter.ArticleList;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alifabdulrahman.malaysiakinireader.Activity.Enter.ArticleView.ArticleViewActivity;
import com.alifabdulrahman.malaysiakinireader.Activity.Enter.NewsSectionActivity;
import com.alifabdulrahman.malaysiakinireader.R;
import com.alifabdulrahman.malaysiakinireader.adapter.ArticleListAdapter;
import com.alifabdulrahman.malaysiakinireader.model.ArticleData;
import com.alifabdulrahman.malaysiakinireader.model.sorting;
import com.alifabdulrahman.malaysiakinireader.storage.substorage.NewsStorage;
import com.alifabdulrahman.malaysiakinireader.storage.substorage.currentArticle;
import com.alifabdulrahman.malaysiakinireader.storage.substorage.newsSectionStorage;
import com.alifabdulrahman.malaysiakinireader.storage.substorage.settings;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class ArticleListingActivity extends AppCompatActivity implements Serializable {

    private ArrayList<ArticleData> articleDatas = new ArrayList<>(); // current
    //private ArrayList<ArticleData> articleDatas2; // new/temp
    private ArrayList<ArticleData> articleDatas2 = new ArrayList<>(); // stores all read articles, only clears when reset
    private boolean orderLatest;
    private ListView listView;
    private String newsSectionURL, newsType;
    private ArticleListAdapter articleListAdapter;
    private SwipeRefreshLayout pullToRefresh;
    private boolean newContentAvailable;
    private String wasReading;
    private newsSectionStorage newsSectionStorage;
    private NewsStorage newsStorage;
    private sorting sorting;
    private settings settings;
    private currentArticle currentArticle;
    //private boolean readContentAvailable;

    @SuppressLint({"WrongViewCast", "WrongThread"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_listing);

        //readContentAvailable = false;
        newsSectionStorage = new newsSectionStorage(this);
        sorting = new sorting(this);
        settings = new settings(this);


        //get news type and news section URL based on tapped sections
        newsSectionURL = newsSectionStorage.getSectionURL();
        newsType = newsSectionStorage.getNewsSectionType();


        newsStorage = new NewsStorage(this, newsType);

        newsStorage.loadData();
        orderLatest = newsStorage.isOrderLatest();

        articleDatas = newsStorage.loadArt1();

        if (articleDatas == null)
            articleDatas = new ArrayList<>();

        if(articleDatas.isEmpty()) {
            new GetContents(ArticleListingActivity.this).execute();
        }


        if (!articleDatas.isEmpty()){
            new CheckNewContents().execute();
        }


        //System.out.println(articleDatas);




        // loadReading();

        //Implement pull to refresh
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                newsSectionURL = newsSectionStorage.getSectionURL();
                newsType = newsSectionStorage.getNewsSectionType();


                newsStorage = new NewsStorage(ArticleListingActivity.this, newsType);

                newsStorage.loadData();
                orderLatest = newsStorage.isOrderLatest();

                articleDatas = newsStorage.loadArt1();

                if (articleDatas == null)
                    articleDatas = new ArrayList<>();

                if(articleDatas.isEmpty()) {
                    new GetContents(ArticleListingActivity.this).execute();
                }


                if (!articleDatas.isEmpty()){
                    new CheckNewContents().execute();
                }

                setupListView();

                pullToRefresh.setRefreshing(false);
            }
        });


        //checkReadStuff();
        setupListView();
        //articleDatas2 = articleDatas;
        //loadReading();
    }

    @Override
    public void onResume() {
        super.onResume();
        //newsStorage.loadData();
        newsStorage.loadData();
        articleDatas = newsStorage.loadArt1();
        setupListView();
    }


    private void setupListView() {

        listView = findViewById(R.id.news_list);
        articleListAdapter = new ArticleListAdapter(this, articleDatas);
        listView.setAdapter(articleListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentArticle = new currentArticle(ArticleListingActivity.this);
                String link = articleDatas.get(i).getLink();
                currentArticle.saveData(link);

                boolean checker = true;
                Intent toView = new Intent(ArticleListingActivity.this, ArticleViewActivity.class);

                toView.putExtra("index", i);
                articleDatas.get(i).setReadNews(true);

                newsStorage.saveData(articleDatas);
                startActivity(toView);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        wasReading = "no";

        Intent toSection = new Intent(ArticleListingActivity.this, NewsSectionActivity.class);
        startActivity(toSection);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    //Get news contents and fill it inside newsdata array
    public class GetContents extends AsyncTask<String, Void, ArrayList<String>> {

        //Create progress dialog when getting contents and prevent user from doing anything else.
        private ProgressDialog dialog;
        public GetContents(ArticleListingActivity thisActivity){
            dialog = new ProgressDialog(thisActivity);
            dialog.setCancelable(true);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Getting contents. Please wait...");
            dialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {

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

            //Now to go each article links to scrap the contents

            /*
            The only problem with using JSoup to scrap the html contents is that I couldn't get
            paid contents that require login because JSoup.connect doesn't share the login info from
            Android WebView.

            To tackle this issue, we get the paid contents when user is viewing the article itself.
            The drawback is that it's slower to read paid articles since it needs to parse the html
            first.
             */
            String altPubDateStr = "1970-01-01 00:00:00+00:00";
            ArrayList<String> tempURL = new ArrayList<>();
            Document localDoc = null;
            for(int i = 0; i < newArticles.size(); i++){
                try{
                    localDoc = Jsoup.connect(newArticles.get(i).getLink()).get();
                } catch (Exception e)
                {
                    Log.d("Error", e.getMessage());
                }

                //System.out.println("Local doc: " + localDoc);
                //Get authors
                Elements author = localDoc.select("meta[property='article:author']");
                String tempAuthor = author.attr("content");

                if(tempAuthor.equals(""))
                    tempAuthor = "-";

                newArticles.get(i).setAuthor(tempAuthor);


                //Get all <p> from HTML
                Elements contentContainer = localDoc.select("div[id $= full-content-container]");
                //Elements contentContainer = localDoc.select("script[id$=__NEXT_DATA__]");

                Elements docContents = contentContainer.select("p, li");

                //Create temporary array to hold the contents
                ArrayList<String> tempList = new ArrayList<>();
                tempList.clear();

                //if author name is unique, add to the contents to be read
                if(tempAuthor.equals("-") || tempAuthor.equals("Bernama") || tempAuthor.equals("Reuters")){
                    tempList.add(newArticles.get(i).getTitle());
                }
                else{
                    tempList.add(newArticles.get(i).getTitle() + ". By " + tempAuthor);
                }

                //add the temporary array into the articledata only if they're not empty
                for(Element e : docContents){
                    if(!(e.text().equals(""))){
                        tempList.add(e.text());
                    }
                }

                System.out.println("here0" + tempList);

                //Pass the temporary array into the articleData
                newArticles.get(i).setContent(tempList);

                //Check if article is paid or free
                Elements metas = localDoc.select("meta[name='mk:free']");
                String checkStr = metas.attr("content");

                newArticles.get(i).setPaidNews(!checkStr.equals("true"));

                //Add published date and time
                Elements metaPubDate = localDoc.select("meta[property='article:published_time']");
                String pubDateStr = metaPubDate.attr("content");
                int year = 1970, month = 1, day = 1, hourOfDay = 0, minute = 0, second = 0;
                if (pubDateStr.length() > 0) {
                    year = Integer.parseInt(pubDateStr.substring(0, 4));
                    month = Integer.parseInt(pubDateStr.substring(5, 7)) - 1;
                    day = Integer.parseInt(pubDateStr.substring(8, 10));
                    hourOfDay = Integer.parseInt(pubDateStr.substring(11, 13));
                    minute = Integer.parseInt(pubDateStr.substring(14, 16));
                    second = Integer.parseInt(pubDateStr.substring(17, 19));
                    altPubDateStr = pubDateStr;
                } else {
                    pubDateStr = altPubDateStr;
                }

                //System.out.println((i+1) + ": " + pubDateStr);

                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                c.set(year, month, day, hourOfDay, minute, second);
                Date pubDate = c.getTime();

                newArticles.get(i).setPublishDate(pubDate);


                //Add modifed date and time
                Elements metaModDate = localDoc.select("meta[property='article:published_time']");
                String modDateStr = metaModDate.attr("content");

                int modYear = Integer.parseInt(pubDateStr.substring(0, 4));
                int modMonth = Integer.parseInt(pubDateStr.substring(5, 7))-1;
                int modDay = Integer.parseInt(pubDateStr.substring(8, 10));
                int modHourOfDay = Integer.parseInt(pubDateStr.substring(11, 13));
                int modMinute = Integer.parseInt(pubDateStr.substring(14, 16));
                int modSecond = Integer.parseInt(pubDateStr.substring(17, 19));

                c.set(modYear, modMonth, modDay, modHourOfDay, modMinute, modSecond);
                Date modDate = c.getTime();

                SimpleDateFormat sf =
                        new SimpleDateFormat("E dd.MM.yyyy '|' hh:mm");

                String pDate = sf.format(pubDate);
                String mDate = sf.format(modDate);


                //Get which links in the newly fetched contents was updated
                if(!(pDate.equals(mDate))){
                    tempURL.add(newArticles.get(i).getLink());
                }
            }

            //First launch or clear data
            if(articleDatas.isEmpty()){
                articleDatas = new ArrayList<>(newArticles);

                if(!orderLatest){
                    articleDatas = sorting.sortByOldest(articleDatas);
                }
            }
            else{

                if(!tempURL.isEmpty()){

                    ArrayList<ArticleData> tempArticleData = new ArrayList<>();
                    //find links that's in articleDatas
                    for(ArticleData a : articleDatas){
                        for(String s : tempURL){
                            if(s.equals(a.getLink())){
                                tempArticleData.add(new ArticleData(a));
                                break;
                            }
                        }
                    }

                    //remove links that's going to be replaced by newArticles
                    if(!tempArticleData.isEmpty()){
                        articleDatas.removeAll(tempArticleData);

                        ArrayList<ArticleData> newTemp = new ArrayList<>();

                        for(ArticleData a : tempArticleData){
                            for(ArticleData z : articleDatas){
                                if(a.getLink().equals(z.getLink())){
                                    newTemp.add(new ArticleData(z));
                                    break;
                                }
                            }
                        }

                        if(!newTemp.isEmpty()){
                            articleDatas.removeAll(newTemp);
                        }
                    }


                }


                //remove article already in articleDatas
                newArticles.removeAll(articleDatas);



                //Add newArticles to current articleData
                articleDatas.addAll(newArticles);

                articleDatas = removeDuplicates(articleDatas);
/*
                System.out.println("Passed this point");
                System.out.println("articleDatas = " + articleDatas.size());
                System.out.println("articleDatas2 = " + articleDatas2.size());
                System.out.println("articleDatas2 = " + articleDatas2.size());

                for (int i = 0; i < articleDatas.size(); i++) {
                    for (int j = 0; j < articleDatas2.size(); j++)
                        // if item in current list is available in old list and is flagged as read, remove item in current list
                        if (articleDatas.get(i).getTitle().equals(articleDatas2.get(j).getTitle()))
                            if (articleDatas2.get(j).getReadNews() == true) {
                                articleDatas.remove(i);
                            }
                }
*/
                //Sort according to user's settings
                if(orderLatest){
                    articleDatas = sorting.sortByLatest(articleDatas);
                }
                else{
                    articleDatas = sorting.sortByOldest(articleDatas);
                }
            }

            checkReadStuff();
            newsStorage.saveData(articleDatas);

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> dummy){
            if (dialog.isShowing()) {
                dialog.dismiss();
            }


            //Display
            checkReadStuff();
            setupListView();
        }
    }

    //Check if there are new contents
    public class CheckNewContents extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            ArrayList<String> current = new ArrayList<>();
            ArrayList<String> checkNew = new ArrayList<>();
            //ArrayList<String> checkOld = new ArrayList<>();
            /*for (ArticleData a : articleDatas) {
                current.add(a.getTitle());
            }
             */

            for (int a = 0; a < articleDatas.size(); a++) {
                current.add(articleDatas.get(a).getTitle());
            }

            //Checking is done based on titles. if there are new titles in the array return true.
            try {
                Document doc = Jsoup.connect(newsSectionURL).get();
                Elements items = doc.select("item");
                Elements titles = items.select("title");

                for (Element title : titles) {
                    checkNew.add(title.text());
                }

            } catch (Exception e) {
                Log.d("JSOUPERROR: ", e.getMessage());
            }

            Collections.sort(current);
            Collections.sort(checkNew);

            //System.out.println("checkOld: " + checkOld.size());
            //System.out.println("current: " + current.size());
            //System.out.println("checkNew: " + checkNew.size());

            if(current.size() == checkNew.size()){
                if(current.equals(checkNew)){
                    newContentAvailable = false;
                }
                else{
                    newContentAvailable = true;
                }
            }
            else{
                checkNew.removeAll(current);
                if(checkNew.isEmpty()){
                    newContentAvailable = false;
                }
                else{
                    newContentAvailable = true;
                }
            }

            //if (checkOld.size() != current.size()) {
            //    readContentAvailable = true;
            //}
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> dummy) {
            //System.out.println("test");
            if (newContentAvailable) {
                new GetContents(ArticleListingActivity.this).execute();
            } else {
                Toast.makeText(ArticleListingActivity.this, "No new contents available", Toast.LENGTH_LONG).show();
            }
            //if (articleDatas2 == null) System.out.println("null");
            //if ((articleDatas2 != null) && (!articleDatas2.isEmpty())) System.out.println(articleDatas.size() + "|" + articleDatas2.size());

            checkReadStuff();
            setupListView();
        }
    }

    //autoupdate attempt #1, dysfunctional
    /*
    public boolean updater () {
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run() {
                new CheckNewContents().execute();
            }
        }, 0, 1000);
        return true;
    }
    */

    //autoupdate attempt #2, functional
    final Handler handler = new Handler();
    Runnable timedTask = new Runnable() {
        public void run() {
            settings.saveSettings(newsType, orderLatest);
            articleListAdapter.notifyDataSetChanged();
            new CheckNewContents().execute();
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listview_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //Also you can do this for sub menu
        if(orderLatest)
            menu.getItem(1).getSubMenu().getItem(0).setChecked(true);
        else
            menu.getItem(1).getSubMenu().getItem(1).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.latest:
                if (!orderLatest) {
                    orderLatest = !orderLatest;
                    settings.saveSettings(newsType, orderLatest);
                    articleDatas = sorting.sortByLatest(articleDatas);
                    articleListAdapter.notifyDataSetChanged();
                }
                return true;
            case R.id.oldest:
                if (orderLatest) {
                    orderLatest = !orderLatest;
                    settings.saveSettings(newsType, orderLatest);
                    articleDatas = sorting.sortByOldest(articleDatas);
                    articleListAdapter.notifyDataSetChanged();
                }
                return true;
            case R.id.minute:
                handler.postDelayed(timedTask, 60000);
                return true;
            case R.id.hour:
                handler.postDelayed(timedTask, 60 * 60000);
                return true;
            case R.id.day:
                handler.postDelayed(timedTask, 24 * 60 * 60000);
                return true;

            case R.id.unread:
                for (int i = 0; i < articleDatas.size(); i++) {
                    articleDatas.get(i).setReadNews(false);
                }


            case R.id.clearread:

                if (articleDatas2.size() >= 30) {
                    articleDatas2.remove(0);
                }
                for (int i = 0; i < articleDatas.size(); i++) {
                    if (articleDatas.get(i).getReadNews()) {
                        boolean checker = true;
                        if (articleDatas2 != null) {
                            for (int a = 0; a < articleDatas2.size(); a++) {
                                if (articleDatas.get(i).getTitle().equals(articleDatas2.get(a).getTitle()))
                                    checker = false;
                            }
                        }
                        if (checker) articleDatas2.add(articleDatas.get(i));
                        if ((articleDatas != null) && (!articleDatas.isEmpty())) {
                            articleDatas.remove(i);
                        }
                        i--;
                    }

                    checkReadStuff();
                    newsStorage.saveData(articleDatas);


                    setupListView();
                }
                return true;

            case R.id.reset:
                articleDatas.clear();
                articleListAdapter.clear();
                Toast.makeText(ArticleListingActivity.this, "Article list cleared. Refreshing...", Toast.LENGTH_LONG).show();
                articleDatas2 = new ArrayList<>();
                new GetContents(this).execute();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void checkReadStuff() {
        if ((articleDatas2 != null)) {
            for (int i = 0; i < articleDatas.size(); i++) {
                for (int j = 0; j < articleDatas2.size(); j++) {
                    // if item in current list is available in old list and is flagged as read, remove item in current list
                    if (articleDatas.get(i).getTitle().equals(articleDatas2.get(j).getTitle())) {
                        //System.out.println("BEFORE: " + articleDatas.size());
                        articleDatas.remove(i);
                        i--;
                        //System.out.println("AFTER: " + articleDatas.size());
                        break;
                    }
                }
            }
            newsStorage.saveData();
            //setupListView();
        }
    }

    private ArrayList<ArticleData> removeDuplicates(ArrayList<ArticleData> list){
        ArrayList<ArticleData> newList = new ArrayList<>();
        for(ArticleData element : list){
            if(!newList.contains(element)){
                newList.add(element);
            }
        }

        return newList;
    }
}




