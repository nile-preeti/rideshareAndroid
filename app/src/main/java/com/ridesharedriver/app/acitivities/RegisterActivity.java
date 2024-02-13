package com.ridesharedriver.app.acitivities;

import static com.ridesharedriver.app.pojo.Global.checkRotationFromCamera;
import static com.ridesharedriver.app.pojo.Global.getImageOrientation;
import static com.ridesharedriver.app.utils.RealPathUtil.getDataColumn;
import static com.ridesharedriver.app.utils.RealPathUtil.isDownloadsDocument;
import static com.ridesharedriver.app.utils.RealPathUtil.isExternalStorageDocument;
import static com.ridesharedriver.app.utils.RealPathUtil.isGooglePhotosUri;
import static com.ridesharedriver.app.utils.RealPathUtil.isMediaDocument;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ridesharedriver.app.custom.CheckConnection;
import com.ridesharedriver.app.custom.camera.CameraRotation;
import com.ridesharedriver.app.pojo.CountryCode;
import com.ridesharedriver.app.pojo.SignupResponse;
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
//import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.ridesharedriver.app.R;
import com.ridesharedriver.app.Server.Server;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.custom.GPSTracker;
import com.ridesharedriver.app.custom.Utils;
import com.ridesharedriver.app.helper.HelperMethods;
import com.ridesharedriver.app.pojo.Register;
import com.ridesharedriver.app.session.SessionManager;
import com.hbb20.CountryCodePicker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ridesharedriver.app.utils.PhoneNumberFormat;
import com.ridesharedriver.app.utils.RealPathUtil;
import com.ridesharedriver.app.utils.SSNFormattingTextWatcher;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import cz.msebera.android.httpclient.Header;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by android on 7/3/17.
 */

//Register Screen
public class RegisterActivity extends ActivityManagePermission implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {

    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};

    private static final String EQUAL_8 = "8";
    private static final String ABOVE_8 = "Above 8";
    private static final String TAG = "Register";
    EditText input_email, input_password, input_confirmPassword, input_mobile, input_name, input_lname, input_num, input_color, input_ssn;
    EditText input_home_address;
    AppCompatButton sign_up;
    LinearLayout signupbtn;
    AppCompatButton uploadDocs;
    String token, brandId = "", brandVal = "", modelId = "", modelVal = "", typeId = "", typeVal = "",
            typeRate = "", yearVal = "", seatNo = "",
            dateOfBirth = "", identityName = "", identityId = "";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    private CheckBox chk_wifi, chk_tv, chk_luxury_seats;
    private RelativeLayout rl_premium_facilities_layout;
    private LinearLayout ll_identification_layout;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView txt_register, rate_txt, insurance_issue_date, insurance_expiry_date,
            license_issue_date, license_expiry_date, car_issue_date, car_expiry_date,
            identity_issue_date, identity_expire_date, identification_txt;
    Spinner brand_spinner, model_spinner, year_spinner, type_spinner, seat_no_spinner, identity_spinner;
    ArrayList<String> brand_names = new ArrayList<>();
    ArrayList<String> brand_id = new ArrayList<>();
    ArrayList<String> model_names = new ArrayList<>();
    ArrayList<String> model_id = new ArrayList<>();
    ArrayList<String> identity_names = new ArrayList<>();
    ArrayList<String> identity_id = new ArrayList<>();
    ArrayList<String> type_names = new ArrayList<>();
    ArrayList<String> type_id = new ArrayList<>();
    ArrayList<String> type_rate = new ArrayList<>();
    ArrayList<String> years = new ArrayList<String>();
    ArrayList<String> seat_nos = new ArrayList<String>();
    private boolean IsDocumentsUploaded;
    private File finalFile;
    private Toolbar toolbar;
    public static final int PICK_IMAGE = 1;
    TextView insurance_txt, txt_dob;
    ImageView insurance_img, dob_img, identification_img, avatar_img;
    TextView permit;
    ImageView permit_img;
    TextView licence;
    ImageView licence_img;
    TextView carpic;
    TextView carRegistration;
    TextView submit;
    ImageView carpic_img;
    ImageView carRegistrationImg;
    InputStream inputStream;
    //File filetoupload;
    private String fileType = "";
    private final static int IMAGE_RESULT = 200;

    String[] permissionsFile = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final String IMAGE_DIRECTORY = "/RideShareDriver";


    private File insurenceFinalFile, licFinalFile, permitFinalFile, carFinalFile, carRegistrationFinalFile, identificationFinalFile, avatarFinalFile;
    private String insuranceIssueDate = "", insuranceExpireDate = "", licenceIssueDate = "", licenceExpireDate = "", carIssueDate = "", carExpireDate = "",
            identityIssueDate = "", identityExpireDate = "";
    private Place homePlace;

    CountryCodePicker codePicker;
    private ActivityResultLauncher<Intent> galleryLauncher;
    File file;
    Uri fileUri, avataarUri, verificationIdUri, carPicUri, insurenceUri, carRegistrationUri,
            permitUri, licenseUri;

    String avatar_path = "", identification_path = "", car_path = "", insurance_path = "", lic_path = "", permit_path = "",
            car_reg_path = "";
    MultipartBody.Part avtarFileToupload, identificationFinalFileToupload, carFinalFileToupload;
    ArrayList<String> title_names = new ArrayList<>(Arrays.asList("Boss", "Mr.", "Ms."));
    Spinner name_title;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Places.initialize(getApplicationContext(), getString(R.string.google_android_map_api_key));

        //here we get FCM Token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();
                        SessionManager.setGcmToken(token);
                    }
                });

        BindView();
        //toApply Fonts
        applyfonts();


        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            fileUri = data.getData();
                            String path = fileUri.getPath();
                            if (!path.isEmpty()) {
                                file = new File(path);
                                switch (fileType) {
                                    case "insurance_file":
                                        insurenceUri = data.getData();
                                        Glide.with(RegisterActivity.this).load(insurenceUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(insurance_img);
                                        insurance_path = insurenceUri.getPath();
                                        break;

                                    case "verification_id":
                                        verificationIdUri = data.getData();
                                        Glide.with(RegisterActivity.this).load(verificationIdUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(identification_img);
                                        identification_path = verificationIdUri.getPath();
                                        break;

                                    case "avatar":
                                        avataarUri = data.getData();
                                        Glide.with(RegisterActivity.this).load(avataarUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(avatar_img);
                                        avatar_path = avataarUri.getPath();
                                        break;
                                    case "permit_file":
                                        permitUri = data.getData();
                                        Glide.with(RegisterActivity.this).load(permitUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(permit_img);
                                        permit_path = permitUri.getPath();
                                        break;
                                    case "licence_file":
                                        licenseUri = data.getData();
                                        Glide.with(RegisterActivity.this).load(licenseUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(licence_img);
                                        lic_path = licenseUri.getPath();
                                        break;
                                    case "carpic_file":
                                        carPicUri = data.getData();
                                        Glide.with(RegisterActivity.this).load(carPicUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(carpic_img);
                                        car_path = carPicUri.getPath();
                                        break;
                                    case "car_registration_file":
                                        carRegistrationUri = data.getData();
                                        Glide.with(RegisterActivity.this).load(carRegistrationUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(carRegistrationImg);
                                        car_reg_path = carRegistrationUri.getPath();
                                        break;
                                }


                            }


                        }


                    }
                });

        avatar_img.setOnClickListener(e -> {
            fileType = "avatar";
            showPictureDialog();
        });

        identification_img.setOnClickListener(e -> {
            fileType = "verification_id";
            showPictureDialog();
        });


        //calender dialog
        Calendar cal = Calendar.getInstance();
        dob_img.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(RegisterActivity.this)
                    .callback(RegisterActivity.this)
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(cal.get(Calendar.YEAR) - 18, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .maxDate(cal.get(Calendar.YEAR) - 18, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .minDate(cal.get(Calendar.YEAR) - 65, 0, 1)
                    .build()
                    .show();
        });

        //locationPicker

        input_home_address.setOnClickListener(e -> {
            launchPlacePicker();
            //Dummy Code
//            input_home_address.setText("Jodhpur,Rajsthan");
        });


//identity issue date dialog picker
        identity_issue_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(RegisterActivity.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            identityIssueDate = format.format(newCalendar.getTime());
                            identity_issue_date.setText(identityIssueDate);
                        }
                    })
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .maxDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .minDate(1900, 0, 1)
                    .build()
                    .show();
        });

        //identity expire date dialog picker
        identity_expire_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(RegisterActivity.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            identityExpireDate = format.format(newCalendar.getTime());
                            identity_expire_date.setText(identityExpireDate);
                        }
                    })
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .maxDate(cal.get(Calendar.YEAR) + 25, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .minDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .build()
                    .show();
        });

        //insurance issue date picker
        insurance_issue_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(RegisterActivity.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            insuranceIssueDate = format.format(newCalendar.getTime());
                            insurance_issue_date.setText(insuranceIssueDate);
                        }
                    })
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .maxDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .minDate(1900, 0, 1)
                    .build()
                    .show();
        });

        //insurance expire date picker
        insurance_expiry_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(RegisterActivity.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            insuranceExpireDate = format.format(newCalendar.getTime());
                            insurance_expiry_date.setText(insuranceExpireDate);
                        }
                    })
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .maxDate(cal.get(Calendar.YEAR) + 5, 11, 31)
                    // .minDate(1900, 0, 1)
                    .minDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .build()
                    .show();
        });

        //license issue date picker
        license_issue_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(RegisterActivity.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            licenceIssueDate = format.format(newCalendar.getTime());
                            license_issue_date.setText(licenceIssueDate);
                        }
                    })
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .maxDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .minDate(1900, 0, 1)
                    .build()
                    .show();
        });

        //licence expire date picker
        license_expiry_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(RegisterActivity.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            licenceExpireDate = format.format(newCalendar.getTime());
                            license_expiry_date.setText(licenceExpireDate);
                        }
                    })
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .maxDate(cal.get(Calendar.YEAR) + 20, 11, 31)
                    .minDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .build()
                    .show();
        });

        //car registration issue date picker
        car_issue_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(RegisterActivity.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            carIssueDate = format.format(newCalendar.getTime());
                            car_issue_date.setText(carIssueDate);
                        }
                    })
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .maxDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .minDate(1900, 0, 1)
                    .build()
                    .show();
        });

        //car registeration expire date
        car_expiry_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(RegisterActivity.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            carExpireDate = format.format(newCalendar.getTime());
                            car_expiry_date.setText(carExpireDate);
                        }
                    })
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .maxDate(cal.get(Calendar.YEAR) + 20, 11, 31)
                    .minDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .build()
                    .show();
        });


        //several clickable parts of multiple texts in a single textview
        SpannableString ss = new SpannableString("Already have an account? Sign In");
        final ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do some thing
                Intent intent1 = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
            }
        };

        ss.setSpan(span1, 25, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_register.setText(ss);
        txt_register.setMovementMethod(LinkMovementMethod.getInstance());
        txt_register.setHighlightColor(Color.TRANSPARENT);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(RegisterActivity.this, UploadDocs.class));

                View view = getCurrentFocus();
                if (view != null) {
                    Utils.hideKeyboard(getApplicationContext(), view);
                }
                if (Utils.haveNetworkConnection(getApplicationContext())) {
                    if (validate()) {
                        String latitude = "";
                        String longitude = "";
                        latitude = String.valueOf(currentLatitude);
                        longitude = String.valueOf(currentLongitude);
                        String city = null, state = null, country = null;
                        String email = input_email.getText().toString().trim();
                        String mobile = input_mobile.getText().toString().trim();
//                        String countryCode = codePicker.getSelectedCountryCode();
                        String countryCode = "+1";
                        String password = input_password.getText().toString().trim();
                        String name = input_name.getText().toString().trim();
                        String lname = input_lname.getText().toString().trim();
                        String ssn = input_ssn.getText().toString().trim();
                        String homeAddress = input_home_address.getText().toString().trim();
                        Geocoder geocoder;

                        try {
                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            if (latitude != null && longitude != null) {
                                if (!latitude.equals("0.0") && !longitude.equals("0.0")) {
                                    try {
                                        List<Address> addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
                                        if (addresses != null && addresses.size() > 0) {
                                            String merged = "";
                                            city = addresses.get(0).getLocality();
                                            country = addresses.get(0).getCountryName();
                                            state = addresses.get(0).getAdminArea();
                                            if (city != null) {
                                                merged = city;
                                            } else {
                                                city = "null";
                                            }
                                            if (state != null) {
                                                merged = city + "," + state;

                                            } else {
                                                state = "null";
                                            }
                                            if (country != null) {
                                                merged = city + "," + state + "," + country;

                                            } else {
                                                country = "null";
                                            }
                                        }
                                    } catch (IOException | IllegalArgumentException e) {

                                        //  e.printStackTrace();
                                        Log.e("data", e.toString());
                                    }
                                } else {
                                    latitude = "0.0";
                                    longitude = "0.0";
                                    city = "null";
                                    state = "null";
                                    country = "null";
                                }
                            } else {
                                latitude = "0.0";
                                longitude = "0.0";
                                city = "null";
                                state = "null";
                                country = "null";
                            }
                        } catch (Exception e) {
                            latitude = "0.0";
                            longitude = "0.0";
                            city = "null";
                            state = "null";
                            country = "null";
                        }
                        if (!Utils.isValidMobile(mobile)) {
                            Toast.makeText(RegisterActivity.this, "Please Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (dateOfBirth.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "Please select date of birth", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (!isValidSSN(ssn)) {
                            Toast.makeText(RegisterActivity.this, "Please enter valid ssn", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (brandId.equalsIgnoreCase("")) {
                            Toast.makeText(RegisterActivity.this, "Please Select Vehicle Make", Toast.LENGTH_LONG).show();
                            return;
                        } else if (modelId.equalsIgnoreCase("")) {
                            Toast.makeText(RegisterActivity.this, "Please Select Vehicle Model", Toast.LENGTH_LONG).show();
                            return;
                        } else if (yearVal.equalsIgnoreCase("")) {
                            Toast.makeText(RegisterActivity.this, "Please Select Vehicle Year", Toast.LENGTH_LONG).show();
                            return;
                        } else if (input_color.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(RegisterActivity.this, "Please Enter Vehicle Color", Toast.LENGTH_LONG).show();
                            return;
                        } else if (seatNo.equalsIgnoreCase("")) {
                            Toast.makeText(RegisterActivity.this, "Please Select Number of Seats", Toast.LENGTH_LONG).show();
                            return;
//                        } else if (typeId.equalsIgnoreCase("")) {
//                            Toast.makeText(RegisterActivity.this, "Please Select Vehicle Type", Toast.LENGTH_LONG).show();
//                            return;
                        } else if (input_num.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(RegisterActivity.this, "Please Enter Car Number Plate", Toast.LENGTH_LONG).show();
                            return;
                        }/*else if (fileType.equalsIgnoreCase("permit_file") && permitFinalFile.getName().isEmpty() || permitFinalFile.getName().equalsIgnoreCase("")) {
                            Toast.makeText(RegisterActivity.this, "Please upload permit image", Toast.LENGTH_LONG).show();
                            return;
                        }*/ else if (avatar_path.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "Please upload profile pic.", Toast.LENGTH_LONG).show();
                            return;
                        } else if (car_path.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "Please upload car image", Toast.LENGTH_LONG).show();
                            return;
                        } else if (identityId.equalsIgnoreCase("")) {
                            Toast.makeText(RegisterActivity.this, "Please Select Identity Document.", Toast.LENGTH_LONG).show();
                            return;
                        } else if (identification_path.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "Please upload Id Proof.", Toast.LENGTH_LONG).show();
                            return;
                        } else if (identityIssueDate.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "Please select identity issue Date", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (identityExpireDate.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "Please select identity expire Date", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        register(email, mobile, countryCode, password, name, lname, latitude, longitude, country, state, city, token, ssn,
                                "2", dateOfBirth, homeAddress);


                    } else {
                        // do nothing
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
                }
            }
        });

        insurance_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileType = "insurance_file";
                showPictureDialog();

               /* Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                fileType = "insurance_file";
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);*/
            }
        });

        permit_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileType = "permit_file";
                showPictureDialog();

               /* Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                fileType = "permit_file";
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);*/
            }
        });

        licence_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileType = "licence_file";
                showPictureDialog();
               /* Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                fileType = "licence_file";
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);*/
            }
        });

        carpic_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileType = "carpic_file";
                showPictureDialog();
               /* Intent intent = new Intent();`````````````````````
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                fileType = "carpic_file";
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);*/
            }
        });

        carRegistrationImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileType = "car_registration_file";
                showPictureDialog();
            }
        });

        getBrand_details();
        getIdentity_details();
        getSeat_nos();
//        getYear_details();
        initializeModels();
        initializeYears();
        // getType_details();
    }

    public void runtimeEnableAutoInit() {
        // [START fcm_runtime_enable_auto_init]
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        // [END fcm_runtime_enable_auto_init]
    }

    //image picker dialog
    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
//                                choosePhotoFromGallary();
                                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                                i.setType("image/*");
                                i.addCategory(Intent.CATEGORY_OPENABLE);
                                galleryLauncher.launch(i);
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    //clicking images from camera
    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(getExternalCacheDir(),
                String.valueOf(System.currentTimeMillis()) + ".jpg");
        fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, IMAGE_RESULT);
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();

        return res;
    }


    //permission check
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissionsFile) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }


    //Indentity Details Api
    public void getIdentity_details() {
        RequestParams params = new RequestParams();
        Server.get("get_documentidentity_get", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    identity_names.clear();
                    identity_id.clear();
                    if (response.has("status") && response.getBoolean("status")) {
                        identity_names.add("Select Identity");

                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            identity_names.add(jsonObject.getString("document_name"));
                            identity_id.add(jsonObject.getString("id"));
                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, identity_names);
                        identity_spinner.setAdapter(dataAdapter);

                        identity_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                                if (!identity_spinner.getItemAtPosition(position).equals("Select Identity")) {
                                    identityId = (identity_id.get(position - 1));
                                    identityName = adapterView.getItemAtPosition(position).toString();
                                    ll_identification_layout.setVisibility(View.VISIBLE);
                                    resetIdentity(identityName);
                                } else {
                                    identityId = "";
                                    identityName = "";
                                    ll_identification_layout.setVisibility(View.GONE);
                                    identification_txt.setText(getString(R.string.upload_identity_document));
                                }

                                Log.d("onItemSelected \n", "brandId : " + brandId);
                                Log.d("onItemSelected \n", "brandVal : " + brandVal);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                // DO Nothing here
                            }
                        });
                    } else {
                        identity_names.clear();
                        identity_id.clear();
                        identity_names.add("Select Identity");

                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, identity_names);
                        identity_spinner.setAdapter(dataAdapter1);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(RegisterActivity.this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    //get Brand Details
    public void getBrand_details() {
        RequestParams params = new RequestParams();
        Server.get("getCategory", params, new JsonHttpResponseHandler() {
            @Override

            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    brand_names.clear();
                    brand_id.clear();
                    if (response.has("status") && response.getBoolean("status")) {
                        brand_names.add("Select Vehicle Make");

                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            brand_names.add(jsonObject.getString("title"));
                            brand_id.add(jsonObject.getString("id"));
                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, brand_names);
                        brand_spinner.setAdapter(dataAdapter);

                        brand_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                                if (!brand_spinner.getItemAtPosition(position).equals("Select Vehicle Make")) {
                                    brandId = (brand_id.get(position - 1));
                                    brandVal = adapterView.getItemAtPosition(position).toString();

                                    getModel_details(brandId);
                                    getYear_Details(brandId);
                                } else {
                                    brandId = "";
                                    brandVal = "";
                                }

                                Log.d("onItemSelected \n", "brandId : " + brandId);
                                Log.d("onItemSelected \n", "brandVal : " + brandVal);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                // DO Nothing here
                            }
                        });
                    } else {
                        brand_names.clear();
                        brand_id.clear();
                        brand_names.add("Select Vehicle Make");

                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, brand_names);
                        brand_spinner.setAdapter(dataAdapter1);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(RegisterActivity.this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //here me set model into the adapter
    public void initializeModels() {
        model_names.clear();
        model_id.clear();
        model_names.add("Select Vehicle Model");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, model_names);
        model_spinner.setAdapter(dataAdapter);

        model_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (!model_spinner.getItemAtPosition(position).equals("Select Vehicle Model")) {
                    modelVal = adapterView.getItemAtPosition(position).toString();
                } else {
                    modelId = "";
                    modelVal = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
    }

    //here me set model into the adapter
    public void initializeYears() {
        years.clear();
        years.add("Select Vehicle Year");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, years);
        year_spinner.setAdapter(dataAdapter);

        year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (!year_spinner.getItemAtPosition(position).equals("Select Vehicle Year")) {
                    yearVal = adapterView.getItemAtPosition(position).toString();
                } else {
                    yearVal = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
    }

    //here we get models
    public void getModel_details(String brandId) {
        RequestParams params = new RequestParams();
        params.put("category_id", brandId);
        Server.post("getSubcategory", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    model_names.clear();
                    model_id.clear();
                    seat_nos.clear();
                    if (response.has("status") && response.getBoolean("status")) {
                        model_names.add("Select Vehicle Model");

                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            model_names.add(jsonObject.getString("model_name"));
                            model_id.add(jsonObject.getString("id"));
                            seat_nos.add(jsonObject.getString("seat"));
                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, model_names);
                        model_spinner.setAdapter(dataAdapter);

                        model_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                                if (!model_spinner.getItemAtPosition(position).equals("Select Vehicle Model")) {
                                    modelId = (model_id.get(position - 1));
                                    modelVal = adapterView.getItemAtPosition(position).toString();

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, seat_nos);
                                    seat_no_spinner.setAdapter(adapter);
                                    seat_no_spinner.setSelection(position - 1);
                                    seat_no_spinner.setEnabled(false);
                                } else {
                                    modelId = "";
                                    modelVal = "";
                                }

                                Log.d("onItemSelected \n", "modelId : " + modelId);
                                Log.d("onItemSelected \n", "modelVal : " + modelVal);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                // DO Nothing here
                            }
                        });
                    } else {
                        model_names.clear();
                        model_id.clear();
                        model_names.add("Select Vehicle Model");

                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, model_names);
                        model_spinner.setAdapter(dataAdapter1);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(RegisterActivity.this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //here we get year
    public void getYear_Details(String categoryId) {
        RequestParams params = new RequestParams();
        params.put("category_id", categoryId);
        Server.post("get-vehicle-year", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    years.clear();
                    if (response.has("status") && response.getBoolean("status")) {
                        years.add("Select Vehicle Year");

                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            years.add(jsonObject.getString("category_year"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, years);
                        year_spinner.setAdapter(adapter);


                        year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                                if (!year_spinner.getItemAtPosition(position).equals("Select Vehicle Year")) {
                                    yearVal = adapterView.getItemAtPosition(position).toString();
                                } else {
                                    yearVal = "";
                                }

                                Log.d("onItemSelected \n", "yearVal : " + yearVal);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                // DO Nothing here
                            }
                        });

                    } else {
                        years.clear();
                        years.add("Select Vehicle Year");

                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, years);
                        year_spinner.setAdapter(dataAdapter1);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(RegisterActivity.this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //here we get years of vechical
    public void getYear_details() {
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add("Select Vehicle Year");
        for (int i = thisYear; i > thisYear - 10; i--) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, years);
        year_spinner.setAdapter(adapter);

        year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (!year_spinner.getItemAtPosition(position).equals("Select Vehicle Year")) {
                    yearVal = adapterView.getItemAtPosition(position).toString();
                } else {
                    yearVal = "";
                }

                Log.d("onItemSelected \n", "yearVal : " + yearVal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
    }


    //here we get sheats for cars
    public void getSeat_nos() {

        seat_nos.add("Select Number Of Seats");
        seat_nos.add("1");
        seat_nos.add("2");
        seat_nos.add("3");
        seat_nos.add("4");
        seat_nos.add("5");
        seat_nos.add("6");
        seat_nos.add("7");
        seat_nos.add("8");
        seat_nos.add("Above 8");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, seat_nos);
        seat_no_spinner.setAdapter(adapter);

        seat_no_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (!seat_no_spinner.getItemAtPosition(position).equals("Select Number Of Seats")) {
                    seatNo = adapterView.getItemAtPosition(position).toString();
                    if (seatNo.equalsIgnoreCase(EQUAL_8)) {
                        rl_premium_facilities_layout.setVisibility(View.VISIBLE);
                    } else {
                        rl_premium_facilities_layout.setVisibility(View.GONE);
                    }
                } else {
                    seatNo = "";
                }

                Log.d("onItemSelected \n", "yearVal : " + yearVal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
    }

    public void getType_details() {
        RequestParams params = new RequestParams();
        Server.post("getVehicleType", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    type_names.clear();
                    type_id.clear();
                    type_rate.clear();
                    if (response.has("status") && response.getBoolean("status")) {
                        type_names.add("Select Vehicle Type");

                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            type_names.add(jsonObject.getString("title"));
                            type_id.add(jsonObject.getString("id"));
                            type_rate.add(jsonObject.getString("rate"));
                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, type_names);
                        type_spinner.setAdapter(dataAdapter);

                        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                                if (!type_spinner.getItemAtPosition(position).equals("Select Vehicle Type")) {
                                    typeId = (type_id.get(position - 1));
                                    typeVal = adapterView.getItemAtPosition(position).toString();
                                    typeRate = type_rate.get(position - 1);
                                    rate_txt.setText(typeRate);
                                } else {
                                    typeId = "";
                                    typeVal = "";
                                    rate_txt.setText("");
                                }

                                Log.d("onItemSelected \n", "typeId : " + typeId);
                                Log.d("onItemSelected \n", "typeVal : " + typeVal);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                // DO Nothing here
                            }
                        });
                    } else {
                        type_names.clear();
                        type_id.clear();
                        type_names.add("Select Vehicle Type");

                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, type_names);
                        type_spinner.setAdapter(dataAdapter1);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(RegisterActivity.this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //here we validate SSN
    public boolean isValidSSN(String ssn) {
        // Regex to check SSN
        // (Social Security Number).
        String regex = "^(?!666|000|9\\d{2})\\d{3}"
                + "-(?!00)\\d{2}-"
                + "(?!0{4})\\d{4}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the string is empty
        // return false
        if (ssn == null) {
            return false;
        }

        // Pattern class contains matcher()
        //  method to find matching between
        //  given string and regular expression.
        Matcher m = p.matcher(ssn);

        // Return if the string
        // matched the ReGex
        return m.matches();
    }

    //validation for user input
    public Boolean validate() {
        Boolean value = true;

        if (input_name.getText().toString().trim().equals("")) {
            input_name.setError(getString(R.string.fname_is_required));
            value = false;
        } else {
            input_name.setError(null);
        }

        if (input_lname.getText().toString().trim().equals("")) {
            input_lname.setError(getString(R.string.lname_is_required));
            value = false;
        } else {
            input_lname.setError(null);
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input_email.getText().toString().trim()).matches()) {
            input_email.setError(getString(R.string.email_invalid));
            value = false;
        } else {
            input_email.setError(null);
        }
        if (input_mobile.getText().toString().trim().equals("")) {
            input_mobile.setError(getString(R.string.mobile_invalid));
            value = false;
        } else {
            input_mobile.setError(null);
        }
        if (input_home_address.getText().toString().trim().equals("")) {
            input_home_address.setError("Please Enter Home Address");
            value = false;
        } else {
            input_home_address.setError(null);
        }
        if (input_ssn.getText().toString().trim().equals("")) {
            input_ssn.setError(getString(R.string.ssn_invalid));
            value = false;
        } else {
            input_ssn.setError(null);
        }
        if (!(input_password.length() >= 6)) {
            value = false;
            input_password.setError(getString(R.string.password_length));
        } else {
            input_password.setError(null);
        }
        if (!input_password.getText().toString().trim().equals("") && (!input_confirmPassword.getText().toString().trim().equals(input_password.getText().toString().trim()))) {
            value = false;
            input_confirmPassword.setError(getString(R.string.password_nomatch));
        } else {
            input_confirmPassword.setError(null);
        }

        return value;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        IsDocumentsUploaded = prefs.getBoolean("IsDocumentsUploaded", false);

        if (IsDocumentsUploaded) {
            //signupbtn.setVisibility(View.VISIBLE);
        } else {
            //signupbtn.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void BindView() {
//        countryCodeSpinner = findViewById(R.id.countryCodeSpinner);
//        spinerLayout = findViewById(R.id.spinerLayout);
//        txtCountryCode = findViewById(R.id.txtCountryCode);
        //  filetoupload = new File("");
        codePicker = findViewById(R.id.country_code);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sign Up");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //premium facilities starts
        chk_wifi = findViewById(R.id.chk_wifi);
        chk_luxury_seats = findViewById(R.id.chk_luxury_seats);
        chk_tv = findViewById(R.id.chk_tv);
        rl_premium_facilities_layout = findViewById(R.id.premium_facilities_layout);

        //premium facilities ends

        //identity
        ll_identification_layout = findViewById(R.id.ll_identification_layout);
        ll_identification_layout.setVisibility(View.GONE);
        identity_spinner = findViewById(R.id.identity_doc_spinner);
        identity_issue_date = findViewById(R.id.identification_issue_date);
        identity_expire_date = findViewById(R.id.identification_expire_date);
        identification_txt = findViewById(R.id.identification_txt);
        //identity

        insurenceFinalFile = new File("");
        licFinalFile = new File("");
        carFinalFile = new File("");
        permitFinalFile = new File("");
        carRegistrationFinalFile = new File("");
        identificationFinalFile = new File("");
        avatarFinalFile = new File("");
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        input_email = findViewById(R.id.input_email);
        input_name = findViewById(R.id.input_name);
        name_title = findViewById(R.id.name_title);
        input_lname = findViewById(R.id.input_lname);
        input_password = findViewById(R.id.input_password);
        input_confirmPassword = findViewById(R.id.input_confirmPassword);
        input_mobile = findViewById(R.id.input_mobile);
        input_ssn = findViewById(R.id.input_ssn);
        input_home_address = findViewById(R.id.input_home_address);
        input_ssn.addTextChangedListener(new SSNFormattingTextWatcher(input_ssn));
        input_mobile.addTextChangedListener(new PhoneNumberFormat(input_mobile));
        input_num = findViewById(R.id.input_num);
        input_color = findViewById(R.id.input_color);
        //uploadDocs = findViewById(R.id.upload_docs);
        sign_up = findViewById(R.id.sign_up);
        txt_register = findViewById(R.id.txt_register);
        brand_spinner = findViewById(R.id.brand_spinner);
        model_spinner = findViewById(R.id.model_spinner);
        year_spinner = findViewById(R.id.year_spinner);
        type_spinner = findViewById(R.id.type_spinner);
        seat_no_spinner = findViewById(R.id.seat_no_spinner);
        rate_txt = findViewById(R.id.rate_txt);
        signupbtn = findViewById(R.id.signupbtn);
        dob_img = findViewById(R.id.date_img);
        txt_dob = findViewById(R.id.txt_dob);
        avatar_img = findViewById(R.id.avatar_img);
        identification_img = findViewById(R.id.identification_img);
        insurance_issue_date = findViewById(R.id.insurance_issue_date);
        insurance_expiry_date = findViewById(R.id.insurance_expire_date);
        license_issue_date = findViewById(R.id.licence_issue_date);
        license_expiry_date = findViewById(R.id.licence_expire_date);
        car_issue_date = findViewById(R.id.car_registration_issue_date);
        car_expiry_date = findViewById(R.id.car_registration_expire_date);


        insurance_txt = findViewById(R.id.insurance_txt);
        insurance_img = findViewById(R.id.insurance_img);

        permit = findViewById(R.id.permit_txt);
        permit_img = findViewById(R.id.permit_img);

        licence = findViewById(R.id.licence_txt);
        licence_img = findViewById(R.id.licence_img);

        carpic = findViewById(R.id.carpic_txt);
        submit = findViewById(R.id.submit_txt);
        carpic_img = findViewById(R.id.car_img);

        carRegistration = findViewById(R.id.car_registration_txt);
        carRegistrationImg = findViewById(R.id.car_registration);

        /*uploadDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, UploadDocs.class);
                startActivity(intent);
            }
        });*/

        AskPermission();
        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));

        ArrayAdapter<String> titleNameAdapter = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, title_names);
        name_title.setAdapter(titleNameAdapter);
    }

    //here we  enable GPS
    public Boolean GPSEnable() {
        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
        if (gpsTracker.canGetLocation()) {
            return true;

        } else {
            return false;
        }

    }

    //for permission
    public void AskPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getCurrentlOcation();
            return;
        }
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
//                openSettingsApp(getApplicationContext());
            }
        });
    }

    //Apply fonts for the texts
    public void applyfonts() {
        TextView textView = (TextView) findViewById(R.id.txt_register);
        Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Medium.otf");
        Typeface font1 = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
        Typeface font2 = Typeface.createFromAsset(getAssets(), "font/montserrat_regular.ttf");
        Typeface font3 = Typeface.createFromAsset(getAssets(), "font/montserrat_bold.ttf");
        textView.setTypeface(font2);
        input_name.setTypeface(font2);
        input_lname.setTypeface(font2);
        input_num.setTypeface(font2);
        input_color.setTypeface(font2);
        input_email.setTypeface(font2);
        input_password.setTypeface(font2);
        input_confirmPassword.setTypeface(font2);
        input_mobile.setTypeface(font2);
        input_ssn.setTypeface(font2);
//        sign_up.setTypeface(font3);
        //uploadDocs.setTypeface(font);
        txt_register.setTypeface(font2);
        rate_txt.setTypeface(font2);
    }

    //Register API
    public void register(String email, String mobile, String countryCode, String password, String name, String lname, String
            latitude, String longitude,
                         String country, String state, String city, String gcm_token, String ssn, String utype, String dob, String homeAddress) {

        String premiumFacilities = "";
        if (seatNo.equals(ABOVE_8)) {
            seatNo = "9";
        } else if (seatNo.equalsIgnoreCase(EQUAL_8)) {
            if (chk_tv.isChecked() || chk_wifi.isChecked() || chk_luxury_seats.isChecked()) {
                seatNo = "9";
                if (chk_wifi.isChecked())
                    premiumFacilities += "Wi-Fi,";
                if (chk_tv.isChecked())
                    premiumFacilities += " T.V.,";
                if (chk_luxury_seats.isChecked())
                    premiumFacilities += " Luxury Seats";

            } else {
                seatNo = "8";
            }
        }

        Map<String, RequestBody> params = new HashMap();
        params.put("email", createRequestBody(email));
        params.put("mobile", createRequestBody(mobile));
        params.put("country_code", createRequestBody(countryCode));
        params.put("password", createRequestBody(password));
        params.put("name_title", createRequestBody(name_title.getSelectedItem().toString()));
        params.put("name", createRequestBody(name));
        params.put("last_name", createRequestBody(lname));
        params.put("latitude", createRequestBody(latitude));
        params.put("longitude", createRequestBody(longitude));
        params.put("country", createRequestBody(country));
        params.put("state", createRequestBody(state));
        params.put("city", createRequestBody(city));
        params.put("utype", createRequestBody(utype));
        params.put("gcm_token", createRequestBody(gcm_token));
        params.put("year", createRequestBody(yearVal));
        params.put("vehicle_no", createRequestBody(input_num.getText().toString()));
        params.put("brand", createRequestBody(brandId));
        params.put("model", createRequestBody(modelId));
        params.put("color", createRequestBody(input_color.getText().toString()));
        params.put("vehicle_type", createRequestBody(typeId));
        params.put("seat_no", createRequestBody(seatNo));
        params.put("ssn", createRequestBody(ssn));
        params.put("dob", createRequestBody(dob));
        params.put("home_address", createRequestBody(homeAddress));
        params.put("premium_facility", createRequestBody(premiumFacilities));
        params.put("identification_document_id", createRequestBody(identityId));
        params.put("identification_issue_date", createRequestBody(identityIssueDate));
        params.put("identification_expiry_date", createRequestBody(identityExpireDate));

        if (!avatar_path.isEmpty()) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(RegisterActivity.this.getContentResolver(), avataarUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final int rotation = getImageOrientation(avatar_path);
            bitmap = checkRotationFromCamera(bitmap, rotation);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imageBytes = outputStream.toByteArray();


            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            avtarFileToupload = MultipartBody.Part.createFormData("avatar", "avatar.jpg", requestBody);
        }
        if (!identification_path.isEmpty()) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(RegisterActivity.this.getContentResolver(), verificationIdUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final int rotation = getImageOrientation(identification_path);
            bitmap = checkRotationFromCamera(bitmap, rotation);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imageBytes = outputStream.toByteArray();


            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            identificationFinalFileToupload = MultipartBody.Part.createFormData("verification_id", "document.jpg", requestBody);
        }
        if (!car_path.isEmpty()) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(RegisterActivity.this.getContentResolver(), carPicUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final int rotation = getImageOrientation(car_path);
            bitmap = checkRotationFromCamera(bitmap, rotation);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            carFinalFileToupload = MultipartBody.Part.createFormData("car_pic", "car_image.jpg", requestBody);
        }

//        MultipartBody.Part avtarFileToupload = MultipartBody.Part.createFormData("avatar", avatarFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), avatarFinalFile));
//        MultipartBody.Part identificationFinalFileToupload = MultipartBody.Part.createFormData("verification_id", identificationFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), identificationFinalFile));
//        MultipartBody.Part carFinalFileToupload = MultipartBody.Part.createFormData("car_pic", carFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), carFinalFile));

        Log.e("SIGNUP_PARAMS", params.toString());

        final ProgressDialog progressDialog = new ProgressDialog(this);
        if (CheckConnection.haveNetworkConnection(this)) {
            progressDialog.setMessage("Registering.....");
            progressDialog.setCancelable(false);
            progressDialog.show();

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<SignupResponse> call =
                    apiService.signin(params, carFinalFileToupload, avtarFileToupload, identificationFinalFileToupload);
            call.enqueue(new Callback<SignupResponse>() {
                @Override
                public void onResponse(Call<SignupResponse> call, retrofit2.Response<SignupResponse> response) {
                    SignupResponse jsonResponse = response.body();
                    if (jsonResponse != null) {
                        if (jsonResponse.isStatus()) {
                            progressDialog.cancel();
                            Log.d("REGISTER", jsonResponse.getMessage().toString());
                            if (jsonResponse.isStatus()) {
                                Log.d(TAG, response.toString());
                                Toast.makeText(RegisterActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                                //startActivity(new Intent(RegisterActivity.this, UploadDocs.class));
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                prefs.edit().putBoolean("IsDocumentsUp..loaded", false).apply();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                                resetSeats();
                            }

                        } else {
                            progressDialog.cancel();
                            Log.d("REGISTER_ERROR", jsonResponse.getMessage().toString());
                            Toast.makeText(RegisterActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();


                        }
                    }
                }

                @Override
                public void onFailure(Call<SignupResponse> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                    progressDialog.cancel();
                }
            });
        } else {
            progressDialog.cancel();
            Toast.makeText(this, getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }


        //  ApiClient.getApiService().signin(params,carFinalFileToupload,avtarFileToupload,identificationFinalFileToupload);


//        Server.post("register", params, new JsonHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
//                try {
//
//                    if (response.has("status") && response.getBoolean("status")) {
//                        Log.d(TAG, response.toString());
//                        Log.e("SIGNUP_RESPONSE",response.toString());
//                        loading.cancel();
//                        Toast.makeText(RegisterActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
//                        //startActivity(new Intent(RegisterActivity.this, UploadDocs.class));
//                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                        prefs.edit().putBoolean("IsDocumentsUp..loaded", false).apply();
//                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//                        finish();
//                    } else {
//                        loading.cancel();
//                        Toast.makeText(RegisterActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
//                        resetSeats();
//                    }
//                } catch (JSONException e) {
//                    loading.cancel();
//                    resetSeats();
//                    Toast.makeText(RegisterActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
//            }
//
//            @Override
//            public void onFinish() {
//                super.onFinish();
//                loading.cancel();
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
    }

    public RequestBody createRequestBody(String data) {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody requestBody = RequestBody.create(mediaType, data);
        return requestBody;
    }


    private void resetIdentity(String identityName) {

        identification_txt.setText(String.format("Upload %s", identityName));
        identity_expire_date.setText("Expire Date");
        identity_issue_date.setText("Issue Date");
        identificationFinalFile = new File("");
        identification_img.setImageDrawable(getDrawable(R.drawable.ic_upload_pic));
        identityIssueDate = "";
        identityExpireDate = "";

    }

    //reset seats
    private void resetSeats() {
        seat_no_spinner.setSelection(0);
        seatNo = "";
        rl_premium_facilities_layout.setVisibility(View.GONE);
        chk_wifi.setChecked(false);
        chk_tv.setChecked(false);
        chk_luxury_seats.setChecked(false);
    }

    //documents Upload
    private void documentsUpload(String email, String mobile, String password, String
            name, String latitude, String longitude,
                                 String country, String state, String city, String gcm_token, String utype, File
                                         finalFile) {
        HelperMethods.showProgressDialog(this);
        ApiNetworkCall apiService = ApiClient.getApiService();

        RequestBody request_email = RequestBody.create(
                MediaType.parse("text/plain"),
                email);
        RequestBody request_mobile = RequestBody.create(
                MediaType.parse("text/plain"),
                mobile);
        RequestBody request_password = RequestBody.create(
                MediaType.parse("text/plain"),
                password);
        RequestBody request_name = RequestBody.create(
                MediaType.parse("text/plain"),
                name);
        RequestBody request_latitude = RequestBody.create(
                MediaType.parse("text/plain"),
                latitude);
        RequestBody request_longitude = RequestBody.create(
                MediaType.parse("text/plain"),
                longitude);
        RequestBody request_country = RequestBody.create(
                MediaType.parse("text/plain"),
                country);
        RequestBody request_state = RequestBody.create(
                MediaType.parse("text/plain"),
                state);
        RequestBody request_city = RequestBody.create(
                MediaType.parse("text/plain"),
                city);
        RequestBody request_utype = RequestBody.create(
                MediaType.parse("text/plain"),
                utype);
        RequestBody request_gcmtoken = RequestBody.create(
                MediaType.parse("text/plain"),
                gcm_token);
        RequestBody request_year = RequestBody.create(
                MediaType.parse("text/plain"),
                yearVal);
        RequestBody request_vehicleno = RequestBody.create(
                MediaType.parse("text/plain"),
                input_num.getText().toString());
        RequestBody request_brand = RequestBody.create(
                MediaType.parse("text/plain"),
                brandId);
        RequestBody request_modelid = RequestBody.create(
                MediaType.parse("text/plain"),
                modelId);
        RequestBody request_inputcolor = RequestBody.create(
                MediaType.parse("text/plain"),
                input_color.getText().toString());
        RequestBody request_vehicletype = RequestBody.create(
                MediaType.parse("text/plain"),
                typeId);
        RequestBody request_rate = RequestBody.create(
                MediaType.parse("text/plain"),
                typeRate);

        MultipartBody.Part insurance = MultipartBody.Part.createFormData("insurance", finalFile.getName(), RequestBody.create(MediaType.parse("image/*"), finalFile));
        MultipartBody.Part permit = MultipartBody.Part.createFormData("permit", finalFile.getName(), RequestBody.create(MediaType.parse("image/*"), finalFile));
        MultipartBody.Part carspic = MultipartBody.Part.createFormData("car_pic", finalFile.getName(), RequestBody.create(MediaType.parse("image/*"), finalFile));
        MultipartBody.Part licence = MultipartBody.Part.createFormData("license", finalFile.getName(), RequestBody.create(MediaType.parse("image/*"), finalFile));

        Call<Register> call = apiService.upload(request_email, request_mobile, request_password, request_name, request_latitude, request_longitude,
                request_country, request_state, request_city, request_utype, request_gcmtoken, request_year, request_vehicleno, request_brand, request_modelid, request_inputcolor,
                request_vehicletype, request_rate, insurance, permit, carspic, licence);
        call.enqueue(new Callback<Register>() {
            @Override
            public void onResponse(Call<Register> call, Response<Register> response) {
                Register requestResponse = response.body();
                HelperMethods.hideProgressDialog(RegisterActivity.this);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putBoolean("IsDocumentsUploaded", true).apply();
                Toast.makeText(RegisterActivity.this, requestResponse.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<Register> call, Throwable t) {
                //progressBar.setVisibility(View.GONE);
                HelperMethods.hideProgressDialog(RegisterActivity.this);
                Log.d("Failed", "RetrofitFailed");
            }
        });
    }

    //for current location of thr driver
    public void getCurrentlOcation() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        mGoogleApiClient.connect();
    }

    //turning on GPS
    public void tunonGps() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30 * 1000);
            mLocationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
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
                                status.startResolutionForResult(RegisterActivity.this, 1000);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude

            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            //Toast.makeText(getActivity(), currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_RESULT && resultCode == RESULT_OK) {

            switch (fileType) {
                case "insurance_file":
                    insurenceUri = fileUri;
                    Glide.with(this).load(insurenceUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(insurance_img);
                    insurance_path = insurenceUri.getPath();
                    break;

                case "verification_id":
                    verificationIdUri = fileUri;
                    Glide.with(this).load(verificationIdUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(identification_img);
                    identification_path = verificationIdUri.getPath();
                    break;

                case "avatar":
                    avataarUri = fileUri;
                    Glide.with(this).load(avataarUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(avatar_img);
                    avatar_path = avataarUri.getPath();
                    break;
                case "permit_file":
                    permitUri = fileUri;
                    Glide.with(this).load(permitUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(permit_img);
                    permit_path = permitUri.getPath();
                    break;
                case "licence_file":
                    licenseUri = fileUri;
                    Glide.with(this).load(licenseUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(licence_img);
                    lic_path = licenseUri.getPath();
                    break;
                case "carpic_file":
                    carPicUri = fileUri;
                    Glide.with(this).load(carPicUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(carpic_img);
                    car_path = carPicUri.getPath();
                    break;

                case "car_registration_file":
                    carRegistrationUri = fileUri;
                    Glide.with(this).load(carRegistrationUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(carRegistrationImg);
                    car_reg_path = carRegistrationUri.getPath();
                    break;
            }

        }
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                getCurrentlOcation();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        //from gallery
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }


            try {
                inputStream = this.getContentResolver().openInputStream(data.getData());
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);

                //Convert bitmap to byte array
                Bitmap bitmap = bmp;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                switch (fileType) {
                    case "insurance_file":
                        Glide.with(this).asBitmap().load(bmp).into(insurance_img);
                        //String insurance_path = getRealPathFromURI(getImageUri(this, bmp));
                        String insurance_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        insurenceFinalFile = new File(insurance_path);
                        break;

                    case "verification_id":
                        Glide.with(this).asBitmap().load(bmp).into(identification_img);
                        String identification_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        identificationFinalFile = new File(identification_path);
                        break;

                    case "avatar":
                        Glide.with(this).asBitmap().load(bmp).into(avatar_img);
                        String avatar_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        avatarFinalFile = new File(avatar_path);
                        break;
                    case "permit_file":
                        Glide.with(this).asBitmap().load(bmp).into(permit_img);
                        //String permit_path = getRealPathFromURI(getImageUri(this, bmp));
                        String permit_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        permitFinalFile = new File(permit_path);
                        break;
                    case "licence_file":
                        Glide.with(this).asBitmap().load(bmp).into(licence_img);
                        // String lic_path = getRealPathFromURI(getImageUri(this, bmp));
                        String lic_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        licFinalFile = new File(lic_path);
                        break;
                    case "carpic_file":
                        Glide.with(this).asBitmap().load(bmp).into(carpic_img);
//                        String car_path = getRealPathFromURI(getImageUri(this, bmp));
                        car_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        carFinalFile = new File(car_path);
                        break;
                    case "car_registration_file":
                        Glide.with(this).asBitmap().load(bmp).into(carRegistrationImg);
                        String car_reg_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        carRegistrationFinalFile = new File(car_reg_path);
                        break;
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }

        if (requestCode == 1234) {
            if (resultCode == RESULT_OK) {
                homePlace = Autocomplete.getPlaceFromIntent(data);
                input_home_address.setText(homePlace.getAddress());
            }
        }
    }


    public String getRealPathFromURI1(Context context, Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    if (split.length > 1) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                        return Environment.getExternalStorageDirectory() + "/";
                    }
                } else {
                    return "/storage/" + docId.replace(":", "/");
                }
            } else if (isDownloadsDocument(uri)) {
                String fileName = getFilePath(context, uri);
                if (fileName != null) {
                    return Environment.getExternalStorageDirectory() + "/Download/" + fileName;
                }
                String id = DocumentsContract.getDocumentId(uri);
                if (id.startsWith("raw:")) {
                    id = id.replaceFirst("raw:", "");
                    File file = new File(id);
                    if (file.exists()) {
                        return id;
                    }
                }
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                Uri contentUri = null;
                switch (type) {
                    case "image":
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "video":
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "audio":
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        break;
                }

                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            } else {
                return getDataColumn(context, uri, null, null);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public String getFilePath(Context context, Uri uri) {
        Cursor cursor = null;
        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
        try {
            if (uri == null) return null;
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (!wallpaperDirectory.exists()) {  // have the object build the directory structure, if needed.
            wallpaperDirectory.mkdirs();
        }
        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());
            finalFile = new File(f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(
                inContext.getContentResolver(),
                inImage,
                "Title",
                null
        );
        return Uri.parse(path);
    }

    public String convertToBase64(String imageUrl) {
        String encodedImageUrl = "";
        try {
            Bitmap bm = BitmapFactory.decodeFile(imageUrl);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
            byte[] b = baos.toByteArray();
            encodedImageUrl = Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception ex) {
            Log.e("BASE64", ex.getMessage());
        }
        return encodedImageUrl;
    }


    public File compressImage(String imageUrl) {
        int compressionRatio = 2; //1 == originalImage, 2 = 50% compression, 4=25% compress
        File file = new File(imageUrl);
        try {
            BitmapFactory.Options Options = new BitmapFactory.Options();
            Options.inSampleSize = 4;
            Options.inJustDecodeBounds = false;
            // Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), Options);
            Bitmap bitmap = CameraRotation.handleSamplingAndRotationBitmap(this, Uri.parse(imageUrl));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, new FileOutputStream(file));
            return file;
        } catch (Throwable t) {
            Log.e("ERROR", "Error compressing file." + t.toString());
            t.printStackTrace();
            return file;
        }
    }

    public File compressImage1(String imageUrl) {
        try {
            File file = new File(imageUrl);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            // Decode the image bounds without loading the full image into memory
            BitmapFactory.decodeFile(file.getPath(), options);

            // Calculate the inSampleSize value to scale the image
            options.inSampleSize = calculateInSampleSize(options, 800, 600);

            // Decode the image with the calculated inSampleSize value
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);

            // Compress the bitmap to reduce file size
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int quality = 90; // Initial compression quality
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            while (outputStream.toByteArray().length > 1024 * 1024) {
                outputStream.reset();
                quality -= 10; // Decrease compression quality by 10%
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            }

            // Write the compressed bitmap to a file
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(outputStream.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();

            // Recycle the bitmap to free up memory
            bitmap.recycle();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return new File(imageUrl); // Return the original file if any exception occurs
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        SimpleDateFormat format = new SimpleDateFormat("MMM-dd-yyyy", Locale.ENGLISH);
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
        dateOfBirth = format.format(newCalendar.getTime());
        txt_dob.setText(dateOfBirth);

    }

    private void launchPlacePicker() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
//        AutocompleteSupportFragment.newInstance().getView().setBackgroundColor(R.drawable.form_outline_background);
        startActivityForResult(intent, 1234);
    }

    private List<CountryCode> loadCountryCodeData() {
        List<CountryCode> countryCodeList = new ArrayList<>();

        try {
            // Get the input stream for the raw resource
            Resources resources = getResources();
            InputStream inputStream = resources.openRawResource(R.raw.country_codes);

            // Read the input stream line by line
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            // Close the reader and input stream
            reader.close();
            inputStream.close();

            // Convert the JSON string to a list of CountryCode objects
            String json = stringBuilder.toString();
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String name = jsonObject.getString("name");
                String code = jsonObject.getString("code");
                String phoneCode = jsonObject.getString("phone_code");
                String flag = jsonObject.getString("flag");

                CountryCode countryCode = new CountryCode();
                countryCode.setName(name);
                countryCode.setCode(code);
                countryCode.setPhone_code(phoneCode);
                countryCode.setFlag(flag);

                countryCodeList.add(countryCode);
            }

        } catch (Resources.NotFoundException | IOException | JSONException e) {
            e.printStackTrace();
        }

        return countryCodeList;
    }

}



