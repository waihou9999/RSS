package com.alifabdulrahman.malaysiakinireader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alifabdulrahman.malaysiakinireader.R;
import com.alifabdulrahman.malaysiakinireader.model.ArticleData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ArticleListAdapter extends ArrayAdapter<ArticleData> {

    public ArticleListAdapter(@NonNull Context context, ArrayList<ArticleData> articleDatas) {
        super(context, 0 , articleDatas);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        ArticleData currentData = getItem(position);

        View listNewsData = convertView;
        if(listNewsData == null){
            listNewsData = LayoutInflater.from(getContext()).inflate(R.layout.list_news_item,parent,false);
        }

        TextView author = listNewsData.findViewById(R.id.author);
        author.setText(currentData.getAuthor());

        TextView date = listNewsData.findViewById(R.id.subtitle);

        //SimpleDateFormat sf =
        //        new SimpleDateFormat("E dd.MM.yyyy '|' HH:mma");
        //date.setText(sf.format(currentData.getPublishDate()));

        String pattern = "E dd.MM.yyyy | hh:mm a";
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        date.setText(sf.format(currentData.getPublishDate()));

        TextView title = listNewsData.findViewById(R.id.maintitle);

        if(currentData.getReadNews()){
            title.setText(currentData.getTitle());
            title.setTextColor(Color.rgb(128,0,128));
            author.setTextColor(Color.rgb(169, 169, 169));
            date.setTextColor(Color.rgb(169, 169, 169));
        }
        else{
            title.setText(currentData.getTitle());
            title.setTextColor(Color.rgb(0,0,255));
            author.setTextColor(Color.rgb(0, 0, 0));
            date.setTextColor(Color.rgb(0, 0, 0));
        }


        return listNewsData;
    }
}
