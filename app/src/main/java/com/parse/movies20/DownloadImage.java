package com.parse.movies20;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jakob on 2017-01-20.
 */

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(String... urls) {

        try {

            URL url = new URL(urls[0]);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }
}