package com.ridesharedriver.app.custom;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

//import com.crashlytics.android.Crashlytics;
import com.ridesharedriver.app.R;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.pojo.AppVersionUpdateResponse;
import com.ridesharedriver.app.pojo.CheckDeviceTokenResponse;
import com.ridesharedriver.app.session.SessionManager;
import com.mapbox.mapboxsdk.Mapbox;


import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDexApplication;

import static com.ridesharedriver.app.acitivities.HomeActivity.updateDriverLatLong;

import retrofit2.Call;
import retrofit2.Callback;

//import io.fabric.sdk.android.Fabric;

/**
 * Created by android on 15/3/17.
 */

public class MyApplication extends MultiDexApplication {

    Timer timer;
    public static TimerTask timerTask;
    Handler hand = new Handler(Looper.getMainLooper());

    String sCurrentVersion, sLatestVersion;
    String userDeviceToken;
    Boolean userLoggedIn = false;


    @Override
    public void onCreate() {
        super.onCreate();
        // Fabric.with(this, new Crashlytics());
        Mapbox.getInstance(getApplicationContext(), String.valueOf(R.string.mapboxkey));

        SessionManager.initialize(getApplicationContext());
        appVersionUpdateApi();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.e("TIMER", "RUNNING");
                hand.post(new Runnable() {
                    @Override
                    public void run() {

                        userLoggedIn = SessionManager.getStatus();
                        if (userLoggedIn){
                            userDeviceToken = SessionManager.getDeviceId();
                            updateDriverLatLong();
                            checkDeviceTokenApi();
                        }

                    }
                });

            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 15 * 1000); //15 Second

    }

    public static boolean isM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return true;
        return false;
    }

    public static boolean isO() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            return true;
        return false;
    }



    public void checkDeviceTokenApi() {

        // final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            // progressDialog.setMessage("Session checking.....");
            //progressDialog.setCancelable(false);
            //progressDialog.show();

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<CheckDeviceTokenResponse> call =
                    apiService.checkDeviceToken("Bearer " + SessionManager.getKEY());
            call.enqueue(new Callback<CheckDeviceTokenResponse>() {
                @Override
                public void onResponse(Call<CheckDeviceTokenResponse> call, retrofit2.Response<CheckDeviceTokenResponse> response) {
                    CheckDeviceTokenResponse jsonResponse = response.body();
                    Log.d("response",response.toString());
                    if (jsonResponse != null) {
                        Log.d("TakeResponse",jsonResponse.toString());
                        if (jsonResponse.getStatus()) {
//                            Log.d("token",auth_token.toString());
                            Log.d("deviceID",jsonResponse.getDevice_token());
                            Log.d("deviceID",userDeviceToken);
                            if (!jsonResponse.getDevice_token().toString().equals(userDeviceToken)){
                                SessionManager.logoutUser(getApplicationContext());
                            }
                            //progressDialog.cancel();
                            //Toast.makeText(HomeActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            //progressDialog.cancel();
                            Toast.makeText(getApplicationContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }

                }

                @Override
                public void onFailure(Call<CheckDeviceTokenResponse> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                    //   progressDialog.cancel();
                }
            });
        } else {
            //progressDialog.cancel();
            Toast.makeText(getApplicationContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
            // progressDialog.dismiss();
        }
    }

    public void appVersionUpdateApi() {

        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {

            Map<String, String> param = new HashMap();
            param.put("device_type", "android");
            param.put("app_version", BuildConfig.VERSION_NAME.toString());//
            param.put("user_type", "2");

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<AppVersionUpdateResponse> call =

                    apiService.updateAppVersionApi("Bearer " + SessionManager.getKEY(), param);
            call.enqueue(new Callback<AppVersionUpdateResponse>() {
                @Override
                public void onResponse(Call<AppVersionUpdateResponse> call, retrofit2.Response<AppVersionUpdateResponse> response) {
                    AppVersionUpdateResponse jsonResponse = response.body();
                    Log.d("response", response.toString());
                    if (jsonResponse != null) {

                    }

                }

                @Override
                public void onFailure(Call<AppVersionUpdateResponse> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                }
            });
        } else {
            //progressDialog.cancel();
            Toast.makeText(getApplicationContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
            // progressDialog.dismiss();
        }
    }

}
