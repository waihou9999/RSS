package com.alifabdulrahman.malaysiakinireader.Activity.About;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alifabdulrahman.malaysiakinireader.R;
import com.alifabdulrahman.malaysiakinireader.Activity.MainActivity.MainActivity;

public class NewsSectionActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout lView = new LinearLayout(this);

        TextView myTextTitle = new TextView(this);
        String a = System.getProperty("line.separator") +
                "About this app";
        myTextTitle.setText(a);
        myTextTitle.setTextSize(20);
        myTextTitle.setPadding(60, 0, 60, 0);
        myTextTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        myTextTitle.setTextColor(Color.BLACK);

        TextView myText = new TextView(this);
        String x = System.getProperty("line.separator") +
                "RSS News Reader ver. 2021.03" +
                System.getProperty("line.separator") + System.getProperty("line.separator") +
                "Created by Alif Abdul Rahman and William Kang" +
                System.getProperty("line.separator") + System.getProperty("line.separator") +
                "Supervised by Dr Ian Chai" +
                System.getProperty("line.separator") + System.getProperty("line.separator") +
                "\u00A9 2021 Multimedia University Malaysia"
                ;
        myText.setText(x);
        myText.setTextSize(18);
        myText.setPadding(60, 0, 60, 0);
        myText.setGravity(Gravity.CENTER_HORIZONTAL);
        myText.setTextColor(Color.BLACK);

        lView.setGravity(Gravity.CENTER_HORIZONTAL);
        lView.setOrientation(LinearLayout.VERTICAL);
        lView.addView(myTextTitle);
        lView.addView(myText);

        setContentView(lView);
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    public void onBackPressed(){
        finish();
        super.onBackPressed();
        Intent toMain = new Intent(NewsSectionActivity2.this, MainActivity.class);
        startActivity(toMain);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }



}
