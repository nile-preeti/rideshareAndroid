package com.ridesharedriver.app.fragement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.model.Distance;
import com.ridesharedriver.app.acitivities.UploadDocs;
import com.ridesharedriver.app.acitivities.WebView;
import com.ridesharedriver.app.custom.GetDumaWorkManager;
import com.ridesharedriver.app.interfaces.SetOnlineStatusInterface;
import com.ridesharedriver.app.pojo.CallingRiderResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.ridesharedriver.app.R;
import com.ridesharedriver.app.Server.Server;
import com.ridesharedriver.app.acitivities.HomeActivity;
import com.ridesharedriver.app.acitivities.TrackingActivity;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.custom.CheckConnection;
import com.ridesharedriver.app.custom.GPSTracker;
import com.ridesharedriver.app.custom.Utils;
import com.ridesharedriver.app.pojo.Global;
import com.ridesharedriver.app.pojo.PendingRequestPojo;
import com.ridesharedriver.app.pojo.changepassword.ChangePasswordResponse;
import com.ridesharedriver.app.pojo.driverEarning.DriverEarningResponse;
import com.ridesharedriver.app.pojo.getRideStatus.GetRideStatus;
import com.ridesharedriver.app.pojo.google.DistanceMatrixResponse;
import com.ridesharedriver.app.pojo.last_ride.LastRideData;
import com.ridesharedriver.app.pojo.last_ride.LastRideResponse;
import com.ridesharedriver.app.session.SessionManager;
import com.ridesharedriver.app.tracker.TrackingService;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ridesharedriver.app.utils.ShowServiceAlert;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import cz.msebera.android.httpclient.Header;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.ridesharedriver.app.Server.NotificationReceiver.mCountDownTimerBackground;
import static com.ridesharedriver.app.session.SessionManager.getKEY;
import static com.ridesharedriver.app.session.SessionManager.getSwitchState;
import static com.ridesharedriver.app.session.SessionManager.getUserEmail;
import static com.ridesharedriver.app.session.SessionManager.getUserName;
import static com.ridesharedriver.app.session.SessionManager.initialize;
import static com.ridesharedriver.app.tracker.TrackingService.IS_ACTIVITY_RUNNING;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

//Home
public class HomeFragment extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback, Animation.AnimationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, SetOnlineStatusInterface {

    private static final String LOCATION_UPDATE = "location_update";
    private String licenceApproveStatus = "1", insuranceApproveStatus = "1", carRegistrationApproveStatus = "1", inspectionApproveStatus = "1", verificationIdApproveStatus = "1", backgroundApproveStatus = "1";
    private boolean licenceStatus = false, carRegStatus = false, insuranceStatus = false, identityStatus = false, userStatus = false, inspectionStatus = false;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1234;
    private PassDataToActivity passDataToActivity;
    String WAKING = "waking";
    private static int first_online = 0;
    public String NETWORK;
    public String ERROR = "error occurred", pickupAddress = "", destinationAddress = "";
    public String TRYAGAIN;
    Boolean flag = false;
    GoogleMap myMap;
    ImageView current_location, clear;
    MapView mMapView;
    //TO keep screen alive while riding
    PowerManager pm;
    PowerManager.WakeLock wl;
    private CountDownTimer mCountDownTimer = null;
    private int j = 0;
    private static int millisInFuture = 20000;
    private static final int countDownInterval = 1000;

    int i = 0;
    String result = "";
    Animation animFadeIn, animFadeOut;
    String TAG = "home";
    LinearLayout linear_request, upload_doc_layout;
    ImageView goto_upload_img;
    private Bundle bundle;
    String[] permissionAsk = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    CardView rides, earnings, pending_amount_layout;
    private final String driver_id = "";
    private final String cost = "";
    private final String unit = "";
    String driverName = "", rideId = "", driverEmail = "", postedRideId;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static Double currentLatitude;
    public static Double currentLongitude;
    private View rootView;
    private final String check = "";
    private String drivername = "", contactNo;
    private String ridername = "";
    private Marker my_marker;
    private final boolean isShown = true;
    ProgressDialog progressDialog, waitingProgressDialog;
    TextView textView_today, txtDriverName, textView_week, textView_overall, textView_totalride, txt_from_add, txt_to_add, rideFare, rideDistance, riderName, is_online, textViewAmountPending, txtRest, txt_required_doc_message, doneBtn, skipBtn;
    String status = "", destinationLatitude, destinationLongitude, sourceLatitude, sourceLongitude;
    LinearLayout rideStatusLayout, startRideLayout, acceptRideLayout, completeRideLayout, completeAmountPendingLayout, ratingLayout;
    Button acceptBtn, rejectBtn, startRideBtn, trackRideBtn, callBtn, startRecordingBtn, stopRecordingBtn, navigateRideBtn, completeRideBtn, destination, markAsReceivedBtn, technicalIssueBtn, cancelRideBtn;
    LinearLayout detailsLayout;
    RelativeLayout rl_action_layout;
    File AudioSavePathInDevice = null;
    ProgressBar acceptRideProgressbar;
    MediaRecorder mediaRecorder;
    Random random;
    boolean checkStatus = true;
    SwitchCompat switchCompat;
    int[][] states = new int[][]{new int[]{-android.R.attr.state_checked}, new int[]{android.R.attr.state_checked},};

    int[] thumbColors = new int[]{Color.RED, Color.GREEN,};
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    public MediaPlayer mediaPlayer;
    Handler handler = new Handler(Looper.getMainLooper());
    Timer rideStatusTimer;
    public static TimerTask rideTimerTask;
    Handler hand = new Handler(Looper.getMainLooper());
    boolean earning = true;
    boolean rideStatus = false;
    boolean startRide = false;
    boolean completeRide = false;
    ProgressDialog pDialog;
    private boolean refreshEarning = false;
    private static final int REQUEST_CALL_PERMISSION = 1;
    RatingBar ratingBar;
    static String rating = "", comment = "", ratingvalue;
    EditText input_comment;
    private Distance milesDistance;
    private static boolean feedbackShown = true;
    long mLastClickTime, mLastHitTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NETWORK = getString(R.string.network_not_available);
        TRYAGAIN = getString(R.string.tryagian);
    }

    private boolean isGetLastRide = false;
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver1", "Got message: " + message);
            if (!status.equalsIgnoreCase("pending")) {
                getLastRide1();
            }

            millisInFuture = 18000;
            isGetLastRide = true;
        }
    };
    private final BroadcastReceiver mMessageReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver1", "Got message: " + message);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {

            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver, new IntentFilter("custom-event-name1"));
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver1, new IntentFilter("online_status"));

            rootView = inflater.inflate(R.layout.home_fragment, container, false);
            // globatTitle = "Home";
            ((HomeActivity) getActivity()).fontToTitleBar("Home");
            pm = (PowerManager) requireActivity().getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, WAKING);

            bindView(savedInstanceState);

//            if (Global.getDistanceVisibility("VisibilityStatus", false, getActivity())) {
//                trackRideBtn.setVisibility(View.GONE);
//                startRideBtn.setVisibility(View.VISIBLE);
//            }
            Animation animZoomOut = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);
            Animation animZoomIn = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);
            navigateRideBtn.startAnimation(animZoomIn);
            trackRideBtn.startAnimation(animZoomIn);

            animZoomIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    navigateRideBtn.startAnimation(animZoomOut);
                    trackRideBtn.startAnimation(animZoomOut);
//                    if (startRideBtn.getVisibility() == View.VISIBLE) {
//                        startRideBtn.startAnimation(animZoomOut);
//                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            animZoomOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    navigateRideBtn.startAnimation(animZoomIn);
                    trackRideBtn.startAnimation(animZoomIn);
//                    if (startRideBtn.getVisibility() == View.VISIBLE) {
//                        startRideBtn.startAnimation(animZoomIn);
//                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            Log.d("token", SessionManager.getKEY());

            bundle = getArguments();
            if (bundle != null) {
                status = bundle.getString("status");
            } else {
                // getLastRide1();
                if (lastRideData != null) {
                    SessionManager.setActiveRideId(lastRideData.getRideId());
                    postedRideId = SessionManager.getActiveRideId();
                    status = lastRideData.getStatus();
                } else status = "";
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askCompactPermissions(permissionAsk, new PermissionResult() {
                    @Override
                    public void permissionGranted() {
                        if (!GPSEnable()) {
                            tunonGps();
                        } else {
                            getCurrentlOcation();
                        }
                    }

                    @Override
                    public void permissionDenied() {
                    }

                    @Override
                    public void permissionForeverDenied() {
                        openSettingsApp(getActivity());
                    }
                });
            } else {
                if (!GPSEnable()) {
                    tunonGps();
                } else {
                    getCurrentlOcation();
                }
            }
        } catch (Exception e) {
            Log.e("tag", "Inflate exception   " + e);
        }

        initialize(getApplicationContext());
//        Log.d("token", getKEY());
        acceptRideStatus();

        return rootView;
    }


    long startTime = 0;

    //accepte ride status
    public void acceptRideStatus() {
        if (status.equalsIgnoreCase("PENDING")) {

            final Runnable r = new Runnable() {

                public void run() {
                    //Do thing after 20 sec
                    stopPlaying();
                    //     getLastRide1();
                    //   rejectRide();

                    acceptRideProgressbar.setVisibility(View.GONE);
                }
            };
            handler.postDelayed(r, 22000);
            // getAcceptedRequest(status);
        } else {
            //rides.setVisibility(View.VISIBLE);
            getLastRide1();
            if (earnings != null) earnings.setVisibility(View.GONE);
            if (rideStatusLayout != null) rideStatusLayout.setVisibility(View.GONE);
            if (acceptRideProgressbar != null) acceptRideProgressbar.setVisibility(View.GONE);
            detailsLayout.setVisibility(View.GONE);
            ratingLayout.setVisibility(View.GONE);

            earning = true;
            rideStatus = false;
            startRide = false;
            completeRide = false;
        }

        if (earning) {
            completeRideLayout.setVisibility(View.GONE);
            startRideLayout.setVisibility(View.GONE);
            acceptRideLayout.setVisibility(View.GONE);
            acceptRideProgressbar.setVisibility(View.GONE);
            detailsLayout.setVisibility(View.GONE);
            if (earnings != null) earnings.setVisibility(View.GONE);
            //rides.setVisibility(View.VISIBLE);
        }

        if (rideStatus) {
            //rides.setVisibility(View.GONE);
            if (earnings != null) earnings.setVisibility(View.GONE);
            rideStatusLayout.setVisibility(View.VISIBLE);
            acceptRideProgressbar.setVisibility(View.VISIBLE);
            detailsLayout.setVisibility(View.VISIBLE);
        }

        if (startRide) {
            completeRideLayout.setVisibility(View.GONE);
            acceptRideLayout.setVisibility(View.GONE);
            acceptRideProgressbar.setVisibility(View.GONE);
            startRideLayout.setVisibility(View.VISIBLE);
            detailsLayout.setVisibility(View.GONE);
            if (earnings != null) earnings.setVisibility(View.GONE);
            //rides.setVisibility(View.GONE);
        }

        if (completeRide) {
            completeRideLayout.setVisibility(View.VISIBLE);
            detailsLayout.setVisibility(View.VISIBLE);
            startRideLayout.setVisibility(View.GONE);
            acceptRideProgressbar.setVisibility(View.GONE);
            acceptRideLayout.setVisibility(View.GONE);
            if (earnings != null) earnings.setVisibility(View.GONE);
            //rides.setVisibility(View.GONE);
        }
    }

    //media stopper
    private void stopPlaying() {
        try {
            Log.e("MUSIC", "Stopping...");
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //location service starting
    private void startLocationService() {
        Intent intent = new Intent(getApplicationContext(), TrackingActivity.class);
        intent.putExtra("driver_name", driverName);
        intent.putExtra("ride_id", rideId);
        intent.putExtra("driver_email", driverEmail);
//        startWorkManager(rideId, driverEmail, driverName);
        requireActivity().startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                getCurrentlOcation();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                Toast.makeText(getContext(), "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                String address = place.getAddress();
                // do query with address

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getContext(), "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();

            if (mediaPlayer != null) {
                mediaPlayer.pause();
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            j = 0;
        }
        try {
            if (getActivity() != null && mMapView != null) {
                mMapView.onPause();
            }
            if (mGoogleApiClient != null) {
                if (mGoogleApiClient.isConnected()) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                    mGoogleApiClient.disconnect();
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver1);
        //setSwitchState(false);
        if (wl != null) {
            if (wl.isHeld()) wl.release();
        }
        Log.d("call", "onDestroy");

        //  is_online(SessionManager.getUserId(), "3", false);
        try {
            if (mMapView != null) {
                mMapView.onDestroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            if (mMapView != null) {
                mMapView.onSaveInstanceState(outState);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        try {
            if (mMapView != null) {
                mMapView.onLowMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            if (mMapView != null) {
                mMapView.onStop();
            }
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {

        }
    }


    private LastRideData lastRideData = null;

    public void getLastRide() {
        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<LastRideResponse> call = apiService.getLastRide("Bearer " + SessionManager.getKEY());
        call.enqueue(new Callback<LastRideResponse>() {
            @Override
            public void onResponse(Call<LastRideResponse> call, retrofit2.Response<LastRideResponse> response) {
                LastRideResponse jsonResponse = response.body();

                if (jsonResponse.getStatus()) {

                    lastRideData = jsonResponse.getData();
                    Log.e("LAST RIDE DATA: ", jsonResponse.getData().toString());
                    Log.d("LAST USER NAME: ", jsonResponse.getData().getUserName().toString());
                    //    Toast.makeText(requireContext(), "Success while getting last ride data.", Toast.LENGTH_LONG).show();
                    SessionManager.setActiveRideId(lastRideData.getRideId());
                    if (SessionManager.getActiveRideId() != null) {
                        postedRideId = SessionManager.getActiveRideId();
//                        riderName.setText(jsonResponse.getData().getUserName());
                        riderName.setText(jsonResponse.getData().getUserLastname());
                        getRideStatus();
                        status = lastRideData.getStatus();


                        if (status.equalsIgnoreCase("pending")) {
                            Log.e("REJECTSTATUS", "reject ride accepted " + lastRideData.getRideId());
                            rejectRide("");
                        }
                        //acceptRideStatus();

                    }
                    // progressDialog.cancel();

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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();


        if ((upload_doc_layout.getVisibility() == View.VISIBLE)) setOnlineSwitch(false);

        if (mCountDownTimerBackground != null) {
            mCountDownTimerBackground.cancel();
        }
        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            ((HomeActivity) requireActivity()).getProfile();
        } else {
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }

        if (!isGetLastRide) {
            getLastRide1();
            millisInFuture = 20000;
        }
        isGetLastRide = false;
        first_online++;
//        ((HomeActivity) requireActivity()).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//              getLastRide1();
//                if (lastRideData != null) {
//                    status = lastRideData.getStatus();
//                    Log.e("OnResumeStatus",status);
//                    SessionManager.setActiveRideId(lastRideData.getRideId());
//                    if (SessionManager.getActiveRideId() != null) {
//                        postedRideId = SessionManager.getActiveRideId();
//
//                         getRideStatus();
//                        //acceptRideStatus();
//
//                    }
//
//                }
//
//            }
//        });

        // Toast.makeText(requireContext(), "Last Ride Status: "+lastRideData.getStatus(), Toast.LENGTH_SHORT).show();
//        bundle = getArguments();
//        // Toast.makeText(getApplicationContext(), bundle+"on stop ======", Toast.LENGTH_SHORT).show();
//        if (bundle != null) {
//            status = bundle.getString("status");
//
//
//        } else {
//            getRideStatus();
//        }

        try {
            if (mMapView != null) {
                mMapView.onResume();
            }
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        myMap.setMaxZoomPreference(18);
        setCustomMapStyle();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // True for Show Blue Blue Pointer
        myMap.setMyLocationEnabled(true);
        myMap.setTrafficEnabled(false);
        myMap.getUiSettings().setMyLocationButtonEnabled(false);

        Log.d("map", "onmapready");

        if (myMap != null) {
            tunonGps();
        }
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public interface PassDataToActivity {
        void passData(boolean[] data);
    }


    public void setOnPassDataToActivity(PassDataToActivity passDataToActivity) {
        this.passDataToActivity = passDataToActivity;
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.calling_dialog, null);
        builder.setView(dialogView);

        TextView textHeader = dialogView.findViewById(R.id.textHeader);
        TextView textDescription = dialogView.findViewById(R.id.textDescription);
        Button btnOK = dialogView.findViewById(R.id.btnOK);

        textHeader.setText("RideShare Driver");
        textDescription.setText("Our team will call you shortly");

        AlertDialog dialog = builder.create();
        dialog.show();

        btnOK.setOnClickListener(view -> {
            dialog.dismiss();
        });


    }

    public void bindView(Bundle savedInstanceState) {

        MapsInitializer.initialize(this.getActivity());
        mMapView = (MapView) rootView.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);

        upload_doc_layout = rootView.findViewById(R.id.upload_doc_layout);
        goto_upload_img = rootView.findViewById(R.id.goto_upload_img);
        rl_action_layout = rootView.findViewById(R.id.rl_action_layout);
        txt_required_doc_message = rootView.findViewById(R.id.txt_required_doc_message);
        //driver rest
        txtRest = rootView.findViewById(R.id.txtRest);

        // Rating Bar
        input_comment = rootView.findViewById(R.id.input_comment);
        ratingBar = rootView.findViewById(R.id.rating);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingvalue = String.valueOf((Float) ratingBar.getRating());
            }
        });

        acceptRideProgressbar = rootView.findViewById(R.id.progress_accept_ride);
        mMapView.getMapAsync(this);
        // load animations
        animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.dialogue_scale_anim_open);
        animFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.dialogue_scale_anim_exit);
        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);
        switchCompat = rootView.findViewById(R.id.online);

        completeAmountPendingLayout = rootView.findViewById(R.id.complete_payment_pending_layout);
        cancelRideBtn = rootView.findViewById(R.id.cancelRideBtn);
        textViewAmountPending = rootView.findViewById(R.id.tv_complete_amount_pending1);
        pending_amount_layout = rootView.findViewById(R.id.pending_amount_layout);
//        textView_online_status = rootView.findViewById(R.id.tv_online_status);
        acceptBtn = rootView.findViewById(R.id.accepteBtn);
        rejectBtn = rootView.findViewById(R.id.rejectBtn);
        callBtn = rootView.findViewById(R.id.callBtn);
        trackRideBtn = rootView.findViewById(R.id.trackRideBtn);
        startRideBtn = rootView.findViewById(R.id.startRideBtn);
        rideStatusLayout = rootView.findViewById(R.id.rideStatusLayout);
        startRideLayout = rootView.findViewById(R.id.startRideLayout);
        txtDriverName = rootView.findViewById(R.id.txtDriverName);
        acceptRideLayout = rootView.findViewById(R.id.acceptRideLayout);
        completeRideLayout = rootView.findViewById(R.id.completeRideLayout);
        //technical issue
        technicalIssueBtn = rootView.findViewById(R.id.technicalIssueBtn);
        //technical issue
        navigateRideBtn = rootView.findViewById(R.id.navigateRideBtn);
        destination = rootView.findViewById(R.id.destination);
        completeRideBtn = rootView.findViewById(R.id.completeRideBtn);
        detailsLayout = rootView.findViewById(R.id.detailsLayout);
        ratingLayout = rootView.findViewById(R.id.ratingLayout);
        startRecordingBtn = rootView.findViewById(R.id.startRecordingBtn);
        stopRecordingBtn = rootView.findViewById(R.id.stopRecordingBtn);
        rides = rootView.findViewById(R.id.cardview_totalride);
        earnings = rootView.findViewById(R.id.earnings);
        textView_today = rootView.findViewById(R.id.txt_todayearning);
        textView_week = rootView.findViewById(R.id.txt_weekearning);
        textView_overall = rootView.findViewById(R.id.txt_overallearning);
        textView_totalride = rootView.findViewById(R.id.txt_total_ridecount);
        txt_to_add = rootView.findViewById(R.id.txt_to_add);
        txt_from_add = rootView.findViewById(R.id.txt_from_add);
        rideFare = rootView.findViewById(R.id.rideFare);
        rideDistance = rootView.findViewById(R.id.rideDistance);
        riderName = rootView.findViewById(R.id.riderName);
        markAsReceivedBtn = rootView.findViewById(R.id.markAsReceivedBtn);
        doneBtn = rootView.findViewById(R.id.doneBtn);
        skipBtn = rootView.findViewById(R.id.skipBtn);
        markAsReceivedBtn.setVisibility(View.GONE);
        Utils.overrideFonts(getActivity(), rootView);

        is_online = (TextView) rootView.findViewById(R.id.is_online);

        progressDialog = new ProgressDialog(getActivity());
        waitingProgressDialog = new ProgressDialog(getActivity());
        waitingProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        acceptBtn.setOnClickListener(this);
        rejectBtn.setOnClickListener(this);
        callBtn.setOnClickListener(this);
        trackRideBtn.setOnClickListener(this);
        startRideBtn.setOnClickListener(this);
        navigateRideBtn.setOnClickListener(this);
        completeRideBtn.setOnClickListener(this);
        startRecordingBtn.setOnClickListener(this);
        stopRecordingBtn.setOnClickListener(this);
        destination.setOnClickListener(this);
        markAsReceivedBtn.setOnClickListener(this);
        technicalIssueBtn.setOnClickListener(this);
        cancelRideBtn.setOnClickListener(this);

        ((HomeActivity) requireActivity()).getEarningInfo("month", "1");


        goto_upload_img.setOnClickListener(e -> {
            // TODO: 7/26/2022
            passDataToActivity = new UploadDocs();
            passDataToActivity.passData(new boolean[]{licenceStatus, insuranceStatus, carRegStatus});
            if (txt_required_doc_message.getText().toString().equalsIgnoreCase(getResources().getString(R.string.vehicle_expired))) {
                ((HomeActivity) requireActivity()).ll_earning.setVisibility(View.GONE);
                ((HomeActivity) requireActivity()).changeFragment(new ProfileFragment(), getString(R.string.profile));

            } else {
                if (identityStatus) {
                    ((HomeActivity) requireActivity()).ll_earning.setVisibility(View.GONE);
                    ((HomeActivity) requireActivity()).changeFragment(new ProfileFragment(), "Change Profile");
                } else {
                    Intent intent = new Intent(requireContext(), UploadDocs.class);
                    intent.putExtra("car", carRegStatus);
                    intent.putExtra("insurance", insuranceStatus);
                    intent.putExtra("licence", licenceStatus);
                    intent.putExtra("inspection", inspectionStatus);
                    requireActivity().startActivity(intent);
                }
            }
        });

        // Skip Rating
        skipBtn.setOnClickListener(e -> {
            feedbackShown = true;
            ratingLayout.setVisibility(View.GONE);
        });

        //  Submit Rating
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                    if (ratingvalue != null && !ratingvalue.isEmpty() && !ratingvalue.equalsIgnoreCase("0.0")) {
                        submitFeedBack();
                    } else {
                        Toast.makeText(getActivity(), "Please give rating", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //setting online switch
    public void setOnlineSwitch(boolean status) {
        try {
            ((HomeActivity) requireActivity()).setDisableEnableSwitch(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isCheckedOnline = false;


    //changing status
    public void changeOnlineStatus(boolean isChecked) {
        isCheckedOnline = isChecked;
        Log.e("Change", isChecked ? "true" : "false");
        if (isChecked) {
            new ShowServiceAlert().showAlert(requireContext(), "", "1");
        } else {
            try {

            } catch (Exception ex) {

            }
        }
    }

    //getting earing
    public void getEarningInfo() {
        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            progressDialog.show();

            String url = Server.BASE_URL.concat("earn");
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject data = new JSONObject(response);
                        Log.e("Response", "EarningonResponse = \n " + response);

                        if (data.has("status") && data.getBoolean("status")) {
                            if (!data.getJSONObject("data").getString("today_earning").isEmpty()) {
                                textView_today.setText(data.getJSONObject("data").getString("unit") + data.getJSONObject("data").getString("today_earning"));
                            } else {
                                textView_today.setText(data.getJSONObject("data").getString("unit") + "0");
                            }
                            if (!data.getJSONObject("data").getString("week_earning").isEmpty()) {
                                textView_week.setText(data.getJSONObject("data").getString("unit") + data.getJSONObject("data").getString("week_earning"));
                            } else {
                                textView_week.setText(data.getJSONObject("data").getString("unit") + "0");
                            }
                            if (!data.getJSONObject("data").getString("total_earning").isEmpty()) {
                                textView_overall.setText(data.getJSONObject("data").getString("unit") + data.getJSONObject("data").getString("total_earning"));
                                textView_totalride.setText(data.getJSONObject("data").getString("total_rides"));
                            } else {
                                textView_overall.setText(data.getJSONObject("data").getString("unit") + "0");
                            }
                            /*txt_from_add.setText(data.getJSONObject("data").getJSONObject("request").getString("pickup_adress"));
                            txt_to_add.setText(data.getJSONObject("data").getJSONObject("request").getString("drop_address"));
                            postedRideId = data.getJSONObject("data").getJSONObject("request").getString("ride_id");

                            sourceLatitude = data.getJSONObject("data").getJSONObject("request").getString("pickup_lat");
                            sourceLongitude = data.getJSONObject("data").getJSONObject("request").getString("pickup_long");
                            destinationLatitude = data.getJSONObject("data").getJSONObject("request").getString("drop_lat");
                            destinationLongitude = data.getJSONObject("data").getJSONObject("request").getString("drop_long");
                            contactNo = data.getJSONObject("data").getJSONObject("request").getString("user_mobile");
                            rideFare.setText("$ " + data.getJSONObject("data").getJSONObject("request").getString("amount"));
                            rideDistance.setText(data.getJSONObject("data").getJSONObject("request").getString("distance") + " miles");*/

                        } else {
//                        Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
//                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    String auth = "Bearer " + getKEY();
                    headers.put("Authorization", auth);
                    return headers;
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.cancel();
        }
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                int height = 150;
                int width = 200;
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.car_ride);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap carMarker = Bitmap.createScaledBitmap(b, width, height, false);

                if (myMap != null) {
                    myMap.clear();
//                    my_marker = myMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).icon(bitmapDescriptorFromVector(getActivity(), R.drawable.car_ride))
                    my_marker = myMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).icon(BitmapDescriptorFactory.fromBitmap(carMarker)).zIndex(1.0f).flat(true));
                    my_marker.showInfoWindow();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 18);
                    myMap.animateCamera(cameraUpdate);

                }
                //  setCurrentLocation(currentLatitude, currentLongitude);
                if (!currentLatitude.equals(0.0) && !currentLongitude.equals(0.0)) {
//                    setCurrentLocation(currentLatitude, currentLongitude);
                    updateDriverLatLong(currentLatitude, currentLongitude);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.couldnt_get_location), Toast.LENGTH_LONG).show();
                }

            }
        } catch (Exception e) {

        }
    }

    //setting current location
    public void setCurrentLocation(final Double lat, final Double log) {
        try {
            my_marker.setPosition(new LatLng(lat, log));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 15);
            myMap.animateCamera(cameraUpdate);
            RequestParams par = new RequestParams();
            Server.setHeader(getKEY());
            par.put("user_id", SessionManager.getUserId());
            par.add("latitude", String.valueOf(currentLatitude));
            par.add("longitude", String.valueOf(currentLongitude));
            Server.post(Server.UPDATE, par, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.e("UPDATE_LAT_LONG", response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.e("UPDATE_LAT_LONG", "Lat long failed1" + currentLatitude + " " + currentLongitude);
                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }

    //on location changed
    @Override
    public void onLocationChanged(android.location.Location location) {
        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            if (!currentLatitude.equals(0.0) && !currentLongitude.equals(0.0)) {
//                setCurrentLocation(currentLatitude, currentLongitude);
                updateDriverLatLong(currentLatitude, currentLongitude);
                Log.d("LAT_LONG_ONLOCATION", sourceLatitude + "Longtitude" + sourceLongitude + "status" + status);
//                if (sourceLatitude != null && sourceLongitude != null && startRideBtn.getVisibility() == View.GONE && status.equalsIgnoreCase("accepted")) {
//                    fetchDistance(currentLatitude, currentLongitude, Double.parseDouble(sourceLatitude), Double.parseDouble(sourceLongitude), 0.0f);
//                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.couldnt_get_location), Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("LOCATION_FAILED", location.toString());
        }
    }

    //getting current Location of driver
    public void getCurrentlOcation() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(300 * 1000); // 5mint = 300 * 1000
        mLocationRequest.setFastestInterval(300 * 1000);  // 5mint
    }

    //turning on Gps
    public void tunonGps() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(300 * 1000);
            mLocationRequest.setFastestInterval(300 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            getCurrentlOcation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and checkky the result in onActivityResult().
                                status.startResolutionForResult(getActivity(), 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }

    }

    //enable GPS
    public Boolean GPSEnable() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        return gpsTracker.canGetLocation();
    }

    //adding svg as custom google location marker
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //getting earning
    public void getEarning() {
        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            String auth = "Bearer " + getKEY();
            Map<String, String> details = new HashMap<>();
            details.put("driver_id", SessionManager.getUserId());

            ApiNetworkCall apiService = ApiClient.getApiService();
            Call<DriverEarningResponse> call = apiService.getDriverEarning(auth, details);
            call.enqueue(new Callback<DriverEarningResponse>() {
                @Override
                public void onResponse(Call<DriverEarningResponse> call, retrofit2.Response<DriverEarningResponse> response) {
                    DriverEarningResponse jsonResponse = response.body();
                    if (jsonResponse.getStatus()) {
                        // Toast.makeText(getContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();

                    } else {
                        //Toast.makeText(getContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<DriverEarningResponse> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                }
            });
        } else {
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }

    //cash payment recived
    public void paymentReceivedCOD(String ride_id, String driverStatus) {
        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            final ProgressDialog progressDialog = new ProgressDialog(requireContext());
            progressDialog.setMessage("Updating Payment......");
            progressDialog.show();
            String url = Server.BASE_URL.concat("/api/driver/payment_as_recieved");

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {


                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject data = new JSONObject(response);

                        if (data.has("status") && data.getBoolean("status")) {
                            JSONObject object = data.getJSONObject("data");
                            // Toast.makeText(requireActivity(),object.getString("message"),Toast.LENGTH_SHORT).show();
                            if (!(upload_doc_layout.getVisibility() == View.VISIBLE))
                                setOnlineSwitch(true);
                            progressDialog.cancel();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
//                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                        progressDialog.cancel();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ResponseF", "" + error.getMessage());
                    Toast.makeText(requireActivity(), "Please try after sometime.", Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
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
                        params.put("ride_id", ride_id);
                        params.put("driver_status", driverStatus);
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
        } else {
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.cancel();
        }
    }


    //alert dialog
    private void showAlert(String message) {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        dialogBuilder.setMessage(message).setCancelable(false).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                paymentReceivedCOD(postedRideId, "1");
                dialog.cancel();
            }
        });


        androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.cancelRideBtn:
                confirmCancelRideAlert();
                break;

            case R.id.technicalIssueBtn:
                showTechnicalIssueAlert();
                break;

            case R.id.markAsReceivedBtn:
                Log.e("ResponseF", postedRideId);
                showAlert("Do you want to receive payment via cash?");
                break;

            case R.id.accepteBtn:
                setOnlineSwitch(false);
                stopPlaying();
                acceptRide();
                break;
            case R.id.rejectBtn:
                Log.e("REEJCTBUTTON", "I was Called.");
                stopPlaying();
                rejectRide("");
                SessionManager.setActiveRideId(null);
                break;
            case R.id.startRideBtn:
                wl.acquire(60 * 60 * 1000L /*60 minutes*/);
//                fetchDistance(currentLatitude, currentLongitude, Double.parseDouble(sourceLatitude), Double.parseDouble(sourceLongitude), 0.0f);
                startRide();
                break;
            case R.id.trackRideBtn:
                trackRide();
                //navigateRide();
                break;
            case R.id.callBtn:
//                isPermissionGranted();
//                callToRider();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + "+67 " + contactNo));
                startActivity(intent);
                break;
            case R.id.navigateRideBtn:
                navigateRide();
//                openMap();
                break;
            case R.id.completeRideBtn:
                SessionManager.setActiveRideId(null);
                //is_online(SessionManager.getUserId(), "2", false);
                completeRide("");
                if (wl.isHeld()) wl.release();
                break;
            case R.id.startRecordingBtn:
                //startRecorder();
                break;
            case R.id.stopRecordingBtn:
                stopRecording();
                break;
            case R.id.destination:
                //onSearchCalled();
                break;
        }
    }


    //starting recording
    private void startRecorder() {
        new androidx.appcompat.app.AlertDialog.Builder(getActivity()).setMessage("Start recording is on please do not kill your app").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startRecording();
                stopRecordingBtn.setVisibility(View.GONE);
                startRecordingBtn.setVisibility(View.GONE);
            }
        }).show();
    }

    //for permissions
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    //media recoder
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

    //starting audio recording
    public void startRecording() {
        random = new Random();
        if (checkPermission()) {
            try {
                flag = false;
                AudioSavePathInDevice = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Camera/" + CreateRandomAudioFileName(5) + "AudioRecording.ogg");

                if (!AudioSavePathInDevice.getParentFile().exists())
                    AudioSavePathInDevice.getParentFile().mkdirs();
                if (!AudioSavePathInDevice.exists()) AudioSavePathInDevice.createNewFile();
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


            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();

            Log.d("path", String.valueOf(AudioSavePathInDevice));
        } else {
            requestPermission();
        }
    }

    //creating name for audio file
    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

    //requesting permissions
    private void requestPermission() {

        ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    //uploading recording on server
    private void uplaodRecordingToServer() {
        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            String token = "Bearer " + SessionManager.getKEY();
            Log.d("token", token);
//        final ProgressDialog loading = ProgressDialog.show(getApplicationContext(), "Please wait...", "Uploading data...", false, false);

            ApiNetworkCall apiService = ApiClient.getApiService();
            RequestBody ride_id = RequestBody.create(MediaType.parse("text/plain"), rideId);


            MultipartBody.Part fileToUpload;
            //empty file
            RequestBody empty_file = RequestBody.create(MediaType.parse("audio/*"), "");

            if (AudioSavePathInDevice != null) {
                fileToUpload = MultipartBody.Part.createFormData("audio", AudioSavePathInDevice.getName(), RequestBody.create(MediaType.parse("audio/*"), AudioSavePathInDevice));
            } else {
                fileToUpload = MultipartBody.Part.createFormData("audio", "", empty_file);
            }

            Call<ChangePasswordResponse> call = apiService.uploadRecording(token, fileToUpload, ride_id);
            call.enqueue(new Callback<ChangePasswordResponse>() {
                @Override
                public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                    ChangePasswordResponse requestResponse = response.body();
                    try {
                        assert requestResponse != null;
                        Toast.makeText(getActivity(), requestResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Exception ex) {

                    }
                    //loading.cancel();
                }

                @Override
                public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                    // progressBar.setVisibility(View.GONE);
                    //  loading.cancel();
                    Log.d("Failed", "RetrofitFailed");
                }
            });

        } else {
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }

    }

    //stoping recoding
    public void stopRecording() {
        try {
            mediaRecorder.stop();
        } catch (Exception exception) {
        }
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        new androidx.appcompat.app.AlertDialog.Builder(getActivity()).setIcon(drawable).setTitle("Save Recording").setMessage("Are you sure you want to save recording?").setNegativeButton("Cancel", null).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                uplaodRecordingToServer();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();

        stopRecordingBtn.setVisibility(View.GONE);
        startRecordingBtn.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), "Recording Completed", Toast.LENGTH_LONG).show();
    }

    //on complete ride
    private void completeRide(String tech_issue) {
        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            progressDialog.show();
            String url = "";
            url = Server.BASE_URL.concat("accept_ride");

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject data = new JSONObject(response);
                        Log.e("Response", "onResponse = \n " + response);

                        if (data.has("status") && data.getBoolean("status")) {
                            Log.e("COMP", data.getString("message") + "com1");
                            Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("COMP", data.getString("message") + "com2");
                            Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        completeRideLayout.setVisibility(View.GONE);
                        startRideLayout.setVisibility(View.GONE);
                        acceptRideLayout.setVisibility(View.GONE);
                        acceptRideProgressbar.setVisibility(View.GONE);
                        detailsLayout.setVisibility(View.GONE);
                        if (earnings != null) earnings.setVisibility(View.GONE);
                        //rides.setVisibility(View.VISIBLE);

                        earning = true;
                        rideStatus = false;
                        startRide = false;
                        completeRide = false;

                        stopLocationService();
                        progressDialog.dismiss();

                        //new change on 25-01-2022
                        if (tech_issue.isEmpty()) {
                            ((HomeActivity) requireActivity()).is_online(SessionManager.getUserId(), "2", false);
                        } else {

                            if (mCountDownTimerBackground != null) {
                                mCountDownTimerBackground.cancel();
                            }

                            ((HomeActivity) requireActivity()).setMySwitch(true);
                            changeOnlineStatus(false);
                            ((HomeActivity) requireActivity()).is_online(SessionManager.getUserId(), "3", false);

                        }
                        getLastRide1();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
//                    Toast.makeText(getActivity(), getActivity().getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
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
                        params.put("ride_id", postedRideId);
                        params.put("status", "COMPLETED");
                        params.put("is_technical_issue", tech_issue);
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
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.cancel();
        }
    }

    //navigation
    private void navigateRide() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                String uri = "http://maps.google.com/maps?saddr=" + currentLatitude + "," + currentLongitude + "&daddr=" +
//                        destinationLatitude + "," + destinationLongitude;
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                getActivity().startActivity(intent);

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(requireContext(), Locale.getDefault());
                String address = "";
                try {
                    addresses = geocoder.getFromLocation(Double.parseDouble(destinationLatitude), Double.parseDouble(destinationLongitude), 1);
                    address = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                    Log.e("Address", address);
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(destinationAddress) + "," + "&mode=d");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
//                    if (mapIntent.resolveActivity(getContext().getPackageManager()) != null)
                    startActivity(mapIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destinationLatitude + "," + destinationLongitude + "," + "&mode=d");
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                if (mapIntent.resolveActivity(getContext().getPackageManager()) != null)
//                    startActivity(mapIntent);
            }
        }, 2000);
    }

    //tracking ride
    private void trackRide() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(requireContext(), Locale.getDefault());
                String address = "";

                try {
                    addresses = geocoder.getFromLocation(Double.parseDouble(sourceLatitude), Double.parseDouble(sourceLongitude), 1);
                    Log.d("addresses", addresses.toString());
                    Log.d("addresses", addresses.get(0).getAddressLine(0).toString());
                    Log.d("addresses", addresses.get(0).getLocale().toString());
                    address = addresses.get(0).getAddressLine(0);
                    Log.e("Address", address);
                    Uri gmmIntentUri;
                    if (pickupAddress != null && !pickupAddress.isEmpty()) {
                        gmmIntentUri = Uri.parse("google.navigation:q=" + pickupAddress + "," + "&mode=d");
                    } else {
                        gmmIntentUri = Uri.parse("google.navigation:q=" + address + "," + "&mode=d");
                    }
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
//                    if (mapIntent.resolveActivity(getContext().getPackageManager()) != null)
                    startActivity(mapIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }

//
//                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + sourceLatitude + "," + sourceLongitude + "," + "&mode=d");
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                if (mapIntent.resolveActivity(getContext().getPackageManager()) != null)
//                    startActivity(mapIntent);
            }
        }, 1000);
    }

    //starting ride
    private void startRide() {
        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            String url = Server.BASE_URL.concat("accept_ride");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject data = new JSONObject(response);

                        if (data.has("status") && data.getBoolean("status")) {
                            Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        completeRideLayout.setVisibility(View.VISIBLE);
                        detailsLayout.setVisibility(View.VISIBLE);
                        startRideLayout.setVisibility(View.GONE);
                        acceptRideLayout.setVisibility(View.GONE);
                        acceptRideProgressbar.setVisibility(View.GONE);
                        if (earnings != null) earnings.setVisibility(View.GONE);
                        //rides.setVisibility(View.GONE);

                        earning = false;
                        rideStatus = false;
                        startRide = false;
                        completeRide = true;

                        progressDialog.dismiss();
//                        showAlert(getContext(), "Navigate To Destination", "Do you need navigation to rider destination?", 2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
//                    Toast.makeText(getActivity(), getActivity().getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
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
                        params.put("ride_id", postedRideId);
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

        } else {
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.cancel();
        }

    }

    //calling the rider
    private void callToRider() {
        Map<String, String> param = new HashMap();
        param.put("Caller", SessionManager.getUserMobile().toString());
        param.put("country_code", SessionManager.getUserCountryCode().toString());
        param.put("ride_id", rideId.toString());
        Log.d("rideID", rideId.toString());


        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        if (CheckConnection.haveNetworkConnection(requireContext())) {
            progressDialog.setMessage("Requesting.....");
            progressDialog.setCancelable(false);
            progressDialog.show();

            ApiNetworkCall apiService = ApiClient.getApiService();
            Log.d("CallParam", param.toString());

            Call<CallingRiderResponse> call = apiService.callingRiderApi("Bearer " + SessionManager.getKEY(), param);
            call.enqueue(new Callback<CallingRiderResponse>() {
                @Override
                public void onResponse(Call<CallingRiderResponse> call, retrofit2.Response<CallingRiderResponse> response) {
                    CallingRiderResponse jsonResponse = response.body();
                    Log.d("CallResposnse", response.body().toString());
                    if (jsonResponse != null) {
                        if (jsonResponse.getStatus() == 200) {
                            progressDialog.cancel();
                            showCustomDialog();

                            Log.d("CallResposnse", response.body().getMessage().toString());

                        } else {
                            progressDialog.cancel();
                            Toast.makeText(requireContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }

                }

                @Override
                public void onFailure(Call<CallingRiderResponse> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                    progressDialog.cancel();
                }
            });
        } else {
            progressDialog.cancel();
            Toast.makeText(requireContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }


    private void makePhoneCall(String number) {
        String phoneNumber = number; // Replace with the desired phone number

        // Check if the CALL_PHONE permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            // Permission already granted, make the call
            String dialUri = "tel:" + phoneNumber;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(dialUri));
            startActivity(intent);
        }
    }

    //calling
    public void call_action() {
        String phnum = contactNo;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phnum));
        getActivity().startActivity(callIntent);
    }

    //checking permission
    public void isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                call_action();
            } else {

                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);

            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            call_action();
        }
    }

    //accepted rides
    public void getAcceptedRequest(String status) {
        if (CheckConnection.haveNetworkConnection(requireActivity())) {

            String url = Server.BASE_URL.concat("rides?id=").concat("&status=").concat(status);
        /*String url = Server.BASE_URL.concat("rides?id=").concat(SessionManager.getUserId()).concat("&status=").concat(status)
                .concat("&utype=2");*/
            Log.e("getAcceptedRequest_URL", url);
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject data = new JSONObject(response);
                    /*JSONArray obj=data.getJSONObject("data").getJSONArray("audio");
                    for(int j=0;j<obj.length();j++) {
                        JSONObject jsonObject = obj.getJSONObject(j);
                        Audio audio = new Audio();
                        audio.setAudio(jsonObject.getString("audio"));
                    }*/
                        Log.e("getAccepted_Response", "onResponse =  " + response);


                        if (data.has("status") && data.getBoolean("status")) {
                            JSONArray jsonArray = data.getJSONArray("data");
                            //JSONArray obj=data.getJSONObject("data").getJSONArray("audio");
                            if (jsonArray != null && jsonArray.length() > 0) {

                                try {
                                    if (mediaPlayer != null) {
                                        mediaPlayer.pause();
                                        mediaPlayer.stop();
                                        mediaPlayer.reset();
                                        mediaPlayer.release();
                                        mediaPlayer = null;
                                    }
                                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.driver_chime_2);
                                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                    mediaPlayer.setLooping(false);
                                    if (!mediaPlayer.isPlaying()) {
                                        mediaPlayer.start();
                                        Log.e("MEDIA_START", "I am playing");
                                    }

                                } catch (Exception e) {

                                    e.printStackTrace();
                                }

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    PendingRequestPojo lists = new PendingRequestPojo();
                                    List<String> a = new ArrayList<>();
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);

//                                    JSONArray obj = jsonObject.getJSONArray("audio");
//                                    if (obj.length() == 0) {
//                                        flag = false;
//                                    } else {
//                                        flag = true;
//                                        for (int j = 0; j < obj.length(); j++) {
//                                            JSONObject jsonObject1 = obj.getJSONObject(0);
//                                            a.add(jsonObject1.getString("audio"));
//                                        }
//                                    }
                                    //  lists.setAudio(a);

                                    jsonObject.getString("time");
                                    txt_from_add.setText(jsonObject.getString("pickup_adress"));
                                    txt_to_add.setText(jsonObject.getString("drop_address"));
                                    pickupAddress = jsonObject.getString("pickup_adress");
                                    destinationAddress = jsonObject.getString("drop_address");
                                    postedRideId = jsonObject.getString("ride_id");
                                    SessionManager.setActiveRideId(jsonObject.getString("ride_id"));
                                    jsonObject.getString("status");
                                    sourceLatitude = jsonObject.getString("pickup_lat");
                                    sourceLongitude = jsonObject.getString("pickup_long");
                                    destinationLatitude = jsonObject.getString("drop_lat");
                                    destinationLongitude = jsonObject.getString("drop_long");
                                    contactNo = jsonObject.getString("user_mobile");
                                    rideFare.setText("$ " + jsonObject.getString("amount"));
                                    rideDistance.setText(jsonObject.getString("distance") + " miles");
                                    ridername = jsonObject.getString("user_lastname");
                                    riderName.setText(ridername);
                                }

                                //rides.setVisibility(View.GONE);
                                if (earnings != null) earnings.setVisibility(View.GONE);

                                setOnlineSwitch(false);
                                rideStatusLayout.setVisibility(View.VISIBLE);

                                detailsLayout.setVisibility(View.VISIBLE);
                                acceptRideLayout.setVisibility(View.VISIBLE);
                                acceptRideProgressbar.setVisibility(View.VISIBLE);
                                startTime = System.nanoTime();
                                if (mCountDownTimer != null) {
                                    mCountDownTimer.cancel();
                                    // j=0;
                                }
                                runProgressBar();
                                earning = false;
                                rideStatus = true;
                                startRide = false;
                                completeRide = false;
                            } else {
                                // Toast.makeText(getActivity(), "No ride available", Toast.LENGTH_LONG).show();
                            }
                        } else {
//                        Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
//                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
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
        } else {
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }

    //accepting ride
    private void acceptRide() {
        try {
            if (CheckConnection.haveNetworkConnection(requireActivity())) {
                if (mCountDownTimer != null) mCountDownTimer.cancel();
                j = 0;
                progressDialog.show();

                String url = Server.BASE_URL.concat("accept_ride");

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.getCache().clear();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            Log.e("Response", "onResponse confirm ride= \n " + response);

                            initialize(getApplicationContext());
                            Log.d("ride", "driver name " + getUserName());
                            Log.d("ride", "ride_id  " + rideId);
                            Log.d("ride", "driver email " + getUserEmail());

                            driverName = getUserName();
                            rideId = postedRideId;
                            driverEmail = getUserEmail();

                            if (data.has("status") && data.getBoolean("status")) {
                                Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();
                                acceptRideLayout.setVisibility(View.GONE);
                                acceptRideProgressbar.setVisibility(View.GONE);
                                startRideLayout.setVisibility(View.VISIBLE);
                                earning = false;
                                rideStatus = false;
                                startRide = true;
                                completeRide = false;
                                handler.removeCallbacksAndMessages(null);
                                progressDialog.dismiss();
//                            showAlert(getContext(), "Track Rider Address", "Do you want to track rider?", 1);

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            startLocationService();
                            //((AcceptedRequestFragment) fragment).getAcceptedRequest("PENDING");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
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
                            params.put("ride_id", postedRideId);
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


            } else {
                Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                progressDialog.cancel();
            }
        } catch (Exception e) {
            Log.e("ACCEPT_RIDE_ERROR", e.toString());
        }
    }

    //rejecting ride
    private void rejectRide(String reason) {
        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            String url = Server.BASE_URL.concat("accept_ride");

            try {
                progressDialog.show();
            } catch (WindowManager.BadTokenException e) {
                //use a log message
            }

            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
            j = 0;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e("REJECT", "I was called.");
                        JSONObject data = new JSONObject(response);
                        Log.e("Response", "onResponse = \n " + response);

                        if (data.has("count_ride") && data.getString("count_ride").equalsIgnoreCase("3")) {
                            AlertDialogCreate("Are you sure?", "Are you sure you want to be online?", "");
                        } else {
                            try {
                                if (data.has("status") && data.getBoolean("status")) {

                                    Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();
                                    getLastRide1();
                                    Log.e("REJECTSTATUS", data.getString("message") + " if");
                                } else {
                                    Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();
                                    Log.e("REJECTSTATUS", data.getString("message") + " else");
                                }
                            } catch (Exception ex) {

                            }
                        }
                        //rides.setVisibility(View.VISIBLE);
                        if (earnings != null) earnings.setVisibility(View.GONE);
                        rideStatusLayout.setVisibility(View.GONE);

                        startRideLayout.setVisibility(View.GONE);

                        earning = true;
                        rideStatus = false;
                        startRide = false;
                        completeRide = false;

                        stopLocationService();
                        handler.removeCallbacksAndMessages(null);
                        progressDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
//                    Toast.makeText(getActivity(), getActivity().getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
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
                        params.put("ride_id", postedRideId);
                        params.put("status", "CANCELLED");
                        params.put("reason", reason);
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
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.cancel();
        }

    }

    //stoping Location service
    private void stopLocationService() {
        Intent myService = new Intent(getApplicationContext(), TrackingService.class);
        getApplicationContext().stopService(myService);
        WorkManager.getInstance().cancelAllWorkByTag(LOCATION_UPDATE);
    }

    //alert dialog
    public void AlertDialogCreate(String title, String message, final String status) {
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        new androidx.appcompat.app.AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK).setIcon(drawable).setTitle(title).setMessage(message).setNegativeButton(getString(R.string.cancel), null).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    public void onSearchCalled() {
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).setCountry("IN") //NIGERIA
                .build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    //getAcceptedRide details
    public void getAcceptedRequestDetails(String status) {
        if (CheckConnection.haveNetworkConnection(requireActivity())) {
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
                        Log.e("Request Response ", "onResponse = \n " + response);


                        if (data.has("status") && data.getBoolean("status")) {
                            JSONArray jsonArray = data.getJSONArray("data");
                            //JSONArray obj=data.getJSONObject("data").getJSONArray("audio");
                            if (jsonArray != null && jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    PendingRequestPojo lists = new PendingRequestPojo();
                                    List<String> a = new ArrayList<>();
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);

//                                    JSONArray obj = jsonObject.getJSONArray("audio");
//                                    if (obj.length() == 0) {
//                                        flag = false;
//                                    } else {
//                                        flag = true;
//                                        for (int j = 0; j < obj.length(); j++) {
//                                            JSONObject jsonObject1 = obj.getJSONObject(0);
//                                            a.add(jsonObject1.getString("audio"));
//                                        }
//                                    }
                                    //  lists.setAudio(a);
                                    jsonObject.getString("user_lastname");
                                    jsonObject.getString("time");
                                    txt_from_add.setText(jsonObject.getString("pickup_adress"));
                                    pickupAddress = jsonObject.getString("pickup_adress");
                                    txt_to_add.setText(jsonObject.getString("drop_address"));
                                    destinationAddress = jsonObject.getString("drop_address");
                                    postedRideId = jsonObject.getString("ride_id");

                                    jsonObject.getString("status");
                                    sourceLatitude = jsonObject.getString("pickup_lat");
                                    sourceLongitude = jsonObject.getString("pickup_long");
                                    destinationLatitude = jsonObject.getString("drop_lat");
                                    destinationLongitude = jsonObject.getString("drop_long");
                                    contactNo = jsonObject.getString("user_mobile");
                                    rideFare.setText("$ " + jsonObject.getString("amount"));
                                    rideDistance.setText(jsonObject.getString("distance") + " miles");
                                    riderName.setText(jsonObject.getString("user_lastname"));
                                }
                            } else {
                                //Toast.makeText(getActivity(), "No ride available", Toast.LENGTH_LONG).show();
                            }
                        } else {
//                        Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("error_occurred", e.toString());
                        Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("AcceptedRequestDetails", "" + error.getMessage());
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

        } else {
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }

    //getting ride status
    public void getRideStatus() {

        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            final Dialog dialog = new Dialog(getApplicationContext());
            Map<String, String> details = new HashMap<>();
            details.put("ride_id", postedRideId);
            Log.e("RIDEData ID", postedRideId);
            String token = "Bearer " + SessionManager.getKEY();
            Log.d("token", token);
            Log.e("getRideStatus", "getRideStatus");

            ApiNetworkCall apiService = ApiClient.getApiService();
            Call<GetRideStatus> call = apiService.getRideStatus(token, details);
            call.enqueue(new Callback<GetRideStatus>() {
                @Override
                public void onResponse(Call<GetRideStatus> call, retrofit2.Response<GetRideStatus> response) {
                    GetRideStatus jsonResponse = response.body();
                    Log.d("Response", response.toString());
                    if (jsonResponse.getStatus()) {
                        dialog.cancel();
                        dialog.cancel();
                        Log.e("STATUS_RIDE", jsonResponse.getData().getStatus());
                        Log.e("DATA", jsonResponse.getData().toString());
                        if (jsonResponse.getData().getStatus().equalsIgnoreCase("ACCEPTED")) {
                            Global.putAlertValue("AgeAlertStatus", true, getActivity());
                            //rides.setVisibility(View.GONE);
                            if (earnings != null) earnings.setVisibility(View.GONE);
                            acceptRideLayout.setVisibility(View.GONE);
                            startRideLayout.setVisibility(View.VISIBLE);
                            detailsLayout.setVisibility(View.VISIBLE);
                            rideStatusLayout.setVisibility(View.VISIBLE);

                            if (mediaPlayer != null) stopPlaying();

                            getAcceptedRequestDetails("Accepted");

                        } else if (jsonResponse.getData().getStatus().equalsIgnoreCase("START_RIDE")) {
                            Global.putDistanceVisibility("VisibilityStatus", false, getActivity());
                            completeRideLayout.setVisibility(View.VISIBLE);
                            detailsLayout.setVisibility(View.VISIBLE);
                            rideStatusLayout.setVisibility(View.VISIBLE);
                            startRideLayout.setVisibility(View.GONE);
                            acceptRideLayout.setVisibility(View.GONE);
                            acceptRideProgressbar.setVisibility(View.GONE);
                            if (earnings != null) earnings.setVisibility(View.GONE);
                            if (mediaPlayer != null) stopPlaying();
                            getAcceptedRequestDetails("start_ride");
                            //rides.setVisibility(View.GONE);

                        } else if (jsonResponse.getData().getStatus().equalsIgnoreCase("COMPLETED")) {
                            Global.putDistanceVisibility("VisibilityStatus", false, getActivity());
                            completeRideLayout.setVisibility(View.GONE);
                            startRideLayout.setVisibility(View.GONE);
                            acceptRideLayout.setVisibility(View.GONE);
                            acceptRideProgressbar.setVisibility(View.GONE);
                            detailsLayout.setVisibility(View.GONE);
                            if (earnings != null) earnings.setVisibility(View.GONE);
                            if (mediaPlayer != null) stopPlaying();
                            //rides.setVisibility(View.VISIBLE);
//                        if(status.equalsIgnoreCase("Completed"))
//                        {
//                            if(lastRideData!=null) {
//                                if (lastRideData.getPaymentStatus().equalsIgnoreCase("Completed")) {
//                                    pending_amount_layout.setVisibility(View.GONE);
//                                } else {
//                                    pending_amount_layout.setVisibility(View.VISIBLE);
//                                    textViewAmountPending.setText("$ " + lastRideData.getAmount() + " amount is pending." );
//                                }
//                            }
//                        }

                        } else if (jsonResponse.getData().getStatus().equalsIgnoreCase("COMPLETED") && jsonResponse.getData().getPaymentStatus().equalsIgnoreCase("COMPLETED")) {
                            Global.putDistanceVisibility("VisibilityStatus", false, getActivity());
                            completeRideLayout.setVisibility(View.GONE);
                            startRideLayout.setVisibility(View.GONE);
                            acceptRideLayout.setVisibility(View.GONE);
                            acceptRideProgressbar.setVisibility(View.GONE);
                            detailsLayout.setVisibility(View.GONE);
                            if (earnings != null) earnings.setVisibility(View.GONE);
                            pending_amount_layout.setVisibility(View.GONE);
                        } else if (jsonResponse.getData().getStatus().equalsIgnoreCase("COMPLETED") && !jsonResponse.getData().getPaymentStatus().equalsIgnoreCase("COMPLETED")) {
                            Global.putDistanceVisibility("VisibilityStatus", false, getActivity());
                            completeRideLayout.setVisibility(View.GONE);
                            startRideLayout.setVisibility(View.GONE);
                            acceptRideLayout.setVisibility(View.GONE);
                            acceptRideProgressbar.setVisibility(View.GONE);
                            detailsLayout.setVisibility(View.GONE);
                            if (earnings != null) earnings.setVisibility(View.GONE);
                            pending_amount_layout.setVisibility(View.VISIBLE);
                            Log.d("CheckAmount", "DONE");
                            if (lastRideData != null)
                                textViewAmountPending.setText("$ " + lastRideData.getTotalAmount() + " amount is pending.");
                        } else if (jsonResponse.getData().getStatus().equalsIgnoreCase("CANCELLED")) {
                            if (mediaPlayer != null) {
                                stopPlaying();
                            }
                            Global.putAlertValue("AgeAlertStatus", true, getActivity());
                            Global.putDistanceVisibility("VisibilityStatus", false, getActivity());
                            SessionManager.setActiveRideId(null);
                            completeRideLayout.setVisibility(View.GONE);
                            startRideLayout.setVisibility(View.GONE);
                            acceptRideLayout.setVisibility(View.GONE);
                            acceptRideProgressbar.setVisibility(View.GONE);
                            detailsLayout.setVisibility(View.GONE);

                            //new change on 02/05/2022
                            if (!(upload_doc_layout.getVisibility() == View.VISIBLE) && getSwitchState(false))
                                ((HomeActivity) requireActivity()).is_online(jsonResponse.getData().getUserId(), "1", false);

                            //change ends here
                            if (earnings != null) earnings.setVisibility(View.GONE);

                        } else if (jsonResponse.getData().getStatus().equalsIgnoreCase("PENDING")) {
                            Global.putDistanceVisibility("VisibilityStatus", false, getActivity());
                            completeRideLayout.setVisibility(View.GONE);
                            startRideLayout.setVisibility(View.GONE);
                            acceptRideLayout.setVisibility(View.VISIBLE);
                            acceptRideProgressbar.setVisibility(View.VISIBLE);
                            detailsLayout.setVisibility(View.VISIBLE);
                            if (Global.getAlertValue("AgeAlertStatus", true, getActivity())) {
                                showAgeAlert();
                            }
                            getAcceptedRequest("Pending");
                            // acceptRideStatus();

                        } else if (jsonResponse.getData().getStatus().equalsIgnoreCase("NOT_CONFIRMED")) {
                            Global.putDistanceVisibility("VisibilityStatus", false, getActivity());
                            completeRideLayout.setVisibility(View.GONE);
                            startRideLayout.setVisibility(View.GONE);
                            acceptRideLayout.setVisibility(View.VISIBLE);
                            acceptRideProgressbar.setVisibility(View.VISIBLE);
                            detailsLayout.setVisibility(View.VISIBLE);
                            if (Global.getAlertValue("AgeAlertStatus", true, getActivity())) {
                                showAgeAlert();
                            }
                            getAcceptedRequest("NOT_CONFIRMED");
                        }

                    } else {

                        dialog.cancel();
                    }
                }

                @Override
                public void onFailure(Call<GetRideStatus> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                    Log.e("Error_On_getRideStatus", t.getMessage());
                    dialog.cancel();
                }
            });
        } else {
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }


    private static final int progressBarStatus = 0;
    private static final Handler progressBarbHandler = new Handler();


    //progress
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void runProgressBar() {

        acceptRideProgressbar.setProgress(j, true);
        mCountDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("Log_tag", "Tick of Progress: " + j + " " + millisUntilFinished);
                j++;
                acceptRideProgressbar.setProgress((int) j * 100 / (millisInFuture / countDownInterval));
                // acceptRideProgressbar.setProgress(j*4);
            }

            @Override
            public void onFinish() {
                j++;
                acceptRideProgressbar.setProgress(100);
                stopPlaying();
                getLastRide();
                Log.e("REJECTSTATUS", "reject ride accepted " + status);

            }
        };
        mCountDownTimer.start();

//        ObjectAnimator animation = ObjectAnimator.ofInt(acceptRideProgressbar, "progress", 0, 100);
//        animation.setDuration(250000);
//        animation.setInterpolator(new DecelerateInterpolator());
//        animation.addListener(new Animator.AnimatorListener() {
//            @Overri   de
//            public void onAnimationStart(Animator animator) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                //do something when the countdown is complete
//                animator.cancel();
//                if (!status.equalsIgnoreCase("ACCEPTED")) {
//                    rejectRide();
//                }
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//            }
//        });
//        animation.start();

//        long elapsedTime = System.nanoTime() - startTime;
//        progressBarStatus = (int) ((20000 - elapsedTime) / 1000);
//        Log.e("ProgressBar", progressBarStatus + "");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (progressBarStatus = 0; progressBarStatus <= 20; progressBarStatus++) {
//
//                    progressBarbHandler.post(new Runnable() {
//                        @RequiresApi(api = Build.VERSION_CODES.M)
//                        public void run() {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                acceptRideProgressbar.setProgress(progressBarStatus, true);
//                            } else {
//                                acceptRideProgressbar.setProgress(progressBarStatus);
//                            }
//                                if(progressBarStatus==20 && !status.equalsIgnoreCase("ACCEPTED"))
//                                {
//                                    rejectRide();
//                                }
////                                if(progressBarStatus==10)
////                                    acceptRideProgressbar.setProgressTintList(requireContext().getColor(R.color.red)));
//                        }
//                    });
//
//
//                    try {
//                        Thread.sleep(1800);
//                    } catch (Exception ex) {
//                    }
//                }
//            }
//        }).start();

    }

    //getting last rides
    public void getLastRide1() {

        refreshEarning = false;
        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            final ProgressDialog progressDialog = new ProgressDialog(requireContext());
            progressDialog.setMessage("Loading......");
            // progressDialog.show();
            String url = Server.BASE_URL.concat("get_last_ride");
        /*String url = Server.BASE_URL.concat("rides?id=").concat(SessionManager.getUserId()).concat("&status=").concat(status)
                .concat("&utype=2");*/

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {


                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject data = new JSONObject(response);

                        if (data.has("status") && data.getBoolean("status")) {

                            LastRideResponse lastRideResponse;
                            JSONObject object = data.getJSONObject("data");
                            Gson gson = new Gson();

                            lastRideResponse = gson.fromJson(data.toString(), LastRideResponse.class);

                            lastRideData = gson.fromJson(object.toString(), LastRideData.class);

                            Log.e("LastRideStatus", lastRideData.getStatus());
                            Log.e("LastRIDEData", object.toString());

                            progressDialog.cancel();


                            //Check expiry of docs
                            checkDocumentExpiry(data);
                            //end check expiry of docs

                            if (lastRideData != null) {
                                status = lastRideData.getStatus();

                                if (status.equalsIgnoreCase("ACCEPTED")) {
                                    setOnlineSwitch(false);
                                    feedbackShown = false;
                                    if (SessionManager.getActiveRideId() != null) {
                                        if (!IS_ACTIVITY_RUNNING) {
                                            drivername = lastRideData.getDriverName();
                                            ridername = lastRideData.getUserName();
                                            riderName.setText(ridername);
                                            rideId = lastRideData.getRideId();
                                            driverEmail = getUserEmail();
                                            startLocationService();
                                        }
                                    }
                                }
                                if (status.equalsIgnoreCase("START_RIDE")) {
                                    setOnlineSwitch(false);
                                    feedbackShown = false;
                                    if (SessionManager.getActiveRideId() != null) {
                                        if (!IS_ACTIVITY_RUNNING) {
                                            drivername = lastRideData.getDriverName();
                                            rideId = lastRideData.getRideId();
                                            driverEmail = getUserEmail();
                                            startLocationService();
                                        }
                                    }
                                }
                                if (status.equalsIgnoreCase("Cancelled")) {
                                    try {
                                        if (IS_ACTIVITY_RUNNING) {
                                            stopLocationService();
                                        }
                                    } catch (Exception ex) {

                                    }
                                }
                                if (status.equalsIgnoreCase("Failed")) {
                                    try {
                                        if (IS_ACTIVITY_RUNNING) {
                                            stopLocationService();
                                        }
                                    } catch (Exception ex) {

                                    }
                                }
                                if (status.equalsIgnoreCase("Completed")) {
                                    if (lastRideData != null) {

                                        try {
                                            if (IS_ACTIVITY_RUNNING) {
                                                stopLocationService();
                                            }
                                        } catch (Exception ex) {

                                        }

                                        if (lastRideData.getIsTechnicalIssue().equalsIgnoreCase("Yes") || lastRideData.getIsTechnicalIssue().equalsIgnoreCase("No")) {
                                            pending_amount_layout.setVisibility(View.GONE);
                                            if (!(upload_doc_layout.getVisibility() == View.VISIBLE))
                                                setOnlineSwitch(true);
                                        } else if (lastRideData.getPaymentStatus().equalsIgnoreCase("Completed") && pending_amount_layout.getVisibility() == View.VISIBLE) {
                                            ((HomeActivity) requireActivity()).getEarningInfo("month", "1");
                                            Toast.makeText(requireContext(), "$ " + lastRideData.getTotalAmount() + " amount is received.", Toast.LENGTH_SHORT).show();
                                            pending_amount_layout.setVisibility(View.GONE);
                                            feedbackShown = false;
                                            //   is_online(lastRideData.getUserId(), "1", false);
                                            if (!(upload_doc_layout.getVisibility() == View.VISIBLE)) {
//                                                changeOnlineStatus(true);
//                                                getSwitchState(true);
                                                ((HomeActivity) requireActivity()).is_online(SessionManager.getUserId(), "1", false);
                                            }
                                            if (!(upload_doc_layout.getVisibility() == View.VISIBLE))
                                                setOnlineSwitch(true);
                                            refreshEarning = true;
                                        } else {
                                            if (!lastRideData.getPaymentStatus().equalsIgnoreCase("Completed")) {
                                                Log.d("CheckAmount", "NOT_DONE");
                                                ((HomeActivity) requireActivity()).is_online(lastRideData.getUserId(), "2", false);
                                                pending_amount_layout.setVisibility(View.VISIBLE);
                                                textViewAmountPending.setText("$ " + lastRideData.getTotalAmount() + " amount is pending.");
                                                handler.removeCallbacks(null);
                                                setOnlineSwitch(false);
                                            } else {
                                                Log.d("feedbackShown", String.valueOf(feedbackShown));
                                                if (!feedbackShown && lastRideData.getPaymentStatus().equalsIgnoreCase("Completed")) {
                                                    rideStatusLayout.setVisibility(View.VISIBLE);
                                                    ratingLayout.setVisibility(View.VISIBLE);
                                                    if(Utils.isNullOrEmpty(lastRideData.getUserLastname())){
                                                        txtDriverName.setText("How was your experience with Boss.");
                                                    }else{
                                                        txtDriverName.setText("How was your experience with " + lastRideData.getUserLastname());
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                                Log.e("OnResumeStatus", status);
                                SessionManager.setActiveRideId(lastRideData.getRideId());
                                if (SessionManager.getActiveRideId() != null) {
                                    postedRideId = SessionManager.getActiveRideId();
                                    Log.e("getRideStatus", "called");
                                    getRideStatus();
                                    //acceptRideStatus();

                                }
                            } else {
                                progressDialog.cancel();
                            }
                        } else {
                            progressDialog.cancel();
                            //Check expiry of docs
                            checkDocumentExpiry(data);
                            //end check expiry of docs
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
//                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                        progressDialog.cancel();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Response ", "" + error.getMessage());
                    progressDialog.cancel();
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
        } else {
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.cancel();
        }
    }

    //document varification
    @SuppressLint("SuspiciousIndentation")
    private void checkDocumentExpiry(JSONObject lastRideResponse) {
        try {
            carRegStatus = lastRideResponse.getBoolean("car_expiry");
            insuranceStatus = lastRideResponse.getBoolean("insurance_expiry");
            licenceStatus = lastRideResponse.getBoolean("license_expiry");
            identityStatus = lastRideResponse.getBoolean("identification_expiry");
            inspectionStatus = lastRideResponse.getBoolean("inspection_expiry");
            userStatus = lastRideResponse.getString("user_status").equalsIgnoreCase("3");
            licenceApproveStatus = lastRideResponse.getJSONObject("document_approval").getString("license_approve_status");
            insuranceApproveStatus = lastRideResponse.getJSONObject("document_approval").getString("insurance_approve_status");
            carRegistrationApproveStatus = lastRideResponse.getJSONObject("document_approval").getString("car_registration_approve_status");
            inspectionApproveStatus = lastRideResponse.getJSONObject("document_approval").getString("inspection_approval_status");
            verificationIdApproveStatus = lastRideResponse.getJSONObject("document_approval").getString("verification_id_approval_atatus");
            backgroundApproveStatus = lastRideResponse.getJSONObject("document_approval").getString("background_approval_status");
        } catch (Exception ex) {

        }
        try {

            if (userStatus || licenceApproveStatus.equalsIgnoreCase("2") || insuranceApproveStatus.equalsIgnoreCase("2") || carRegistrationApproveStatus.equalsIgnoreCase("2") || inspectionApproveStatus.equalsIgnoreCase("2") || verificationIdApproveStatus.equalsIgnoreCase("2") || backgroundApproveStatus.equalsIgnoreCase("2")) {
                upload_doc_layout.setVisibility(View.VISIBLE);
                rl_action_layout.setVisibility(View.GONE);
                txtRest.setVisibility(View.VISIBLE);
                txtRest.setText(getString(R.string.approval));
                ((HomeActivity) requireActivity()).setMySwitch(true);
                setOnlineSwitch(false);
                if (((HomeActivity) requireActivity()).textView_online_status.getText().toString().equalsIgnoreCase("online")) {
//                    changeOnlineStatus(false);
                    ((HomeActivity) requireActivity()).is_online(SessionManager.getUserId(), "3", false);
//                    ((HomeActivity) requireActivity()).mySwitch.performClick();
                }
            } else if (carRegStatus || insuranceStatus || licenceStatus || identityStatus || inspectionStatus) {
                upload_doc_layout.setVisibility(View.VISIBLE);
                rl_action_layout.setVisibility(View.VISIBLE);
                txt_required_doc_message.setText(getResources().getString(R.string.upload_required_docs));
                txtRest.setVisibility(View.GONE);
                setOnlineSwitch(false);
                if (((HomeActivity) requireActivity()).textView_online_status.getText().toString().equalsIgnoreCase("online")) {
                    ((HomeActivity) requireActivity()).is_online(SessionManager.getUserId(), "3", false);
                }


            } else if (lastRideResponse.getBoolean("driver_rest_time")) {
                upload_doc_layout.setVisibility(View.VISIBLE);
                rl_action_layout.setVisibility(View.GONE);
                txtRest.setVisibility(View.VISIBLE);
                txtRest.setText(getString(R.string.driver_in_rest));
                setOnlineSwitch(false);
                if (((HomeActivity) requireActivity()).textView_online_status.getText().toString().equalsIgnoreCase("online")) {
                    ((HomeActivity) requireActivity()).is_online(SessionManager.getUserId(), "3", false);

                }

            } else if (lastRideResponse.getBoolean("change_vehicle")) {
                upload_doc_layout.setVisibility(View.VISIBLE);
                rl_action_layout.setVisibility(View.VISIBLE);
                txt_required_doc_message.setText(getResources().getString(R.string.vehicle_expired));
                txtRest.setVisibility(View.GONE);

                setOnlineSwitch(false);
                if (((HomeActivity) requireActivity()).textView_online_status.getText().toString().equalsIgnoreCase("online")) {
                    ((HomeActivity) requireActivity()).is_online(SessionManager.getUserId(), "3", false);
                }

            } else {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 5000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                upload_doc_layout.setVisibility(View.GONE);
                if (!(upload_doc_layout.getVisibility() == View.VISIBLE)) setOnlineSwitch(true);
                if (((HomeActivity) requireActivity()).textView_online_status.getText().toString().equalsIgnoreCase("offline")) {
                    // card_online_status.performClick();
                    if (!getSwitchState(false) && !status.equalsIgnoreCase("accepted") && !status.equalsIgnoreCase("start_ride")) {
//                        Toast.makeText(requireContext(), "Now you can be online.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(requireContext(), R.string.online_now, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        } catch (Exception ex) {
        }

    }

    private void refreshFragment() {
        Fragment frg = null;
        frg = requireActivity().getSupportFragmentManager().findFragmentByTag(getString(R.string.requests));
        final FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null);
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    //updateing driver late-long
    public void updateDriverLatLong(final Double lat, final Double log) {
        try {
            if (CheckConnection.haveNetworkConnection(getContext())) {
                final ProgressDialog progressDialog = new ProgressDialog(requireContext());
                progressDialog.setMessage("Loading......");
                // progressDialog.show();
                String url = Server.BASE_URL.concat("/api/driver/UPDATE_LAT_LONG");

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.getCache().clear();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.cancel();
                        Log.e("UPDATE_LAT_LONG_DRIVER", "Lat long updated success" + currentLatitude + " " + currentLongitude);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("UPDATE_LAT_LONG_DRIVER", error.getMessage());
                        Log.e("UPDATE_LAT_LONG_DRIVER", "Lat long updated failed1" + currentLatitude + " " + currentLongitude);
                        progressDialog.cancel();
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
                            params.put("lat", String.valueOf(lat));
                            params.put("long", String.valueOf(log));
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
            } else {
                Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                progressDialog.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //turns between two location
    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    //alert dialog
    public void showAlert(Context context, String title, String message, int whichOne) {
        new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK).setTitle(title).setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        if (whichOne == 1) {
                            trackRide();
                        } else if (whichOne == 2) {
                            navigateRide();
                        }
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    //showing technical issues
    public void showTechnicalIssueAlert() {
        final Dialog alertDialog = new Dialog(requireActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.technical_alert_layout);
        alertDialog.setCancelable(false);
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        ProgressBar progressBar = alertDialog.findViewById(R.id.technical_progress);
        Button btnCancel = alertDialog.findViewById(R.id.btn_cancel_tech);
        Button btnSubmit = alertDialog.findViewById(R.id.btn_yes_tech);
        LinearLayout callingLayout = alertDialog.findViewById(R.id.calling_layout_tech);
        LinearLayout buttonLayout = alertDialog.findViewById(R.id.btn_layout);
        callingLayout.setVisibility(View.GONE);

        btnCancel.setOnClickListener(e -> {
            alertDialog.dismiss();
            showTechnicalIssueConfirmationAlert();

        });


        btnSubmit.setOnClickListener(e -> {
            completeRide("Yes");
            buttonLayout.setVisibility(View.GONE);
            callingLayout.setVisibility(View.VISIBLE);
        });

        callingLayout.setOnClickListener(e -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + getString(R.string.rideshare_call_center)));
            requireActivity().startActivity(intent);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    //confirmation of technical issues
    public void showTechnicalIssueConfirmationAlert() {
        final Dialog alertDialog = new Dialog(requireActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.technical_alert_layout);
        alertDialog.setCancelable(false);
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ProgressBar progressBar = alertDialog.findViewById(R.id.technical_progress);
        Button btnCancel = alertDialog.findViewById(R.id.btn_cancel_tech);
        Button btnSubmit = alertDialog.findViewById(R.id.btn_yes_tech);
        TextView textView = alertDialog.findViewById(R.id.txt_tech_msg);
        textView.setText(getString(R.string.complete_ride_confirmation));
        LinearLayout callingLayout = alertDialog.findViewById(R.id.calling_layout_tech);
        LinearLayout buttonLayout = alertDialog.findViewById(R.id.btn_layout);
        callingLayout.setVisibility(View.GONE);

        btnCancel.setOnClickListener(e -> {
            alertDialog.dismiss();
        });

        btnSubmit.setOnClickListener(e -> {
            alertDialog.dismiss();
            completeRide("No");
        });
        alertDialog.show();
    }

    //dialog for cancel ride
    public void confirmCancelRideAlert() {
        final Dialog alertDialog = new Dialog(requireActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.technical_alert_layout);
        alertDialog.setCancelable(false);
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ProgressBar progressBar = alertDialog.findViewById(R.id.technical_progress);
        Button btnCancel = alertDialog.findViewById(R.id.btn_cancel_tech);
        EditText editReason = alertDialog.findViewById(R.id.txt_reason);
        Button btnSubmit = alertDialog.findViewById(R.id.btn_yes_tech);
        TextView textView = alertDialog.findViewById(R.id.txt_tech_msg);
        textView.setText(getString(R.string.cancel_ride_confirm));
        LinearLayout callingLayout = alertDialog.findViewById(R.id.calling_layout_tech);
        LinearLayout reasonLayout = alertDialog.findViewById(R.id.reason_layout_tech);
        LinearLayout buttonLayout = alertDialog.findViewById(R.id.btn_layout);
        reasonLayout.setVisibility(View.VISIBLE);
        callingLayout.setVisibility(View.GONE);

        btnCancel.setOnClickListener(e -> {
            alertDialog.dismiss();
        });

        btnSubmit.setOnClickListener(e -> {
            if (editReason.getText().toString().isEmpty()) {
                Toast.makeText(requireActivity(), "Please write a reason.", Toast.LENGTH_SHORT).show();
            } else {
                alertDialog.dismiss();
                rejectRide(editReason.getText().toString());
            }
        });

        alertDialog.show();
    }

    //work manager
    private void startWorkManager(String ride_id, String driver_email, String driver_name) {

        WorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(GetDumaWorkManager.class)
//                        .setInitialDelay(2, TimeUnit.MINUTES)
                .addTag(LOCATION_UPDATE).setInputData(new Data.Builder().putString("ride_id", String.valueOf(ride_id)).putString("driver_name", driver_name).putString("driver_email", driver_email).build()).build();

        WorkManager.getInstance(requireContext()).enqueue(myWorkRequest);

    }

    private void setCustomMapStyle() {
        try {
            // Load the custom map style from the raw resource
            boolean success = myMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
            if (!success) {
                // Handle the case if the custom style couldn't be loaded
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showAgeAlert() {
        Global.putAlertValue("AgeAlertStatus", false, getActivity());
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
        pictureDialog.setTitle(R.string.app_name);
        pictureDialog.setMessage("NO MINORS ALLOWED TO RIDE WITHOUT AN ADULT");
        pictureDialog.setPositiveButton("OK", null);
        pictureDialog.setCancelable(false);
        pictureDialog.show();
    }

    private void openMap() {
        startActivity(new Intent(getContext(), WebView.class));
    }

    //submiting feedback
    private void submitFeedBack() {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Submitting feedback", false, false);
        comment = input_comment.getText().toString();
        loading.show();
        Log.d("rating", String.valueOf(ratingvalue));
        Log.d("comment", comment);
        Log.d("ride_id", String.valueOf(postedRideId));
        Log.d("rider_id", lastRideData.getUserId());

        Map<String, String> feedBack = new HashMap<>();
        feedBack.put("ride_id", String.valueOf(postedRideId));
        feedBack.put("rating", String.valueOf(ratingvalue));
        feedBack.put("comment", comment);
        feedBack.put("rider_id", lastRideData.getUserId());

        ApiNetworkCall apiService = ApiClient.getApiService();
        Call<ChangePasswordResponse> call = apiService.giveFeedBack("Bearer " + getKEY(), feedBack);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                ChangePasswordResponse jsonResponse = response.body();
                if (jsonResponse.getStatus()) {
                    loading.cancel();
                    Toast.makeText(getActivity(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    ratingLayout.setVisibility(View.GONE);
                    feedbackShown = true;
                } else {
                    loading.cancel();
                    Toast.makeText(getActivity(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                loading.cancel();
                Log.d("FeedbackFailed", t.getMessage());
            }

        });
    }

    // Fetch Distance
    public void fetchDistance(double originLat, double originLong, double distLat, double distLong, float bearing) {
        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            Log.d("DistanceAPI", "Lat" + originLat + "Long" + originLong);
            if (SystemClock.elapsedRealtime() - mLastHitTime < 3000) {
                return;
            }
            mLastHitTime = SystemClock.elapsedRealtime();

            Log.d("DistanceAPIHit", "TRUE");
            String base_url = "https://maps.googleapis.com/";
            String origin = originLat + "," + originLong;
            String dest = distLat + "," + distLong;
            String key = getString(R.string.google_android_map_api_key);
            Retrofit retrofit = new Retrofit.Builder().baseUrl(base_url).addConverterFactory(GsonConverterFactory.create()).build();
            ApiNetworkCall service = retrofit.create(ApiNetworkCall.class);
            service.getDistanceFromDistanceMatrix(origin, dest, key).enqueue(new Callback<DistanceMatrixResponse>() {
                @Override
                public void onResponse(Call<DistanceMatrixResponse> call, retrofit2.Response<DistanceMatrixResponse> response) {
                    Log.e("DistanceAPIResponse", response.toString());
                    DistanceMatrixResponse matrixResponse = response.body();
//                    Log.e("DistanceAPIValue", matrixResponse.getRows().get(0).getElements().get(0).getDistance().getValue().toString());
                    try {
                        double distanceInMeters = Double.parseDouble(matrixResponse.getRows().get(0).getElements().get(0).getDistance().getValue().toString());
                        double distanceInMiles1 = distanceInMeters * 0.00062137;
                        double distanceInMile = distanceInMeters * 0.00062137;

                        Log.d("distance", matrixResponse.getRows().get(0).getElements().get(0).getDistance().getValue().toString());
                        Log.d("duration", matrixResponse.getRows().get(0).getElements().get(0).getDuration().getValue().toString());

                        //here we replaced getduration to getdistance
                        if (Integer.parseInt(matrixResponse.getRows().get(0).getElements().get(0).getDistance().getValue().toString()) <= 1000) {
//                            Toast.makeText(getApplicationContext(), "You have reached near pickup location.", Toast.LENGTH_SHORT).show();
                            startRide();
                            Global.putDistanceVisibility("VisibilityStatus", true, getActivity());
//                            trackRideBtn.setVisibility(View.GONE);
//                            startRideBtn.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getApplicationContext(), "You haven't reached near Pickup Location so you can't start your ride.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<DistanceMatrixResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Some Error Occurred", Toast.LENGTH_SHORT).show();
                    Log.e("DistanceAPI", t.toString());
                }
            });
        } else {
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }

}


