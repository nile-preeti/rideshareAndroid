package com.ridesharedriver.app.custom;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class CallUtils {

    private static final int PERMISSION_REQUEST_CODE = 123;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isOnCall(Context context) {
        // Check if the READ_PHONE_STATE permission is granted
        if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            requestPhoneStatePermission(context);
            return false;
        }

        // Permission granted, check the call state
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int callState = telephonyManager.getCallState();
        return callState != TelephonyManager.CALL_STATE_IDLE;
    }

    private static void requestPhoneStatePermission(Context context) {
        // Request the READ_PHONE_STATE permission
        if (context instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
        }
    }

}
