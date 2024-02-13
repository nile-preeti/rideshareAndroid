package com.ridesharedriver.app.acitivities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import com.ridesharedriver.app.R;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.custom.OnClearFromRecentService;
import com.ridesharedriver.app.pojo.upload_docs.DocStatusResponse;
import com.ridesharedriver.app.session.SessionManager;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import retrofit2.Call;
import retrofit2.Callback;

//Splash screen
public class SplashActivity extends ActivityManagePermission {
    private final static int SPLASH_TIME_OUT = 5000;
    String[] permissionAsk = {PermissionUtils.Manifest_CAMERA, Manifest.permission.POST_NOTIFICATIONS, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    String[] permissionAskfor13 = {PermissionUtils.Manifest_CAMERA, Manifest.permission.POST_NOTIFICATIONS, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    private VideoView videoView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.splash_activity);

    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView = findViewById(R.id.videoView);

        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.intro_rideshare;
        Uri videoUri = Uri.parse(videoPath);
        videoView.setVideoURI(videoUri);

        videoView.setOnCompletionListener(mp -> {
            if (!(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                RequestLocationWithDisclosure();
            } else {
                redirect();
            }
        });

        videoView.start();
    }

    //Permission Dialog
    private void RequestLocationWithDisclosure() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Location Permission");
        alert.setMessage("This application request location permission. It collects users precise" + " or approximate location data, " + "to calculate the travelled distance by that user." + " Ridesharerates collects this data when the Ridesharerates app is running" + " in the foreground (app open and on-screen) or background (app open but not on-screen) " + "of their mobile device.");
        alert.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Askpermission();
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                redirect();
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }


    //Permission Method
    public void Askpermission() {
        askCompactPermissions(permissionAsk, new PermissionResult() {
            @Override
            public void permissionGranted() {
                redirect();
            }

            @Override
            public void permissionDenied() {
                redirect();
            }

            @Override
            public void permissionForeverDenied() {
                redirect();
            }
        });
    }


    //redirect to Login Page
    private void redirect() {
        if (SessionManager.getStatus()) {
            getDocsStatus();
        } else {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        finish();
    }


    //here we get Document status of driver.
    public void getDocsStatus() {
        ApiNetworkCall apiService = ApiClient.getApiService();
        Call<DocStatusResponse> call = apiService.getDocsStatus("Bearer " + SessionManager.getKEY());
        call.enqueue(new Callback<DocStatusResponse>() {
            @Override
            public void onResponse(Call<DocStatusResponse> call, retrofit2.Response<DocStatusResponse> response) {
                DocStatusResponse jsonResponse = response.body();
                if (jsonResponse != null) {

                    if (jsonResponse.getStatus()) {
                        if (jsonResponse.getInsuranceExpiry() || jsonResponse.getLicenseExpiry() || jsonResponse.getCarExpiry() || jsonResponse.getInspectionExpiry()) {
                            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<DocStatusResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
            }
        });
    }
}
