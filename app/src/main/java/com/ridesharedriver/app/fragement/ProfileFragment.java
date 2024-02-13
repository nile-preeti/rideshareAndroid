package com.ridesharedriver.app.fragement;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ridesharedriver.app.custom.camera.CameraRotation;
import com.ridesharedriver.app.pojo.CountryCode;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.gson.Gson;
import com.ridesharedriver.app.R;
import com.ridesharedriver.app.Server.Server;
import com.ridesharedriver.app.acitivities.EditDetails;
import com.ridesharedriver.app.acitivities.HomeActivity;
import com.ridesharedriver.app.adapter.VehicleAdapter;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.custom.CheckConnection;
import com.ridesharedriver.app.custom.Utils;
import com.ridesharedriver.app.interfaces.AddVehicleInterface;
import com.ridesharedriver.app.interfaces.CheckInterface;
import com.ridesharedriver.app.pojo.User;
import com.ridesharedriver.app.pojo.changeStatus.ChangeVehicleStatusResponse;
import com.ridesharedriver.app.pojo.changepassword.ChangePasswordResponse;
import com.ridesharedriver.app.pojo.getprofile.GetProfile;
import com.ridesharedriver.app.pojo.profileresponse.ProfileResponse;
import com.ridesharedriver.app.pojo.profileresponse.VehicleDetail;
import com.ridesharedriver.app.session.SessionManager;
import com.hbb20.CountryCodePicker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ridesharedriver.app.utils.PhoneNumberFormat;
import com.ridesharedriver.app.utils.RealPathUtil;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;
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
import java.util.regex.Pattern;

import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import cz.msebera.android.httpclient.Header;
import gun0912.tedbottompicker.TedBottomPicker;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;
import static com.ridesharedriver.app.adapter.AcceptedRequestAdapter.mediaPlayer;
import static com.ridesharedriver.app.pojo.Global.checkRotationFromCamera;
import static com.ridesharedriver.app.pojo.Global.getImageOrientation;
import static com.ridesharedriver.app.session.SessionManager.getUserEmail;
import static com.ridesharedriver.app.session.SessionManager.getUserMobile;
import static com.ridesharedriver.app.session.SessionManager.getUserName;
import static com.ridesharedriver.app.session.SessionManager.getUserVehicleNo;
import static com.ridesharedriver.app.session.SessionManager.setUserMobile;
import static com.ridesharedriver.app.session.SessionManager.setUserName;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

/**
 * Created by android on 14/3/17.
 */

//profile
public class ProfileFragment extends FragmentManagePermission {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private View view;
    private File imageFile;
    private ProfileUpdateListener profileUpdateListener;
    private UpdateListener listener;
    private GoogleApiClient mGoogleApiClient;
    private EditText input_email, input_vehicle, input_name, input_lname, input_paypalId, input_mobile, input_vehicle_name,
            vehicleBrandName, vehicleType, vehicleYear, vehicleColor;
    private Button btn_update, btn_change, btnAddVehicle;
    ImageView profile_pic, img_edit_profile, img_change_password, licence_img;
    private TextView txtAddVehicle, txt_close, txt_drivername, txt_email, txt_phone, insurance_issue_date, insurance_expire_date, car_registration_issue_date,
            car_registration_expire_date, identity_issue_date, identity_expire_date, identification_txt, inspection_issue_date, inspection_expire_date, licence_issue_date, licence_expire_date;
    private ProgressBar progressBar;

    private SwipeRefreshLayout swipeRefreshLayout;
    public static final int PICK_IMAGE = 1;
    private File finalFile, inspectionFinalFile, licenceFinalFile;
    private static final String EQUAL_8 = "8";
    private static final String ABOVE_8 = "Above 8";

    private CheckBox chk_wifi, chk_tv, chk_luxury_seats;
    private RelativeLayout rl_premium_facilities_layout;

    private boolean selectedItem = false;
    EditText input_num, input_color;
    String image, insuranceIssueDate = "", insuranceExpireDate = "", licenceIssueDate = "", licenceExpireDate = "", carRegistrationIssueDate = "", carRegistrationExpireDate = "", inspectionIssueDate = "", inspectionExpireDate = "";
    private ArrayList<VehicleDetail> data;
    private boolean firstLoad = true;
    private LinearLayoutCompat expendable_layout, ll_identification_layout;

    String token, brandId = "", brandVal = "", modelId = "", modelVal = "", typeId = "", typeVal = "", typeRate = "", yearVal = "", seat = "",
            identityName = "", identityId = "", identityIssueDate = "", identityExpireDate = "", savedIdentityId = "";
    Spinner brand_spinner, model_spinner, year_spinner, type_spinner, seat_spinner, identity_spinner;
    ArrayList<String> brand_names = new ArrayList<>();
    ArrayList<String> brand_id = new ArrayList<>();
    ArrayList<String> model_names = new ArrayList<>();
    ArrayList<String> model_id = new ArrayList<>();
    ArrayList<String> type_names = new ArrayList<>();
    ArrayList<String> type_id = new ArrayList<>();
    ArrayList<String> type_rate = new ArrayList<>();
    ArrayList<String> years = new ArrayList<String>();
    ArrayList<String> seatNumbers = new ArrayList<String>();
    ArrayList<String> identity_names = new ArrayList<>();
    ArrayList<String> identity_id = new ArrayList<>();
    ImageView carpic_img, insurance_img, car_registration_img, identification_img, inspection_img;
    private String fileType = "";
    InputStream inputStream;
    private boolean activateVehicle = false, isOnresume = false;
    Button btnSaveVehicle, btnCancel;
    // File filetoupload;
    String[] permissionsFile = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    RecyclerView vehicleRecyclerView;
    VehicleAdapter vehicleAdapter;
    private static final String IMAGE_DIRECTORY = "/OcoryDriver";

    private LocationRequest mLocationRequest;
    private final static int IMAGE_RESULT = 200;
    LinearLayout vehicle_layout;
    LinearLayout profileLayout;
    private File carFinalFile, profileFinalFile, insuranceFinalFile, carRegistrationFinalFile, identityFinalFile;

    CountryCodePicker codePicker;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri carPicUri, licenceUri, profilePicUri, identityPicUri, insurance_fileUri,
            inspectionFileUri, car_registration_fileUri;
    private String car_path = "", licence_path = "", profile_path = "", identity_path = "",
            insurance_path = "", inspection_path = "", registration_path = "";

    ArrayList<String> title_names = new ArrayList<>(Arrays.asList("Boss", "Mr.", "Ms."));
    Spinner name_title;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.myswitch);
        item.setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment, container, false);
        setHasOptionsMenu(true);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.profile));

        bindView();
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                fileUri = data.getData();
                                String path = fileUri.getPath();
                                switch (fileType) {

                                    case "carpic_file":
                                        carPicUri = fileUri;
                                        Glide.with(requireContext()).load(carPicUri).into(carpic_img);
                                        car_path = carPicUri.getPath();
                                        break;
                                    case "licence":
                                        licenceUri = fileUri;
                                        Glide.with(requireContext()).load(licenceUri).into(licence_img);
                                        licence_path = carPicUri.getPath();
                                        break;
                                    case "profile_pic":
                                        profilePicUri = fileUri;
                                        Glide.with(requireContext()).load(profilePicUri).into(profile_pic);
                                        profile_path = profilePicUri.getPath();
                                        break;
                                    case "identity_pic":
                                        identityPicUri = fileUri;
                                        Glide.with(requireContext()).load(identityPicUri).into(identification_img);
                                        identity_path = identityPicUri.getPath();
                                        break;
                                    case "insurance_file":
                                        insurance_fileUri = fileUri;
                                        Glide.with(requireContext()).load(insurance_fileUri).into(insurance_img);
                                        insurance_path = insurance_fileUri.getPath();
                                        break;

                                    case "inspection_file":
                                        inspectionFileUri = fileUri;
                                        Glide.with(requireContext()).load(inspectionFileUri).into(inspection_img);
                                        inspection_path = inspectionFileUri.getPath();
                                        break;
                                    case "car_registration_file":
                                        car_registration_fileUri = fileUri;
                                        Glide.with(requireContext()).load(car_registration_fileUri).into(car_registration_img);
                                        registration_path = car_registration_fileUri.getPath();
                                        break;
                                }

                            } else {
                                switch (fileType) {
                                    case "carpic_file":
                                        carPicUri = fileUri;
                                        Glide.with(requireContext()).load(carPicUri).into(carpic_img);
                                        car_path = carPicUri.getPath();
                                        break;
                                    case "licence":
                                        licenceUri = fileUri;
                                        Glide.with(requireContext()).load(licenceUri).into(licence_img);
                                        licence_path = carPicUri.getPath();
                                        break;
                                    case "profile_pic":
                                        profilePicUri = fileUri;
                                        Glide.with(requireContext()).load(profilePicUri).into(profile_pic);
                                        profile_path = profilePicUri.getPath();
                                        break;
                                    case "identity_pic":
                                        identityPicUri = fileUri;
                                        Glide.with(requireContext()).load(identityPicUri).into(identification_img);
                                        identity_path = identityPicUri.getPath();
                                        break;
                                    case "insurance_file":
                                        insurance_fileUri = fileUri;
                                        Glide.with(requireContext()).load(insurance_fileUri).into(insurance_img);
                                        insurance_path = insurance_fileUri.getPath();
                                        break;

                                    case "inspection_file":
                                        inspectionFileUri = fileUri;
                                        Glide.with(requireContext()).load(inspectionFileUri).into(inspection_img);
                                        inspection_path = inspectionFileUri.getPath();
                                        break;
                                    case "car_registration_file":
                                        car_registration_fileUri = fileUri;
                                        Glide.with(requireContext()).load(car_registration_fileUri).into(car_registration_img);
                                        registration_path = car_registration_fileUri.getPath();
                                        break;
                                }
                            }
                        }


                    }

                });

        Calendar cal = Calendar.getInstance();
        licence_issue_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(requireContext())
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            licenceIssueDate = format.format(newCalendar.getTime());
                            licence_issue_date.setText(licenceIssueDate);
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

        licence_expire_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(requireContext())
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            licenceExpireDate = format.format(newCalendar.getTime());
                            licence_expire_date.setText(licenceExpireDate);
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

        identity_issue_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(requireContext())
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

        identity_expire_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(requireContext())
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
                    .minDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + 1)
                    .build()
                    .show();
        });

        insurance_issue_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(requireContext())
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

        insurance_expire_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(requireContext())
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            insuranceExpireDate = format.format(newCalendar.getTime());
                            insurance_expire_date.setText(insuranceExpireDate);
                        }
                    })
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .maxDate(cal.get(Calendar.YEAR) + 5, 11, 31)
                    .minDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + 1)
                    .build()
                    .show();
        });

        inspection_issue_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(requireContext())
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            inspectionIssueDate = format.format(newCalendar.getTime());
                            inspection_issue_date.setText(inspectionIssueDate);
                        }
                    })
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .maxDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + 1)
                    .minDate(1900, 0, 1)
                    .build()
                    .show();
        });

        inspection_expire_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(requireContext())
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            inspectionExpireDate = format.format(newCalendar.getTime());
                            inspection_expire_date.setText(inspectionExpireDate);
                        }
                    })
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .maxDate(cal.get(Calendar.YEAR) + 10, 11, 31)
                    .minDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + 1)
                    .build()
                    .show();
        });


        car_registration_issue_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(requireContext())
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            carRegistrationIssueDate = format.format(newCalendar.getTime());
                            car_registration_issue_date.setText(carRegistrationIssueDate);
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

        car_registration_expire_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(requireContext())
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            carRegistrationExpireDate = format.format(newCalendar.getTime());
                            car_registration_expire_date.setText(carRegistrationExpireDate);
                        }
                    })
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .maxDate(cal.get(Calendar.YEAR) + 20, 11, 31)
                    .minDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + 1)
                    .build()
                    .show();
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    Utils.hideKeyboard(getActivity(), view);
                }
                if (Utils.haveNetworkConnection(getActivity())) {
                    Server.setHeader(SessionManager.getKEY());
                    if (validate()) {
                        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                            String name = input_name.getText().toString().trim();
                            String lname = input_lname.getText().toString().trim();
                            String title_name = name_title.getSelectedItem().toString();
                            String mobile = input_mobile.getText().toString().trim();
                            String CountryCode = codePicker.getSelectedCountryCode();
                            Log.d("CountryCode", codePicker.getSelectedCountryCode().toString());
                            if (Utils.isValidMobile(mobile)) {
                                if (!selectedItem) {
                                    updateUserProfile(name, lname,title_name, mobile, CountryCode);
                                } else {
                                    if (validateIdentity()) {
                                        updateUserProfile(name, lname,title_name, mobile, CountryCode);
                                    }
                                }
                            } else {
                                showAlert("Enter valid mobile number.");
                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                }
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expendable_layout.setVisibility(View.VISIBLE);
                fileType = "profile_pic";
                showPictureDialog();
            }
        });

        identification_img.setOnClickListener(e -> {
            fileType = "identity_pic";
            showPictureDialog();
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //picture dialog
    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                galleryLauncher.launch(intent);
//                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    File file;
    Uri fileUri;

    //taking picture from camera
    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(getActivity().getExternalCacheDir(),
                String.valueOf(System.currentTimeMillis()) + ".jpg");
        fileUri = FileProvider.getUriForFile(requireContext(), getApplicationContext().getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        galleryLauncher.launch(intent);
//        startActivityForResult(intent, IMAGE_RESULT);
    }

    //uploading images
    private void uploadImage() {
        if (checkPermissions()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        } else {
            checkPermissions();
        }
    }

    //checking permissions
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissionsFile) {
            result = ContextCompat.checkSelfPermission(getActivity(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            profileUpdateListener = (ProfileUpdateListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
        try {
            listener = (UpdateListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void upload_pic(String type) {
        progressBar.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams();
        if (imageFile != null) {
            try {

                if (type.equals("jpg")) {
                    params.put("avatar", imageFile, "image/jpeg");
                } else if (type.equals("jpeg")) {
                    params.put("avatar", imageFile, "image/jpeg");
                } else if (type.equals("png")) {
                    params.put("avatar", imageFile, "image/png");
                } else {
                    params.put("avatar", imageFile, "image/gif");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("catch", e.toString());
            }
        }
        Server.setHeader(SessionManager.getKEY());
        params.put("user_id", SessionManager.getUserId());

        Server.post(Server.UPDATE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("success", response.toString());
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        String url = response.getJSONObject("data").getString("avatar");

                        try {
                            Glide.with(getActivity()).load(url).apply(new RequestOptions().error(R.drawable.user_default)).into(profile_pic);
                        } catch (Exception e) {

                        }
                        User user = SessionManager.getUser();
                        user.setAvatar(url);
                        Gson gson = new Gson();
                        SessionManager.setUser(gson.toJson(user));
                        profileUpdateListener.update(url);
                        input_name.setText(user.getName());
                        input_email.setText(user.getEmail());
                        input_mobile.setText(user.getMobile());
                        input_vehicle.setText(user.getVehicle_info());
                    } else {
                        progressBar.setVisibility(View.GONE);
                        if (response.has("data")) {
                            Toast.makeText(getActivity(), response.getString("data"), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();

            }
        });

    }

    //reset seats
    private void resetSeats() {
        seat_spinner.setSelection(0);
        seat = "";
        rl_premium_facilities_layout.setVisibility(View.GONE);
        chk_wifi.setChecked(false);
        chk_tv.setChecked(false);
        chk_luxury_seats.setChecked(false);
    }

    //getting user info
    private void getUserInfoOnline() {
        RequestParams params = new RequestParams();
        params.put("user_id", SessionManager.getUserId());
        User user = SessionManager.getUser();

        Glide.with(getActivity()).load(user.getAvatar()).apply(new RequestOptions().error(R.drawable.user_default)).into(profile_pic);
        input_name.setText(user.getName());
        input_email.setText(user.getEmail());
        input_vehicle.setText(user.getVehicle_info());
        input_mobile.setText(user.getMobile());
        input_paypalId.setText(user.getPaypal_id());
        Server.setHeader(user.getKey());
        Server.get(Server.GET_PROFILE, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        Gson gson = new Gson();
                        User user1 = gson.fromJson(response.getJSONObject("data").toString(), User.class);

                        Glide.with(ProfileFragment.this).load(user1.getAvatar()).apply(new RequestOptions().error(R.drawable.user_default)).into(profile_pic);
                        input_name.setText(user1.getName());
                        input_email.setText(user1.getEmail());
                        input_vehicle.setText(user1.getVehicle_info());
                        input_mobile.setText(user1.getMobile());
                        input_paypalId.setText(user1.getPaypal_id());
                        user1.setKey(SessionManager.getKEY());

                        SessionManager.setUser(gson.toJson(user1));
                        profileUpdateListener.update(user1.getAvatar());
                        listener.name(user1.getName());


                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getUserInfoOffline() {
        input_name.setText(getUserName());
        input_email.setText(getUserEmail());
        input_mobile.setText(getUserMobile());
        if (getUserVehicleNo() != null)
            input_vehicle.setText(getUserVehicleNo());
        //input_vehicle.setText(getV);

       /* input_name.setText(SessionManager.getUser().getName());
        input_email.setText(SessionManager.getUser().getEmail());
        input_mobile.setText(SessionManager.getUser().getMobile());
        input_vehicle.setText(SessionManager.getUser().getVehicle_info());
        Glide.with(getActivity()).load(SessionManager.getUser().getAvatar()).apply(new RequestOptions().error(R.drawable.user_default)).into(profile_pic);
        input_paypalId.setText(SessionManager.getUser().getPaypal_id());*/
    }

    //validation
    public Boolean validate() {
        Boolean value = true;

        if (input_name.getText().toString().trim().equals("")) {
            input_name.setError(getString(R.string.fname_is_required));
            value = false;
        }
        if (input_lname.getText().toString().trim().equals("")) {
            input_lname.setError(getString(R.string.lname_is_required));
            value = false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input_email.getText().toString().trim()).matches()) {
            input_email.setError(getString(R.string.email_invalid));
            value = false;
        }
        if (identityId.isEmpty()) {
            Toast.makeText(getContext(), "Please Select Identity", Toast.LENGTH_LONG).show();
            value = false;
        }
        if (input_mobile.getText().toString().isEmpty()) {
            input_mobile.setError(getString(R.string.mobile_invalid));
            value = false;
        }
       /* if (input_vehicle.getText().toString().trim().equals("")) {
            value = false;
            input_vehicle.setError(getString(R.string.fiels_is_required));
        } else {
            input_vehicle.setError(null);
        }*/
        return value;
    }

    public void bindView() {

        codePicker = view.findViewById(R.id.country_code);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);

        //premium facilities starts
        chk_wifi = view.findViewById(R.id.chk_wifi);
        chk_luxury_seats = view.findViewById(R.id.chk_luxury_seats);
        chk_tv = view.findViewById(R.id.chk_tv);
        rl_premium_facilities_layout = view.findViewById(R.id.premium_facilities_layout);


        //premium facilities ends


        //inspection

        inspection_issue_date = view.findViewById(R.id.car_inspection_issue_date);
        inspection_expire_date = view.findViewById(R.id.car_inspection_expire_date);
        inspection_img = view.findViewById(R.id.vehicle_inspection_img);
        //insppection


        //driving licence
        licence_img = view.findViewById(R.id.licence_img);
        licence_issue_date = view.findViewById(R.id.licence_issue_date);
        licence_expire_date = view.findViewById(R.id.licence_expire_date);

        //driving licence

        profile_pic = (ImageView) view.findViewById(R.id.profile_pic);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        input_email = (EditText) view.findViewById(R.id.input_email);
        /*input_vehicle = (EditText) view.findViewById(R.id.input_vehicle);
        input_vehicle_name = (EditText) view.findViewById(R.id.input_vehicle_name);*/
        input_name = (EditText) view.findViewById(R.id.input_name);
        input_lname = (EditText) view.findViewById(R.id.input_lname);
        name_title = (Spinner) view.findViewById(R.id.name_title);
        // input_password = (EditText) view.findViewById(R.id.input_password);
        input_mobile = (EditText) view.findViewById(R.id.input_mobile);
        input_mobile.addTextChangedListener(new PhoneNumberFormat(input_mobile));
        input_paypalId = (EditText) view.findViewById(R.id.input_paypal_id);
        btn_update = view.findViewById(R.id.btn_update);
        btn_change = view.findViewById(R.id.btn_change);
        /*vehicleBrandName=view.findViewById(R.id.input_brand_name);
        vehicleType=view.findViewById(R.id.input_vehicle_type);
        vehicleColor=view.findViewById(R.id.input_vehicle_color);
        vehicleYear=view.findViewById(R.id.input_vehicle_year);*/
        btnAddVehicle = view.findViewById(R.id.btnAddVehicle);
        vehicle_layout = view.findViewById(R.id.vehicle_layout);
        profileLayout = view.findViewById(R.id.profileLayout);
        brand_spinner = view.findViewById(R.id.brand_spinner);
        model_spinner = view.findViewById(R.id.model_spinner);
        year_spinner = view.findViewById(R.id.year_spinner);
        seat_spinner = view.findViewById(R.id.seat_no_spinner);
        type_spinner = view.findViewById(R.id.type_spinner);
        carpic_img = view.findViewById(R.id.car_img);
        insurance_img = view.findViewById(R.id.insurance_img);
        car_registration_img = view.findViewById(R.id.car_registration);
        input_num = view.findViewById(R.id.input_num);
        input_color = view.findViewById(R.id.input_color);
        btnSaveVehicle = view.findViewById(R.id.btnSaveVehicle);
        btnCancel = view.findViewById(R.id.btnCancel);
        txtAddVehicle = view.findViewById(R.id.txt_add_vehicle);
        img_edit_profile = view.findViewById(R.id.img_edit_profile);
        img_change_password = view.findViewById(R.id.img_change_password);
        txt_close = view.findViewById(R.id.txt_close);
        expendable_layout = view.findViewById(R.id.expendable_layout);
        txt_drivername = view.findViewById(R.id.txt_drivername);
        txt_email = view.findViewById(R.id.txt_email);
        txt_phone = view.findViewById(R.id.txt_phone);
        //update identity
        identity_spinner = view.findViewById(R.id.identity_doc_spinner);
        identity_issue_date = view.findViewById(R.id.identification_issue_date);
        identity_expire_date = view.findViewById(R.id.identification_expire_date);
        identification_img = view.findViewById(R.id.identification_img);
        ll_identification_layout = view.findViewById(R.id.ll_identification_layout);
        identification_txt = view.findViewById(R.id.identification_txt);
        //update identity

        carFinalFile = new File("");
        insuranceFinalFile = new File("");
        carRegistrationFinalFile = new File("");
        profileFinalFile = new File("");
        identityFinalFile = new File("");
        inspectionFinalFile = new File("");
        licenceFinalFile = new File("");
        data = new ArrayList<>();
        vehicleRecyclerView = view.findViewById(R.id.vehicleRecyclerView);
        vehicleRecyclerView.setNestedScrollingEnabled(false);
        vehicleRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        vehicleRecyclerView.setLayoutManager(layoutManager);

        insurance_issue_date = view.findViewById(R.id.insurance_issue_date);
        insurance_expire_date = view.findViewById(R.id.insurance_expire_date);
        car_registration_issue_date = view.findViewById(R.id.car_registration_issue_date);
        car_registration_expire_date = view.findViewById(R.id.car_registration_expire_date);


       /* BookFont(btn_update);
        BookFont(btn_change);*/


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        img_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expendable_layout.getVisibility() == View.VISIBLE)
                    expendable_layout.setVisibility(View.GONE);
                else
                    expendable_layout.setVisibility(View.VISIBLE);
            }
        });

        img_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.haveNetworkConnection(getActivity())) {
                    changepassword_dialog(getString(R.string.change_password));
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();

                }
            }
        });

        txt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expendable_layout.setVisibility(View.GONE);
            }
        });


        carpic_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileType = "carpic_file";
                showPictureDialog();
            }
        });

        licence_img.setOnClickListener(e -> {
            fileType = "licence";
            showPictureDialog();
        });
        insurance_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileType = "insurance_file";
                showPictureDialog();
            }
        });
        car_registration_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileType = "car_registration_file";
                showPictureDialog();
            }
        });

        inspection_img.setOnClickListener(e -> {
            fileType = "inspection_file";
            showPictureDialog();
        });

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.haveNetworkConnection(getActivity())) {
                    changepassword_dialog(getString(R.string.change_password));
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();

                }
            }
        });

        btnAddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vehicle_layout.setVisibility(View.VISIBLE);
                profileLayout.setVisibility(View.GONE);
            }
        });

        txtAddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAddVehicleFields();
                vehicle_layout.setVisibility(View.VISIBLE);
                profileLayout.setVisibility(View.GONE);
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Add Vehicle");
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vehicle_layout.setVisibility(View.GONE);
                profileLayout.setVisibility(View.VISIBLE);
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Profile");
            }
        });
        if (Utils.haveNetworkConnection(getActivity())) {
            //getUserInfoOnline();
        } else {
            Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            //  getUserInfoOffline();
        }

        btnSaveVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (brandId.isEmpty() || brandId == null) {
                    Toast.makeText(getContext(), "Please select make", Toast.LENGTH_LONG).show();
                    return;
                } else if (modelId.isEmpty() || modelId == null) {
                    Toast.makeText(getContext(), "Please select model", Toast.LENGTH_LONG).show();
                    return;
                } else if (yearVal.isEmpty() || yearVal == null) {
                    Toast.makeText(getContext(), "Please select year", Toast.LENGTH_LONG).show();
                    return;
                } else if (seat.isEmpty() || seat == null) {
                    Toast.makeText(getContext(), "Please select number of seats", Toast.LENGTH_LONG).show();
                    return;
                } else if (input_num.getText().toString().trim().isEmpty() || input_num.getText().toString().trim() == null) {
                    Toast.makeText(getContext(), "Please enter car number plate", Toast.LENGTH_LONG).show();
                    return;
                } else if (input_color.getText().toString().trim().isEmpty() || input_color.getText().toString().trim() == null) {
                    Toast.makeText(getContext(), "Please enter color", Toast.LENGTH_LONG).show();
                    return;
                } else if (fileType.equalsIgnoreCase("carpic_file") && carFinalFile.getName().isEmpty() || carFinalFile.getName().equalsIgnoreCase("")) {
                    Toast.makeText(requireContext(), "Please upload car image", Toast.LENGTH_LONG).show();
                    return;
                } else if (fileType.equalsIgnoreCase("licence") && licenceFinalFile.getName().isEmpty() || licenceFinalFile.getName().equalsIgnoreCase("")) {
                    Toast.makeText(requireContext(), "Please upload Driver's License image", Toast.LENGTH_LONG).show();
                    return;
                } else if (licenceIssueDate.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select licence issue Date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (licenceExpireDate.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select licence expire Date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (fileType.equalsIgnoreCase("inspection_file") && inspectionFinalFile.getName().isEmpty() || inspectionFinalFile.getName().equalsIgnoreCase("")) {
                    Toast.makeText(requireContext(), "Please upload inspection image", Toast.LENGTH_LONG).show();
                    return;
                } else if (inspectionIssueDate.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select inspection issue Date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (inspectionExpireDate.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select inspection expire Date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (fileType.equalsIgnoreCase("insurance_file") && insuranceFinalFile.getName().isEmpty() || insuranceFinalFile.getName().equalsIgnoreCase("")) {
                    Toast.makeText(requireContext(), "Please upload insurance image", Toast.LENGTH_LONG).show();
                    return;
                } else if (insuranceIssueDate.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select insurance issue Date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (insuranceExpireDate.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select insurance expire Date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (fileType.equalsIgnoreCase("car_registration_file") && carRegistrationFinalFile.getName().isEmpty() || carRegistrationFinalFile.getName().equalsIgnoreCase("")) {
                    Toast.makeText(requireContext(), "Please upload car licence image", Toast.LENGTH_LONG).show();
                    return;
                } else if (carRegistrationIssueDate.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select car registration issue Date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (carRegistrationExpireDate.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select car registration expire Date", Toast.LENGTH_SHORT).show();
                    return;
                }

                addNewVehicle();
            }
        });
        getProfile();
        getBrand_details();
        getIdentity_details();
        getYear_details();

        getSeat_nos();
        initializeModels();
        // getType_details();

    }


    //reset vichical field
    public void resetAddVehicleFields() {
        getBrand_details();

        getYear_details();

        getSeat_nos();
        initializeModels();
        input_color.setText("");
        input_num.setText("");
        insuranceFinalFile = new File("");
        carFinalFile = new File("");
        carRegistrationFinalFile = new File("");
        insurance_img.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_upload_pic));
        carpic_img.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_upload_pic));
        car_registration_img.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_upload_pic));
    }


    //getting Identity details
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

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(requireContext(), R.layout.spinner_item, identity_names);
                        identity_spinner.setAdapter(dataAdapter);
                        if (savedIdentityId != null && !savedIdentityId.isEmpty()) {
                            int spinnerPosition = dataAdapter.getPosition(savedIdentityId);
                            identity_spinner.setSelection(Integer.parseInt(savedIdentityId));
                            ll_identification_layout.setVisibility(View.VISIBLE);
                            selectedItem = false;
                        }
                        identity_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                                if (!identity_spinner.getItemAtPosition(position).equals("Select Identity")) {
                                    identityId = (identity_id.get(position - 1));
                                    identityName = adapterView.getItemAtPosition(position).toString();
                                    ll_identification_layout.setVisibility(View.VISIBLE);
                                    if (identity_names.get(Integer.parseInt(savedIdentityId)).equalsIgnoreCase(identityName)) {
                                        identification_txt.setText(String.format("Update %s", identityName));
                                    } else {
                                        resetIdentity(identityName);
                                    }

                                } else {
                                    identityId = "";
                                    identityName = "";
                                    ll_identification_layout.setVisibility(View.GONE);
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

                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(requireContext(), R.layout.spinner_item, identity_names);
                        identity_spinner.setAdapter(dataAdapter1);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(requireContext(), getString(R.string.try_again), Toast.LENGTH_LONG).show();
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

    //validating identity
    private boolean validateIdentity() {
        if (identityId.equalsIgnoreCase("")) {
            Toast.makeText(requireContext(), "Please Select Identity Document.", Toast.LENGTH_LONG).show();
            return false;
        } else if (fileType.equalsIgnoreCase("identity_pic") && identityFinalFile.getName().isEmpty() || identityFinalFile.getName().equalsIgnoreCase("")) {
            Toast.makeText(requireContext(), "Please upload Id Proof.", Toast.LENGTH_LONG).show();
            return false;
        } else if (identityIssueDate.isEmpty()) {
            Toast.makeText(requireContext(), "Please select identity issue Date", Toast.LENGTH_SHORT).show();
            return false;
        } else if (identityExpireDate.isEmpty()) {
            Toast.makeText(requireContext(), "Please select identity expire Date", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    //reserting identity
    private void resetIdentity(String identityName) {

        identification_txt.setText(String.format("Update %s", identityName));
        identity_expire_date.setText("Expire Date");
        identity_issue_date.setText("Issue Date");
        identityFinalFile = new File("");
        identification_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_upload_pic, null));
        identityIssueDate = "";
        identityExpireDate = "";
        selectedItem = true;

    }

    //getting brand details
    public void getBrand_details() {
        RequestParams params = new RequestParams();
        Server.get("getBrand", params, new JsonHttpResponseHandler() {
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
                            brand_names.add(jsonObject.getString("brand_name"));
                            brand_id.add(jsonObject.getString("id"));
                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, brand_names);
                        brand_spinner.setAdapter(dataAdapter);

                        brand_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                                if (!brand_spinner.getItemAtPosition(position).equals("Select Vehicle Make")) {
                                    brandId = (brand_id.get(position - 1));
                                    brandVal = adapterView.getItemAtPosition(position).toString();

                                    getModel_details(brandId);
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

                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, brand_names);
                        brand_spinner.setAdapter(dataAdapter1);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_LONG).show();
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

    //getting models
    public void initializeModels() {
        model_names.clear();
        model_id.clear();
        model_names.add("Select Vehicle Model");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(requireContext(), R.layout.spinner_item, model_names);
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

    //getting model details
    public void getModel_details(String brandId) {
        RequestParams params = new RequestParams();
        params.put("brand_id", brandId);
        Server.post("getModel", params, new JsonHttpResponseHandler() {
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
                    if (response.has("status") && response.getBoolean("status")) {
                        model_names.add("Select Vehicle Model");

                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            model_names.add(jsonObject.getString("model_name"));
                            model_id.add(jsonObject.getString("id"));
                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, model_names);
                        model_spinner.setAdapter(dataAdapter);

                        model_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                                if (!model_spinner.getItemAtPosition(position).equals("Select Vehicle Model")) {
                                    modelId = (model_id.get(position - 1));
                                    modelVal = adapterView.getItemAtPosition(position).toString();
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

                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, model_names);
                        model_spinner.setAdapter(dataAdapter1);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_LONG).show();
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

    //getting seat count
    public void getSeat_nos() {
        seatNumbers.clear();
        seatNumbers.add("Select Number Of Seats");
        seatNumbers.add("4");
        seatNumbers.add("5");
        seatNumbers.add("6");
        seatNumbers.add("7");
        seatNumbers.add("8");
        seatNumbers.add("Above 8");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, seatNumbers);
        seat_spinner.setAdapter(adapter);

        seat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (!seat_spinner.getItemAtPosition(position).equals("Select Number Of Seats")) {
                    seat = adapterView.getItemAtPosition(position).toString();
                    if (seat.equalsIgnoreCase(EQUAL_8)) {
                        rl_premium_facilities_layout.setVisibility(View.VISIBLE);
                    } else {
                        rl_premium_facilities_layout.setVisibility(View.GONE);
                    }
                } else {
                    seat = "";
                }

                Log.d("onItemSelected \n", "yearVal : " + yearVal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
    }

    //getting year
    public void getYear_details() {
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.clear();
        years.add("Select Vehicle Year");
        for (int i = thisYear; i > thisYear - 10; i--) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, years);
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

    //get type details
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

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, type_names);
                        type_spinner.setAdapter(dataAdapter);

                        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                                if (!type_spinner.getItemAtPosition(position).equals("Select Vehicle Type")) {
                                    typeId = (type_id.get(position - 1));
                                    typeVal = adapterView.getItemAtPosition(position).toString();
                                    typeRate = type_rate.get(position - 1);
                                } else {
                                    typeId = "";
                                    typeVal = "";
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

                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, type_names);
                        type_spinner.setAdapter(dataAdapter1);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_LONG).show();
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

    //changing Status of vechical
    public void changeVehicleStatus(String vehicleDetailsId, boolean isChecked, String status) {
        final Dialog dialog = new Dialog(getApplicationContext());
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Loading...", false, false);
        Map<String, String> details = new HashMap<>();
        details.put("vehicle_detail_id", vehicleDetailsId);

        if (status.equals("3"))
            details.put("status", status);
        else {
            if (isChecked) {
                details.put("status", "1");
            } else {
                details.put("status", "2");
            }
        }

        String token = "Bearer " + SessionManager.getKEY();
        Log.d("token", token);

        ApiNetworkCall apiService = ApiClient.getApiService();
        Call<ChangeVehicleStatusResponse> call = apiService.changeVehicleStatus(token, details);
        call.enqueue(new Callback<ChangeVehicleStatusResponse>() {
            @Override
            public void onResponse(Call<ChangeVehicleStatusResponse> call, retrofit2.Response<ChangeVehicleStatusResponse> response) {
                ChangeVehicleStatusResponse jsonResponse = response.body();
                if (jsonResponse.getStatus()) {
                    loading.cancel();
                    if (!firstLoad) {
                        // Toast.makeText(getContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    firstLoad = false;
                    dialog.cancel();
                    loading.cancel();
                    //if (status.equals("3")||status.equals("1"))
                    getProfile();
                } else {
                    // Toast.makeText(getContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.cancel();

                }
            }

            @Override
            public void onFailure(Call<ChangeVehicleStatusResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                dialog.cancel();
                loading.cancel();
            }
        });
    }


    public void BookFont(AppCompatButton view1) {
        //  Typeface font1 = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Book.otf");
        Typeface font1 = Typeface.createFromAsset(getActivity().getAssets(), "font/montserrat_bold.ttf");
        view1.setTypeface(font1);
    }

    public void MediumFont(EditText view) {
        // Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf");
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/montserrat_regular.ttf");
        view.setTypeface(font);
    }

    public void MediumFont(TextView view) {
        // Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf");
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/montserrat_regular.ttf");
        view.setTypeface(font);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                getProfile();

            }
        }

        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                //getCurrentlOcation();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        if (requestCode == IMAGE_RESULT && resultCode == RESULT_OK) {


            switch (fileType) {
                case "insurance_file":
                    Glide.with(requireContext()).load(fileUri.toString()).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(insurance_img);
                    file = compressImage(fileUri.getPath());
                    String insurance_path = file.getPath();
                    insuranceFinalFile = new File(insurance_path);
                    break;

                case "licence":
                    Glide.with(requireContext()).load(fileUri.toString()).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(licence_img);
                    file = compressImage(fileUri.getPath());
                    String licence_path = file.getPath();
                    licenceFinalFile = new File(licence_path);
                    break;

                case "inspection_file":
                    Glide.with(requireContext()).load(fileUri.toString()).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(inspection_img);
                    file = compressImage(fileUri.getPath());
                    String inspection_path = file.getPath();
                    inspectionFinalFile = new File(inspection_path);
                    break;

                case "car_registration_file":
                    Glide.with(requireContext()).load(fileUri.toString()).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(car_registration_img);
                    file = compressImage(fileUri.getPath());
                    String car_reg_path = file.getPath();
                    carRegistrationFinalFile = new File(car_reg_path);
                    break;

                case "carpic_file":
                    Glide.with(requireContext()).load(fileUri.toString()).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(carpic_img);
                    file = compressImage(fileUri.getPath());
                    String car_pic_path = file.getPath();
                    carFinalFile = new File(car_pic_path);
                    break;
                case "profile_pic":
                    try {
                        Bitmap bitmap = CameraRotation.handleSamplingAndRotationBitmap(requireContext(), fileUri);
                        Glide.with(requireContext()).load(bitmap).apply(new RequestOptions().error(R.drawable.img_logo)).into(profile_pic);
                    } catch (IOException e) {
                        Glide.with(requireContext()).load(fileUri.toString()).apply(new RequestOptions().error(R.drawable.img_logo)).into(profile_pic);
                        e.printStackTrace();
                    }

                    file = compressImage(fileUri.getPath());
                    String profile_path = file.getPath();
                    profileFinalFile = new File(profile_path);

//                    Bitmap thumbnail = BitmapFactory.decodeFile(file.getPath());
//                    profile_pic.setImageBitmap(thumbnail);
//                    saveImage(thumbnail);
                    break;

                case "identity_pic":
                    Glide.with(requireContext()).load(fileUri.toString()).apply(new RequestOptions().error(R.drawable.img_logo)).into(identification_img);
                    file = compressImage(fileUri.getPath());
                    String identity_path = file.getPath();
                    identityFinalFile = new File(identity_path);

//                    Bitmap thumbnail = BitmapFactory.decodeFile(file.getPath());
//                    profile_pic.setImageBitmap(thumbnail);
//                    saveImage(thumbnail);
                    break;

            }
        }

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }


            try {
                inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                //create a file to write bitmap data
                //  filetoupload = new File(this.getCacheDir(), "OcoryDocuments");
                //   filetoupload.createNewFile();

                //Convert bitmap to byte array
                Bitmap bitmap = bmp;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                switch (fileType) {

                    case "carpic_file":
                        Glide.with(requireContext()).asBitmap().load(bmp).into(carpic_img);
                        String car_path = compressImage(RealPathUtil.getRealPath(requireContext(), data.getData())).getPath();
                        carFinalFile = new File(car_path);
                        break;
                    case "licence":
                        Glide.with(requireContext()).asBitmap().load(bmp).into(licence_img);
                        String licence_path = compressImage(RealPathUtil.getRealPath(requireContext(), data.getData())).getPath();
                        licenceFinalFile = new File(licence_path);
                        break;
                    case "profile_pic":
                        Glide.with(requireContext()).asBitmap().load(bmp).into(profile_pic);
                        String profile_path = compressImage(RealPathUtil.getRealPath(requireContext(), data.getData())).getPath();
                        profileFinalFile = new File(profile_path);
//                        Bitmap thumbnail = BitmapFactory.decodeFile(file.getPath());
//                        profile_pic.setImageBitmap(thumbnail);
//                        saveImage(thumbnail);
                        break;
                    case "identity_pic":
                        Glide.with(requireContext()).asBitmap().load(bmp).into(identification_img);
                        String identity_path = compressImage(RealPathUtil.getRealPath(requireContext(), data.getData())).getPath();
                        identityFinalFile = new File(identity_path);
//                        Bitmap thumbnail = BitmapFactory.decodeFile(file.getPath());
//                        profile_pic.setImageBitmap(thumbnail);
//                        saveImage(thumbnail);
                        break;
                    case "insurance_file":
                        Glide.with(requireContext()).asBitmap().load(bmp).into(insurance_img);
                        String insurance_path = compressImage(RealPathUtil.getRealPath(requireContext(), data.getData())).getPath();
                        insuranceFinalFile = new File(insurance_path);
                        break;

                    case "inspection_file":
                        Glide.with(requireContext()).asBitmap().load(bmp).into(inspection_img);
                        String inspection_path = compressImage(RealPathUtil.getRealPath(requireContext(), data.getData())).getPath();
                        inspectionFinalFile = new File(inspection_path);
                        break;
                    case "car_registration_file":
                        Glide.with(requireContext()).asBitmap().load(bmp).into(car_registration_img);
                        String registration_path = compressImage(RealPathUtil.getRealPath(requireContext(), data.getData())).getPath();
                        carRegistrationFinalFile = new File(registration_path);
                        break;
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

       /* if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);

                profile_pic.setImageBitmap(bmp);

                //Convert bitmap to byte array
                Bitmap bitmap = bmp;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos);

                String path = getRealPathFromURI(getImageUri(getActivity(), bmp));
                finalFile = new File(path);

                Log.d("Image Path", "++++++++++++++++" + path);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        }
    }

    //compressing image
    public File compressImage(String imageUrl) {
        int compressionRatio = 2; //1 == originalImage, 2 = 50% compression, 4=25% compress
        File file = new File(imageUrl);
        try {
            BitmapFactory.Options Options = new BitmapFactory.Options();
            Options.inSampleSize = 4;
            Options.inJustDecodeBounds = false;
//            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), Options);
            Bitmap bitmap = CameraRotation.handleSamplingAndRotationBitmap(requireContext(), Uri.parse(imageUrl));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, new FileOutputStream(file));
            return file;
        } catch (Throwable t) {
            Log.e("ERROR", "Error compressing file." + t.toString());
            t.printStackTrace();
            return file;
        }
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
            MediaScannerConnection.scanFile(getActivity(),
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

    //adding new vechical
    public void addNewVehicle() {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Please wait...", false, false);

       /* RequestParams params = new RequestParams();
        params.put("year", yearVal);
        params.put("vehicle_no", input_num.getText().toString());
        params.put("brand", brandId);
        params.put("model", modelId);
        params.put("color", input_color.getText().toString());
        params.put("vehicle_type", typeId);

        try {
            params.put("car_pic", carFinalFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

        String token = "Bearer " + SessionManager.getKEY();
        Log.d("token", token);
//        final ProgressDialog loading = ProgressDialog.show(getApplicationContext(), "Please wait...", "Uploading data...", false, false);

        ApiNetworkCall apiService = ApiClient.getApiService();

        String premiumFacilities = "";
        if (seat.equals(ABOVE_8)) {
            seat = "9";
        } else if (seat.equalsIgnoreCase(EQUAL_8)) {
            if (chk_tv.isChecked() || chk_wifi.isChecked() || chk_luxury_seats.isChecked()) {
                seat = "9";
                if (chk_wifi.isChecked())
                    premiumFacilities += "Wi-Fi,";
                if (chk_tv.isChecked())
                    premiumFacilities += " T.V.,";
                if (chk_luxury_seats.isChecked())
                    premiumFacilities += " Luxury Seats";

            } else {
                seat = "8";
            }
        }

        RequestBody Request_year = RequestBody.create(
                MediaType.parse("text/plain"),
                yearVal);
        RequestBody Req_car_facilities = RequestBody.create(
                MediaType.parse("text/plain"),
                premiumFacilities);

        RequestBody Request_color = RequestBody.create(
                MediaType.parse("text/plain"),
                input_color.getText().toString());
        RequestBody Request_no = RequestBody.create(
                MediaType.parse("text/plain"),
                input_num.getText().toString());
        RequestBody Request_brand = RequestBody.create(
                MediaType.parse("text/plain"),
                brandId);
        RequestBody Request_model = RequestBody.create(
                MediaType.parse("text/plain"),
                modelId);
        RequestBody Req_type = RequestBody.create(
                MediaType.parse("text/plain"),
                typeId);
        RequestBody Req_seat = RequestBody.create(
                MediaType.parse("text/plain"),
                seat);
        RequestBody Req_insurance_issue = RequestBody.create(
                MediaType.parse("text/plain"),
                insuranceIssueDate);
        RequestBody Req_insurance_expire = RequestBody.create(
                MediaType.parse("text/plain"),
                insuranceExpireDate);
        RequestBody Req_car_issue = RequestBody.create(
                MediaType.parse("text/plain"),
                carRegistrationIssueDate);
        RequestBody Req_car_expire = RequestBody.create(
                MediaType.parse("text/plain"),
                carRegistrationExpireDate);
        RequestBody Req_inspection_issue = RequestBody.create(
                MediaType.parse("text/plain"),
                inspectionIssueDate);
        RequestBody Req_inspection_expire = RequestBody.create(
                MediaType.parse("text/plain"),
                inspectionExpireDate);
        RequestBody Req_licence_issue = RequestBody.create(
                MediaType.parse("text/plain"),
                licenceIssueDate);
        RequestBody Req_licence_expire = RequestBody.create(
                MediaType.parse("text/plain"),
                licenceExpireDate);

        MultipartBody.Part carFile, insuranceFile, registrationFile, inspectionFileToUpload, licencePicUpload;
        //empty file
        RequestBody empty_file = RequestBody.create(
                MediaType.parse("text/plain"),
                "");

        if (licenceFinalFile != null && licenceFinalFile.exists()) {
            licencePicUpload = MultipartBody.Part.createFormData("license", licenceFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), licenceFinalFile));
        } else {
            licencePicUpload = MultipartBody.Part.createFormData("license", "", empty_file);
        }
        if (inspectionFinalFile != null && inspectionFinalFile.exists()) {
            inspectionFileToUpload = MultipartBody.Part.createFormData("inspection_document", inspectionFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), inspectionFinalFile));
        } else {
            inspectionFileToUpload = MultipartBody.Part.createFormData("inspection_document", "", empty_file);
        }
        if (carFinalFile != null && carFinalFile.exists()) {
            carFile = MultipartBody.Part.createFormData("car_pic", carFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), carFinalFile));
        } else {
            carFile = MultipartBody.Part.createFormData("car_pic", "", empty_file);
        }
        if (carRegistrationFinalFile != null && carRegistrationFinalFile.exists()) {
            registrationFile = MultipartBody.Part.createFormData("car_registration", carRegistrationFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), carRegistrationFinalFile));
        } else {
            registrationFile = MultipartBody.Part.createFormData("car_registration", "", empty_file);
        }

        if (insuranceFinalFile != null && insuranceFinalFile.exists()) {
            insuranceFile = MultipartBody.Part.createFormData("insurance", insuranceFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), insuranceFinalFile));
        } else {
            insuranceFile = MultipartBody.Part.createFormData("insurance", "", empty_file);
        }

        Call<ChangePasswordResponse> call = apiService.addVehicle(token,
                Request_year, Request_no, Request_brand, Request_model, Request_color, Req_type, Req_seat, Req_car_facilities,
                carFile, insuranceFile, Req_insurance_issue, Req_insurance_expire, registrationFile, Req_car_issue, Req_car_expire,
                Req_inspection_issue, Req_inspection_expire, inspectionFileToUpload, Req_licence_issue, Req_licence_expire, licencePicUpload);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                ChangePasswordResponse requestResponse = response.body();
                Toast.makeText(getActivity(), requestResponse.getMessage(), Toast.LENGTH_LONG).show();
                loading.cancel();
                profileLayout.setVisibility(View.VISIBLE);
                vehicle_layout.setVisibility(View.GONE);
                getProfile();
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                // progressBar.setVisibility(View.GONE);
                loading.cancel();
                resetSeats();
                Log.d("Failed", "RetrofitFailed");
            }
        });



        /*Server.post("add_vehicle_detail", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    loading.cancel();
                    if (response.has("status") && response.getBoolean("status")) {

                        Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();
                        //startActivity(new Intent(RegisterActivity.this, UploadDocs.class));
                       profileLayout.setVisibility(View.VISIBLE);
                       vehicle_layout.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    loading.cancel();
                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                loading.cancel();
                swipeRefreshLayout.setRefreshing(false);
            }
        });*/
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("permisson", "granted");
                    TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(getActivity())
                            .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                                @Override
                                public void onImageSelected(Uri uri) {
                                    // here is selected uri
                                    profile_pic.setImageURI(uri);
                                }
                            }).setOnErrorListener(new TedBottomPicker.OnErrorListener() {
                                @Override
                                public void onError(String message) {
                                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_LONG).show();
                                    Log.d(getTag(), message);
                                }
                            })
                            .create();

                    tedBottomPicker.show(getActivity().getSupportFragmentManager());

                } else {

                }
            }
        }
    }


    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    private boolean checkIfAlreadyhavePermission() {
        int fine = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        int read = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (fine == PackageManager.PERMISSION_GRANTED) {
            return true;

        }
        if (read == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (write == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    //updating user profile
    private void updateUserProfile(String name, String lname,String title_name, String mobile, String CountryCode) {
        final ProgressDialog loading =
                ProgressDialog.show(getActivity(), "", "Updating data", false, false);

        ApiNetworkCall apiService = ApiClient.getApiService();


        RequestBody request_name = RequestBody.create(
                MediaType.parse("text/plain"),
                name);

        RequestBody request_lname = RequestBody.create(
                MediaType.parse("text/plain"),
                lname);
        RequestBody request_title_name = RequestBody.create(
                MediaType.parse("text/plain"),
                title_name);

        RequestBody request_mobile = RequestBody.create(
                MediaType.parse("text/plain"),
                mobile);

        RequestBody request_countrycode = RequestBody.create(
                MediaType.parse("text/plain"),
                CountryCode);

        RequestBody request_identification_id = RequestBody.create(
                MediaType.parse("text/plain"),
                identityId);

        RequestBody request_identity_issue = RequestBody.create(
                MediaType.parse("text/plain"),
                identityIssueDate);

        RequestBody request_identity_expire = RequestBody.create(
                MediaType.parse("text/plain"),
                identityExpireDate);

        MultipartBody.Part fileToUpload, identityFile;
        //empty file
        RequestBody empty_file = RequestBody.create(
                MediaType.parse("image/*"),
                "");


        if (!profile_path.isEmpty()) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), profilePicUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final int rotation = getImageOrientation(profile_path);
            bitmap = checkRotationFromCamera(bitmap, rotation);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            fileToUpload = MultipartBody.Part.createFormData("profile_pic", "profile_pic.jpg", requestBody);
        } else {
            fileToUpload = MultipartBody.Part.createFormData("profile_pic", "", empty_file);
        }
        if (!identity_path.isEmpty()) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), identityPicUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final int rotation = getImageOrientation(identity_path);
            bitmap = checkRotationFromCamera(bitmap, rotation);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            identityFile = MultipartBody.Part.createFormData("verification_id", "verification_id.jpg", requestBody);


        } else {
            identityFile = MultipartBody.Part.createFormData("verification_id", "", empty_file);
        }

//        if (profileFinalFile != null && profileFinalFile.exists()) {
//            fileToUpload = MultipartBody.Part.createFormData("profile_pic", profileFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), profileFinalFile));
//        } else {
//            fileToUpload = MultipartBody.Part.createFormData("profile_pic", "", empty_file);
//        }
//        if (identityFinalFile != null && identityFinalFile.exists()) {
//            identityFile = MultipartBody.Part.createFormData("verification_id", identityFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), identityFinalFile));
//        } else {
//            identityFile = MultipartBody.Part.createFormData("verification_id", "", empty_file);
//        }

        Call<ProfileResponse> call = apiService.updateProfile("Bearer " + SessionManager.getKEY(),
                fileToUpload, request_name, request_lname,request_title_name, request_mobile, request_countrycode,
                request_identification_id, request_identity_issue, request_identity_expire, identityFile);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, retrofit2.Response<ProfileResponse> response) {
                ProfileResponse jsonResponse = response.body();
                if (jsonResponse.getStatus()) {
                    loading.cancel();
                    setUserName(input_name.getText().toString().trim());
                    setUserMobile(input_mobile.getText().toString().trim());
                    codePicker.setDetectCountryWithAreaCode(true);


                    Toast.makeText(getActivity(), "Your profile has been changed.", Toast.LENGTH_LONG).show();
//                    Log.d("Updated CountryCode", jsonResponse.getData().getCountryCode().toString());
                    SessionManager.setUserCountryCode("+1");
                   /* input_name.setText(getUserName());
                    input_mobile.setText(getUserMobile());
                    input_name.setText(jsonResponse.getData().getName());
                    input_mobile.setText(jsonResponse.getData().getMobile());*/

                    firstLoad = true;
                    getProfile();

                } else {
                    Toast.makeText(getActivity(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    loading.cancel();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                loading.cancel();
            }
        });
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }

    @Override
    public void onResume() {
        super.onResume();
        isOnresume = true;
        // getProfile();
    }


    //update profile
    public interface ProfileUpdateListener {
        void update(String url);

    }

    //update listner
    public interface UpdateListener {
        void name(String name);

    }

    //password change dialog
    public void changepassword_dialog(String title) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.changepassword_dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tle = (TextView) dialog.findViewById(R.id.title);
        final EditText password = (EditText) dialog.findViewById(R.id.input_Password);
        final EditText confirm_password = (EditText) dialog.findViewById(R.id.input_confirmPassword);
        final EditText old_password = (EditText) dialog.findViewById(R.id.old_Password);
        AppCompatButton btn_change = (AppCompatButton) dialog.findViewById(R.id.change_password);
//        overrideFonts(getActivity(), dialog.getCurrentFocus());
//        MediumFont(tle);
//        MediumFont(password);
//        MediumFont(confirm_password);
//        MediumFont(old_password);
        //BookFont(btn_change);
        tle.setText(title);
        // btn_change.setText(getString(R.string.change));


        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpassword = password.getText().toString().trim();
                String confirmpassword = confirm_password.getText().toString().trim();
                String oldPassword = old_password.getText().toString().trim();

                if (oldPassword.isEmpty()) {
                    old_password.setError(getString(R.string.old_password_required));
                } else if (password.getText().toString().trim().equals("")) {
                    password.setError(getString(R.string.password_required));
                } else if (!confirm_password.getText().toString().trim().equals("")) {

                    if (CheckConnection.haveNetworkConnection(getApplicationContext())) {

                        if (oldPassword.equals(SessionManager.getOldPassword()))
                            changeUserPassword(dialog, oldpassword, confirmpassword);
                        else
                            showAlert("Old password does not match!");

                    } else {
                        Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();

                    }

                } else {

                    confirm_password.setError(getString(R.string.newpwd_required));
                }

            }
        });
        dialog.show();

    }


    //showing alert
    private void showAlert(String message) {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        dialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    //overridenFonts
    private void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof AppCompatButton) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Book.otf"));
            } else if (v instanceof EditText) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf"));
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Book.otf"));
            }

        } catch (Exception e) {
        }
    }

    //change password
    public void changepassword(final Dialog dialog, String id, String oldpassword, String newpassword) {
        RequestParams params = new RequestParams();
        params.put("old_password", oldpassword);
        params.put("new_password", newpassword);
        params.put("user_id", id);
        Server.setHeader(SessionManager.getKEY());
        Server.post(Server.PASSWORD_RESET, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        dialog.cancel();
                        Toast.makeText(getActivity(), getString(R.string.password_updated), Toast.LENGTH_LONG).show();

                    } else {
                        String error = response.getString("data");
                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    //changeUserPassword
    private void changeUserPassword(final Dialog dialog, String newPassword, String cNewPassword) {
        Map<String, String> details = new HashMap<>();
        details.put("new_password", newPassword);
        details.put("confirm_password", cNewPassword);

        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<ChangePasswordResponse> call = apiService.changePassword("Bearer " + SessionManager.getKEY(), details);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                ChangePasswordResponse jsonResponse = response.body();
                if (jsonResponse.getStatus()) {
                    Toast.makeText(getActivity(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.cancel();

                } else {
                    Toast.makeText(getActivity(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    // dialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                dialog.cancel();
            }
        });
    }

    //get profile
    private void getProfile() {
        final ProgressDialog loading = ProgressDialog.show(getActivity(),
                "", "Please wait...", false, false);

        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<GetProfile> call = apiService.getProfile("Bearer " + SessionManager.getKEY());
        call.enqueue(new Callback<GetProfile>() {
            @Override
            public void onResponse(Call<GetProfile> call, retrofit2.Response<GetProfile> response) {
                GetProfile jsonResponse = response.body();

                if (jsonResponse.getStatus()) {
                    loading.cancel();
                    Log.d("Testing", response.body().toString());
                    input_email.setText(jsonResponse.getData().getEmail());
                    input_mobile.setText(jsonResponse.getData().getMobile());
                    input_name.setText(jsonResponse.getData().getName());
                    input_lname.setText(jsonResponse.getData().getLastName());
                    codePicker.setDefaultCountryUsingNameCode("+1");
                    txt_drivername.setText(jsonResponse.getData().getName());
//                    String CountryCode = jsonResponse.getData().getCountryCode().toString();
                    String CountryCode = "+1";
                    String mobile = jsonResponse.getData().getMobile().toString();
                    txt_phone.setText(CountryCode + " " + mobile);
                    txt_email.setText(jsonResponse.getData().getEmail());
//                    SessionManager.setUserCountryCode(jsonResponse.getData().getCountryCode());
                    SessionManager.setUserCountryCode("+1");
                   /* input_vehicle.setText(jsonResponse.getData()getVehicleNo());
                    input_vehicle_name.setText(jsonResponse.getData().get(0).getModelName());
                    vehicleBrandName.setText(jsonResponse.getData().get(0).getBrandName());
                    vehicleColor.setText(jsonResponse.getData().get(0).getColor());
                    vehicleYear.setText(jsonResponse.getData().get(0).getYear());
                    vehicleType.setText(jsonResponse.getData().get(0).getVehicleType());*/

                    ArrayAdapter<String> titleNameAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, title_names);
                    name_title.setAdapter(titleNameAdapter);
                    int spinnerPosition = titleNameAdapter.getPosition(jsonResponse.getData().getNameTitle());
                    name_title.setSelection(spinnerPosition);


                    if (jsonResponse.getData().getIdentificationDocID() != null) {
                        savedIdentityId = jsonResponse.getData().getIdentificationDocID();

                    } else {
                        savedIdentityId = "";
                    }
                    if (jsonResponse.getData().getIdentificationIssueDate() != null) {
                        identityIssueDate = jsonResponse.getData().getIdentificationIssueDate();
                        identity_issue_date.setText(identityIssueDate);
                    } else {
                        identityIssueDate = "";
                    }
                    if (jsonResponse.getData().getIdentificationExpiryDate() != null) {
                        identityExpireDate = jsonResponse.getData().getIdentificationExpiryDate();
                        identity_expire_date.setText(identityExpireDate);
                    } else {
                        identityExpireDate = "";
                    }

                    if (jsonResponse.getData().getVerificationId() != null) {
                        String identificationUrl = jsonResponse.getData().getVerificationId();
                        if (identificationUrl.isEmpty() || identificationUrl.equalsIgnoreCase("")) {
                            Glide.with(getActivity()).load(R.drawable.icon).into(identification_img);

                        } else {
                            Glide.with(getActivity()).load(jsonResponse.getData().getVerificationId()).into(identification_img);

                        }
                    }

                    if (identityFinalFile != null && identityFinalFile.exists()) {

                        Glide.with(requireContext()).load(identityFinalFile.getPath()).apply(new RequestOptions().error(R.drawable.img_logo)).into(identification_img);
                    }


                    if (jsonResponse.getData().getProfileImage() != null) {
                        String profileUrl = jsonResponse.getData().getProfileImage();
                        if (profileUrl.isEmpty() || profileUrl.equalsIgnoreCase("")) {
                            Glide.with(getActivity()).load(R.drawable.icon).into(profile_pic);
                        } else {
                            try {
                                Bitmap bitmap = CameraRotation.handleSamplingAndRotationBitmap(requireContext(), Uri.parse(jsonResponse.getData().getProfilePic()));
                                Glide.with(getActivity()).load(bitmap).into(profile_pic);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Glide.with(getActivity()).load(jsonResponse.getData().getProfilePic()).into(profile_pic);
                            }

                        }
                    }
                    if (profileFinalFile != null && profileFinalFile.exists()) {
                        try {
                            Bitmap bitmap1 = CameraRotation.handleSamplingAndRotationBitmap(requireContext(), FileProvider.getUriForFile(requireContext(), getApplicationContext().getPackageName() + ".provider", profileFinalFile));
                            Glide.with(requireContext()).load(bitmap1).apply(new RequestOptions().error(R.drawable.img_logo)).into(profile_pic);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Glide.with(requireContext()).load(profileFinalFile.getPath()).apply(new RequestOptions().error(R.drawable.img_logo)).into(profile_pic);
                        }

                    }

                    if (data.size() > 0) {
                        data.clear();
                    }
                    for (int i = 0; i < jsonResponse.getData().getVehicleDetail().size(); i++) {
                        data.add(jsonResponse.getData().getVehicleDetail().get(i));
                    }

                    vehicleAdapter = new VehicleAdapter(getActivity(), data, new AddVehicleInterface() {
                        @Override
                        public void onItemClicked(String brand,
                                                  String model,
                                                  String type, String no, String color, String year,
                                                  String id, String seatNumber) {

                            Intent intent = new Intent(getContext(), EditDetails.class);
                            intent.putExtra("vehicle_id", id);
                            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
                            //final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Please wait...", false, false);
                               /* RequestParams params = new RequestParams();

                                params.put("year", year);
                                params.put("vehicle_no", no);
                                params.put("brand", brand);
                                params.put("model", model);
                                params.put("color", color);
                                params.put("vehicle_type", type);
                                params.put("vehicle_detail_id", id);
*/
                               /* Server.post("update_vehicle_detail", params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onStart() {
                                        super.onStart();
                                        swipeRefreshLayout.setRefreshing(true);
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        super.onSuccess(statusCode, headers, response);
                                        try {
                                            loading.cancel();
                                            if (response.has("status") && response.getBoolean("status")) {

                                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();

                                            } else {
                                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            loading.cancel();
                                            Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        super.onFinish();
                                        loading.cancel();
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                });
*/
                            //


                            /*String token = "Bearer " + SessionManager.getKEY();
                            Log.d("token", token);
//        final ProgressDialog loading = ProgressDialog.show(getApplicationContext(), "Please wait...", "Uploading data...", false, false);

                            ApiNetworkCall apiService = ApiClient.getApiService();
                            RequestBody Request_year = RequestBody.create(
                                    MediaType.parse("text/plain"),
                                    year);
                            RequestBody Request_color = RequestBody.create(
                                    MediaType.parse("text/plain"),
                                    color);
                            RequestBody Request_no = RequestBody.create(
                                    MediaType.parse("text/plain"),
                                    no);
                            RequestBody Request_brand = RequestBody.create(
                                    MediaType.parse("text/plain"),
                                    brand);
                            RequestBody Request_model = RequestBody.create(
                                    MediaType.parse("text/plain"),
                                    model);
                            RequestBody Req_type = RequestBody.create(
                                    MediaType.parse("text/plain"),
                                    type);
                            RequestBody Req_id = RequestBody.create(
                                    MediaType.parse("text/plain"),
                                    id);


                            MultipartBody.Part fileToUpload;
                            //empty file
                            RequestBody empty_file = RequestBody.create(
                                    MediaType.parse("image/*"),
                                    "");

                            if (finalFile != null) {
                                fileToUpload = MultipartBody.Part.createFormData("car_pic", carFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), finalFile));
                            } else {
                                fileToUpload = MultipartBody.Part.createFormData("car_pic", "", empty_file);
                            }


                            //  MultipartBody.Part carspic = MultipartBody.Part.createFormData("car_pic", carFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), finalFile));


                            Call<ChangePasswordResponse> call = apiService.updateVehicle(token,
                                    Request_year, Request_no,Request_brand,Request_model,Request_color,Req_id,
                                    Req_type,fileToUpload);
                            call.enqueue(new Callback<ChangePasswordResponse>() {
                                @Override
                                public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                                    ChangePasswordResponse requestResponse = response.body();
                                    Toast.makeText(getActivity(), requestResponse.getMessage(), Toast.LENGTH_LONG).show();
                                    loading.cancel();

                                }

                                @Override
                                public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                                    // progressBar.setVisibility(View.GONE);
                                    loading.cancel();
                                    Log.d("Failed", "RetrofitFailed");
                                }
                            });*/
                        }
                    }, new CheckInterface() {
                        @Override
                        public void onCheckClicked(String vehicleDetailsId, boolean isChecked, String status) {
                            if (status.equals("3")) {
                                showSettingsAlert(vehicleDetailsId, isChecked, status);
                            } else {
                                if (isChecked)
                                    activateVehicle = true;
                                else
                                    activateVehicle = false;
                                changeVehicleStatus(vehicleDetailsId, activateVehicle, status);
                            }
                        }
                    });
                    vehicleRecyclerView.setAdapter(vehicleAdapter);

                    vehicleAdapter.notifyDataSetChanged();

                    getIdentity_details();

                } else {
                    Toast.makeText(getActivity(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    loading.cancel();
                }
            }

            @Override
            public void onFailure(Call<GetProfile> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                loading.cancel();
            }
        });
    }

    //show Setting alert
    public void showSettingsAlert(String id, boolean isChecked, String status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());

        // Setting Dialog Title
        alertDialog.setTitle("Delete Vehicle");

        // Setting Dialog Message
        alertDialog.setMessage("Do you want to delete this vehicle?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                changeVehicleStatus(id, isChecked, status);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
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

                CountryCode countryCode = new CountryCode();
                countryCode.setName(name);
                countryCode.setCode(code);
                countryCode.setPhone_code(phoneCode);
                countryCodeList.add(countryCode);
            }

        } catch (Resources.NotFoundException | IOException | JSONException e) {
            e.printStackTrace();
        }

        return countryCodeList;
    }

}
