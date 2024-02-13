package com.ridesharedriver.app.Server;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//network setup
public class Server {
    OkHttpClient okHttpClient = new OkHttpClient();
//    public static final String BASE_URL = "https://app.ridesharerates.com/staging_ridesharerates/"; // STAGING URL
    public static final String BASE_URL = "https://app.ridesharerates.com/"; // LIVE URL
    private static final String TAG = "server";
    private static AsyncHttpClient client = new AsyncHttpClient();
    public static final String FORMAT = "/format/json";
    public static final String APPROVE_PAYMENT = BASE_URL + "api/user/rides" + FORMAT;
    public static final String STATUS_CHANGE = BASE_URL + "api/user/rides" + FORMAT;
    public static final String UPDATE = BASE_URL + "api/user/update" + FORMAT;
    public static final String UPDATE_lAT_LONG = BASE_URL + "update_lat_long" ;
    public static final String FORGOT_PASSWORD = BASE_URL + "user/forgot_password" + FORMAT;
    public static final String GET_PROFILE = BASE_URL + "api/user/profile" + FORMAT;
    public static final String PASSWORD_RESET = BASE_URL + "api/user/change_password" + FORMAT;
    public static final String PAYMENT_HISTORY = BASE_URL + "payment_history" ;
    public static final String UPLOAD = BASE_URL + "uploadDocument";
    public static final String UPDATE_DRIVER_STATUS = BASE_URL + "update_driver_status"+ FORMAT;

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(20000);
        client.get(getAbsoluteUrl(url), params, responseHandler);
        Log.d(TAG, getAbsoluteUrl(url));
    }

    public static void postSync(String url, RequestParams params, JsonHttpResponseHandler jsonHttpResponseHandler) {
        try {
            SyncHttpClient client = new SyncHttpClient();
            client.post(getAbsoluteUrl(url), params, jsonHttpResponseHandler);
            Log.d(TAG, getAbsoluteUrl(url));
        } catch (Exception e) {
        }
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(20000);
        client.post(getAbsoluteUrl(url), params, responseHandler);
        Log.e(TAG, getAbsoluteUrl(url));
    }

    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void setHeader(String header) {
       client.addHeader("X-API-KEY", header);
    }

    public static void setAuthHeader(String header) {
        client.addHeader("Authorization", header);
    }

    public static void setContentType() {
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
    }

    String doGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }
}
