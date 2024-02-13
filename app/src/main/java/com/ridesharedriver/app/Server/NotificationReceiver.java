package com.ridesharedriver.app.Server;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ridesharedriver.app.R;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.custom.CheckConnection;
import com.ridesharedriver.app.pojo.last_ride.LastRideData;
import com.ridesharedriver.app.pojo.last_ride.LastRideResponse;
import com.ridesharedriver.app.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

//notification
public class NotificationReceiver extends WakefulBroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        playNotificationSound(context, intent);
//        Log.e("NOTIFICATION", intent.getStringExtra("action"));

    }

   // playing notification sound
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void playNotificationSound(Context context, Intent intent) {
        try {
            Ringtone r = null;
            Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Uri customSoundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.driver_chime_2);
            if (intent.getStringExtra("action").equalsIgnoreCase("Pending")) {
                r = RingtoneManager.getRingtone(context, customSoundUri);
            } else {
                r = RingtoneManager.getRingtone(context, defaultUri);
            }

            r.play();
            checkNotificationStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private LastRideData lastRideData=null;
    private String status="";
    public static CountDownTimer mCountDownTimerBackground = null;
    private int j = 0;
    private static int millisInFuture = 20000, countDownInterval = 1000;
    private String postedRideId="";


    //geting last ride
    public void getLastRide() {

        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<LastRideResponse> call =
                apiService.getLastRide("Bearer " + SessionManager.getKEY());
        call.enqueue(new Callback<LastRideResponse>() {
            @Override
            public void onResponse(Call<LastRideResponse> call, retrofit2.Response<LastRideResponse> response) {
                LastRideResponse jsonResponse = response.body();

                if (jsonResponse.getStatus()) {

                    lastRideData = jsonResponse.getData();
                    Log.e("LAST RIDE DATA: ", jsonResponse.getData().toString());


                        status = lastRideData.getStatus();
                        postedRideId = lastRideData.getRideId();
                        if (status.equalsIgnoreCase("pending")) {
                            Log.e("REJECTSTATUS", "reject ride accepted ");
                            rejectRide();
                        }


                } else {
                    //  Toast.makeText(requireContext(), "Error while getting last ride data.", Toast.LENGTH_LONG).show();
                    //  progressDialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<LastRideResponse> call, Throwable t) {

                //  Toast.makeText(requireContext(), "Failure while getting last ride data." + t.toString(), Toast.LENGTH_LONG).show();
                Log.e("Last Ride Data", t.toString());
                //  progressDialog.cancel();
            }
        });

    }


    //removing notification
    private void removeNotification()
    {
        NotificationManager notificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel("FireBaseService",1);
    }


    //rejecting ride
    private void rejectRide() {
        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            String url = Server.BASE_URL.concat("accept_ride");
            if (mCountDownTimerBackground != null) {
                mCountDownTimerBackground.cancel();
                removeNotification();
            }
            j = 0;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e("REJECT_Broadcast", "I was called.");
                        JSONObject data = new JSONObject(response);
                        Log.e("Response", "onResponse = \n " + response);
                    } catch (JSONException e) {
                        e.printStackTrace();

//                    Toast.makeText(getActivity(), getActivity().getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
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
            RetryPolicy policy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } else {

        }

    }


    //status of notificcation
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void checkNotificationStatus() {


        mCountDownTimerBackground = new CountDownTimer(millisInFuture, countDownInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("Log_tag", "Tick of Progress: " + j + " " + millisUntilFinished);
                j++;

            }

            @Override
            public void onFinish() {
                j++;
                getLastRide();
                Log.e("REJECTSTATUS", "reject ride accepted " + status);

            }
        };
        mCountDownTimerBackground.start();



    }
}