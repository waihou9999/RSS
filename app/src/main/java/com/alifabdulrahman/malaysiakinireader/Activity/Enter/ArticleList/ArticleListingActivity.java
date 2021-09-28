package com.alifabdulrahman.malaysiakinireader.Activity.Enter.ArticleList;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
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
import com.alifabdulrahman.malaysiakinireader.storage.substorage.NewsStorage;
import com.alifabdulrahman.malaysiakinireader.storage.substorage.currentArticle;
import com.alifabdulrahman.malaysiakinireader.storage.substorage.newsSectionStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class ArticleListingActivity extends AppCompatActivity implements Serializable{

    private ArrayList<ArticleData> articleDatas = new ArrayList<>(); // current
    //private ArrayList<ArticleData> articleDatas2; // new/temp
    private ArrayList<ArticleData> articleDatas2 = new ArrayList<>(); // stores all read articles, only clears when reset
    private boolean orderLatest;
    private ListView listView;
    private String newsSectionURL, newsType;
    private String newsType2 = "a";
    private ArticleListAdapter articleListAdapter;
    private SwipeRefreshLayout pullToRefresh;
    private boolean newContentAvailable;
    private String wasReading;
    private Map<String, String> mapLoggedInCookies;
    private currentArticle currentArticle;
    private newsSectionStorage newsSectionStorage;
    private NewsStorage newsStorage;
    //private boolean readContentAvailable;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_listing);

        newContentAvailable = true;
        //readContentAvailable = false;
        newsSectionStorage = new newsSectionStorage(this);

        //get news type and news section URL based on tapped sections
        newsSectionURL = newsSectionStorage.getSectionURL();
        newsType = newsSectionStorage.getNewsSectionType();

        newsStorage = new NewsStorage(this, newsType);
        newsStorage.loadData();

       // loadReading();

        //Implement pull to refresh
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
                new CheckNewContents().execute();
            }
        });

        //For first launch, populate the listview directly
        if(articleDatas.isEmpty()) {
            new GetContents(ArticleListingActivity.this).execute();
        }

        //Else check for updates and update only if there are new things
        else{
            new CheckNewContents().execute();
        }
        //checkReadStuff();
        //setupListView();
        //articleDatas2 = articleDatas;
        //loadReading();
    }

    @Override
    public void onResume(){
        super.onResume();
        newsStorage.loadData();
        setupListView();
    }


    private void setupListView(){

        listView = findViewById(R.id.news_list);
        articleListAdapter = new ArticleListAdapter(this, articleDatas);
        listView.setAdapter(articleListAdapter);
        //System.out.println("SIZE: " + articleDatas.size());
        /*
        for (int i = 0; i < articleDatas.size(); i++) {
            System.out.println("ArticleDatas: " + articleDatas.get(i).getTitle());
        }
        if (articleDatas2 != null) {
            for (int j = 0; j < articleDatas2.size(); j++) {
                System.out.println("ArticleDatas2: " + articleDatas2.get(j).getTitle());
            }
        }
         */
        /*
        if (articleDatas2 != null) {
            for (int a = 0; a < articleDatas2.size(); a++) {
                System.out.println(a + ". " + articleDatas2.get(a).getTitle());
            }
        }

         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                boolean checker = true;
                Intent toView = new Intent(ArticleListingActivity.this, ArticleViewActivity.class);

              //  currentArticle.saveArticle(i);

                articleDatas.get(i).setReadNews(true);
                if (articleDatas2.size() >= 30) {
                    articleDatas2.remove(0);
                }
                if (articleDatas2 != null) {
                    for (int a = 0; a < articleDatas2.size(); a++) {
                        if (articleDatas.get(i).getTitle().equals(articleDatas2.get(a).getTitle())) checker = false;
                    }
                }
                if (checker) articleDatas2.add(articleDatas.get(i));
                /*
                System.out.println("List of articles in articleDatas2:");
                for (int a = 0; a < articleDatas2.size(); a++) {
                   System.out.println(articleDatas2.get(a).getTitle());
                }
                 */
              //  currentArticle.saveArticle();
                newsStorage.saveData();
                startActivity(toView);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public void onBackPressed(){
        finish();
        super.onBackPressed();
        wasReading = "no";
       // currentArticle.saveList(wasReading);
        Intent toSection = new Intent(ArticleListingActivity.this, NewsSectionActivity.class);
        startActivity(toSection);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

            /*
            Connection.Response res = null;

            try{
                res = Jsoup.connect("https://membership.malaysiakini.com/auth/local?redirectUrl=https://www.malaysiakini.com&flow=normal&lang=en")
                        .data("username", "hou-hou99@hotmail.com", "password", "ZEDsolonoob99?!'")
                        .method(Connection.Method.POST)
                        .execute();

            } catch (IOException e) {
                e.printStackTrace();
            }

            Map<String, String> cookies = res.cookies();



*/
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

            try{
                Connection.Response response =
                        Jsoup.connect("https://membership.malaysiakini.com/auth/local?redirectUrl=https://www.malaysiakini.com/&flow=normal&lang=en")
                                .referrer("https://www.malaysiakini.com/")
                                .userAgent("Mozilla/5.0")
                                .timeout(10 * 1000)
                                .followRedirects(true)
                                .ignoreContentType(true)
                                .execute();

                //System.out.println("Fetched login page");

                //get the cookies from the response, which we will post to the action URL
                Map<String, String> mapLoginPageCookies = response.cookies();

                Map<String, String> mapParms = new HashMap<>();
                mapParms.put("email", "hou-hou99@hotmaial.com");
                mapParms.put("password", "ZEDsolonoob99?!'");
                mapParms.put("submit", "submit");


                //URL found in form's action attribute
                String strActionURL = "https://membership.malaysiakini.com/auth/local?redirectUrl=https://www.malaysiakini.com/&flow=normal&lang=en";

                //String a = getCookie(strActionURL,"cookieName");
               // System.out.println(a);

                Connection.Response responsePostLogin = Jsoup.connect(strActionURL)
                        //referrer will be the login page's URL
                        .referrer("https://malaysiakini.com/")
                        //user agent
                        .userAgent("Mozilla/5.0")
                        //connect and read time out
                        .timeout(10 * 1000)
                        //post parameters
                        .data(mapParms)
                        //cookies received from login page
                        .cookies(mapLoginPageCookies)
                        //many websites redirects the user after login, so follow them
                        .followRedirects(true)
                        .execute();

               // System.out.println("HTTP Status Code: " + responsePostLogin.statusCode());

                //parse the document from response
                Document document = responsePostLogin.parse();
               // System.out.println("whatdocument" + document);

                //get the cookies
                mapLoggedInCookies = responsePostLogin.cookies();


                /*
                 * You may need to send all the cookies you received
                 * from the post response to the subsequent requests.
                 *
                 * You can do that using cookies method of Connection
                 */

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
                    localDoc = Jsoup.connect(newArticles.get(i).getLink()).cookies(mapLoggedInCookies).get();
                } catch (Exception e)
                {
                    Log.d("Error", e.getMessage());
                }

                //Get authors
                Elements author = localDoc.select("meta[property='article:author']");
                String tempAuthor = author.attr("content");

                if(tempAuthor.equals(""))
                    tempAuthor = "-";

                newArticles.get(i).setAuthor(tempAuthor);


                //Get all <p> from HTML
                Elements contentContainer = localDoc.select("#full-content-container");
                //Elements contentContainer = localDoc.select("script[id$=__NEXT_DATA__]");
                //System.out.println("contcont");
                //System.out.println(contentContainer);

                Elements docContents = localDoc.select("p");

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
                    articleDatas = sortByOldest(articleDatas);
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
                    articleDatas = sortByLatest(articleDatas);
                }
                else{
                    articleDatas = sortByOldest(articleDatas);
                }
            }

            checkReadStuff();
            newsStorage.saveData();

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> dummy){
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            //Display
            //checkReadStuff();
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
            //System.out.println("tomakesure1" + articleDatas);

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

            //checkReadStuff();
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
            saveSettings();
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
        switch(item.getItemId()){
            case R.id.latest:
                if(!orderLatest){
                    orderLatest = !orderLatest;
                    saveSettings();
                    articleDatas = sortByLatest(articleDatas);
                    articleListAdapter.notifyDataSetChanged();
                }
                return true;
            case R.id.oldest:
                if(orderLatest){
                    orderLatest = !orderLatest;
                    saveSettings();
                    articleDatas = sortByOldest(articleDatas);
                    articleListAdapter.notifyDataSetChanged();
                }
                return true;
            case R.id.minute:
                handler.postDelayed(timedTask, 60000);
                return true;
            case R.id.hour:
                handler.postDelayed(timedTask, 60*60000);
                return true;
            case R.id.day:
                handler.postDelayed(timedTask, 24*60*60000);
                return true;
            case R.id.clearread:
                if (articleDatas2.size() >= 30) {
                    articleDatas2.remove(0);
                }
                for (int i = 0; i < articleDatas.size(); i++) {
                    if (articleDatas.get(i).getReadNews()) {
                        boolean checker = true;
                        if (articleDatas2 != null) {
                            for (int a = 0; a < articleDatas2.size(); a++) {
                                if (articleDatas.get(i).getTitle().equals(articleDatas2.get(a).getTitle())) checker = false;
                            }
                        }
                        if (checker) articleDatas2.add(articleDatas.get(i));
                        if ((articleDatas != null) && (!articleDatas.isEmpty())) {
                            articleDatas.remove(i);
                        }
                        i--;
                    }
                }
                checkReadStuff();
                newsStorage.saveData();
                setupListView();
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


    //Sort by oldest by comparing the Date object in ArticleData
    private ArrayList<ArticleData> sortByOldest(ArrayList<ArticleData> toSort){
        Collections.sort(toSort, new Comparator<ArticleData>() {
            @Override
            public int compare(ArticleData o1, ArticleData o2) {
                return o1.getPublishDate().compareTo(o2.getPublishDate());
            }
        });

        return toSort;
    }

    //Sort by latest by comparing the Date object in ArticleData
    private ArrayList<ArticleData> sortByLatest(ArrayList<ArticleData> toSort){
        toSort = sortByOldest(toSort);
        Collections.reverse(toSort);

        return toSort;
    }

    //Save the user's order settings
    private void saveSettings(){
        SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("order" + newsType, orderLatest);
        editor.apply();
    }



    /*
    //Load the user's order settings
    private void loadSettings(){
        SharedPreferences sp = getSharedPreferences("settings", MODE_PRIVATE);
        orderLatest = sp.getBoolean("order" + newsType, true);
    }

    //Save data of articles retrieved
    private void saveData() {
        SharedPreferences sp = getSharedPreferences("NewsStorage", MODE_PRIVATE);
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
        toSaveInOrder = sortByLatest(toSaveInOrder);
        json = gson.toJson(toSaveInOrder);
        editor.putString(newsType, json);

        editor.apply();
    }

     */
/*
    private void saveReading() {
        SharedPreferences sp = getSharedPreferences("currentArticle", MODE_PRIVATE);
        //SharedPreferences xp = getSharedPreferences("ReadNews", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("lastIndex3", lastIndex3);
        editor.putString("lastNewsType2", lastNewsType2);
        editor.putBoolean("lastOrder2", lastOrder2);

        editor.apply();
    }
*/
    /*
    //Load data of articles
    private void loadData() {
        SharedPreferences sp = getSharedPreferences("NewsStorage", MODE_PRIVATE);
        //SharedPreferences xp = getSharedPreferences("ReadNews", MODE_PRIVATE);
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

        if (articleDatas2 == null) {
            articleDatas2 = new ArrayList<>();
        }
    }

     */

    /*
    private void loadReading() {
        SharedPreferences sp = getSharedPreferences("currentArticle", MODE_PRIVATE);
        String wasReading = sp.getString("wasReading", "no");
        String lastNewsType2 = sp.getString("lastNewsType2", "");
        Boolean lastOrder2 = sp.getBoolean("lastOrder2", false);
        int lastIndex3 = sp.getInt("lastIndex3", 0);

        if (!wasReading.equals("yes")){

        }

        if (wasReading.equals("yes")){
            Intent toView = new Intent(ArticleListingActivity.this, ArticleViewActivity.class);
            toView.putExtra("index", lastIndex3);
            toView.putExtra("NewsType", lastNewsType2);
            toView.putExtra("OrderLatest", lastOrder2);
            startActivity(toView);
        }
    }
     */

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
