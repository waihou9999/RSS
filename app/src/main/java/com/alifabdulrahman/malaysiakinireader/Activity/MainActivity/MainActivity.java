package com.alifabdulrahman.malaysiakinireader.Activity.MainActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.alifabdulrahman.malaysiakinireader.Activity.Enter.NewsSectionActivity;
import com.alifabdulrahman.malaysiakinireader.Activity.Help.NewsSectionActivity1;
import com.alifabdulrahman.malaysiakinireader.Activity.About.NewsSectionActivity2;
import com.alifabdulrahman.malaysiakinireader.model.NewsSource;
import com.alifabdulrahman.malaysiakinireader.R;

import java.io.Serializable;
import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class MainActivity extends AppCompatActivity implements Serializable {

    private int backButtonCount = 0;
    private ArrayList<NewsSource> newsSources;
    private ArrayAdapter<NewsSource> adapter;
    private ListView lv;
    private AlertDialog.Builder dialog_builder;
    private AlertDialog.Builder startUp;
    private String wasReading = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsSources = new ArrayList<>();
        dialog_builder = new AlertDialog.Builder(MainActivity.this);
        startUp = new AlertDialog.Builder(this)
                .setTitle("Disclaimer")
                .setMessage("This application is not officially affiliated with any of the news sources included. It will only read the news aloud.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        //Display disclaimer on first run
        checkFirstRun();
        loadLastArticle();

        lv = findViewById(R.id.news_list);
        newsSources.add(new NewsSource("Enter", ""));
        newsSources.add(new NewsSource("Help", ""));
        newsSources.add(new NewsSource("About", ""));
        //newsSources.add(new NewsSource("The New York Times (in development)", "https://www.nytimes.com/"));
        //newsSources.add(new NewsSource("The Star (in development)", "https://www.thestar.com.my"));
        //https://www.thestar.com.my
        adapter = new ArrayAdapter<NewsSource>(this, android.R.layout.simple_list_item_1, newsSources){
                @Override
                public View getView(int pos, View convertView, ViewGroup parent){
                    View view = super.getView(pos, convertView, parent);

                    TextView tv = view.findViewById(android.R.id.text1);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

                    LayoutParams params = view.getLayoutParams();
                    params.height = 125;
                    view.setLayoutParams(params);

                    return view;
                }
        };

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    toSection0();
                }
                if(position == 1) {
                    toSection1();
                }
                if(position == 2){
                    toSection2();
                }
            }
        });
    }

    //Go to login
    /*
    public void toLogin(View view){

        new AlertDialog.Builder(this).
                setTitle("Login help")
                .setMessage("To access RSS feeds that require logging into an on-site account, you may access the login page from within an article page of your selected news site.")
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public void toAbout(View view){

        new AlertDialog.Builder(this).
                setTitle("About")
                .setMessage("RSS News Reader version 2021.03\n\n" +
                        "by Alif Abdul Rahman and William Kang\n\n" +
                        "Supervised by Dr Ian Chai")
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

     */


    //Go to news section
    public void toSection0(){
        Intent toNewsSection = new Intent(MainActivity.this, NewsSectionActivity.class);
        startActivity(toNewsSection);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void toSection1(){
        Intent toNewsSection1 = new Intent(MainActivity.this, NewsSectionActivity1.class);
        startActivity(toNewsSection1);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void toSection2(){
        Intent toNewsSection2 = new Intent(MainActivity.this, NewsSectionActivity2.class);
        startActivity(toNewsSection2);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed()
    {
        //Quit app
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    private void checkFirstRun(){
        boolean firstRun = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("firstRun", true);
        if(firstRun){
            startUp.show();

            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstRun", false)
                    .apply();
        }
    }

    private void saveReading() {
        SharedPreferences sp = getSharedPreferences("currentArticle", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("wasReading", wasReading);

        editor.apply();
    }

    public void loadLastArticle() {
        SharedPreferences sp = getSharedPreferences("currentArticle", MODE_PRIVATE);
        String wasReading = sp.getString("wasReading", "no");

        if (wasReading.equals("yes")){
            toSection0();
        } else {
            // do nothing
        }
    }

}
