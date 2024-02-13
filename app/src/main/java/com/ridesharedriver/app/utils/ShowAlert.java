package com.ridesharedriver.app.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

public class ShowAlert {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public  void showAlert(Context context, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context,android.R.style.Theme_DeviceDefault_Dialog_Alert);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
