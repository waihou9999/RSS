package com.alifabdulrahman.malaysiakinireader.Activity.Help;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alifabdulrahman.malaysiakinireader.R;
import com.alifabdulrahman.malaysiakinireader.Activity.MainActivity.MainActivity;

public class NewsSectionActivity1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView sView = new ScrollView(this);
        LinearLayout lView = new LinearLayout(this);

        TextView myTextTitle = new TextView(this);
        String a = System.getProperty("line.separator") +
                "Help";
        myTextTitle.setText(a);
        myTextTitle.setTextSize(20);
        myTextTitle.setPadding(60, 0, 60, 0);
        myTextTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        myTextTitle.setTextColor(Color.BLACK);
        lView.addView(myTextTitle);

        TextView myTextHeader1 = new TextView(this);
        String b = System.getProperty("line.separator") +
                "Logins"
                ;
        myTextHeader1.setText(b);
        myTextHeader1.setTextSize(18);
        myTextHeader1.setPadding(60, 0, 60, 0);
        myTextHeader1.setGravity(Gravity.CENTER_HORIZONTAL);
        myTextHeader1.setTextColor(Color.BLACK);
        lView.addView(myTextHeader1);

        TextView myText1 = new TextView(this);
        String c = System.getProperty("line.separator") +
                "To access RSS feeds that require logging into an on-site account, you may access the login page from within an article page of your selected news site." +
                System.getProperty("line.separator")
                ;
        myText1.setText(c);
        myText1.setTextSize(18);
        myText1.setPadding(60, 0, 60, 0);
        myText1.setGravity(Gravity.START);
        myText1.setTextColor(Color.BLACK);
        lView.addView(myText1);

        TextView myTextHeader2 = new TextView(this);
        String d = System.getProperty("line.separator") +
                "Adding and removing feeds"
                ;
        myTextHeader2.setText(d);
        myTextHeader2.setTextSize(18);
        myTextHeader2.setPadding(60, 0, 60, 0);
        myTextHeader2.setGravity(Gravity.CENTER_HORIZONTAL);
        myTextHeader2.setTextColor(Color.BLACK);
        lView.addView(myTextHeader2);

        TextView myText2 = new TextView(this);
        String e = System.getProperty("line.separator") +
                "You may add or remove custom RSS feeds by using \"Add RSS Feed\" and \"Remove RSS Feed\" respectively. However, note that the default MalaysiaKini sources are not removable by design." +
                System.getProperty("line.separator")
                ;
        myText2.setText(e);
        myText2.setTextSize(18);
        myText2.setPadding(60, 0, 60, 0);
        myText2.setGravity(Gravity.START);
        myText2.setTextColor(Color.BLACK);
        lView.addView(myText2);
/*
        TextView myTextHeader3 = new TextView(this);
        String f = System.getProperty("line.separator") +
                "Article "
                ;
        myTextHeader3.setText(f);
        myTextHeader3.setTextSize(18);
        myTextHeader3.setPadding(60, 0, 60, 0);
        myTextHeader3.setGravity(Gravity.CENTER_HORIZONTAL);
        myTextHeader3.setTextColor(Color.BLACK);
        lView.addView(myTextHeader3);

        TextView myText3 = new TextView(this);
        String g = System.getProperty("line.separator") +
                "Body" +
                System.getProperty("line.separator")
                ;
        myText3.setText(g);
        myText3.setTextSize(18);
        myText3.setPadding(60, 0, 60, 0);
        myText3.setGravity(Gravity.START);
        myText3.setTextColor(Color.BLACK);
        lView.addView(myText3);

 */

        TextView myTextHeader4 = new TextView(this);
        String h = System.getProperty("line.separator") +
                "Action Bar buttons"+
                System.getProperty("line.separator")
                ;
        myTextHeader4.setText(h);
        myTextHeader4.setTextSize(18);
        myTextHeader4.setPadding(60, 0, 60, 0);
        myTextHeader4.setGravity(Gravity.CENTER_HORIZONTAL);
        myTextHeader4.setTextColor(Color.BLACK);
        lView.addView(myTextHeader4);

        int share = android.R.drawable.ic_menu_share;
        int prev = android.R.drawable.ic_media_previous;
        int rew = android.R.drawable.ic_media_rew;
        int play = android.R.drawable.ic_media_play;
        int pause = android.R.drawable.ic_media_pause;
        int ff = android.R.drawable.ic_media_ff;
        int next = android.R.drawable.ic_media_next;
        int resc = android.R.drawable.ic_menu_rotate;

        ImageView ishare = new ImageView(this);
        ishare.setImageResource(share);
        ishare.setBackgroundColor(Color.BLUE);
        lView.addView(ishare);

        TextView myText4 = new TextView(this);
        String i =
                "Share article with social media" +
                System.getProperty("line.separator")
                ;
        myText4.setText(i);
        myText4.setTextSize(18);
        myText4.setPadding(60, 0, 60, 0);
        myText4.setGravity(Gravity.START);
        myText4.setTextColor(Color.BLACK);
        lView.addView(myText4);

        ImageView iprev = new ImageView(this);
        iprev.setImageResource(prev);
        iprev.setBackgroundColor(Color.RED);
        lView.addView(iprev);

        TextView myText10 = new TextView(this);
        String o =
                "Go to previous article" +
                System.getProperty("line.separator")
                ;
        myText10.setText(o);
        myText10.setTextSize(18);
        myText10.setPadding(60, 0, 60, 0);
        myText10.setGravity(Gravity.START);
        myText10.setTextColor(Color.BLACK);
        lView.addView(myText10);

        ImageView irew = new ImageView(this);
        irew.setImageResource(rew);
        irew.setBackgroundColor(Color.RED);
        lView.addView(irew);

        TextView myText11 = new TextView(this);
        String p =
                "Skip to previous paragraph" +
                System.getProperty("line.separator")
                ;
        myText11.setText(p);
        myText11.setTextSize(18);
        myText11.setPadding(60, 0, 60, 0);
        myText11.setGravity(Gravity.START);
        myText11.setTextColor(Color.BLACK);
        lView.addView(myText11);

        ImageView iplay = new ImageView(this);
        iplay.setImageResource(play);
        iplay.setBackgroundColor(Color.RED);
        lView.addView(iplay);

        TextView myText5 = new TextView(this);
        String j =
                "Start text-to-speech" +
                System.getProperty("line.separator")
                ;
        myText5.setText(j);
        myText5.setTextSize(18);
        myText5.setPadding(60, 0, 60, 0);
        myText5.setGravity(Gravity.START);
        myText5.setTextColor(Color.BLACK);
        lView.addView(myText5);

        ImageView ipause = new ImageView(this);
        ipause.setImageResource(pause);
        ipause.setBackgroundColor(Color.RED);
        lView.addView(ipause);

        TextView myText6 = new TextView(this);
        String k =
                "Pause text-to-speech" +
                System.getProperty("line.separator")
                ;
        myText6.setText(k);
        myText6.setTextSize(18);
        myText6.setPadding(60, 0, 60, 0);
        myText6.setGravity(Gravity.START);
        myText6.setTextColor(Color.BLACK);
        lView.addView(myText6);

        ImageView iff = new ImageView(this);
        iff.setImageResource(ff);
        iff.setBackgroundColor(Color.RED);
        lView.addView(iff);

        TextView myText7 = new TextView(this);
        String l =
                "Skip to next paragraph" +
                System.getProperty("line.separator")
                ;
        myText7.setText(l);
        myText7.setTextSize(18);
        myText7.setPadding(60, 0, 60, 0);
        myText7.setGravity(Gravity.START);
        myText7.setTextColor(Color.BLACK);
        lView.addView(myText7);

        ImageView inext = new ImageView(this);
        inext.setImageResource(next);
        inext.setBackgroundColor(Color.RED);
        lView.addView(inext);

        TextView myText8 = new TextView(this);
        String m =
                "Go to next article" +
                System.getProperty("line.separator")
                ;
        myText8.setText(m);
        myText8.setTextSize(18);
        myText8.setPadding(60, 0, 60, 0);
        myText8.setGravity(Gravity.START);
        myText8.setTextColor(Color.BLACK);
        lView.addView(myText8);

        ImageView iresc = new ImageView(this);
        iresc.setImageResource(resc);
        iresc.setBackgroundColor(Color.BLUE);
        lView.addView(iresc);

        TextView myText9 = new TextView(this);
        String n =
                "Retrieve article data for text-to-speech again" +
                System.getProperty("line.separator")
                ;
        myText9.setText(n);
        myText9.setTextSize(18);
        myText9.setPadding(60, 0, 60, 0);
        myText9.setGravity(Gravity.START);
        myText9.setTextColor(Color.BLACK);
        lView.addView(myText9);

        lView.setGravity(Gravity.CENTER_HORIZONTAL);
        lView.setOrientation(LinearLayout.VERTICAL);

        sView.addView(lView);
        setContentView(sView);
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    public void onBackPressed(){
        finish();
        super.onBackPressed();
        Intent toMain = new Intent(NewsSectionActivity1.this, MainActivity.class);
        startActivity(toMain);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
