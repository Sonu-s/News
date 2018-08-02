package com.example.user.news;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

public class EntNewsActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> arrayList = new ArrayList<>();

    ArrayList<String> content = new ArrayList<>();

    ArrayList<String> arrayImages = new ArrayList<>();


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nevigation,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.homeNews){

            //https://sport.aktuality.sk/c/348289/tour-de-france-nibaliho-caka-na-buduci-tyzden-operacia-chce-stihnut-vueltu
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.sportNews)
        {

            Intent intent = new Intent(this,SportNewsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ent_news);

        setTitle("Entertainment");

        listView = findViewById(R.id.listView);
        // arrayList.add("news");
       // arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayList);
        //listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(),NewsActivity.class);
                intent.putExtra("content",content.get(i));
                startActivity(intent);



            }
        });


        DownloadTask task = new DownloadTask();
        task.execute("https://newsapi.org/v2/top-headlines?country=in&category=entertainment&apiKey=90411851ee1a476294eb7XXXXXX");
    }


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
                    // Log.i("_Url",newsUrl);
                    if(!(TextUtils.isEmpty(title)) && !(TextUtils.isEmpty(newsUrl)) && !(TextUtils.isEmpty(imageUrl))) {

                        arrayList.add(title);
                        content.add(newsUrl);
                        arrayImages.add(imageUrl);
                    }

                }

                MyAdapter adapter = new MyAdapter(getApplicationContext(),arrayList,arrayImages);
                listView.setAdapter(adapter);
               // arrayAdapter.notifyDataSetChanged();

            } catch (JSONException e) {

                e.printStackTrace();

            }
        }
    }
}
