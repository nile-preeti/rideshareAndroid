package com.ridesharedriver.app.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ridesharedriver.app.Server.Server;
import com.ridesharedriver.app.acitivities.HomeActivity;
import com.ridesharedriver.app.acitivities.TrackingActivity;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.custom.Utils;
import com.ridesharedriver.app.fragement.AcceptedRequestFragment;
import com.ridesharedriver.app.fragement.PaymentDetail;
import com.ridesharedriver.app.pojo.PendingRequestPojo;
import com.ridesharedriver.app.pojo.changepassword.ChangePasswordResponse;
import com.ridesharedriver.app.session.SessionManager;
import com.ridesharedriver.app.tracker.TrackingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.ridesharedriver.app.fragement.HomeFragment.currentLatitude;
import static com.ridesharedriver.app.fragement.HomeFragment.currentLongitude;
import static com.ridesharedriver.app.session.SessionManager.getUserEmail;
import static com.ridesharedriver.app.session.SessionManager.getUserName;
import static com.ridesharedriver.app.session.SessionManager.initialize;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

//Accepts Adapter
public class AcceptedRequestAdapter extends RecyclerView.Adapter implements ActivityCompat.OnRequestPermissionsResultCallback {

    Context context;
    private List<PendingRequestPojo> list;
    AcceptedRequestFragment fragment;
    ProgressDialog progressDialog;
    String driverName = "", rideId = "", driverEmail = "";
    String sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude, contactNo;
    File AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    public static MediaPlayer mediaPlayer;
    boolean flag = true;
    SeekBar mSeekBar;

    public AcceptedRequestAdapter(Context context, List<PendingRequestPojo> list, AcceptedRequestFragment fragment) {
        this.context = context;
        this.list = list;
        this.fragment = fragment;

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.acceptedrequest_item, parent, false));

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.acceptedrequest_item, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Typeface font = Typeface.createFromAsset(context.getAssets(), "font/montserrat_regular.ttf");
        Typeface font1 = Typeface.createFromAsset(context.getAssets(), "font/AvenirLTStd_Book.otf");
        if (list.get(position).getStatus().equalsIgnoreCase("pending")) {
            ((Holder) holder).linear_layout.setVisibility(View.VISIBLE);
            ((Holder) holder).reject_img.setVisibility(View.VISIBLE);
            ((Holder) holder).accept_img.setVisibility(View.VISIBLE);
            ((Holder) holder).view3_border.setVisibility(View.VISIBLE);
            ((Holder) holder).track_img.setVisibility(View.GONE);
            ((Holder) holder).call_img.setVisibility(View.GONE);
            ((Holder) holder).start_img.setVisibility(View.GONE);
            ((Holder) holder).pause_recording_img.setVisibility(View.GONE);
            ((Holder) holder).navigate_img.setVisibility(View.GONE);
            ((Holder) holder).recording_img.setVisibility(View.GONE);
            ((Holder) holder).stop_recording_img.setVisibility(View.GONE);
            ((Holder) holder).accept_txt.setText("Accept Ride");
        } else if (list.get(position).getStatus().equalsIgnoreCase("accepted")) {
            ((Holder) holder).linear_layout.setVisibility(View.VISIBLE);
            ((Holder) holder).view3_border.setVisibility(View.VISIBLE);
            ((Holder) holder).reject_img.setVisibility(View.GONE);
            ((Holder) holder).track_img.setVisibility(View.VISIBLE);
            ((Holder) holder).call_img.setVisibility(View.VISIBLE);
            ((Holder) holder).start_img.setVisibility(View.VISIBLE);
            ((Holder) holder).accept_img.setVisibility(View.GONE);
            ((Holder) holder).navigate_img.setVisibility(View.GONE);
            ((Holder) holder).recording_img.setVisibility(View.GONE);
            ((Holder) holder).stop_recording_img.setVisibility(View.GONE);
            ((Holder) holder).pause_recording_img.setVisibility(View.GONE);

        } else if (list.get(position).getStatus().equalsIgnoreCase("start_ride")) {
            ((Holder) holder).linear_layout.setVisibility(View.VISIBLE);
            ((Holder) holder).view3_border.setVisibility(View.VISIBLE);
            ((Holder) holder).reject_img.setVisibility(View.GONE);
            ((Holder) holder).navigate_img.setVisibility(View.GONE);
            ((Holder) holder).accept_img.setVisibility(View.VISIBLE);
            ((Holder) holder).navigate_img.setVisibility(View.VISIBLE);
            ((Holder) holder).recording_img.setVisibility(View.VISIBLE);
            ((Holder) holder).stop_recording_img.setVisibility(View.GONE);
            ((Holder) holder).track_img.setVisibility(View.GONE);
            ((Holder) holder).call_img.setVisibility(View.GONE);
            ((Holder) holder).start_img.setVisibility(View.GONE);
            ((Holder) holder).pause_recording_img.setVisibility(View.GONE);
            ((Holder) holder).accept_txt.setText("Complete ride");
        } else if (list.get(position).getStatus().equalsIgnoreCase("completed")) {
            ((Holder) holder).accept_img.setVisibility(View.GONE);
//            ((Holder) holder).accept_txt.setText("Play recording");
            ((Holder) holder).reject_img.setVisibility(View.GONE);
            ((Holder) holder).track_img.setVisibility(View.GONE);
            ((Holder) holder).call_img.setVisibility(View.GONE);
            ((Holder) holder).start_img.setVisibility(View.GONE);
            ((Holder) holder).navigate_img.setVisibility(View.GONE);
            ((Holder) holder).recording_img.setVisibility(View.GONE);
            ((Holder) holder).stop_recording_img.setVisibility(View.GONE);
            ((Holder) holder).pause_recording_img.setVisibility(View.GONE);
            ((Holder) holder).view3_border.setVisibility(View.GONE);
        } else {
            ((Holder) holder).linear_layout.setVisibility(View.GONE);
            ((Holder) holder).reject_img.setVisibility(View.GONE);
            ((Holder) holder).track_img.setVisibility(View.GONE);
            ((Holder) holder).call_img.setVisibility(View.GONE);
            ((Holder) holder).accept_img.setVisibility(View.GONE);
            ((Holder) holder).start_img.setVisibility(View.GONE);
            ((Holder) holder).navigate_img.setVisibility(View.GONE);
            ((Holder) holder).recording_img.setVisibility(View.GONE);
            ((Holder) holder).stop_recording_img.setVisibility(View.GONE);
            ((Holder) holder).pause_recording_img.setVisibility(View.GONE);
            ((Holder) holder).view3_border.setVisibility(View.GONE);

        }

        Utils utils = new Utils();

        ((Holder) holder).from_add.setText(list.get(position).getPickup_adress());
        ((Holder) holder).to_add.setText(list.get(position).getDrop_address());
        ((Holder) holder).txt_username.setText(list.get(position).getUser_lastname());
        ((Holder) holder).time.setText("Pick up time");
        ((Holder) holder).date.setText(list.get(position).getTime());
//        ((Holder) holder).date.setText(utils.getCurrentDateInSpecificFormat(list.get(position).getTime()) + ", " +
//                Utils.getformattedTime(list.get(position).getTime()));


        ((Holder) holder).cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", list.get(position));

                PaymentDetail detailFragment = new PaymentDetail();
                detailFragment.setArguments(bundle);
                ((HomeActivity) holder.itemView.getContext()).changeFragment(detailFragment, "Ride Detail");
            }
        });

        sourceLatitude = list.get(position).getPickup_lat();
        sourceLongitude = list.get(position).getPickup_long();
        destinationLatitude = list.get(position).getDrop_lat();
        destinationLongitude = list.get(position).getDrop_long();
        contactNo = list.get(position).getUser_mobile();
        rideId = list.get(position).getRide_id();

//        if ((list.get(position).getAudio().size() == 0) &&
//                list.get(position).getStatus().equalsIgnoreCase("completed")) {
//            ((Holder) holder).linear_layout.setVisibility(View.GONE);
//            ((Holder) holder).accept_img.setVisibility(View.GONE);
//
//        } else if ((list.get(position).getAudio().size() != 0) && list.get(position).getStatus().equalsIgnoreCase("completed")) {
//            ((Holder) holder).accept_img.setVisibility(View.VISIBLE);
//            ((Holder) holder).linear_layout.setVisibility(View.VISIBLE);
//        }
        ((Holder) holder).accept_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((Holder) holder).accept_txt.getText().toString().equalsIgnoreCase("accept ride")) {
                    progressDialog.show();

                    String url = Server.BASE_URL.concat("accept_ride");

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.getCache().clear();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject data = new JSONObject(response);

                                initialize(getApplicationContext());


                                driverName = getUserName();
                                rideId = list.get(position).getRide_id();
                                driverEmail = getUserEmail();

                                if (data.has("status") && data.getBoolean("status")) {
                                    Toast.makeText(context, data.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, data.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                stopPlaying();
                                startLocationService();
                                ((AcceptedRequestFragment) fragment).getAcceptedRequest("PENDING","10","1");
                                progressDialog.dismiss();


                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                Toast.makeText(context, context.getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Response ", "" + error.getMessage());
                            progressDialog.dismiss();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            HashMap<String, String> params = new HashMap<String, String>();
                            try {
                                params.put("driver_id", SessionManager.getUserId());
                                params.put("ride_id", list.get(position).getRide_id());
                                params.put("status", "ACCEPTED");
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
                }
//                else if (((Holder) holder).accept_txt.getText().toString().equalsIgnoreCase("Play recording")) {
//                    mediaPlayer = new MediaPlayer();
//                    try {
//                        String file = list.get(position).getAudio().get(0);
//                        mediaPlayer.setDataSource(String.valueOf(file));
//                        ((Holder) holder).accept_txt.setText("Stop Recording");
//                        ((Holder) holder).pause_recording_img.setVisibility(View.VISIBLE);
//                        Log.d("file", file);
//
//                        mediaPlayer.prepare();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    mediaPlayer.start();
//                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mediaPlayer) {
//                            ((Holder) holder).accept_txt.setText("Play Recording");
//                            ((Holder) holder).pause_recording_txt.setText("Pause");
//                            ((Holder) holder).pause_recording_img.setVisibility(View.GONE);
//
//                        }
//                    });
//                } else if (((Holder) holder).accept_txt.getText().toString().equalsIgnoreCase("Stop Recording")) {
//                    if (mediaPlayer != null) {
//                        mediaPlayer.stop();
//                        mediaPlayer.reset();
//                        mediaPlayer.release();
//                        ((Holder) holder).accept_txt.setText("Play Recording");
//                        ((Holder) holder).pause_recording_txt.setText("Pause");
//                        ((Holder) holder).pause_recording_img.setVisibility(View.GONE);
//                        MediaRecorderReady();
//                    }
//                }
                else {
                    progressDialog.show();

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
                                    Toast.makeText(context, data.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, data.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                ((AcceptedRequestFragment) fragment).getAcceptedRequest("ACCEPTED","10","1");
                                stopLocationService();
                                progressDialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                Toast.makeText(context, context.getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Response ", "" + error.getMessage());
                            progressDialog.dismiss();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            HashMap<String, String> params = new HashMap<String, String>();
                            try {
                                params.put("driver_id", SessionManager.getUserId());
                                params.put("ride_id", list.get(position).getRide_id());
                                params.put("status", "COMPLETED");
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
                }
            }
        });


        ((Holder) holder).track_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String uri = "http://maps.google.com/maps?saddr=" + currentLatitude + "," + currentLongitude + "&daddr=" + sourceLatitude + "," + sourceLongitude;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    }
                }, 1000);
            }
        });

        ((Holder) holder).recording_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setMessage("Start recording is on please do not kill your app")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startRecording();
                                ((Holder) holder).stop_recording_img.setVisibility(View.VISIBLE);
                                ((Holder) holder).recording_img.setVisibility(View.GONE);
                            }
                        }).show();
            }
        });

        ((Holder) holder).pause_recording_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    ((Holder) holder).pause_recording_txt.setText("Resume");
                } else {
                    ((Holder) holder).pause_recording_txt.setText("Pause");
                    mediaPlayer.start();
                }
            }
        });
        ((Holder) holder).stop_recording_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
                ((Holder) holder).recording_img.setVisibility(View.VISIBLE);
                ((Holder) holder).stop_recording_img.setVisibility(View.GONE);

            }
        });

        ((Holder) holder).navigate_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String uri = "http://maps.google.com/maps?saddr=" + currentLatitude + "," + currentLongitude + "&daddr=" + destinationLatitude + "," + destinationLongitude;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    }
                }, 1000);
            }
        });

        ((Holder) holder).start_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = Server.BASE_URL.concat("accept_ride");

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.getCache().clear();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);

                            if (data.has("status") && data.getBoolean("status")) {
                                Toast.makeText(context, data.getString("message"), Toast.LENGTH_SHORT).show();
                                ((Holder) holder).accept_img.setVisibility(View.VISIBLE);
                                ((Holder) holder).start_img.setVisibility(View.GONE);
                                ((Holder) holder).track_img.setVisibility(View.GONE);
                                ((Holder) holder).call_img.setVisibility(View.GONE);
                                ((Holder) holder).navigate_img.setVisibility(View.VISIBLE);
                                ((Holder) holder).accept_txt.setText("Complete ride");
                            } else {
                                Toast.makeText(context, data.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            ((AcceptedRequestFragment) fragment).getAcceptedRequest("start_ride","10","1");
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(context, context.getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response ", "" + error.getMessage());
                        progressDialog.dismiss();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String, String> params = new HashMap<String, String>();
                        try {
                            params.put("driver_id", SessionManager.getUserId());
                            params.put("ride_id", list.get(position).getRide_id());
                            params.put("status", "START_RIDE");
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

            }
        });

        ((Holder) holder).call_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contactNo));
                context.startActivity(intent);

                if (isPermissionGranted()) {
                    call_action();
                }
            }
        });

        ((Holder) holder).reject_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();

                String url = Server.BASE_URL.concat("accept_ride");

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.getCache().clear();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            Log.e("Response", "onResponse = \n " + data.toString());

                            if (data.has("count_ride") && data.getString("count_ride").equalsIgnoreCase("2")) {
                                AlertDialogCreate("Are you sure?", "Are you sure you want to reject ride and be online?", "");
                            }

                            if (data.has("status") && data.getBoolean("status")) {
                                Toast.makeText(context, data.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, data.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            ((AcceptedRequestFragment) fragment).getAcceptedRequest("PENDING","10","1");
                            stopLocationService();
                            progressDialog.dismiss();
                            stopPlaying();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(context, context.getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response ", "" + error.getMessage());
                        progressDialog.dismiss();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String, String> params = new HashMap<String, String>();
                        try {
                            params.put("driver_id", SessionManager.getUserId());
                            params.put("ride_id", list.get(position).getRide_id());
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
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView txt_username, from_tv, from_add, to_tv, to_add, date, time, accept_txt, username_tv, recording_txt, pause_recording_txt;
        LinearLayout reject_img, accept_img, linear_layout, track_img, call_img, start_img, navigate_img, pause_recording_img;
        LinearLayout recording_img, stop_recording_img;
        View view3_border;
        CardView cardView;

        public Holder(View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.txt_username);
            from_tv = itemView.findViewById(R.id.from_tv);
            from_add = itemView.findViewById(R.id.txt_from_add);
            to_tv = itemView.findViewById(R.id.to_tv);
            to_add = itemView.findViewById(R.id.txt_to_add);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            reject_img = itemView.findViewById(R.id.reject_img);
            navigate_img = itemView.findViewById(R.id.navigate_img);
            call_img = itemView.findViewById(R.id.call_img);
            track_img = itemView.findViewById(R.id.track_img);
            start_img = itemView.findViewById(R.id.start_img);
            accept_img = itemView.findViewById(R.id.accept_img);
            recording_img = itemView.findViewById(R.id.recording_img);
            linear_layout = itemView.findViewById(R.id.linear_layout);
            accept_txt = itemView.findViewById(R.id.accept_txt);
            username_tv = itemView.findViewById(R.id.username_tv);
            recording_txt = itemView.findViewById(R.id.recording_txt);
            stop_recording_img = itemView.findViewById(R.id.stop_recording_img);
            mSeekBar = itemView.findViewById(R.id.seekBar);
            pause_recording_img = itemView.findViewById(R.id.pause_recording_img);
            pause_recording_txt = itemView.findViewById(R.id.pause_recording_txt);
            view3_border = itemView.findViewById(R.id.view);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public void BookFont(Holder holder, TextView view1) {
        // Typeface font1 = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Book.otf");
        Typeface font1 = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/montserrat_bold.ttf");
        view1.setTypeface(font1);
    }

    public void MediumFont(Holder holder, TextView view) {
        // Typeface font = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Medium.otf");
        Typeface font = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/montserrat_regular.ttf");
        view.setTypeface(font);
    }

    public void call_action() {
        String phnum = contactNo;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phnum));
        context.startActivity(callIntent);
    }

    //for permission
    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {

                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }

    //Starting Tracking Activity
    private void startLocationService() {
        Intent intent = new Intent(getApplicationContext(), TrackingActivity.class);
        intent.putExtra("driver_name", driverName);
        intent.putExtra("ride_id", rideId);
        intent.putExtra("driver_email", driverEmail);
        context.startActivity(intent);
    }

    //Stoping Tracking Service
    private void stopLocationService() {
        Intent myService = new Intent(getApplicationContext(), TrackingService.class);
        context.stopService(myService);
    }

    ////for Permission
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }


    private void requestPermission() {

        ActivityCompat.requestPermissions((Activity) context, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(getApplicationContext(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    //media recorder
    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mediaRecorder.setOutputFile(AudioSavePathInDevice);
        }
        // mediaRecorder.setOutputFile(getFilename());
    }

    //recoding
    public void startRecording() {
        random = new Random();
        if (checkPermission()) {
            try {
                flag = false;
                AudioSavePathInDevice =
                        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Camera/" +
                                CreateRandomAudioFileName(5) + "AudioRecording.ogg");

                if (!AudioSavePathInDevice.getParentFile().exists())
                    AudioSavePathInDevice.getParentFile().mkdirs();
                if (!AudioSavePathInDevice.exists())
                    AudioSavePathInDevice.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MediaRecorderReady();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            Toast.makeText(getApplicationContext(), "Recording started",
                    Toast.LENGTH_LONG).show();

            Log.d("path", String.valueOf(AudioSavePathInDevice));
        } else {
            requestPermission();
        }
    }

    public void stopRecording() {
        try {
            mediaRecorder.stop();
        } catch (Exception exception) {
        }
        Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setIcon(drawable)
                .setTitle("Save Recording")
                .setMessage("Are you sure you want to save recording?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        uplaodRecordingToServer();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

        Toast.makeText(getApplicationContext(), "Recording Completed",
                Toast.LENGTH_LONG).show();
    }

    //seting data
    public void setData(List<PendingRequestPojo> pendingRequestPojoList)
    {
        this.list = pendingRequestPojoList;
        notifyDataSetChanged();
    }

    //uploding recording to server.
    private void uplaodRecordingToServer() {
        String token = "Bearer " + SessionManager.getKEY();
        Log.d("token", token);
//        final ProgressDialog loading = ProgressDialog.show(getApplicationContext(), "Please wait...", "Uploading data...", false, false);

        ApiNetworkCall apiService = ApiClient.getApiService();
        RequestBody ride_id = RequestBody.create(
                MediaType.parse("text/plain"),
                rideId);


        MultipartBody.Part fileToUpload;
        //empty file
        RequestBody empty_file = RequestBody.create(
                MediaType.parse("audio/*"),
                "");

        if (AudioSavePathInDevice != null) {
            fileToUpload = MultipartBody.Part.createFormData("audio", AudioSavePathInDevice.getName(),
                    RequestBody.create(MediaType.parse("audio/*"), AudioSavePathInDevice));
        } else {
            fileToUpload = MultipartBody.Part.createFormData("audio", "", empty_file);
        }

        Call<ChangePasswordResponse> call = apiService.uploadRecording(token, fileToUpload, ride_id);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                ChangePasswordResponse requestResponse = response.body();
                Toast.makeText(context, requestResponse.getMessage(), Toast.LENGTH_LONG).show();
                //loading.cancel();
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                // progressBar.setVisibility(View.GONE);
                //  loading.cancel();
                Log.d("Failed", "RetrofitFailed");
            }
        });

    }

    //alert dialog
    public void AlertDialogCreate(String title, String message, final String status) {
        Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setIcon(drawable)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(context.getString(R.string.cancel), null)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
