package com.ridesharedriver.app.Server;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.O;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ridesharedriver.app.R;
import com.ridesharedriver.app.acitivities.HomeActivity;
import com.ridesharedriver.app.pojo.PendingRequestPojo;
import com.ridesharedriver.app.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by android on 18/4/17.
 */
public class   MyFirebaseMessagingService extends FirebaseMessagingService {
    public static int NOTIFICATION_ID = 1;
    private String TAG = "FireBaseService";
    private String postedRideId;
    MediaPlayer mediaPlayer;
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("ONMESSAGE", "-->On Message Called.."+remoteMessage.getData());

        Map<String, String> pushPayload = remoteMessage.getData();
        sendMessage();
        Log.e("MyFirebaseService", "onMessge " + pushPayload);
        try {
            sendNotification(remoteMessage.getData  ());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("TOKEN", s);
        sendRegistrationToServer(s);
    }


    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("custom-event-name1");
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }


    private Uri customSoundUri  = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
   //Uri customSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ocory_rington);


    private void sendNotification(Map<String, String> data) throws IOException {
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), customSoundUri);

        r.play();
        int num = ++NOTIFICATION_ID;
        Bundle msg = new Bundle();
        for (String key : data.keySet()) {
            Log.e(key, data.get(key));
            msg.putString(key, data.get(key));
        }

        Log.e("ACTION",msg.getString("action"));
        Intent intent = new Intent();

        if (msg.getString("action").equalsIgnoreCase("CANCELLED")) {
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (msg.containsKey("action")) {
            intent.putExtra("action", msg.getString("action"));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, num /* Request code */,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }else{
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, String.valueOf(NOTIFICATION_ID))
                        .setSmallIcon(R.drawable.img_logo)
                      //  .setContentTitle(msg.getString("title"))
                        .setContentTitle(msg.getString("title"))
                        .setContentText(msg.getString("msg"))
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        //  .setPublicVersion()
                        .setSound(customSoundUri, AudioManager.STREAM_ALARM)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent);


        if (msg.size() > 0) {
            Intent intent4 = new Intent(getApplicationContext(), HomeActivity.class);
            intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent4.putExtra("source", msg);
            if (msg.containsKey("action")) {
                intent4.putExtra("action", msg.getString("action"));
            }
            startActivity(intent4);
        }

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(String.valueOf(NOTIFICATION_ID),
                    "RideShare Notification", importance);
           mChannel.setSound(customSoundUri, audioAttributes);
            notificationManager.createNotificationChannel(mChannel);
          //  createNotificationChannel(notificationManager, pendingIntent);
        }


            notificationManager.notify(num, notificationBuilder.build());

    }


    //creating notification channel
    @RequiresApi(api = O)
    private void createNotificationChannel(NotificationManager notificationManager, PendingIntent pendingIntent) {
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName()
                + "/" + R.raw.driver_chime_2); //Here is FILE_NAME is the name of file that you want to play
// Create the NotificationChannel, but only on API 26+ because
// the NotificationChannel class is new and not in the support library if
        if (SDK_INT >= O) {
            CharSequence name = "RideshareRates";
            String description = "You have a new ride.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            NotificationChannel channel = new NotificationChannel(String.valueOf(NOTIFICATION_ID),
                    "RideshareRates Notification", importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(sound, audioAttributes);
            notificationManager.createNotificationChannel(channel);
            Notification mNotification = new Notification.Builder(getApplicationContext(), String.valueOf(NOTIFICATION_ID))
                    .setContentTitle("RideshareRates Notification")
                    .setContentText(description)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_ocory_launcher_foreground)
                    .build();
        }
    }



    // registration to server
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        SessionManager.initialize(getApplicationContext());
        RequestParams params = new RequestParams();
        SessionManager.setGcmToken(token);
        Server.setHeader(SessionManager.getKEY());
        params.put("user_id", SessionManager.getUserId());
        params.put("gcm_token", token);
        Server.postSync("api/user/update/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d(TAG, responseString);
            }
        });
    }

    public void getAcceptedRequest(String status) {

        String url = Server.BASE_URL.concat("rides?id=").concat("&status=").concat(status);
        /*String url = Server.BASE_URL.concat("rides?id=").concat(SessionManager.getUserId()).concat("&status=").concat(status)
                .concat("&utype=2");*/

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data = new JSONObject(response);
                    Log.e("Response Firebase ", "onResponse = \n " + response);

                    if (data.has("status") && data.getBoolean("status")) {
                        JSONArray jsonArray = data.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            PendingRequestPojo lists = new PendingRequestPojo();
                            List<String> a = new ArrayList<>();
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            postedRideId = jsonObject.getString("ride_id");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response ", "" + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String auth = "Bearer " + SessionManager.getKEY();
                headers.put("Authorization", auth);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    return params;
                } catch (Exception e) {
                    Log.e("failure", "Authentication failure.");
                }
                return super.getParams();
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private void rejectRide() {
        String url = Server.BASE_URL.concat("accept_ride");

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data = new JSONObject(response);
                    Log.e("Response", "onResponse = \n " + response);

                    if (data.has("status") && data.getBoolean("status")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response ", "" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();
                try {
                    params.put("driver_id", SessionManager.getUserId());
                    params.put("ride_id", postedRideId);
                    params.put("status", "CANCELLED");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String auth = "Bearer " + SessionManager.getKEY();
                headers.put("Authorization", auth);
                return headers;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
