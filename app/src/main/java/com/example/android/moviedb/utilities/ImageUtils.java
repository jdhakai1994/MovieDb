package com.example.android.moviedb.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;

/**
 * Created by Jayabrata Dhakai on 4/4/2017.
 */

public class ImageUtils {

    /**
     * Helper Method to convert bitmap to byte Array
     * @param bitmap includes the poster/backdrop image
     * @return byte array to saved in the database
     */
    public static byte[] getImageBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    /**
     * Helper Method to convert byte Array to bitmap
     * @param image holds the image returned as byte array from database
     * @return the bitmap version of image
     */
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * Helper method to count the number of columns required to be displayed
     * @param context the application context
     * @return number of columns
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 200);
        return Math.max(2, noOfColumns);
    }
}
