package com.example.user.news;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SportNewsActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> arrayList = new ArrayList<>();
    MyAdapter adapter;
    ArrayList<String> content = new ArrayList<>();
    ArrayList<String> arrayImages = new ArrayList<>();
   // SQLiteDatabase articleDB ;

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nevigation,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.homeNews){

            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);

       }else if (item.getItemId() == R.id.entertainment){

            Intent intent = new Intent(this,EntNewsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_news);

        setTitle("Sport news");

        listView = findViewById(R.id.listView);
         adapter = new MyAdapter(getApplicationContext(),arrayList,arrayImages);
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
        task.execute("https://newsapi.org/v2/top-headlines?country=in&category=sports&apiKey=90411851ee1a476294eb7XXXXXXX");
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
    }  */



    public class DownloadTask extends AsyncTask<String, Void, String> {


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
                        // Log.i("_Url",newsUrl);

                       arrayList.add(title);
                        content.add(newsUrl);

                        arrayImages.add(imageUrl);
                      /*  String sql = "INSERT INTO articles (articleId,title, content) VALUES (?, ?, ? )";
                        SQLiteStatement statement = articleDB.compileStatement(sql);

                        statement.bindString(1,imageUrl);
                        statement.bindString(2,title);
                        statement.bindString(3,newsUrl);
                        statement.execute(); */
                    }

                }

                adapter.notifyDataSetChanged();

            } catch (JSONException e) {

                e.printStackTrace();

            }
        }
    }
}
