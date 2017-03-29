package com.example.android.moviedb.utilities;

import android.content.Context;
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

    /**
     * Helper method to check if the device is connected to the internet
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
     * @param finalUri is the url to be hit in String format
     * @return the unparsed JSON Response in String format
     */
    public static String makeHTTPRequest(String finalUri){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(finalUri)
                .build();

        String jsonData = null;
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful())
                jsonData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(jsonData != null)
            return jsonData;
        else
            return null;
    }
}
