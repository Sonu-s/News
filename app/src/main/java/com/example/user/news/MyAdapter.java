package com.example.user.news;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MyAdapter extends ArrayAdapter {

    ArrayList<String> title = new ArrayList<>();
    ArrayList<String>images = new ArrayList<>();
    Context mContext;

//    private DatabaseHandler dbHelper ;

    public MyAdapter(@NonNull Context context, ArrayList<String> title, ArrayList<String> images) {


        super(context, R.layout.listview_item);

        this.title= title;
        this.images = images;
        this.mContext = context;
      //  dbHelper = new DatabaseHandler(mContext);
    }

    @Override
    public int getCount() {
        return title.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        viewHolder holder = new viewHolder();

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);

            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.textView = convertView.findViewById(R.id.textView);

            convertView.setTag(holder);
        }else{

            holder = (viewHolder) convertView.getTag();
        }



        holder.textView.setText(title.get(position));

        DownloadImage task = new DownloadImage();
        Bitmap myImage;
        try {

            myImage = task.execute(images.get(position)).get();

          // dbHelper.insertBitmap(myImage);

            holder.imageView.setImageBitmap(myImage);

        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (ExecutionException e) {

            e.printStackTrace();
        }


        return convertView;

    }

    static class viewHolder{

        ImageView imageView;
        TextView textView;
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(in);

                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

