package com.ridesharedriver.app.custom;

import android.content.Context;
import android.graphics.Typeface;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.widget.AppCompatButton;

/**
 * Created by android on 12/4/17.
 */

public class SetCustomFont {
    public void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextInputEditText) {
               // ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/AvenirLTStd_Medium.otf"));
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/montserrat_regular.ttf"));
            } else if (v instanceof AppCompatButton) {
               // ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/AvenirLTStd_Book.otf"));
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/montserrat_bold.ttf"));
            } else if (v instanceof EditText) {
              //  ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/AvenirLTStd_Medium.otf"));
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/montserrat_regular.ttf"));
            } else if (v instanceof TextView) {
              //  ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/AvenirLTStd_Medium.otf"));
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/montserrat_regular.ttf"));
            }
        } catch (Exception e) {
            Log.d("catch","font   "+e.toString());
        }
    }

}
