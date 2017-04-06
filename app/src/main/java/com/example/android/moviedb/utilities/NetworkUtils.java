package com.example.android.moviedb.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jayabrata Dhakai on 2/26/2017.
 */

public class NetworkUtils {

    public static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    private static OkHttpClient client = new OkHttpClient();

    /**
     * Helper method to check if the device is connected to the internet
     *
     * @param context has the context of the calling activity or fragment
     * @return true if the device is connected to internet else false
     */
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Helper Method to fetch movie data using OKHttpClient
     *
     * @param finalUrl is the url to be hit in String format
     * @return the unparsed JSON Response in String format
     */
    public static String makeHTTPRequest(String finalUrl) {

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        String jsonData = null;
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful())
                jsonData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (jsonData != null)
            return jsonData;
        else
            return null;
    }
/*
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
*/
    public static Bitmap getBitmapFromURL(String imageUrl) {

        Request request = new Request.Builder()
                .url(imageUrl)
                .build();

        Bitmap bitmap = null;
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful())
                bitmap = BitmapFactory.decodeStream(response.body().byteStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap != null)
            return bitmap;
        else
            return null;
    }
}
