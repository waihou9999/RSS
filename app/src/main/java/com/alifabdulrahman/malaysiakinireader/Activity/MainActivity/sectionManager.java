package com.alifabdulrahman.malaysiakinireader.Activity.MainActivity;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.alifabdulrahman.malaysiakinireader.Activity.About.NewsSectionActivity2;
import com.alifabdulrahman.malaysiakinireader.Activity.Enter.ArticleList.ArticleListingActivity;
import com.alifabdulrahman.malaysiakinireader.Activity.Enter.ArticleView.ArticleViewActivity;
import com.alifabdulrahman.malaysiakinireader.Activity.Enter.NewsSectionActivity;
import com.alifabdulrahman.malaysiakinireader.Activity.Help.NewsSectionActivity1;

public class sectionManager extends AppCompatActivity {
    private Context context;
    private Intent intent = null;

    public sectionManager(Context context) {
        this.context = context;
    }

    public Intent section(int pos) {

        switch(pos){
            case 0:   intent = new Intent(context, NewsSectionActivity.class); break;
            case 1:   intent = new Intent(context, NewsSectionActivity1.class); break;
            case 2:   intent = new Intent(context, NewsSectionActivity2.class); break;
            case 3:   intent = new Intent(context, ArticleListingActivity.class); break;
            case 4:   intent = new Intent(context, MainActivity.class); break;
            case 5:   intent = new Intent(context, ArticleViewActivity.class); break;
        }

        return intent;
    }

    public Intent toView(int lastIndex3, String lastNewsType2, boolean lastOrder2){
        intent = new Intent(context, ArticleViewActivity.class);
        intent.putExtra("index", lastIndex3);
        intent.putExtra("NewsType", lastNewsType2);
        intent.putExtra("OrderLatest", lastOrder2);

        return intent;
    }
}
