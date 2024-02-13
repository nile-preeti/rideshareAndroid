package com.ridesharedriver.app.acitivities;

import static com.ridesharedriver.app.pojo.Global.checkRotationFromCamera;
import static com.ridesharedriver.app.pojo.Global.getImageOrientation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ridesharedriver.app.R;
import com.ridesharedriver.app.Server.Server;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.custom.camera.CameraRotation;
import com.ridesharedriver.app.pojo.changepassword.ChangePasswordResponse;
import com.ridesharedriver.app.pojo.getVehicleDetails.VehicleDetails;
import com.ridesharedriver.app.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ridesharedriver.app.utils.RealPathUtil;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

//Edit Details
public class EditDetails extends AppCompatActivity {

    private static final int IMAGE_RESULT = 200;
    private static final int PICK_IMAGE = 1;
    private static final String EQUAL_8 = "8";
    private static final String ABOVE_8 = "Above 8";

    String[] permissionsFile = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final String IMAGE_DIRECTORY = "/OcoryDriver";
    private InputStream inputStream;
    private EditText vehicle_color, vehicle_num;
    private File finalFile, carRegistrationFinalFile, carFinalImage, licenceFinalFile, inspectionFinalFile;
    private CheckBox chk_wifi, chk_tv, chk_luxury_seats;
    private RelativeLayout rl_premium_facilities_layout;
    private ImageView insurance_img, car_reg_img, car_img, licence_img, inspection_img;
    private TextView insurance_issue_date, insurance_expire_date, car_issue_date, car_expiry_date, licence_issue_date, licence_expire_date, inspection_issue_date, inspection_expire_date;

    Spinner brand_spinner, model_spinner, year_spinner, type_spinner, seat_spinner;
    String savedYear, savedModel, savedMake, savedVehicleType, savedSeatNumber, insuranceIssueDate = "", insuranceExpireDate = "", carIssueDate = "", carExpireDate = "",
            inspectionIssueDate = "", inspectionExpireDate = "",
            savedPremiumFacility = "", licenceIssueDate = "", licenceExpireDate = "";
    private Toolbar toolbar;
    //update details
    private String year, color, vehicleNum, brandName, modelName, vehicletype, vehicleId, fileType = "";
    String brandId = "", brandVal = "", modelId = "", modelVal = "", typeId = "", typeVal = "", typeRate = "", yearVal = "", seat = "";

    private AppCompatButton update_btn;
    ArrayList<String> brand_names = new ArrayList<>();
    ArrayList<String> brand_id = new ArrayList<>();
    ArrayList<String> model_names = new ArrayList<>();
    ArrayList<String> model_id = new ArrayList<>();
    ArrayList<String> type_names = new ArrayList<>();
    ArrayList<String> type_id = new ArrayList<>();
    ArrayList<String> type_rate = new ArrayList<>();
    ArrayList<String> years = new ArrayList<String>();
    ArrayList<String> seatNumbers = new ArrayList<String>();
    String categoryId;
    SwipeRefreshLayout swipeRefreshLayout;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri insuranceUri,inspectionUri,car_registrationUri,car_imgUri,licenseUri;
    private String insurance_path = "", inspection_path = "", car_registrationUri_path = "",
            car_img_path = "",lic_path = "" ;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_details_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //premium facilities starts
        chk_wifi = findViewById(R.id.chk_wifi);
        chk_luxury_seats = findViewById(R.id.chk_luxury_seats);
        chk_tv = findViewById(R.id.chk_tv);
        rl_premium_facilities_layout = findViewById(R.id.premium_facilities_layout);

        //premium facilities ends

        vehicle_color = (EditText) findViewById(R.id.vehicle_color);
        vehicle_num = (EditText) findViewById(R.id.vehicle_num);
        update_btn = (AppCompatButton) findViewById(R.id.update_btn);
        brand_spinner = findViewById(R.id.brand_spinner);
        model_spinner = findViewById(R.id.model_spinner);
        year_spinner = findViewById(R.id.year_spinner);
        type_spinner = findViewById(R.id.type_spinner);
        seat_spinner = findViewById(R.id.seat_no_spinner);
        insurance_img = findViewById(R.id.insurance_img);
        car_reg_img = findViewById(R.id.car_registration);
        car_issue_date = findViewById(R.id.car_registration_issue_date);
        car_expiry_date = findViewById(R.id.car_registration_expire_date);

        car_img = findViewById(R.id.car_img);
        insurance_issue_date = findViewById(R.id.insurance_issue_date);
        insurance_expire_date = findViewById(R.id.insurance_expire_date);
        licence_img = findViewById(R.id.licence_img);
        licence_issue_date = findViewById(R.id.licence_issue_date);
        licence_expire_date = findViewById(R.id.licence_expire_date);

        //inspection
        inspection_issue_date = findViewById(R.id.car_inspection_issue_date);
        inspection_expire_date = findViewById(R.id.car_inspection_expire_date);
        inspection_img = findViewById(R.id.vehicle_inspection_img);
        //inspection

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        licenceFinalFile = new File("");
        finalFile = new File("");
        inspectionFinalFile = new File("");
        carRegistrationFinalFile = new File("");
        carFinalImage = new File("");
        String token = "Bearer " + SessionManager.getKEY();
        Intent intent = getIntent();
        vehicleId = intent.getStringExtra("vehicle_id");

        if (!vehicleId.isEmpty() && vehicleId != null) {
            showVehicleDetails(token, vehicleId);
        }

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        fileUri = data.getData();
                        String path = fileUri.getPath();
                        if (!path.isEmpty()) {
                            file = new File(path);
                            switch (fileType) {
                                case "insurance_file":
                                    insuranceUri = data.getData();
                                    Glide.with(EditDetails.this).load(insuranceUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(insurance_img);
                                    insurance_path = insuranceUri.getPath();
                                    break;

                                case "inspection_file":
                                    inspectionUri = data.getData();
                                    Glide.with(EditDetails.this).load(inspectionUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(inspection_img);
                                    inspection_path = inspectionUri.getPath();
                                    break;

                                case "car_registration":
                                    car_registrationUri = data.getData();
                                    Glide.with(EditDetails.this).load(car_registrationUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(car_reg_img);
                                    car_registrationUri_path = car_registrationUri.getPath();
                                    break;
                                case "car_img":
                                    car_imgUri = data.getData();
                                    Glide.with(EditDetails.this).load(car_imgUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(car_img);
                                    car_img_path = car_imgUri.getPath();
                                    break;
                                case "licence":
                                    licenseUri = data.getData();
                                    Glide.with(EditDetails.this).load(licenseUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(licence_img);
                                    lic_path = licenseUri.getPath();
                                    break;

                            }
                        }

                    } else {
                        switch (fileType) {
                            case "insurance_file":
                                insuranceUri = fileUri;
                                Glide.with(EditDetails.this).load(insuranceUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(insurance_img);
                                insurance_path = insuranceUri.getPath();
                                break;
                            case "inspection_file":
                                inspectionUri = fileUri;
                                Glide.with(EditDetails.this).load(inspectionUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(inspection_img);
                                inspection_path = inspectionUri.getPath();
                                break;
                            case "car_registration":
                                car_registrationUri = fileUri;
                                Glide.with(EditDetails.this).load(car_registrationUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(car_reg_img);
                                car_registrationUri_path = car_registrationUri.getPath();
                                break;
                            case "car_img":
                                car_imgUri = fileUri;
                                Glide.with(EditDetails.this).load(car_imgUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(car_img);
                                car_img_path = car_imgUri.getPath();
                                break;
                            case "licence":
                                licenseUri = fileUri;
                                Glide.with(EditDetails.this).load(licenseUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(licence_img);
                                lic_path = licenseUri.getPath();
                                break;
                        }
                    }
                }


            }
        });


        Calendar cal = Calendar.getInstance();
        insurance_issue_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(this)
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
                    .context(this)
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
                    .minDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .build()
                    .show();
        });

        inspection_issue_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(this)
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
                    .maxDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .minDate(1900, 0, 1)
                    .build()
                    .show();
        });

        inspection_expire_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(this)
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
                    .minDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .build()
                    .show();
        });

        car_issue_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(this)
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


        car_expiry_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(this)
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

        licence_issue_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(this)
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
                    .context(this)
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


        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = vehicle_color.getText().toString().trim();
                vehicleNum = vehicle_num.getText().toString().trim();
                if (validate()) {
                    updateVehicleDetails();
                }

            }
        });
        insurance_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileType = "insurance_file";
                showPictureDialog();

            }
        });

        inspection_img.setOnClickListener(e -> {
            fileType = "inspection_file";
            showPictureDialog();

        });

        car_reg_img.setOnClickListener(e -> {
            fileType = "car_registration";
            showPictureDialog();
        });

        car_img.setOnClickListener(e -> {
            fileType = "car_img";
            showPictureDialog();
//            Toast.makeText(this, "Not editable", Toast.LENGTH_SHORT).show();
        });

        licence_img.setOnClickListener(e -> {
            fileType = "licence";
            showPictureDialog();
        });
    }


    //Brand Details
    public void getBrand_details() {
        RequestParams params = new RequestParams();
        Server.get("getCategory", params, new JsonHttpResponseHandler() {

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

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EditDetails.this, R.layout.spinner_item, brand_names);
                        brand_spinner.setAdapter(dataAdapter);
                        if (savedMake != null) {
                            int spinnerPosition = dataAdapter.getPosition(savedMake);
                            brand_spinner.setSelection(spinnerPosition);
                        }

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

                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(EditDetails.this, R.layout.spinner_item, brand_names);
                        brand_spinner.setAdapter(dataAdapter1);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("Error", responseString.toString());
            }
        });
    }

    //getModel details
    public void getModel_details(String brandId) {
        RequestParams params = new RequestParams();
        params.put("category_id", brandId);
        Server.post("getSubcategory", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    model_names.clear();
                    model_id.clear();
                    seatNumbers.clear();
                    if (response.has("status") && response.getBoolean("status")) {
                        model_names.add("Select Vehicle Model");

                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            model_names.add(jsonObject.getString("model_name"));
                            model_id.add(jsonObject.getString("id"));
                            seatNumbers.add(jsonObject.getString("seat"));
                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EditDetails.this, R.layout.spinner_item, model_names);
                        model_spinner.setAdapter(dataAdapter);
                        if (savedModel != null) {
                            int spinnerPosition = dataAdapter.getPosition(savedModel);
                            model_spinner.setSelection(spinnerPosition);
                        }

                        model_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                                if (!model_spinner.getItemAtPosition(position).equals("Select Vehicle Model")) {
                                    modelId = (model_id.get(position - 1));
                                    modelVal = adapterView.getItemAtPosition(position).toString();

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditDetails.this, R.layout.spinner_item, seatNumbers);
                                    seat_spinner.setAdapter(adapter);
                                    seat_spinner.setSelection(position - 1);
                                    seat_spinner.setEnabled(false);
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

                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(EditDetails.this, R.layout.spinner_item, model_names);
                        model_spinner.setAdapter(dataAdapter1);
                    }
                } catch (JSONException e) {

                }
            }
        });
    }

    //years details
    public void getYear_details() {
        years.clear();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add("Select Vehicle Year");
        for (int i = thisYear; i > thisYear - 10; i--) {
            years.add(Integer.toString(i));
        }


        //years adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, years);
        year_spinner.setAdapter(adapter);
        if (savedYear != null) {
            int spinnerPosition = adapter.getPosition(savedYear);
            year_spinner.setSelection(spinnerPosition);
        }

        //year spinner
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

    public void getType_details() {
        RequestParams params = new RequestParams();
        Server.post("getVehicleType", params, new JsonHttpResponseHandler() {

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

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EditDetails.this, R.layout.spinner_item, type_names);
                        type_spinner.setAdapter(dataAdapter);

                        type_spinner.setAdapter(dataAdapter);
                        if (savedVehicleType != null) {
                            int spinnerPosition = dataAdapter.getPosition(savedVehicleType);
                            type_spinner.setSelection(spinnerPosition);
                        }

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

                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(EditDetails.this, R.layout.spinner_item, type_names);
                        type_spinner.setAdapter(dataAdapter1);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(EditDetails.this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

        });
    }

    //sheat number
    public void getSeat_nos() {
        seatNumbers.clear();
        seatNumbers.add("Select Number Of Seats");
        seatNumbers.add("4");
        seatNumbers.add("5");
        seatNumbers.add("6");
        seatNumbers.add("7");
        seatNumbers.add("8");
        seatNumbers.add("Above 8");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, seatNumbers);
        seat_spinner.setAdapter(adapter);
        try {
            if (savedSeatNumber != null) {
                if (savedPremiumFacility == null)
                    savedPremiumFacility = "";
                if (savedSeatNumber.equalsIgnoreCase("9") && !savedPremiumFacility.isEmpty()) {

                    int spinnerPosition = adapter.getPosition("8");
                    seat_spinner.setSelection(spinnerPosition);
                    rl_premium_facilities_layout.setVisibility(View.VISIBLE);
                } else if (savedSeatNumber.equalsIgnoreCase("9")) {
                    int spinnerPosition = adapter.getPosition(ABOVE_8);
                    seat_spinner.setSelection(spinnerPosition);
                    rl_premium_facilities_layout.setVisibility(View.VISIBLE);
                    rl_premium_facilities_layout.setVisibility(View.GONE);
                } else {
                    int spinnerPosition = adapter.getPosition(savedSeatNumber);
                    seat_spinner.setSelection(spinnerPosition);
                    rl_premium_facilities_layout.setVisibility(View.GONE);
                }
            }
        } catch (Exception ex) {

        }
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


    //vechical Details dialog
    public void showVehicleDetails(String token, String vehicleId) {
        final ProgressDialog loading = ProgressDialog.show(this,
                "Please wait...", "Fetching Data...", false, false);

        ApiNetworkCall apiService = ApiClient.getApiService();
        Call<VehicleDetails> call = apiService.getVehicleDetails(token, vehicleId);
        call.enqueue(new Callback<VehicleDetails>() {
            @Override
            public void onResponse(Call<VehicleDetails> call, retrofit2.Response<VehicleDetails> response) {
                loading.cancel();
                VehicleDetails jsonResponse = response.body();
                if (jsonResponse.getStatus()) {
                    vehicle_color.setText(jsonResponse.getData().getColor());
                    vehicle_num.setText(jsonResponse.getData().getVehicleNo());
                    savedYear = jsonResponse.getData().getYear();
                    savedMake = jsonResponse.getData().getVehicleType();
                    savedModel = jsonResponse.getData().getModelName();
                    savedSeatNumber = jsonResponse.getData().getSeatNo();
                    savedPremiumFacility = jsonResponse.getData().getPremiumFacility();
                    categoryId = jsonResponse.getData().getBrandId();

                    if (savedPremiumFacility != null) {
                        if (savedPremiumFacility.toLowerCase().contains("wi-fi")) {
                            chk_wifi.setChecked(true);
                        }
                        if (savedPremiumFacility.toLowerCase().contains("luxury seats")) {
                            chk_luxury_seats.setChecked(true);
                        }
                        if (savedPremiumFacility.toLowerCase().contains("t.v.")) {
                            chk_tv.setChecked(true);
                        }
                    }
                    insuranceIssueDate = jsonResponse.getData().getInsuranceIssueDate();
                    insurance_issue_date.setText(insuranceIssueDate);

                    insuranceExpireDate = jsonResponse.getData().getInsuranceExpiryDate();
                    insurance_expire_date.setText(insuranceExpireDate);

                    try {
                        inspectionIssueDate = jsonResponse.getData().getInspectionIssueDate();
                        inspection_issue_date.setText(inspectionIssueDate);
                        inspectionExpireDate = jsonResponse.getData().getInspectionExpireDate();
                        inspection_expire_date.setText(inspectionExpireDate);
                    } catch (Exception ex) {
                        inspectionIssueDate = "";
                        inspectionExpireDate = "";
                    }

                    try {
                        carIssueDate = jsonResponse.getData().getCarIssueDate();
                        car_issue_date.setText(carIssueDate);
                        carExpireDate = jsonResponse.getData().getCarExpiryDate();
                        car_expiry_date.setText(carExpireDate);
                    } catch (Exception ex) {

                    }
                    try {
                        licenceIssueDate = jsonResponse.getData().getLicenceIssueDate();
                        licence_issue_date.setText(licenceIssueDate);
                        licenceExpireDate = jsonResponse.getData().getLicenceExpireDate();
                        licence_expire_date.setText(licenceExpireDate);
                    } catch (Exception ex) {

                    }


                    try {
                        Glide.with(EditDetails.this).load(jsonResponse.getData().getInsuranceDoc())
                                .apply(new RequestOptions().error(R.drawable.ic_upload_pic).placeholder(R.drawable.loader)).into(insurance_img);
                    } catch (Exception e) {

                    }
                    try {
                        Glide.with(EditDetails.this).load(jsonResponse.getData().getInspectionDocument())
                                .apply(new RequestOptions().error(R.drawable.ic_upload_pic).placeholder(R.drawable.loader)).into(inspection_img);
                    } catch (Exception e) {

                    }

                    try {
                        Glide.with(EditDetails.this).load(jsonResponse.getData().getCarRegistrationDoc()).apply(new RequestOptions().error(R.drawable.ic_upload_pic).placeholder(R.drawable.loader)).into(car_reg_img);
                    } catch (Exception e) {

                    }
                    try {
                        Glide.with(EditDetails.this).load(jsonResponse.getData().getCarPic()).apply(new RequestOptions().error(R.drawable.ic_upload_pic).placeholder(R.drawable.loader)).into(car_img);
                    } catch (Exception e) {

                    }
                    try {
                        Glide.with(EditDetails.this).load(jsonResponse.getData().getLicenseDoc()).apply(new RequestOptions().error(R.drawable.ic_upload_pic).placeholder(R.drawable.loader)).into(licence_img);
                    } catch (Exception e) {

                    }
                    getBrand_details();
//                    getYear_details();
                    getYear_Details(categoryId);
                    getSeat_nos();
                    // getType_details();
                } else {
                    Toast.makeText(EditDetails.this, "Something went wrong, Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VehicleDetails> call, Throwable t) {
                loading.cancel();
                Log.d("Failed", "RetrofitFailed");
            }
        });
    }

    //update vechical details
    public void updateVehicleDetails() {
        final ProgressDialog loading = ProgressDialog.show(this, "", "Please wait...", false, false);
        String token = "Bearer " + SessionManager.getKEY();

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
        RequestBody Request_color = RequestBody.create(
                MediaType.parse("text/plain"),
                color);
        RequestBody Request_no = RequestBody.create(
                MediaType.parse("text/plain"),
                vehicleNum);
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
        RequestBody Req_id = RequestBody.create(
                MediaType.parse("text/plain"),
                vehicleId);
        RequestBody Req_insurance_issue = RequestBody.create(
                MediaType.parse("text/plain"),
                insuranceIssueDate);
        RequestBody Req_insurance_expire = RequestBody.create(
                MediaType.parse("text/plain"),
                insuranceExpireDate);
        RequestBody Req_car_issue = RequestBody.create(
                MediaType.parse("text/plain"),
                carIssueDate);
        RequestBody Req_car_expire = RequestBody.create(
                MediaType.parse("text/plain"),
                carExpireDate);
        RequestBody Req_licence_issue = RequestBody.create(
                MediaType.parse("text/plain"),
                licenceIssueDate);
        RequestBody Req_licence_expire = RequestBody.create(
                MediaType.parse("text/plain"),
                licenceExpireDate);

        RequestBody Req_inspection_issue = RequestBody.create(
                MediaType.parse("text/plain"),
                inspectionIssueDate);
        RequestBody Req_inspection_expire = RequestBody.create(
                MediaType.parse("text/plain"),
                inspectionExpireDate);

        RequestBody Req_car_facilities = RequestBody.create(
                MediaType.parse("text/plain"),
                premiumFacilities);

        MultipartBody.Part fileToUpload, carFileToUpload, carPicFileToUpload, licencePicUpload, inspectionFileToUpload;
        //empty file
        RequestBody empty_file = RequestBody.create(
                MediaType.parse("image/*"),
                "");


        if (!insurance_path.isEmpty()) {
            // fileToUpload = MultipartBody.Part.createFormData("insurance", finalFile.getName(), RequestBody.create(MediaType.parse("image/*"), finalFile));

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(EditDetails.this.getContentResolver(), insuranceUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final int rotation = getImageOrientation(insurance_path);
            bitmap = checkRotationFromCamera(bitmap, rotation);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imageBytes = outputStream.toByteArray();


            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            fileToUpload = MultipartBody.Part.createFormData("insurance", "insurance.jpg", requestBody);

        } else {
            fileToUpload = MultipartBody.Part.createFormData("insurance", "", empty_file);
        }

        if (!inspection_path.isEmpty()) {
            //inspectionFileToUpload = MultipartBody.Part.createFormData("inspection_document", inspectionFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), inspectionFinalFile));

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(EditDetails.this.getContentResolver(), inspectionUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final int rotation = getImageOrientation(inspection_path);
            bitmap = checkRotationFromCamera(bitmap, rotation);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imageBytes = outputStream.toByteArray();


            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            inspectionFileToUpload = MultipartBody.Part.createFormData("inspection_document", "inspection.jpg", requestBody);

        } else {
            inspectionFileToUpload = MultipartBody.Part.createFormData("inspection_document", "", empty_file);
        }

        if (!car_registrationUri_path.isEmpty()) {
            //  carFileToUpload = MultipartBody.Part.createFormData("car_registration", carRegistrationFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), carRegistrationFinalFile));

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(EditDetails.this.getContentResolver(), car_registrationUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final int rotation = getImageOrientation(car_registrationUri_path);
            bitmap = checkRotationFromCamera(bitmap, rotation);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imageBytes = outputStream.toByteArray();


            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            carFileToUpload = MultipartBody.Part.createFormData("car_registration", "carRegistration.jpg", requestBody);

        } else {
            carFileToUpload = MultipartBody.Part.createFormData("car_registration", "", empty_file);
        }

        if (!car_img_path.isEmpty()) {
            //carPicFileToUpload = MultipartBody.Part.createFormData("car_pic", carFinalImage.getName(), RequestBody.create(MediaType.parse("image/*"), carFinalImage));


            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(EditDetails.this.getContentResolver(), car_imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final int rotation = getImageOrientation(car_img_path);
            bitmap = checkRotationFromCamera(bitmap, rotation);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            carPicFileToUpload = MultipartBody.Part.createFormData("car_pic", "carImage.jpg", requestBody);

        } else {
            carPicFileToUpload = MultipartBody.Part.createFormData("car_pic", "", empty_file);
        }

        if (!lic_path.isEmpty()) {
            //  licencePicUpload = MultipartBody.Part.createFormData("license", licenceFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), licenceFinalFile));

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(EditDetails.this.getContentResolver(), licenseUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final int rotation = getImageOrientation(lic_path);
            bitmap = checkRotationFromCamera(bitmap, rotation);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imageBytes = outputStream.toByteArray();


            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            licencePicUpload = MultipartBody.Part.createFormData("license", "license.jpg", requestBody);

        } else {
            licencePicUpload = MultipartBody.Part.createFormData("license", "", empty_file);
        }


//        if (finalFile != null && finalFile.exists()) {
//            fileToUpload = MultipartBody.Part.createFormData("insurance", finalFile.getName(), RequestBody.create(MediaType.parse("image/*"), finalFile));
//        } else {
//            fileToUpload = MultipartBody.Part.createFormData("insurance", "", empty_file);
//        }
//
//        if (inspectionFinalFile != null && inspectionFinalFile.exists()) {
//            inspectionFileToUpload = MultipartBody.Part.createFormData("inspection_document", inspectionFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), inspectionFinalFile));
//        } else {
//            inspectionFileToUpload = MultipartBody.Part.createFormData("inspection_document", "", empty_file);
//        }
//
//        if (carRegistrationFinalFile != null && carRegistrationFinalFile.exists()) {
//            carFileToUpload = MultipartBody.Part.createFormData("car_registration", carRegistrationFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), carRegistrationFinalFile));
//        } else {
//            carFileToUpload = MultipartBody.Part.createFormData("car_registration", "", empty_file);
//        }
//
//        if (carFinalImage != null && carFinalImage.exists()) {
//            carPicFileToUpload = MultipartBody.Part.createFormData("car_pic", carFinalImage.getName(), RequestBody.create(MediaType.parse("image/*"), carFinalImage));
//        } else {
//            carPicFileToUpload = MultipartBody.Part.createFormData("car_pic", "", empty_file);
//        }
//
//
//        if (licenceFinalFile != null && licenceFinalFile.exists()) {
//            licencePicUpload = MultipartBody.Part.createFormData("license", licenceFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), licenceFinalFile));
//        } else {
//            licencePicUpload = MultipartBody.Part.createFormData("license", "", empty_file);
//        }

        Call<ChangePasswordResponse> call = apiService.updateVehicle(token,
                Request_year, Request_no, Request_brand, Request_model, Request_color, Req_id,
                Req_type, Req_seat, Req_car_facilities, fileToUpload, Req_insurance_issue, Req_insurance_expire, carFileToUpload, Req_car_issue, Req_car_expire, carPicFileToUpload,
                Req_licence_issue, Req_licence_expire, licencePicUpload, Req_inspection_issue, Req_inspection_expire, inspectionFileToUpload);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                ChangePasswordResponse requestResponse = response.body();
                assert requestResponse != null;
                Toast.makeText(EditDetails.this, requestResponse.getMessage(), Toast.LENGTH_LONG).show();
                loading.cancel();
                Intent intent = new Intent();
                intent.putExtra("getprofile", "callme");
                setResult(RESULT_OK, intent);
                finish();

            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                // progressBar.setVisibility(View.GONE);
                loading.cancel();
                Log.d("Failed", "RetrofitFailed");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_RESULT && resultCode == RESULT_OK) {
            switch (fileType) {
                case "insurance_file":
                    insuranceUri = fileUri;
                    Glide.with(this).load(insuranceUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(insurance_img);
                    insurance_path = insuranceUri.getPath();
                    break;
                case "inspection_file":
                    inspectionUri = fileUri;
                    Glide.with(this).load(inspectionUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(inspection_img);
                    inspection_path = inspectionUri.getPath();
                    break;
                case "car_registration":
                    car_registrationUri = fileUri;
                    Glide.with(this).load(car_registrationUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(car_reg_img);
                    car_registrationUri_path = car_registrationUri.getPath();
                    break;
                case "car_img":
                    car_imgUri = fileUri;
                    Glide.with(this).load(car_imgUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(car_img);
                    car_img_path = car_imgUri.getPath();
                    break;
                case "licence":
                    licenseUri = fileUri;
                    Glide.with(this).load(licenseUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(licence_img);
                    lic_path = licenseUri.getPath();
                    break;
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
                Bitmap bitmap = bmp;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                switch (fileType) {
                    case "insurance_file":
                        Glide.with(this).asBitmap().load(bmp).into(insurance_img);
                        String insurance_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        finalFile = new File(insurance_path);
                        break;

                    case "inspection_file":
                        Glide.with(this).asBitmap().load(bmp).into(inspection_img);
                        String inspection_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        inspectionFinalFile = new File(inspection_path);
                        break;
                    case "car_registration":
                        Glide.with(this).asBitmap().load(bmp).into(car_reg_img);
                        String car_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        carRegistrationFinalFile = new File(car_path);
                        break;

                    case "car_img":
                        Glide.with(this).asBitmap().load(bmp).into(car_img);
                        String car_img_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        carFinalImage = new File(car_img_path);
                        break;
                    case "licence":
                        Glide.with(this).asBitmap().load(bmp).into(licence_img);
                        String licence_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        licenceFinalFile = new File(licence_path);
                        break;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }


    public File compressImage1(String imageUrl) {
        int compressionRatio = 2; //1 == originalImage, 2 = 50% compression, 4=25% compress
        File file = new File(imageUrl);
        try {
            BitmapFactory.Options Options = new BitmapFactory.Options();
            Options.inSampleSize = 4;
            Options.inJustDecodeBounds = false;
//            Bitmap bitmap = BitmapFactory.decodeFile (file.getPath (), Options);
            Bitmap bitmap = CameraRotation.handleSamplingAndRotationBitmap(this, Uri.parse(imageUrl));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, new FileOutputStream(file));
            return file;
        } catch (Throwable t) {
            Log.e("ERROR", "Error compressing file." + t.toString());
            t.printStackTrace();
            return file;
        }
    }

    public File compressImage(String imageUrl) {
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

    File file;
    Uri fileUri;

    private void takePhotoFromCamera() {
        //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, MyFileContentProvider.CONTENT_URI);
        file = new File(getExternalCacheDir(),
                String.valueOf(System.currentTimeMillis()) + ".jpg");
        fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        galleryLauncher.launch(intent);
//        startActivityForResult(intent, IMAGE_RESULT);
    }


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

    public Boolean validate() {
        Boolean value = true;

        if (brandId.equalsIgnoreCase("")) {
            Toast.makeText(EditDetails.this, "Please Select Vehicle Make", Toast.LENGTH_LONG).show();
            return false;
        } else if (modelId.equalsIgnoreCase("")) {
            Toast.makeText(EditDetails.this, "Please Select Vehicle Model", Toast.LENGTH_LONG).show();
            return false;
        } else if (yearVal.equalsIgnoreCase("")) {
            Toast.makeText(EditDetails.this, "Please Select Vehicle Year", Toast.LENGTH_LONG).show();
            return false;
        } else if (vehicle_color.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(EditDetails.this, "Please Enter Vehicle Color", Toast.LENGTH_LONG).show();
            return false;
        } else if (vehicle_num.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(EditDetails.this, "Please Enter Car Number Plate", Toast.LENGTH_LONG).show();
            return false;
        }
//        else if (fileType.equalsIgnoreCase("car_img") && carFinalImage.getName().isEmpty() || carFinalImage.getName().equalsIgnoreCase("")) {
//            Toast.makeText(EditDetails.this, "Please upload car image", Toast.LENGTH_LONG).show();
//            return false;
//        } else if (fileType.equalsIgnoreCase("licence") && licenceFinalFile.getName().isEmpty() || licenceFinalFile.getName().equalsIgnoreCase("")) {
//            Toast.makeText(EditDetails.this, "Please upload Driving Licence image", Toast.LENGTH_LONG).show();
//            return false;
//        } else if (fileType.equalsIgnoreCase("inspection_file") && inspectionFinalFile.getName().isEmpty() || inspectionFinalFile.getName().equalsIgnoreCase("")) {
//            Toast.makeText(EditDetails.this, "Please upload Insurance image", Toast.LENGTH_LONG).show();
//            return false;
//        } else if (fileType.equalsIgnoreCase("insurance_file") && finalFile.getName().isEmpty() || finalFile.getName().equalsIgnoreCase("")) {
//            Toast.makeText(EditDetails.this, "Please upload Driving Licence image", Toast.LENGTH_LONG).show();
//            return false;
//        } else if (fileType.equalsIgnoreCase("car_registration") && carRegistrationFinalFile.getName().isEmpty() || carRegistrationFinalFile.getName().equalsIgnoreCase("")) {
//            Toast.makeText(EditDetails.this, "Please upload Driving Licence image", Toast.LENGTH_LONG).show();
//            return false;
//        }
//        else if (licence_issue_date.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(EditDetails.this, "Please select licence issue Date", Toast.LENGTH_SHORT).show();
//            return false;
//        } else if (licence_expire_date.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(EditDetails.this, "Please select licence expire Date", Toast.LENGTH_SHORT).show();
//            return false;
//        } else if (inspection_issue_date.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(EditDetails.this, "Please select inspection issue Date", Toast.LENGTH_SHORT).show();
//            return false;
//        } else if (inspection_expire_date.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(EditDetails.this, "Please select inspection expire Date", Toast.LENGTH_SHORT).show();
//            return false;
//        }else if (insurance_issue_date.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(EditDetails.this, "Please select insurance issue Date", Toast.LENGTH_SHORT).show();
//            return false;
//        } else if (insurance_expire_date.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(EditDetails.this, "Please select insurance expire Date", Toast.LENGTH_SHORT).show();
//            return false;
//        }else if (car_issue_date.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(EditDetails.this, "Please select registration issue Date", Toast.LENGTH_SHORT).show();
//            return false;
//        } else if (car_expiry_date.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(EditDetails.this, "Please select registration expire Date", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        return value;
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

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditDetails.this, R.layout.spinner_item, years);
                        year_spinner.setAdapter(adapter);

                        if (savedYear != null) {
                            int spinnerPosition = adapter.getPosition(savedYear);
                            year_spinner.setSelection(spinnerPosition);
                        }

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

                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(EditDetails.this, R.layout.spinner_item, years);
                        year_spinner.setAdapter(dataAdapter1);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(EditDetails.this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
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

}