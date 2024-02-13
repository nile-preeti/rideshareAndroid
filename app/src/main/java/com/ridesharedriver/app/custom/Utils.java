package com.ridesharedriver.app.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

/**
 * Created by android on 21/3/17.
 */

public class Utils {
    FirebaseAuth mAuth;

    Activity activity;
    private String TAG = "Utils";

    public Utils() {
    }

    public Utils(Activity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
    }

    public static boolean haveNetworkConnection(Context context) {
        boolean conntected = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null) {
            if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                conntected = true;
            } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                conntected = true;
            } else {
                conntected = false;
            }
        }
        return conntected;
    }

    public Boolean isAnonymouslyLoggedIn() {
        if (mAuth.getCurrentUser() == null) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());

                            }

                            // ...
                        }
                    });
        }


        return true;
    }

    public static void hideKeyboard(Context context, View view) {
        // hide virtual keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


    }

    public static void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof AppCompatButton) {
                // ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/AvenirLTStd_Book.otf"));
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/montserrat_bold.ttf"));
            } else if (v instanceof EditText) {
                // ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/AvenirLTStd_Medium.otf"));
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/montserrat_regular.ttf"));
            } else if (v instanceof TextView) {
                // ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/AvenirLTStd_Book.otf"));
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/montserrat_bold.ttf"));
            }

        } catch (Exception e) {

        }
    }

    public static String getformattedTime(String time) {
        String formattedtime = "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
            formattedtime = new SimpleDateFormat("HH:mm a").format(date);
        } catch (ParseException e) {
            Log.d("catch", e.toString());
        }

        return formattedtime;
    }


    public String getCurrentDateInSpecificFormat(String date) {

        Calendar currentCalDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            currentCalDate.setTime(sdf.parse(date));// all done
        } catch (ParseException e) {

        }

        String dayNumberSuffix = getDayNumberSuffix(currentCalDate.get(Calendar.DAY_OF_MONTH));
        DateFormat dateFormat = new SimpleDateFormat("d'" + dayNumberSuffix + "' MMM yyyy");
        return dateFormat.format(currentCalDate.getTime());
    }

    private String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static String getMinimumAddress(Context context, double lat, double longi) throws IOException {
        String address = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        addresses = geocoder.getFromLocation(lat, longi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String street = addresses.get(0).getSubLocality();


        city = city == null ? "" : city;
        street = street == null ? "" : street;
        state = state == null ? "" : state;
        postalCode = postalCode == null ? "" : postalCode;
        country = country == null ? "" : country;

        if (street == null) {
            address = city + ", " + state + " " + postalCode + ", " + country + ".";
        } else {
            address = street + ", " + city + ", " + state + " " + postalCode + ", " + country + ".";
        }
        return address;
    }

    public static String convertCurrency(String value) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setGroupingUsed(false);
        nf.setMinimumFractionDigits(2);
        String amount = nf.format(value);
        return amount;
    }


    public static boolean isValidMobile(String mobile) {
        String regex = "\\(\\d{3}\\)\\s\\d{3}-\\d{4}";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the string is empty
        // return false
        if (mobile == null) {
            return false;
        }
        // Pattern class contains matcher()
        //  method to find matching between
        //  given string and regular expression.
        Matcher m = p.matcher(mobile);

        // Return if the string
        // matched the ReGex
        return m.matches();
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.equalsIgnoreCase("null") || value.trim().length() == 0;
    }

}
