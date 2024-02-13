package com.ridesharedriver.app.pojo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

public class Global {
    public static void putAlertValue(String key, boolean value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Show_Alert", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public static boolean getAlertValue(String key, boolean defaultVal, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Show_Alert", Activity.MODE_PRIVATE);
        return prefs.getBoolean(key, defaultVal);
    }

    public static void putDistanceVisibility(String key, boolean value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Show_Visibility", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public static boolean getDistanceVisibility(String key, boolean defaultVal, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Show_Visibility", Activity.MODE_PRIVATE);
        return prefs.getBoolean(key, defaultVal);
    }
    public static int getImageOrientation(String imagePath) {
        int rotate = 0;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static Bitmap checkRotationFromCamera(Bitmap bitmap, int rotate) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }
}
