package com.alifabdulrahman.malaysiakinireader.Activity.Enter.ArticleView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alifabdulrahman.malaysiakinireader.model.ArticleData;
import com.alifabdulrahman.malaysiakinireader.R;
import com.alifabdulrahman.malaysiakinireader.storage.substorage.NewsStorage;
import com.alifabdulrahman.malaysiakinireader.storage.substorage.newsSectionStorage;
import com.alifabdulrahman.malaysiakinireader.storage.substorage.settings;
import com.alifabdulrahman.malaysiakinireader.storage.substorage.currentArticle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Type;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class ArticleViewActivity extends AppCompatActivity implements View.OnClickListener, AudioManager.OnAudioFocusChangeListener {

    private ArrayList<ArticleData> articleDatas;
    //private ArrayList<ArticleData> articleDatas2;
    private TextToSpeech tts;
    private WebView mWebView;
    private String url, newsType;
    private ImageButton pauseBtn, nextArc, prevArc, nextSent, prevSent, stopBtn, sharebutton, rescrapebutton;
    private int index;
    private int readIndex;
    private AudioManager audioManager;
    private boolean orderLatest;
    private SwipeRefreshLayout pullToRefresh2;
    //ImageButton sharebutton;
    //ImageButton reloadbutton;
    //ImageButton rescrapebutton;
    int timex = 1000;
    int savedIndex;
    int savedReadIndex;
    int starbigoff_ = android.R.drawable.ic_media_play;
    int starbigon_ = android.R.drawable.ic_media_pause;
    boolean startTTS = false;
    String wasReading = "";
    private NewsStorage newsStorage;
    private newsSectionStorage newsSectionStorage;
    private settings settings;
    private currentArticle currentArticle;

    @SuppressLint("WrongViewCast")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);

        //Set buttons to view
        //pauseBtn = findViewById(R.id.pausebtn);
        stopBtn = findViewById(R.id.stopbtn);
        nextArc = findViewById(R.id.nxtarcbtn);
        prevArc = findViewById(R.id.prevarcbtn);
        nextSent = findViewById(R.id.forwbtn);
        prevSent = findViewById(R.id.prevbtn);
        sharebutton = findViewById(R.id.sharebutton);
        //reloadbutton = findViewById(R.id.reloadbutton);
        rescrapebutton = findViewById(R.id.rescrapebutton);

        //set listener to buttons
        //pauseBtn.setOnClickListener(this);
        nextArc.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        prevArc.setOnClickListener(this);
        nextSent.setOnClickListener(this);
        prevSent.setOnClickListener(this);
        sharebutton.setOnClickListener(this);
        //reloadbutton.setOnClickListener(this);
        rescrapebutton.setOnClickListener(this);

        //Get clicked article from NewsListing
//        loadSettings();
       // orderLatest = getIntent().getExtras().getBoolean("OrderLatest");
       // newsType = getIntent().getExtras().getString("NewsType");
        index = getIntent().getExtras().getInt("index");
        //startTTS = getIntent().getExtras().getBoolean("startTTS");
        startTTS = true;

        //System.out.println("index is " + orderLatest + newsType + index);


        newsSectionStorage = new newsSectionStorage(this);
        newsType = newsSectionStorage.getNewsSectionType();
        settings = new settings(this);
        currentArticle = new currentArticle(this);
        newsStorage = new NewsStorage(this, newsType);
        orderLatest = settings.loadSettings(newsType);



        newsStorage.loadData();

        articleDatas = newsStorage.loadArt1();

        if (articleDatas.isEmpty()) {
            articleDatas = new ArrayList<>();
        }

        //System.out.println("wtf" + articleDatas);


        //System.out.println("webload10" + articleDatas);

        //set to read from first sentence
        readIndex = 0;
        savedIndex = index;
        savedReadIndex = readIndex;

        wasReading = "yes";
        currentArticle.saveReading(wasReading, index);
        //saveReading();

        //Get the url to display an set up webview

            //url = articleDatas.get(index).getLink();
        url = currentArticle.loadData();


        mWebView = findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);

        //Bundle extras = getIntent().getExtras();
        //if (extras != null) {
            if (startTTS) {
                initializeTTS();
            } else {
                initializeTTS();
                stopPlay();
                readFreeOrPaid();
            }
            //The key argument here must match that used in the other activity
        //}

        pullToRefresh2 = findViewById(R.id.pullToRefresh2);
        pullToRefresh2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.loadUrl(url);
                System.out.println("webload1");
                pullToRefresh2.setRefreshing(false);
            }
        });

    }

    //Initialize the Text to Speech
    private void initializeTTS(){
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            Toast.makeText(ArticleViewActivity.this, "TTS initialization successful", Toast.LENGTH_SHORT).show();
                        }

                        //What to do after tts has done speaking the current text
                        @Override
                        public void onDone(String utteranceId) {
                            readIndex++;

                            //if there are still more sentences in article, continue reading
                            if(readIndex < articleDatas.get(index).getContent().size() && !tts.isSpeaking()){
                                speakSentences(articleDatas.get(index).getContent());
                                //System.out.println("Reading now " + articleDatas.get(index).getContent());
                                //System.out.println("whatever");
                                //System.out.println(articleDatas.get(index).getContent());
                            }
                            //Else, update UI with next article and read it
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        moveArticle(true);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.e("UtteranceError", " " + utteranceId);
                        }
                    });


                    //String langIDText = articleDatas.get(index).getContent().toString();

                    String langIDText = articleDatas.get(index).getTitle();

                    //Set the language of TTS
                    FirebaseLanguageIdentification languageIdentifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
                    languageIdentifier.identifyLanguage(langIDText).addOnSuccessListener(
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(@Nullable String languageCode) {
                                    //System.out.println("languageCode: " + languageCode);
                                    if (languageCode.equals("en")) {
                                        tts.setLanguage(Locale.ENGLISH);
                                    } else if (languageCode.equals("ms") || languageCode.equals("id")) {
                                        tts.setLanguage(new Locale("id","ID"));
                                    } else if (languageCode.equals("zh")) {
                                        tts.setLanguage(Locale.CHINESE);
                                    } else {
                                        Log.e("error", "This language is not supported");
                                    }
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Model couldn’t be loaded or other internal error.
                                            // ...
                                        }
                                    });
                    /*
                    //int result=tts.setLanguage(Locale.ENGLISH);
                    //int result=tts.setLanguage(new Locale("id","ID"));
                    //int result=tts.setLanguage(Locale.CHINESE);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This language is not supported");
                    }
*/
                    readFreeOrPaid();

                }
                else
                    Log.e("error", "TTS initialization failed!");
            }
        });
    }

    //Decide based on article type.
    private void readFreeOrPaid(){
        loadWebView();
    }

    @Override
    protected void onStop(){
        //if(tts.isSpeaking()) {
        //    pausePlay();
        //}
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        //savedInstanceState.putInt("readIndex", readIndex);
        //savedInstanceState.putString("url", url);
        //savedInstanceState.putInt("index", index);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        removeAudioFocus();
    }

    @Override
    public void onBackPressed() {
        finish();
        wasReading = "no";
        currentArticle.saveReading(wasReading, index);
        //saveReading();
        saveData();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
/* deprecated
    private void loadFreeWebView(){
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading (WebView view, String url){
                return false;
            }
        });
        mWebView.loadUrl(url);
    }
*/
    /*
    Open the paid articles and inject javascript to scrap the HTML contents when it has finished
    loading. The problem is that onPageFinished doesn't actually wait for the page to finish.

    I think this is still the culprit as Android onPageFinished have different definition of when the page is actually done loading
    I'm not sure if they ever updated that if there are other alternative to view a webpage from an app.
    The onPageFinished method from the Android WebView consider the page finished loading even MalaysiaKini is still loading other stuff through javascript.
    I think one of the way to check is checking if the content fetched contain the "To continue reading please subscribe..."
    before reading the news
     */

    @SuppressLint("JavascriptInterface")
    private void loadWebView(){
        mWebView.addJavascriptInterface(new GetHTML(this), "Scrap");
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading (WebView view, String url){
                return false;
            }

            public void onPageFinished(WebView view, String url){
                super.onPageFinished(view, url);
                try {
                    Thread.sleep(timex);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                //second load
                mWebView.loadUrl("javascript:window.Scrap.getHTML" +
                        "(document.getElementsByTagName('html')[0].outerHTML);");
                System.out.println("webloadfinished");


                //String cookies = CookieManager.getInstance().getCookie(url);
                //System.out.println( "All the cookies in a string:" + cookies);
                }
        });

        //first load
        System.out.println("beforewebload");
        mWebView.loadUrl(url);
        System.out.println("afterwebload");
    }

    private void reloadWebView() {
        try {
            Thread.sleep(timex);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        //mWebView.addJavascriptInterface(new GetHTML(this), "Scrap");
        System.out.println("webload4");
        mWebView.loadUrl("javascript:window.Scrap.getHTML" +
                "(document.getElementsByTagName('html')[0].outerHTML);");
        System.out.println("webload5");
        //readIndex = 0;
        //pausePlay();
    }



    //Get the HTML loaded from webview and scrap it to get the article contents
    public class GetHTML{
        private Context ctx;

        public GetHTML(Context ctx){
            this.ctx = ctx;
        }

        @JavascriptInterface
        public Object getHTML(String html){
            //System.out.println("GETHTMLWORKSSS");
            //third load
            System.out.println("at gethtml");
            //Basically the same thing as in ArticleListingActivity GetContents
            Document doc = Jsoup.parse(html);

            ArrayList<String> tempList = new ArrayList<>();
            tempList.clear();

            if(articleDatas.get(index).getAuthor().equals("-") || articleDatas.get(index).getAuthor().equals("Bernama") || articleDatas.get(index).getAuthor().equals("Reuters")){
                tempList.add(articleDatas.get(index).getTitle());
            }
            else{
                tempList.add(articleDatas.get(index).getTitle() + ". By " + articleDatas.get(index).getAuthor());
            }

            //Elements contentContainer = doc.select("div[class$=content-container]");
            //Elements contentContainer = doc.select("script[id$=__NEXT_DATA__]");
            //Elements classContents =  contentContainer.select("div[class$=content]");
            Elements classContents =  doc.select("div[id $= full-content-container]");
            Elements contents = classContents.select("p, li");
            for(Element content : contents){
                if(!(content.text().equals(""))){
                    tempList.add(content.text());
                }
            }


            articleDatas.get(index).getContent().clear();
            articleDatas.get(index).setContent(tempList);

            //prevent duplicate TTS instance on paid news.
            if(!tts.isSpeaking()){
                speakSentences(articleDatas.get(index).getContent());
            }
            return null;
        }
    }

    //Speak the array of sentences.
    private void speakSentences(ArrayList<String> textToRead){
        if(requestAudioFocus()){
            tts.speak(textToRead.get(readIndex), TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
            //stopBtn.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

/*
    private void pausePlay(){
        if(tts.isSpeaking()){
            tts.stop();
            //removeAudioFocus();
            //stopBtn.setImageResource(android.R.drawable.ic_media_play);
        }
        else {
            speakSentences(articleDatas.get(index).getContent());
            //stopBtn.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    private void pauseIconCheck(){
        if (tts.isSpeaking()) {
            //stopBtn.setImageResource(android.R.drawable.ic_media_play);
        } else {
            //stopBtn.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

 */

    private void stopPlay() {
        if (tts != null && tts.isSpeaking()) {
            tts.stop();
        }
        if (tts != null) tts.shutdown();
        removeAudioFocus();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*
            case R.id.pausebtn:
                pausePlay();
                //System.out.println("ttsspeaking: " + tts.isSpeaking());
                break;

             */
            case R.id.stopbtn:
                Drawable starbigon = getResources().getDrawable(starbigon_);
                Drawable starbigoff = getResources().getDrawable(starbigoff_);
                if (stopBtn.getDrawable().getConstantState().equals(starbigon.getConstantState()) || tts.isSpeaking()) {
                    stopPlay();
                    //pauseIconCheck();
                    stopBtn.setImageResource(starbigoff_);
                    startTTS = false;
                    saveData();
                    break;
                }
                if (stopBtn.getDrawable().getConstantState().equals(starbigoff.getConstantState())) {
                    //System.out.println("lol");
                    initializeTTS();
                    tts.stop();
                    //pauseIconCheck();
                    stopBtn.setImageResource(starbigon_);
                    startTTS = true;
                   // System.out.println(articleDatas);
                    saveData();
                    break;
                }
            case R.id.nxtarcbtn:
                moveArticle(true);
                break;
            case R.id.prevarcbtn:
                moveArticle(false);
                break;
            case R.id.forwbtn:
                nextSentence();
                break;
            case R.id.prevbtn:
                previousSentence();
                break;
            case R.id.sharebutton:
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = articleDatas.get(index).getTitle() + ": " + url;
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent, "Share using"));
                break;
            /*case R.id.reloadbutton:
                PopupMenu popup = new PopupMenu(ArticleViewActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.reloadpopup, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.one:
                                /*
                                if (tts.isSpeaking()) {
                                    pausePlay();
                                } *./
                                timex = 1000;
                                Toast.makeText(ArticleViewActivity.this, "Scrape delay: 1 second.", Toast.LENGTH_SHORT).show();
                                //readIndex = 0;
                                return true;
                            case R.id.two:
                                /*
                                if (tts.isSpeaking()) {
                                    pausePlay();
                                } *./
                                timex = 2000;
                                Toast.makeText(ArticleViewActivity.this, "Scrape delay: 2 seconds.", Toast.LENGTH_SHORT).show();
                                //readIndex = 0;
                                return true;
                            case R.id.three:
                                /*
                                if (tts.isSpeaking()) {
                                    pausePlay();
                                } *.
                                timex = 3000;
                                Toast.makeText(ArticleViewActivity.this, "Scrape delay: 3 seconds.", Toast.LENGTH_SHORT).show();
                                //readIndex = 0;
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
                break;
*/
            case R.id.rescrapebutton:
                Toast.makeText(ArticleViewActivity.this, "Rescraping...", Toast.LENGTH_SHORT).show();
                if (tts.isSpeaking()) {
                    //pausePlay();
                }
                try {
                    Thread.sleep(timex);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                mWebView.addJavascriptInterface(new GetHTML(this), "Scrap");
                mWebView.loadUrl("javascript:window.Scrap.getHTML" +
                        "(document.getElementsByTagName('html')[0].outerHTML);");
                System.out.println("weload7");
                //readIndex = 0;
                break;
        }
    }

    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.reloadpopup, menu);
            return true;
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.zero:
                    timex = 0;
                    return true;
                case R.id.one:
                    timex = 1000;
                    return true;
                case R.id.two:
                    timex = 2000;
                    return true;
                case R.id.three:
                    timex = 3000;
                    return true;
                default:
                    return true;
            }
        }

     */
                //Go to next article based on direction(decided by user/system)
    public void moveArticle(boolean dir){
        if(dir) {
            if((index + 1) > articleDatas.size()-1)
                Toast.makeText(ArticleViewActivity.this, "This is the last article", Toast.LENGTH_SHORT).show();
            else{
                if(tts.isSpeaking())
                    tts.stop();
                readIndex = 0;
                index++;
                url = articleDatas.get(index).getLink();
                articleDatas.get(index).setReadNews(true);
                //saveReading();
                currentArticle.saveReading(wasReading, index);
                saveData();
                readFreeOrPaid();
            }
        }
        else{
            if((index) < 1)
                Toast.makeText(ArticleViewActivity.this, "This is the first article", Toast.LENGTH_SHORT).show();
            else {
                if(tts.isSpeaking())
                    tts.stop();
                readIndex = 0;
                index--;
                url = articleDatas.get(index).getLink();
                articleDatas.get(index).setReadNews(true);
                //saveReading();
                currentArticle.saveReading(wasReading, index);
                saveData();
                readFreeOrPaid();
            }
        }
    }

    //int oldsize = 0;
    //int newsize = 1;
    private void nextSentence(){
        if(readIndex < articleDatas.get(index).getContent().size() - 1){
            readIndex++;
            //if (oldsize != newsize) {
                mWebView.addJavascriptInterface(new GetHTML(this), "Scrap");
                mWebView.loadUrl("javascript:window.Scrap.getHTML" +
                        "(document.getElementsByTagName('html')[0].outerHTML);");
            System.out.println("webload8");
            //}
            //oldsize = newsize;
            //newsize = articleDatas.get(index).getContent().size();
            if(tts.isSpeaking())
                tts.stop();
            speakSentences(articleDatas.get(index).getContent());
            //System.out.println("wtf" + articleDatas.get(index).getContent());
        }
        else{
            moveArticle(true);
        }
    }

    private void previousSentence(){
        if(readIndex > 0){
            readIndex--;
            if(tts.isSpeaking())
                tts.stop();
            speakSentences(articleDatas.get(index).getContent());
        }
        else{
            moveArticle(false);
        }
    }

    @Override
    public void onAudioFocusChange(int focusState){
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (!tts.isSpeaking()) speakSentences(articleDatas.get(index).getContent());
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // stop playback
                if(tts.isSpeaking()) {
                    tts.stop();
                    //pauseBtn.setImageResource(android.R.drawable.ic_media_play);
                    stopBtn.setImageResource(android.R.drawable.ic_media_play);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // stop playback
                if(tts.isSpeaking()) {
                    tts.stop();
                    //pauseBtn.setImageResource(android.R.drawable.ic_media_play);
                    stopBtn.setImageResource(android.R.drawable.ic_media_play);
            }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // stop playback
                if(tts.isSpeaking()) {
                    tts.stop();
                    stopBtn.setImageResource(android.R.drawable.ic_media_play);
                    //pauseBtn.setImageResource(android.R.drawable.ic_media_play);
                }
                break;
        }
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        try{
            if(audioManager != null){
                return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                        audioManager.abandonAudioFocus(this);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    private void saveData(){
        SharedPreferences sp = getSharedPreferences("NewsStorage", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json;

        //SharedPreferences sp = getSharedPreferences("startTTS", MODE_PRIVATE);
        //SharedPreferences.Editor editor2 = ap.edit();

        //save in latest order
        ArrayList<ArticleData> toSaveInOrder = new ArrayList<>(articleDatas);
        Collections.sort(toSaveInOrder, new Comparator<ArticleData>() {
            @Override
            public int compare(ArticleData o1, ArticleData o2) {
                return o1.getPublishDate().compareTo(o2.getPublishDate());
            }
        });
        Collections.reverse(toSaveInOrder);
        json = gson.toJson(toSaveInOrder);
        editor.putString(newsType, json);
        //Boolean wasReadingOrNot = startTTS;

        int zson = starbigoff_;
        if (startTTS) zson = starbigon_;

        editor.putBoolean("startTTS", startTTS);
        editor.putInt("starimage", zson);

        editor.apply();
        //System.out.println(json);
    }

    /*
    private void saveReading() {
        SharedPreferences sp = getSharedPreferences("currentArticle", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("wasReading", wasReading);
        editor.putInt("lastIndex3", index);

        editor.apply();
    }

     */

    private void loadData(){
        SharedPreferences sp = getSharedPreferences("NewsStorage", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString(newsType, null);
        Type dataType = new TypeToken<ArrayList<ArticleData>>() {}.getType();
        articleDatas = gson.fromJson(json, dataType);

        //SharedPreferences sp = getSharedPreferences("startTTS", MODE_PRIVATE);
        boolean yson = sp.getBoolean("startTTS", false);
        startTTS = yson;

        int zson = sp.getInt("starimage", starbigoff_);
        stopBtn.setImageResource(zson);

        if (articleDatas != null) {
            if (!orderLatest) Collections.reverse(articleDatas);
        }
    }

    private void loadReading() {
        SharedPreferences sp = getSharedPreferences("currentArticle", MODE_PRIVATE);
        String wasReading = sp.getString("wasReading", "no");
        int lastIndex2 = sp.getInt("lastIndex2", 0);
        String lastNewsType2 = sp.getString("lastNewsType2", "");
        Boolean lastOrder2 = sp.getBoolean("lastOrder2", false);

        if (wasReading.equals("yes")){
            Intent toView = new Intent(ArticleViewActivity.this, ArticleViewActivity.class);
            toView.putExtra("index", lastIndex2);
            toView.putExtra("NewsType", lastNewsType2);
            toView.putExtra("OrderLatest", lastOrder2);
            startActivity(toView);
        }
    }
}
