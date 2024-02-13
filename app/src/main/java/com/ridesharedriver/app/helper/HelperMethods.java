package com.ridesharedriver.app.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shubham.Shubham.claritus on 28/2/17 12:35 PM 3:58 PM.
 */

//Universal methods
public class HelperMethods {
    private static ProgressDialog dialog;

    public static void LogError(String tag, String details) {
        Log.e(tag, details);
    }

    @SuppressLint("HardwareIds")
    public static String GetDeviceId(Context context) {

        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getStringFromId(Context context, int id) {
        return context.getResources().getString(id);
    }


    public static void showToast_S(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast_L(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

   /* public static boolean isInternetConnectionWorking(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static boolean isInternetConnectionWorkingFrag(Activity activity) {

        if(activity!=null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connectivityManager != null;
            return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
        }
        return true;
    }*/

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email.trim());
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }


    public static boolean checkPassword(String password) {
        return TextUtils.isEmpty(password);
    }

    public static String formatDateString(Calendar calendar) {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        return sdf.format(calendar.getTime());
    }

    public static String formatDateStringFromString(String dateString) {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String returnString = "";
        try {
            Date date = new SimpleDateFormat("MM-dd-yyyy").parse(dateString);
            returnString = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnString;
    }

    public static String getDateInDDMMYYY(String dateString) {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String returnString = "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            returnString = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnString;
    }


    public static String formatHolidayDate(String dateString) {
        String myFormat = "dd-MMM (EEE)"; //In which you need put here
        Locale locale = new Locale("en", "UK");

        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
        dateFormatSymbols.setWeekdays(new String[]{
                " ",//due to 0 index
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
        });
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, dateFormatSymbols);
        String returnString = "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            returnString = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnString;
    }

    public static String formatNotificationDate(String dateString) {
        String myFormat = "EEE dd-MMM"; //In which you need put here
        Locale locale = new Locale("en", "US");

        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
        dateFormatSymbols.setWeekdays(new String[]{
                " ",//due to 0 index
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
        });
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, dateFormatSymbols);
        String returnString = "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            returnString = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnString;
    }

    public static Date formatDate(Calendar calendar) {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        try {
            return sdf.parse(sdf.format(calendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date formatDateFromString(String date) {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void showProgressDialog(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (dialog != null) {
                    try {
                        if (dialog.isShowing())
                            dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog = null;
                }

                dialog = new ProgressDialog(activity);
                dialog.setMessage("Loading...");
                //dialog.setIndeterminate(true);
                dialog.show();
                dialog.setCancelable(false);
            }
        });

    }

    public static void hideProgressDialog(Activity activity) {
        if (activity != null)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (dialog != null) {
                        try {
                            if (dialog.isShowing())
                                dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog = null;
                    }
                }
            });
    }


    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(LATITUDE, LONGITUDE, 5);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                /*for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)); //.append("\n");
                }*/
                sb.append(address.getAddressLine(0)); //.append("\n");
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getCountryName());
                return sb.toString();
            }
        } catch (IOException e) {
            Log.e("Location Address Loader", "Unable connect to Geocoder", e);
        }
        return "";

    }

    /*public static void performLogOut(Activity activity) {
        MySharedPref.saveThePreferenceValue(activity, activity.getResources().getString(R.string.userLoginStatus), "");
        MySharedPref.saveThePreferenceValue(activity, activity.getResources().getString(R.string.imagePath), "");
        Intent i = new Intent(activity, SplashActivity.class);
        activity.startActivity(i);
        activity.finish();
    }*/

    public static void openSettingForDinedPermission(Activity activity) {

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);

    }

    public static void closeKeyBoard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /*public static boolean checkPermissions(Activity activity, int REQUEST_PERMISSION_LOCATION) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(activity, REQUEST_PERMISSION_LOCATION);
            return false;
        }
    }

    public static void requestPermissions(Activity activity, int REQUEST_PERMISSION_LOCATION) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSION_LOCATION);
    }*/

    public static boolean checkGPSOnOffStatus(Activity activity) {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void showFetchingProgressDialog(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (dialog != null) {
                    try {
                        if (dialog.isShowing())
                            dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog = null;
                }

                dialog = new ProgressDialog(activity);
                dialog.setMessage("Please wait we are fetching your location...");
                //dialog.setIndeterminate(true);
                dialog.show();
                dialog.setCancelable(true);
            }
        });

    }

    public static void hideFetchingProgressDialog(Activity activity) {
        if (activity != null)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (dialog != null) {
                        try {
                            if (dialog.isShowing())
                                dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog = null;
                    }
                }
            });
    }

    public static void showSettingsAlert(final Activity activity) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(activity);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    /*public static void showForceUpdateDialog(final Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(activity.getString(R.string.youAreNotUpdatedTitle));
        alertDialogBuilder.setMessage(activity.getString(R.string.youAreNotUpdatedMessage) + activity.getString(R.string.youAreNotUpdatedMessage1));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
                dialog.cancel();
            }
        });
        alertDialogBuilder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.show();
    }*/

    public static String checkNull(String string) {
        string = string.trim();
        switch (string) {
            case "":
                return "";
            case "null":
                return "";
            default:
                return string;
        }
    }
}
