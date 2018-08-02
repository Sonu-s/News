package com.example.user.news;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> arrayList = new ArrayList<>();

    ArrayList<String> content = new ArrayList<>();
    ArrayList<String> arrayImages = new ArrayList<>();

   // SQLiteDatabase articleDB ;
    MyAdapter adapter;

    SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nevigation,menu);

       // listView.setFastScrollEnabled(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       if (item.getItemId() == R.id.sportNews){
          // Intent intent = new Intent(this,SportNewsActivity.class);

           Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.user.instagramclone");
           if (launchIntent != null) {
               startActivity(launchIntent);//null pointer check in case package name was not found
           }

          // startActivity(intent);

       }else if (item.getItemId() == R.id.entertainment){

           Intent intent = new Intent(this,EntNewsActivity.class);
           startActivity(intent);
       }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Home news");

        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);


        listView = findViewById(R.id.listView);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

         adapter = new MyAdapter(MainActivity.this,arrayList,arrayImages);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(),NewsActivity.class);
                intent.putExtra("content",content.get(i));
                startActivity(intent);

            }
        });
       // articleDB = this.openOrCreateDatabase("Articles",MODE_PRIVATE,null);
       // articleDB.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY ,articleId VARCHAR, title VARCHAR,content VARCHAR)");

       // updateListView();
        DownloadTask task = new DownloadTask();
        task.execute("https://newsapi.org/v2/top-headlines?country=in&apiKey=90411851ee1a476294ebxxxxxxx");

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shuffle();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


    }


   /* public void updateListView(){

        Cursor c = articleDB.rawQuery("SELECT * FROM articles",null);

        int contentIndex = c.getColumnIndex("content");
        int titleIndex = c.getColumnIndex("title");
        int imageIndex = c.getColumnIndex("articleId");
        if(c.moveToFirst()){

            arrayImages.clear();
            arrayList.clear();
            content.clear();
            do {

                arrayList.add(c.getString(titleIndex));
                content.add(c.getString(contentIndex));
                arrayImages.add(c.getString(imageIndex));

            }while (c.moveToNext());

            adapter.notifyDataSetChanged();

        }
    }*/

    public void shuffle(){

        Collections.shuffle(arrayList,new Random(System.currentTimeMillis()));

    }

    public class DownloadTask extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {

                    char ch = (char) data;
                    result += ch;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            JSONObject jsonObject = null;
          //  articleDB.execSQL("DELETE FROM articles");
            try {
                jsonObject = new JSONObject(result);

            String newsArticles = jsonObject.getString("articles");

            JSONArray jsonArray = new JSONArray(newsArticles);

            for (int i =0 ;i <jsonArray.length(); i++){



                JSONObject jTitle = jsonArray.getJSONObject(i);
                String title= jTitle.getString("title");
                String newsUrl = jTitle.getString("url");
                String imageUrl = jTitle.getString("urlToImage");
                if(!(TextUtils.isEmpty(title)) && !(TextUtils.isEmpty(newsUrl)) && !(TextUtils.isEmpty(imageUrl))) {

                    arrayList.add(title);
                    content.add(newsUrl);
                    arrayImages.add(imageUrl);

                   /* String sql = "INSERT INTO articles (articleId,title, content) VALUES (?, ?, ? )";
                    SQLiteStatement statement = articleDB.compileStatement(sql);

                    statement.bindString(1,imageUrl);
                    statement.bindString(2,title);
                    statement.bindString(3,newsUrl);
                    statement.execute(); */
                }

            }

                adapter.notifyDataSetChanged();

            } catch (Exception e) {

                e.printStackTrace();

            }
        }
    }

}