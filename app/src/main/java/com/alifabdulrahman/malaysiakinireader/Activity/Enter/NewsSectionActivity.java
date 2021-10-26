package com.alifabdulrahman.malaysiakinireader.Activity.Enter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.TextView;

import com.alifabdulrahman.malaysiakinireader.Activity.Enter.ArticleList.ArticleListingActivity;
import com.alifabdulrahman.malaysiakinireader.Activity.MainActivity.MainActivity;
import com.alifabdulrahman.malaysiakinireader.Activity.MainActivity.sectionManager;
import com.alifabdulrahman.malaysiakinireader.R;
import com.alifabdulrahman.malaysiakinireader.model.NewsSectionData;
import com.alifabdulrahman.malaysiakinireader.storage.substorage.currentArticle;
import com.alifabdulrahman.malaysiakinireader.storage.substorage.newsSectionStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;

public class NewsSectionActivity extends AppCompatActivity implements Serializable {
    private ArrayList<NewsSectionData> newsSection = new ArrayList<>();
    private ArrayList<NewsSectionData> newsSection2 = new ArrayList<>();
    //private NewsSectionAdapter newsSectionAdapter;
    private newsSectionStorage newsSectionStorage;
    private sectionManager sectionManager = new sectionManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_section);
        //newsSectionAdapter = new NewsSectionAdapter(this, newsSection2);
        initializeDefaults();

        newsSectionStorage = new newsSectionStorage(this);

        newsSection2 = newsSectionStorage.loadData();

        if (newsSection2 != null) {
            newsSection.addAll(newsSection2);
        }

        loadLastArticle();

        setupListView();
    }

    @Override
    protected void onStop(){
        super.onStop();
        newsSectionStorage.saveData();
    }

    @Override
    public void onResume(){
        super.onResume();
        setupListView();

    }

    private void setupListView() {
        final ArrayAdapter<NewsSectionData> adapter = new ArrayAdapter<NewsSectionData>(this, android.R.layout.simple_list_item_1, newsSection){
            @Override
            public View getView(int pos, View convertView, ViewGroup parent){
                View view = super.getView(pos, convertView, parent);

                TextView tv = view.findViewById(android.R.id.text1);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

                ViewGroup.LayoutParams params = view.getLayoutParams();
                //params.height = 200;
                view.setLayoutParams(params);

                return view;
            }
        };

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = adapter.getItem(position).getSectionLink();
                String newsType = adapter.getItem(position).getSectionName();
                Intent toNewsListing = new Intent(NewsSectionActivity.this, ArticleListingActivity.class);
                startActivity(toNewsListing);
                newsSectionStorage.saveReading(url, newsType, true);
                newsSectionStorage.saveData();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    //ArrayList<CharSequence> arrayListCollection = new ArrayList<>();
    //ArrayAdapter<NewsSectionData> adapter;
    EditText txt1;
    EditText txt2;

    public void initializeDefaults() {
        newsSection.add(new NewsSectionData("MalaysiaKini News (English)", "https://www.malaysiakini.com/rss/en/news.rss"));
        newsSection.add(new NewsSectionData("MalaysiaKini Opinions (English)", "https://www.malaysiakini.com/rss/en/columns.rss"));
        newsSection.add(new NewsSectionData("MalaysiaKini Letters (English)", "https://www.malaysiakini.com/rss/en/letters.rss"));
        newsSection.add(new NewsSectionData("MalaysiaKini News (Bahasa Malaysia)", "https://www.malaysiakini.com/rss/my/news.rss"));
        newsSection.add(new NewsSectionData("MalaysiaKini Opinions (Bahasa Malaysia)", "https://www.malaysiakini.com/rss/my/columns.rss"));
        newsSection.add(new NewsSectionData("MalaysiaKini Letters (Bahasa Malaysia)", "https://www.malaysiakini.com/rss/my/letters.rss"));
        newsSection.add(new NewsSectionData("MalaysiaKini News (Chinese)", "https://www.malaysiakini.com/rss/zh/news.rss"));
        newsSection.add(new NewsSectionData("MalaysiaKini Opinions (Chinese)", "https://www.malaysiakini.com/rss/zh/columns.rss"));
        newsSection.add(new NewsSectionData("MalaysiaKini Letters (Chinese)", "https://www.malaysiakini.com/rss/zh/letters.rss"));
        //newsSection2.add(new NewsSectionData("MalaysiaKini News (English)", "https://www.malaysiakini.com/rss/en/news.rss"));
    }

    public void toAdd(View view){
        //System.out.println(newsSection.size());
        AlertDialog.Builder addNewsSourceAlert = new AlertDialog.Builder(this);
        final EditText editLabel = new EditText(NewsSectionActivity.this);
        final EditText editURL = new EditText(NewsSectionActivity.this);
        editLabel.setHint("Label");
        editLabel.setFilters(new InputFilter[] {new InputFilter.LengthFilter(64)});
        editLabel.setSingleLine();
        editLabel.setPadding(60, 30, 60, 30);
        editURL.setPadding(60, 30, 60, 30);
        editURL.setHint("RSS Feed URL");

        addNewsSourceAlert.setTitle("Add RSS Feed");
        addNewsSourceAlert.setView(editLabel);
        addNewsSourceAlert.setView(editURL);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editLabel);
        layout.addView(editURL);
        addNewsSourceAlert.setView(layout);

        addNewsSourceAlert.setPositiveButton("Add", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        txt1 = editLabel;
                        txt2 = editURL;
                        collectInput(newsSection2);
                    }
                });

        addNewsSourceAlert.setNegativeButton("Cancel", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        addNewsSourceAlert.show();
    }

    public void toRemove(View view){
        // setup the alert builder
        if (!newsSection2.isEmpty()) {
            AlertDialog.Builder removeNewsSourceAlert = new AlertDialog.Builder(this);
            removeNewsSourceAlert.setTitle("Remove RSS Feed");// add a checkbox list
            final String[] newsSection2List = new String[newsSection2.size()];
            final ArrayList<Integer> selectedItems = new ArrayList<>();
            for (int i = 0; i < newsSection2.size(); i++) {
                newsSection2List[i] = newsSection2.get(i).getSectionName();
            }
            boolean[] checkedItems = null;
            removeNewsSourceAlert.setMultiChoiceItems(newsSection2List, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked) {
                        selectedItems.add(which);
                    } else if (selectedItems.contains(which)) {
                        selectedItems.remove(which);
                    }
                }
            });
            removeNewsSourceAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeInput(newsSection2, selectedItems);
                }
            });
            removeNewsSourceAlert.setNegativeButton("Cancel", null);
            AlertDialog dialog = removeNewsSourceAlert.create();
            dialog.show();
        }

    }

    public void collectInput(ArrayList<NewsSectionData> newsSection2) {
        String getInput1 = txt1.getText().toString();
        String getInput2 = txt2.getText().toString();
        if (!(getInput1 == null || getInput1.trim().equals("") || getInput2 == null || getInput2.trim().equals(""))) {
            newsSection2.add(new NewsSectionData(getInput1, getInput2));
            newsSection.add(new NewsSectionData(getInput1, getInput2));
        }
        //newsSection.add(new NewsSectionData("MalaysiaKini News (English)", "https://www.malaysiakini.com/rss/en/news.rss"));
        //System.out.println(newsSection2 == null);
        newsSectionStorage.saveData();
        setupListView();
        //System.out.println(newsSection2.size());
   }

    public void removeInput(ArrayList<NewsSectionData> newsSection2, ArrayList<Integer> selectedItems) {
        for (int i = selectedItems.size()-1; i >= 0; i--) {
            int x = selectedItems.get(i);
            int y = selectedItems.get(i) + 9;
            newsSection2.remove(x);
            newsSection.remove(y);
        }
        newsSectionStorage.saveData();
        setupListView();
        //System.out.println(newsSection2.size());
    }





    @Override
    public void onBackPressed(){
        finish();
        super.onBackPressed();
        newsSectionStorage.setReading(false);
        Intent toMain = new Intent(this, MainActivity.class);
        startActivity(toMain);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void loadLastArticle(){
        currentArticle currentArticle = new currentArticle(this);

        if (currentArticle.loadReading()){
            Intent intent = new Intent(NewsSectionActivity.this, ArticleListingActivity.class);
            startActivity(intent);
        }
    }

}
