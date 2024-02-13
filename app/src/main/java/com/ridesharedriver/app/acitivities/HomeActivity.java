package com.ridesharedriver.app.acitivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ridesharedriver.app.BuildConfig;
import com.ridesharedriver.app.fragement.AboutUs;
import com.ridesharedriver.app.fragement.DocumentApprovalFragment;
import com.ridesharedriver.app.pojo.CheckAppVersionResponse;
import com.ridesharedriver.app.pojo.DeleteAccount;
import com.ridesharedriver.app.pojo.LogoutResponse;
import com.ridesharedriver.app.pojo.driverEarning.Data;
import com.ridesharedriver.app.pojo.driverEarning.DriverEarningResponse;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.ridesharedriver.app.R;
import com.ridesharedriver.app.Server.Server;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.custom.CheckConnection;
import com.ridesharedriver.app.custom.GPSTracker;
import com.ridesharedriver.app.custom.Utils;
import com.ridesharedriver.app.fragement.AcceptedRequestFragment;
import com.ridesharedriver.app.fragement.BankDetailFragment;
import com.ridesharedriver.app.fragement.HelpFragment;
import com.ridesharedriver.app.fragement.HomeFragment;
import com.ridesharedriver.app.fragement.PaymentHistory;
import com.ridesharedriver.app.fragement.PrivacyPolicyFragment;
import com.ridesharedriver.app.fragement.ProfileFragment;
import com.ridesharedriver.app.fragement.SetDestinationFragment;
import com.ridesharedriver.app.fragement.UploadDomentFragment;
import com.ridesharedriver.app.fragement.VehicleInformationFragment;
import com.ridesharedriver.app.pojo.PendingRequestPojo;
import com.ridesharedriver.app.pojo.Tracking;
import com.ridesharedriver.app.pojo.User;
import com.ridesharedriver.app.pojo.changepassword.ChangePasswordResponse;
import com.ridesharedriver.app.pojo.driverstatus.UpdateDriverStatus;
import com.ridesharedriver.app.pojo.getprofile.GetProfile;
import com.ridesharedriver.app.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;

import static com.ridesharedriver.app.session.SessionManager.getKEY;
import static com.ridesharedriver.app.session.SessionManager.getSwitchState;
import static com.ridesharedriver.app.session.SessionManager.getUserEmail;
import static com.ridesharedriver.app.session.SessionManager.getUserName;
import static com.ridesharedriver.app.session.SessionManager.setSwitchState;
import static com.mapbox.mapboxsdk.maps.Telemetry.initialize;

/**
 * Created by android on 7/3/17.
 */

//HomeScreen
public class HomeActivity extends ActivityManagePermission
        implements NavigationView.OnNavigationItemSelectedListener, ProfileFragment.ProfileUpdateListener,
        ProfileFragment.UpdateListener, LocationEngineListener {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean isSwitchChecked = false;
    public Toolbar toolbar;
    private HomeFragment homeFragment;
    private ImageView avatar;
    public TextView username, txt_email,txtDriverRating, txt_day_earning, textView_online_status;
    public CardView card_online_status;
    public LinearLayout linearLayout, ll_earning;
    NavigationView navigationView;

    Bundle source;
    public SwitchCompat mySwitch;
    TextView status_text;

    String go = "";
    private GPSTracker gpsTracker;
    public Dialog alertDialog;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    public static double driverCurrentLat, driverCurrentLong;
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 minute

    int min, hr, sec;
    CountDownTimer newtimer;

    // Declaring a Location Manager
    protected LocationManager locationManager;
    PendingRequestPojo pojo;
    LocationEngine locationEngine;
    String[] permissions =
            {PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};

    TextView textTimer;

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private static final long MILLIS_IN_FUTURE = 100000000;
    private Handler customHandler = new Handler();

    private long startTime = 0L;

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    Handler handler;


    private int seconds = 0;
    boolean running = false;
    int hours,minutes;
    boolean hoursisCompeleted= true;
    boolean alertShown= true;
    //private AppUpdateManager appUpdateManager;
    private static final int FLEXIBLE_APP_UPDATE_REQ_CODE = 123;
    //private InstallStateUpdatedListener installStateUpdatedListener;

    String LatestVersion,CurrentVersion;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);
        initialize();
        checkAppVersionApi();



        handler = new Handler();
        runTimer();


        homeFragment = new HomeFragment();
        if (SessionManager.getStatus()) {
            BindView();
            Intent intent = getIntent();

            if (intent != null && intent.hasExtra("action")) {

                String action = intent.getStringExtra("action");
                homeFragment = new HomeFragment();
                Bundle b = new Bundle();
                b.putString("status", action);
                homeFragment.setArguments(b);
                changeFragment(homeFragment, getString(R.string.requests));
            } else {
                if (intent != null && intent.hasExtra("go")) {
                    go = intent.getStringExtra("go");
                    if (!go.equals("") && go.equals("vehicle")) {
                        changeFragment(new VehicleInformationFragment(), getString(R.string.add_vehicleinfo));
                    } else if (!go.equals("") && go.equals("doc")) {
                        changeFragment(new UploadDomentFragment(), getString(R.string.upload_doc));
                    }
                } else {
                    navigationView.setCheckedItem(R.id.home);
                    onNavigationItemSelected(navigationView.getMenu().findItem(R.id.home));
                }
            }

        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        getLocation();
        updateloginlogout();

        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        callLocationEngine();

        requestPermission();


    }

    //request Permission
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askCompactPermissions(permissions, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    // getLocation();
                    setListener();
                }

                @Override
                public void permissionDenied() {
                }

                @Override
                public void permissionForeverDenied() {
                    openSettingsApp(getApplicationContext());
                }
            });

        } else {
            setListener();
        }
    }

    //getLocation
    public void getLocation() {
        gpsTracker = new GPSTracker(HomeActivity.this);
        if (gpsTracker.canGetLocation()) {
            driverCurrentLat = gpsTracker.getLatitude();
            driverCurrentLong = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void setupDrawer() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                closeKeyboard();
            }
        };
        mDrawerToggle.getDrawerArrowDrawable()
                .setColor(getResources().getColor(R.color.white));//changing drawer icon color
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }


    //close keyboard
    public void closeKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) this
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(),
                    0
            );

        } catch (NullPointerException ex) {

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void drawer_close() {
        mDrawerLayout.closeDrawers();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        AcceptedRequestFragment acceptedRequestFragment;
        Bundle bundle;
        switch (item.getItemId()) {
            case R.id.home:
                card_online_status.setVisibility(View.VISIBLE);
                ll_earning.setVisibility(View.VISIBLE);
                homeFragment = new HomeFragment();
                changeFragment(homeFragment, getString(R.string.home));
                break;
            case R.id.destination:
                card_online_status.setVisibility(View.GONE);
                ll_earning.setVisibility(View.GONE);
                changeFragment(new SetDestinationFragment(), "Set Destination");
                break;
            case R.id.pending_requests:
                card_online_status.setVisibility(View.GONE);
                ll_earning.setVisibility(View.GONE);
                acceptedRequestFragment = new AcceptedRequestFragment();
                bundle = new Bundle();
                bundle.putString("status", "PENDING");
                acceptedRequestFragment.setArguments(bundle);
                changeFragment(acceptedRequestFragment, getString(R.string.requests));
                break;
            case R.id.accepted_requests:
                card_online_status.setVisibility(View.GONE);
                ll_earning.setVisibility(View.GONE);
                acceptedRequestFragment = new AcceptedRequestFragment();
                bundle = new Bundle();
                bundle.putString("status", "ACCEPTED");
                acceptedRequestFragment.setArguments(bundle);
                changeFragment(acceptedRequestFragment, getString(R.string.requests));
                break;
            case R.id.completed_rides:
                ll_earning.setVisibility(View.GONE);
                card_online_status.setVisibility(View.GONE);
                acceptedRequestFragment = new AcceptedRequestFragment();
                bundle = new Bundle();
                bundle.putString("status", "COMPLETED");
                acceptedRequestFragment.setArguments(bundle);
                changeFragment(acceptedRequestFragment, getString(R.string.requests));
                break;
            case R.id.cancelled:
                card_online_status.setVisibility(View.GONE);
                ll_earning.setVisibility(View.GONE);
                acceptedRequestFragment = new AcceptedRequestFragment();
                bundle = new Bundle();
                bundle.putString("status", "CANCELLED");
                acceptedRequestFragment.setArguments(bundle);
                changeFragment(acceptedRequestFragment, getString(R.string.requests));
                break;
            case R.id.vehicle_information:
                ll_earning.setVisibility(View.GONE);
                card_online_status.setVisibility(View.GONE);
                changeFragment(new VehicleInformationFragment(), getString(R.string.vehicle_info));
                break;
            case R.id.payment_detail:
                ll_earning.setVisibility(View.GONE);
                card_online_status.setVisibility(View.GONE);
                changeFragment(new PaymentHistory(), getString(R.string.payment_history));
                break;
            case R.id.profile:
                ll_earning.setVisibility(View.GONE);
                card_online_status.setVisibility(View.GONE);
                changeFragment(new ProfileFragment(), getString(R.string.profile));
                break;

            case R.id.document_status:
                ll_earning.setVisibility(View.GONE);
                card_online_status.setVisibility(View.GONE);
                changeFragment(new DocumentApprovalFragment(), getString(R.string.document_approval));
                break;

            case R.id.help:
                ll_earning.setVisibility(View.GONE);
                card_online_status.setVisibility(View.GONE);
                changeFragment(new HelpFragment(), "Help");

                break;

            case R.id.menu_bank_detail:
                ll_earning.setVisibility(View.GONE);
                card_online_status.setVisibility(View.GONE);
                changeFragment(new BankDetailFragment(), "Bank Detail");
                break;
            case R.id.menu_privacy_policy:
                ll_earning.setVisibility(View.GONE);
                card_online_status.setVisibility(View.GONE);
                changeFragment(new PrivacyPolicyFragment(), "Privacy Policy");
                break;
            case R.id.menu_about:
                ll_earning.setVisibility(View.GONE);
                card_online_status.setVisibility(View.GONE);
                changeFragment(new AboutUs(), "About Us");
                break;
            case R.id.delete_account:
                showAlert(this, "Delete Account", "Do you want to delete your account?");
                break;
            case R.id.logout:
                if (isSwitchChecked){
                    is_online(SessionManager.getUserId(), "3", true);
                }else {
                    logout();
                }

                break;
            default:
                break;
        }
        return true;
    }

    public void disableOnlineSwitch(boolean status) {

    }

    public void showAlert(Context context, String title, String message) {
        new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_DARK)
                .setTitle(title)
                .setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        deleteAccount();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void setDisableEnableSwitch(boolean status) {
        try {
            mySwitch.setEnabled(status);
            card_online_status.setEnabled(status);
        } catch (Exception ex) {

        }
    }

    //delete Account Api
    public void deleteAccount() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        if (CheckConnection.haveNetworkConnection(this)) {
            progressDialog.setMessage("Deleting Account.....");
            progressDialog.setCancelable(false);
            progressDialog.show();

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<DeleteAccount> call =
                    apiService.deleteAccount("Bearer " + SessionManager.getKEY());
            call.enqueue(new Callback<DeleteAccount>() {
                @Override
                public void onResponse(Call<DeleteAccount> call, retrofit2.Response<DeleteAccount> response) {
                    DeleteAccount jsonResponse = response.body();
                    if (jsonResponse != null) {
                        if (jsonResponse.getStatus()) {
                            progressDialog.cancel();
                            Toast.makeText(HomeActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                            SessionManager.logoutUser(HomeActivity.this);
                            finish();
                        } else {
                            progressDialog.cancel();
                            Toast.makeText(HomeActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }

                }

                @Override
                public void onFailure(Call<DeleteAccount> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                    progressDialog.cancel();
                }
            });
        } else {
            progressDialog.cancel();
            Toast.makeText(HomeActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }


    //switch for online offline
    public void setMySwitch(boolean status) {
        if (status) {
            mySwitch.setChecked(false);
            status_text.setText("Offline");
        } else {
            mySwitch.setChecked(true);
            status_text.setText("Online");
        }
    }


    //to send message
    private void sendMessage() {
        Intent intent = new Intent("online_status");
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    //to check driver is online or not
    public void is_online(String user_id, String status, Boolean what) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        if (CheckConnection.haveNetworkConnection(this)) {
            progressDialog.setMessage("Loading.....");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Map<String, String> driverStatus = new HashMap<>();
            driverStatus.put("is_online", status);

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<UpdateDriverStatus> call =
                    apiService.updateDriverStatus("Bearer " + SessionManager.getKEY(), driverStatus);
            call.enqueue(new Callback<UpdateDriverStatus>() {
                @Override
                public void onResponse(Call<UpdateDriverStatus> call, retrofit2.Response<UpdateDriverStatus> response) {
                    UpdateDriverStatus jsonResponse = response.body();
                    if (jsonResponse != null) {
                        sendMessage();
                        if (jsonResponse.getStatus()) {
                            progressDialog.cancel();
//                        Toast.makeText(HomeActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                            if (jsonResponse.gettotal_working_hour() != null) {
                                SessionManager.setTotalTime(jsonResponse.gettotal_working_hour());
                            }
                            if (what) {
                                logout();

                            } else {
                                changeBottomOnlineStatus(status, true);
                                if (homeFragment != null && status.equalsIgnoreCase("1")) {
                                    try {
                                        homeFragment.changeOnlineStatus(isSwitchChecked);
                                    } catch (Exception ex) {

                                    }
                                }
                                isSwitchChecked = false;
                                if (status.equals("1")) {
                                    hoursisCompeleted = true;
                                    setSwitchState(true);
                                    status_text.setText(getString(R.string.online));
                                    mySwitch.setChecked(true);

                                } else if (status.equals("3")) {
                                    hoursisCompeleted = false;
                                    mySwitch.setChecked(false);
                                    status_text.setText(getString(R.string.offline));
                                    setSwitchState(false);
                                }
                            }
                        } else {
                            try {
                                if (jsonResponse.gettotal_working_hour() != null) {
                                    SessionManager.setTotalTime(jsonResponse.gettotal_working_hour());
                                }

                            } catch (Exception ex) {

                            }
                            Toast.makeText(HomeActivity.this, jsonResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            if (what) {
                                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                SessionManager.logoutUser(HomeActivity.this);
                                finish();
                            }
                            changeBottomOnlineStatus(status, false);
                            progressDialog.cancel();
                        }
                        seconds = Integer.parseInt(SessionManager.getTotalTime());
                        if (status.equals("1")) {
                            running = true;
                        } else {
                            running = false;
                        }

                    }

                }

                @Override
                public void onFailure(Call<UpdateDriverStatus> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                    progressDialog.cancel();
                    sendMessage();
                }
            });
        } else {
            progressDialog.cancel();
            Toast.makeText(HomeActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            sendMessage();
        }
    }

    //log out Api
    public void logout() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        if (CheckConnection.haveNetworkConnection(this)) {
            progressDialog.setMessage("Loading.....");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Map<String, String> logoutStatus = new HashMap<>();

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<LogoutResponse> call =
                    apiService.logoutStatus("Bearer " + SessionManager.getKEY(), logoutStatus);
            call.enqueue(new Callback<LogoutResponse>() {
                @Override
                public void onResponse(Call<LogoutResponse> call, retrofit2.Response<LogoutResponse> response) {
                    LogoutResponse jsonResponse = response.body();
                    if (jsonResponse != null) {

                        if (jsonResponse.getStatus()) {
                            progressDialog.cancel();
                            ll_earning.setVisibility(View.GONE);
                            card_online_status.setVisibility(View.GONE);
                            try {
                                SharedPreferences preferences = getSharedPreferences("secret_shared_prefs_bank_detail",
                                        MODE_PRIVATE);
                                preferences.edit().clear().apply();
//                                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                SessionManager.logoutUser(HomeActivity.this);
                                finish();

                            } catch (Exception ex) {

                            }


                        } else {
                            Toast.makeText(getApplicationContext(), jsonResponse.getMessage().toString(), Toast.LENGTH_SHORT).show();

                        }
                    }

                }

                @Override
                public void onFailure(Call<LogoutResponse> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                    progressDialog.cancel();
                    sendMessage();
                }
            });
        } else {
            progressDialog.cancel();
            Toast.makeText(HomeActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            sendMessage();
        }
    }

    //update Login Logout
    public void updateloginlogout() {

        RequestParams params = new RequestParams();
        params.put("status", "2");

        Server.post("updateloginlogout", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {

                    if (response.has("status") && response.getBoolean("status")) {
                        CheckConnection checkConnection = new CheckConnection(HomeActivity.this);
                        checkConnection.isAnonymouslyLoggedIn();


                    } else {
                        Toast.makeText(HomeActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(HomeActivity.this, getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                //swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();


        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
    }

    public void changeFragment(final Fragment fragment, final String fragmenttag) {
        try {
            drawer_close();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
            fragmentTransaction.replace(R.id.frame, fragment, fragmenttag);
            fragmentTransaction.commit();
        } catch (Exception e) {
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected() {
        try {
            locationEngine.requestLocationUpdates();
        } catch (Exception ex) {
            requestPermission();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null && pojo != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            setStatus(pojo, "", false);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            getProfile();
        } else {
            Toast.makeText(HomeActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
        }
        if (locationEngine != null) {
            locationEngine.activate();
        }
        getLocation();
    }

    private void callLocationEngine() {
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.setFastestInterval(5);
        locationEngine.setInterval(10);
        locationEngine.setSmallestDisplacement(5);
        locationEngine.activate();
    }

    @SuppressLint("ParcelCreator")
    public class CustomTypefaceSpan extends TypefaceSpan {

        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        @SuppressLint("WrongConstant")
        private void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }
            paint.setTypeface(tf);
        }
    }

    //Fonts for items
    private void applyFontToMenuItem(MenuItem mi) {
        // Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Medium.otf");
        Typeface font = Typeface.createFromAsset(getAssets(), "font/montserrat_regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    //fonts for titles
    public void fontToTitleBar(String title) {
        // Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
        Typeface font = Typeface.createFromAsset(getAssets(), "font/montserrat_regular.ttf");
        title = "<font color='#FFFFFF'>" + title + "</font>";
        SpannableString s = new SpannableString(title);
        s.setSpan(font, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            toolbar.setTitle(Html.fromHtml(String.valueOf(s), Html.FROM_HTML_MODE_LEGACY));
        } else {
            toolbar.setTitle((Html.fromHtml(String.valueOf(s))));
        }
    }

    //back press handler
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawer_close();
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String action = intent.getStringExtra("action");
            homeFragment = new HomeFragment();
            Bundle b = new Bundle();
            b.putString("status", action);
            homeFragment.setArguments(b);
            changeFragment(homeFragment, getString(R.string.requests));
        }
    }

    public void initViews() {
        //bottom online/ offline
        card_online_status = findViewById(R.id.card_online_status);
        textView_online_status = findViewById(R.id.tv_online_status);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txt_day_earning = findViewById(R.id.txt_day_earning);
        ll_earning = findViewById(R.id.ll_earning);
        toolbar.setTitle(getString(R.string.app_name));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        avatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile);
        avatar.setOnClickListener(e -> {
            changeFragment(new ProfileFragment(), "Change Profile");
        });
        linearLayout = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.linear);
        username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_name);
        txt_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_email);
        txtDriverRating = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtDriverRating);
        TextView v = (TextView) navigationView.getHeaderView(0).findViewById(R.id.version);
        textTimer = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_timer);
        username.setText(getUserName());
        txt_email.setText(getUserEmail());

        if (getSwitchState(false)) {

            startLoginTimer("1", textTimer);

        } else {

            startLoginTimer("3", textTimer);

        }
//        new LoginTimerCountDown(textTimer, this);

        card_online_status.setOnClickListener(e -> {
            mySwitch.performClick();
        });

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            v.setText("V ".concat(version));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_navigation_items, menu);
        MenuItem item = menu.findItem(R.id.myswitch);
        MenuItem status = menu.findItem(R.id.status_text);
        MenuItem home = menu.findItem(R.id.home);
        MenuItem pending_requests = menu.findItem(R.id.pending_requests);
        MenuItem accepted_requests = menu.findItem(R.id.accepted_requests);
        MenuItem completed_rides = menu.findItem(R.id.completed_rides);
        MenuItem cancelled = menu.findItem(R.id.cancelled);
        MenuItem payment_detail = menu.findItem(R.id.payment_detail);
        MenuItem profile = menu.findItem(R.id.profile);
        MenuItem help = menu.findItem(R.id.help);
        MenuItem bankAccount = menu.findItem(R.id.menu_bank_detail);
        MenuItem deletAccount = menu.findItem(R.id.delete_account);
        MenuItem destination = menu.findItem(R.id.destination);
        MenuItem logout = menu.findItem(R.id.logout);
        MenuItem privacyPolicy = menu.findItem(R.id.menu_privacy_policy);
        MenuItem aboutUs = menu.findItem(R.id.menu_about);
        MenuItem docStatus = menu.findItem(R.id.document_status);

        home.setVisible(false);
        docStatus.setVisible(false);
        pending_requests.setVisible(false);
        accepted_requests.setVisible(false);
        completed_rides.setVisible(false);
        cancelled.setVisible(false);
        payment_detail.setVisible(false);
        profile.setVisible(false);
        help.setVisible(false);
        destination.setVisible(false);
        bankAccount.setVisible(false);
        logout.setVisible(false);
        privacyPolicy.setVisible(false);
        aboutUs.setVisible(false);
        deletAccount.setVisible(false);


        item.setActionView(R.layout.switch_layout);

        mySwitch = item.getActionView().findViewById(R.id.switchForActionBar);
        status_text = item.getActionView().findViewById(R.id.status_text);
        if (getSwitchState(false)) {
//            mySwitch.setChecked(true);
//            status_text.setText(getString(R.string.online));
//            is_online(SessionManager.getUserId(), "1", false);
//            // new ShowServiceAlert().showAlert(this, "", "1");
            changeBottomOnlineStatus("1", true);

        } else {
//            mySwitch.setChecked(false);
//            status_text.setText(getString(R.string.offline));
//            is_online(SessionManager.getUserId(), "3", false);
//            // new ShowServiceAlert().showAlert(this, "", "3");
            changeBottomOnlineStatus("3", true);

        }

        //switch listener
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Utils.haveNetworkConnection(getApplicationContext())) {
//                 homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.requests));
                    isSwitchChecked = isChecked;

                    if (isChecked) {
                        is_online(SessionManager.getUserId(), "1", false);
                        Log.d("token",SessionManager.getKEY().toString());
                       // startLoginTimer("1", textTimer);
                        // Toast.makeText(HomeActivity.this,"online",Toast.LENGTH_SHORT).show();
                        //startLoginTimer("1", textTimer);
//                        status_text.setText(getString(R.string.online));
                        //  ShowServiceAlert.showAlert(HomeActivity.this,"","1");

                    } else {
                        is_online(SessionManager.getUserId(), "3", false);
                        Log.d("token",SessionManager.getKEY().toString());

                        //startLoginTimer("3", textTimer);

                        //Toast.makeText(HomeActivity.this,"offline",Toast.LENGTH_SHORT).show();
                        //stopLoginTimer("3",textTimer);
//                        status_text.setText(getString(R.string.offline));
                        //  ShowServiceAlert.showAlert(HomeActivity.this,"","3");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.network_not_available), Toast.LENGTH_LONG).show();
                }
            }
        });
        return true;
    }


    //changing bottom online offline status
    private void changeBottomOnlineStatus(String status, boolean isTrue) {
        if (isTrue) {
            if (status.equals("1")) {
                card_online_status.setCardBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.greenOnline, null));
                textView_online_status.setText(getString(R.string.online));
                status_text.setText(getString(R.string.online));
                mySwitch.setChecked(true);
            } else if (status.equals("3")) {

                card_online_status.setCardBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.redOffline, null));
                status_text.setText(getString(R.string.offline));
                textView_online_status.setText(getString(R.string.offline));
                mySwitch.setChecked(false);
            }
        } else {
            card_online_status.setCardBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.redOffline, null));
            status_text.setText(getString(R.string.offline));
            textView_online_status.setText(getString(R.string.offline));

            mySwitch.setChecked(false);
            status_text.setText(getString(R.string.offline));
            setSwitchState(false);
        }
    }

    public void BindView() {
        initViews();
        setupDrawer();
        // switchCompat.setChecked(true);
        //is_online.setText(getResources().getString(R.string.online));
        //is_online(SessionManager.getUserId(), "1", false);

        Log.d("latitude msg", String.valueOf(latitude));
        Log.d("longitude msg", String.valueOf(longitude));

        SessionManager.setStatus(true);
        Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
        // username.setTypeface(font);
        //is_online.setTypeface(font);
        toolbar.setTitle("");
        if (Utils.haveNetworkConnection(getApplicationContext())) {
            getInfo();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.network_not_available), Toast.LENGTH_LONG).show();
            Glide.with(HomeActivity.this).load(SessionManager.getAvatar()).apply(new RequestOptions().error(R.drawable.user_default)).into(avatar);
        }

        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            getProfile();
        } else {
            Toast.makeText(HomeActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
        }

        Intent intent = getIntent();
        source = intent.getBundleExtra("source");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pojo != null && locationEngine != null) {
            locationEngine.removeLocationEngineListener(this);
        }


//        new change on 26_09_2022
        if (getSwitchState(false)) {

            is_online(SessionManager.getUserId(), "1", false);

        } else {

            is_online(SessionManager.getUserId(), "3", false);

        }

//        new change end here
    }

    @Override
    public void update(String url) {
        if (!url.equals("")) {
            Glide.with(getApplicationContext()).load(url).apply(new RequestOptions().error(R.drawable.user_default)).into(avatar);
        }
    }

    @Override
    public void name(String name) {
        if (!name.equals("")) {
        }
    }

    //updating driver late long
    public static void updateDriverLatLong() {
        Map<String, String> driverStatus = new HashMap<>();
        driverStatus.put("lat", String.valueOf(driverCurrentLat));
        driverStatus.put("long", String.valueOf(driverCurrentLong));

        Log.d("msg lat long ", driverCurrentLat + " " + driverCurrentLong);
        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<ChangePasswordResponse> call = apiService.updateLatLong("Bearer " + SessionManager.getKEY(), driverStatus);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                ChangePasswordResponse jsonResponse = response.body();

            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
            }
        });
    }

//to getinfo
    public void getInfo() {
        RequestParams params = new RequestParams();
        params.put("user_id", SessionManager.getUserId());
        // username.setText(SessionManager.getName());
        Glide.with(getApplicationContext()).load(SessionManager.getAvatar()).apply(new RequestOptions().error(R.drawable.user_default)).into(avatar);

        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.GET_PROFILE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("success", response.toString());
                try {


                    Gson gson = new Gson();
                    User user = gson.fromJson(response.getJSONObject("data").toString(), User.class);
                    user.setKey(SessionManager.getKEY());
                    SessionManager.setUser(gson.toJson(user));

                    Glide.with(getApplicationContext()).load(user.getAvatar()).apply(new RequestOptions().error(R.drawable.user_default)).into(avatar);

                } catch (JSONException e) {

                }
            }
        });
    }

    public void setPojo(PendingRequestPojo pojo) {
        this.pojo = pojo;
    }

    public void setStatus(PendingRequestPojo pojo, String status, boolean what) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Tracking/"
                + pojo.getRide_id());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                        Tracking tracking = dataSnapshot.getValue(Tracking.class);
                        tracking.setClient_id(pojo.getUser_id());
                        tracking.setDriver_id(pojo.getDriver_id());
                        tracking.setRide_id(pojo.getRide_id());
                        tracking.setDriver_latitude(latitude);
                        tracking.setDriver_longitude(longitude);
                        if (what) {
                            tracking.setStatus(status);
                        }
                        reference.setValue(tracking);
                    } else {
                        Tracking tracking1 = new Tracking();
                        tracking1.setClient_id(pojo.getUser_id());
                        tracking1.setDriver_id(pojo.getDriver_id());
                        tracking1.setRide_id(pojo.getRide_id());
                        tracking1.setDriver_latitude(latitude);
                        tracking1.setDriver_longitude(longitude);
                        if (what) {
                            tracking1.setStatus(status);
                        }
                        reference.setValue(tracking1);
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setListener() {
        locationEngine.addLocationEngineListener(this);
    }
//get Profile
    public void getProfile() {

        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<GetProfile> call = apiService.getProfile("Bearer " + SessionManager.getKEY());
        call.enqueue(new Callback<GetProfile>() {
            @Override
            public void onResponse(Call<GetProfile> call, retrofit2.Response<GetProfile> response) {
                GetProfile jsonResponse = response.body();
                if (jsonResponse != null) {
                    if (jsonResponse.getStatus()) {


                        username.setText(jsonResponse.getData().getName());
                        txt_email.setText(jsonResponse.getData().getEmail());
                        txtDriverRating.setText(jsonResponse.getData().getTotalRating());

                        if (jsonResponse.getData().getProfileImage() != null) {
                            String profileUrl = jsonResponse.getData().getProfileImage();
                            if (profileUrl.isEmpty() || profileUrl.equalsIgnoreCase("")) {
                                Glide.with(HomeActivity.this).load(R.drawable.user_default).into(avatar);
                            } else {
                                Glide.with(HomeActivity.this).load(jsonResponse.getData().getProfilePic()).into(avatar);
                            }
                        }
                        String name = jsonResponse.getData().getName();
                        setTitle(name);
                    } else {
                        Toast.makeText(HomeActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetProfile> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
            }
        });
    }

    public void showDialog() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
                showAlert(HomeActivity.this, source);
            }
        });
    }

    //alert dialog
    public static void showAlert(Context context, Bundle messageId) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(messageId.getString("title"));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private List<Data> earningData = null;

    //earning Api
    public void getEarningInfo(String param, String value) {
        if (CheckConnection.haveNetworkConnection(this)) {

            String url = Server.BASE_URL.concat("earn?").concat(param).concat("=").concat(value);
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject data = new JSONObject(response);
                        Log.e("Response", "EarningonResponse = \n " + response);

                        if (data.has("status") && data.getBoolean("status")) {

                            Gson gson = new Gson();
                            DriverEarningResponse driverEarningResponse = gson.fromJson(data.toString(), DriverEarningResponse.class);
                            driverEarningResponse.getData();

                            earningData = driverEarningResponse.getData();
                            try {
                                txt_day_earning.setText(String.format("%s", driverEarningResponse.getCurrentDayEarning()));
                            } catch (Exception ex) {
                                txt_day_earning.setText("0.00");
                            }
                        } else {
//                        Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {

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
            Toast.makeText(this, getString(R.string.network), Toast.LENGTH_LONG).show();

        }
    }

//    public void getVersion() {
//        final Dialog dialog = new Dialog(getApplicationContext());
//        Map<String, String> details = new HashMap<>();
//        details.put("id", "1");
//        String token = "Bearer " + SessionManager.getKEY();
//        ApiNetworkCall apiService = ApiClient.getApiService();
//        Call<VersionResponse> call = apiService.getVersion(token,details);
//        call.enqueue(new Callback<VersionResponse>() {
//            @Override
//            public void onResponse(Call<VersionResponse> call, retrofit2.Response<VersionResponse> response) {
//                ArrayList<VersionResponse> list = new ArrayList();
//
//
//                Toast.makeText(getApplicationContext(),response.body().getMessage().toString(),Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(Call<VersionResponse> call, Throwable t) {
//                Log.d("Failed", "RetrofitFailed");
//                dialog.cancel();
//            }
//        });
//    }



    //This alert for driver before getting offline
    public void showOfflineAlert() {
        alertDialog = new Dialog(this);
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
        btnCancel.setVisibility(View.GONE);
        Button btnSubmit = alertDialog.findViewById(R.id.btn_yes_tech);
        btnSubmit.setText("Ok");
        LinearLayout callingLayout = alertDialog.findViewById(R.id.calling_layout_tech);
        LinearLayout buttonLayout = alertDialog.findViewById(R.id.btn_layout);
        TextView textView = alertDialog.findViewById(R.id.txt_tech_msg);
        textView.setText(getString(R.string.offline_alert));
        callingLayout.setVisibility(View.GONE);


        btnSubmit.setOnClickListener(e -> {
            SessionManager.setOfflineAlertStatus(true);
            alertDialog.dismiss();
        });


        alertDialog.show();

    }


    private void startLoginTimer(String status, TextView textView) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        if (CheckConnection.haveNetworkConnection(this)) {
            progressDialog.setMessage("Loading.....");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Map<String, String> driverStatus = new HashMap<>();
            driverStatus.put("is_online", status);

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<UpdateDriverStatus> call =
                    apiService.updateDriverStatus("Bearer " + SessionManager.getKEY(), driverStatus);
            call.enqueue(new Callback<UpdateDriverStatus>() {
                @Override
                public void onResponse(Call<UpdateDriverStatus> call, retrofit2.Response<UpdateDriverStatus> response) {
                    UpdateDriverStatus jsonResponse = response.body();
                    progressDialog.cancel();
                    if (jsonResponse!=null && jsonResponse.getStatus()) {
//                        Toast.makeText(HomeActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                        if (jsonResponse.gettotal_working_hour() != null) {
                            SessionManager.setTotalTime(jsonResponse.gettotal_working_hour());

                        }
                    } else {
                        try {
                            if (jsonResponse!=null && jsonResponse.gettotal_working_hour() != null) {
                                SessionManager.setTotalTime(jsonResponse.gettotal_working_hour());
                                // new LoginTimerCountDown(textView,HomeActivity.this,false);
                            }

                        } catch (Exception ex) {

                        }


                    }

                    seconds = Integer.parseInt(SessionManager.getTotalTime());
                    if (status.equals("1")) {

                        running = true;

                    } else {
                            running = false;
                    }

                }

                @Override
                public void onFailure(Call<UpdateDriverStatus> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                    progressDialog.cancel();

                }
            });
        } else {
            progressDialog.cancel();
            Toast.makeText(HomeActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();

        }
    }


//Timer
    private void runTimer() {


        // Creates a new Handler
        final Handler handler
                = new Handler();

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(new Runnable() {
            @Override

            public void run() {
                 hours = seconds / 3600;
                 minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // Format the seconds into hours, minutes,
                // and seconds.
                String time
                        = String
                        .format(Locale.getDefault(),
                                "%02d:%02d:%02d", hours,
                                minutes, secs);

                // Set the text view text.
                textTimer.setText(time);

                SessionManager.setLastTime(time);

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++;
                }

                if(hours>=12&&hours<=17&&hoursisCompeleted)
                {
                    if(!SessionManager.getIsLogoutStatus())
                    {
                        try {
                            is_online(SessionManager.getUserId(),"3", false);
                        }
                        catch (Exception ex)
                        {

                        }
                    }
                }
                if(hours<10)
                {
                    if(SessionManager.getOfflineAlertStatus())
                    {
                        SessionManager.setOfflineAlertStatus(false);
                    }
                }
                if(hours>=10 && hours<12 &&alertShown)
                {
                    if(alertDialog!=null)
                    {
                        if(!alertDialog.isShowing())
                        {
                            if(!SessionManager.getOfflineAlertStatus())
                                showOfflineAlert();
                        }
                    }
                    else {
                        if(!SessionManager.getOfflineAlertStatus())
                            showOfflineAlert();
                    }
                    alertShown = false;
                }
                Log.e("login_timer",hours+":"+min+":"+sec);

                // Post the code again
                // with a delay of 1 second.
                    handler.postDelayed(this, 1000);

            }
        });




    }


    private void updateAlertDialog() {
        // Initialize AlertDialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        // Set title
        builder.setTitle(getResources().getString(R.string.app_name));
        // set message
        builder.setMessage("Update Available");
        // Set non cancelable
        builder.setCancelable(false);

        // On update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Open play store
                startActivity(new Intent(Intent .ACTION_VIEW,
                        Uri.parse("market://details?id"+getPackageName())));
                // Dismiss alert dialog
                dialogInterface.dismiss();
            }
        });

        // on cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // cancel alert dialog
                dialogInterface.cancel();
            }
        });

        // show alert dialog
        builder.show();
    }

    public void checkAppVersionApi() {

        // final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            // progressDialog.setMessage("Session checking.....");
            //progressDialog.setCancelable(false);
            //progressDialog.show();
            Map<String,String> param = new HashMap();
            param.put("device_type","android");
            param.put("app_version", BuildConfig.VERSION_NAME.toString());
            param.put("user_type", "2");

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<CheckAppVersionResponse> call =
                    apiService.checkAppVersionApi("Bearer " + SessionManager.getKEY(),param);
            call.enqueue(new Callback<CheckAppVersionResponse>() {
                @Override
                public void onResponse(Call<CheckAppVersionResponse> call, retrofit2.Response<CheckAppVersionResponse> response) {
                    CheckAppVersionResponse jsonResponse = response.body();
                    Log.d("response",response.toString());
                    if (jsonResponse != null) {
                        Log.d("TakeResponse",jsonResponse.getMessage().toString());
                        if (jsonResponse.getStatus()) {
                            if (jsonResponse.getMessage().equals("please update App Version")){
                                isUpdateAvailable(true);
                            }else{
                                isUpdateAvailable(false);
                            }
                        } else {
                            //progressDialog.cancel();
                            isUpdateAvailable(false);
                            Toast.makeText(getApplicationContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }

                }

                @Override
                public void onFailure(Call<CheckAppVersionResponse> call, Throwable t) {
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



    // Method to show the update dialog
    private void showUpdateDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("App Update Available");
        builder.setMessage("A new version of the app is available. Please update to enjoy the latest features.");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle update button click
                // Redirect to Google Play Store or start download process
                final String appPackageName = getPackageName(); // package name of the app
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        builder.setNegativeButton("Remind Me Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle "Remind Me Later" button click
                // ..
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to check if an update is available
    private void isUpdateAvailable(boolean status) {
        // Implement the logic to check for updates
        if (status){
            showUpdateDialog();
        }
    }
}


