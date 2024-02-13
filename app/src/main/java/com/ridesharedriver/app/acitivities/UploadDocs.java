package com.ridesharedriver.app.acitivities;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ridesharedriver.app.R;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.custom.CheckConnection;
import com.ridesharedriver.app.custom.camera.CameraRotation;
import com.ridesharedriver.app.databinding.ActivityUploadDocsBinding;
import com.ridesharedriver.app.databinding.UploadDocumentBinding;
import com.ridesharedriver.app.fragement.HomeFragment;
import com.ridesharedriver.app.pojo.changepassword.ChangePasswordResponse;
import com.ridesharedriver.app.pojo.getprofile.GetProfile;
import com.ridesharedriver.app.pojo.profileresponse.VehicleDetail;
import com.ridesharedriver.app.pojo.upload_docs.UploadDocsResponse;
import com.ridesharedriver.app.session.SessionManager;
import com.ridesharedriver.app.utils.RealPathUtil;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

//Uploade Documents
public class UploadDocs extends AppCompatActivity implements HomeFragment.PassDataToActivity {

    public static final int PICK_IMAGE = 1;
    private String fileType = "";
    private final static int IMAGE_RESULT = 200;

    private String brandId = "", modelId = "", typeId = "", yearVal = "", seat = "", color = "", vehicleNum = "";

    String[] permissionsFile = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final String IMAGE_DIRECTORY = "/RideShareDriver";
    private ActivityUploadDocsBinding binding;
    private File insurenceFinalFile, carRegistrationFinalFile, drivingLicenceFile, inspectionFinalFile;
    private String insuranceIssueDate = "", insuranceExpireDate = "", licenceIssueDate = "", licenceExpireDate = "", carIssueDate = "", carExpireDate = "", inspectionIssueDate = "", inspectionExpireDate = "";
    private InputStream inputStream;
    private String vehicleDetailId = "";
    private boolean[] docStatus;
    private boolean isFromWelcome = false;

    private ActivityResultLauncher<Intent> galleryLauncher;
    File file;
    Uri fileUri, insurenceUri, carRegistrationUri,inspectionUri,licenceUri;

    String  insurance_path="", lic_path="",car_reg_path="",inspection_path="";

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadDocsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.toolbar.setTitle("Upload Docs");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        Intent intent = getIntent();
        docStatus = new boolean[4];
        if (intent != null) {
            isFromWelcome = intent.getBooleanExtra("welcome", false);
            docStatus[0] = intent.getBooleanExtra("licence", true);
            docStatus[1] = intent.getBooleanExtra("insurance", true);
            docStatus[2] = intent.getBooleanExtra("car", true);
            docStatus[3] = intent.getBooleanExtra("inspection", true);
        }

        new HomeFragment().setOnPassDataToActivity(this);

        setView();
        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            if (!isFromWelcome)
                getProfile();

        } else {
            Toast.makeText(this, getString(R.string.network), Toast.LENGTH_LONG).show();
        }

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
                                        insurenceUri = fileUri;
                                        Glide.with(UploadDocs.this).load(insurenceUri).into(binding.insuranceImg);
                                        insurance_path = insurenceUri.getPath();
                                        break;

                                    case "inspection_file":
                                        inspectionUri = fileUri;
                                        Glide.with(UploadDocs.this).load(inspectionUri).into(binding.vehicleInspectionImg);
                                        inspection_path = inspectionUri.getPath();
                                        break;
                                    case "licence_file":
                                        licenceUri = fileUri;
                                        Glide.with(UploadDocs.this).load(licenceUri).into(binding.licenceImg);
                                        lic_path = licenceUri.getPath();
                                        break;
                                    case "car_registration_file":
                                        carRegistrationUri = fileUri;
                                        Glide.with(UploadDocs.this).load(carRegistrationUri).into(binding.carRegistration);
                                        car_reg_path = carRegistrationUri.getPath();
                                        break;

                                }


                            }


                        }


                    }
                });

        binding.submitTxt.setOnClickListener(e -> {

            if (isFromWelcome) {
                if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                    if (docStatus[0]) {
                        uploadDrivingLicence();
                    }
                    if (docStatus[1]) {
                        uploadInsurance();
                    }
                    if (docStatus[2]) {
                        uploadCarReg();
                    }
                    if (docStatus[3]) {
                        uploadInspectionDoc();
                    }

                } else {
                    Toast.makeText(this, getString(R.string.network), Toast.LENGTH_LONG).show();
                }
            } else {
                if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                    updateVehicleDetails();

                } else {
                    Toast.makeText(this, getString(R.string.network), Toast.LENGTH_LONG).show();
                }
            }

        });


    }


    //getting Profile
    public void getProfile() {
        final ProgressDialog loading = ProgressDialog.show(this,
                "", "Please wait...", false, false);
        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<GetProfile> call = apiService.getProfile("Bearer " + SessionManager.getKEY());
        call.enqueue(new Callback<GetProfile>() {
            @Override
            public void onResponse(Call<GetProfile> call, retrofit2.Response<GetProfile> response) {
                GetProfile jsonResponse = response.body();
                if (jsonResponse != null) {
                    if (jsonResponse.getStatus()) {
                        List<VehicleDetail> vehicleDetails = jsonResponse.getData().getVehicleDetail();
                        for (VehicleDetail vehicleDetail : vehicleDetails) {

                            if (vehicleDetail.getStatus().equals("1")) {
                                vehicleDetailId = vehicleDetail.getVehicleDetailId();
                                yearVal = vehicleDetail.getYear();
                                color = vehicleDetail.getColor();
                                vehicleNum = vehicleDetail.getVehicleNo();
                                brandId = vehicleDetail.getBrandId();
                                modelId = vehicleDetail.getModelId();
                                typeId = vehicleDetail.getVehicleTypeId();
                                seat = vehicleDetail.getSeatNo();
                                insuranceIssueDate = vehicleDetail.getInsuranceIssueDate();
                                insuranceExpireDate = vehicleDetail.getInsuranceExpiryDate();
                                if (vehicleDetail.getCarIssueDate() != null)
                                    carIssueDate = vehicleDetail.getCarIssueDate();
                                if (vehicleDetail.getCarExpiryDate() != null)
                                    carExpireDate = vehicleDetail.getCarExpiryDate();
                                if (vehicleDetail.getLicenseIssueDate() != null)
                                    licenceIssueDate = vehicleDetail.getLicenseIssueDate();
                                if (vehicleDetail.getLicenseExpiryDate() != null)
                                    licenceExpireDate = vehicleDetail.getLicenseExpiryDate();
                                if (vehicleDetail.getInspectionIssueDate() != null)
                                    inspectionIssueDate = vehicleDetail.getInspectionIssueDate();
                                if (vehicleDetail.getInspectionExpireDate() != null)
                                    inspectionExpireDate = vehicleDetail.getInspectionExpireDate();
                            }


                        }
                        Log.e("VEHICLE", vehicleDetailId);
                        loading.cancel();
                    } else {
                        loading.cancel();
                        Toast.makeText(UploadDocs.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetProfile> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                loading.cancel();
            }
        });
    }


    //uploding car REgistration files
    public void uploadCarReg() {

        if (car_reg_path.isEmpty()) {
            Toast.makeText(UploadDocs.this, "Please upload car licence image", Toast.LENGTH_LONG).show();
            return;
        } else if (carIssueDate.isEmpty()) {
            Toast.makeText(UploadDocs.this, "Please select car registration issue Date", Toast.LENGTH_SHORT).show();
            return;
        } else if (carExpireDate.isEmpty()) {
            Toast.makeText(UploadDocs.this, "Please select car registration expire Date", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog loading = ProgressDialog.show(this, "", "Uploading....", false, false);
        String token = "Bearer " + SessionManager.getKEY();


        ApiNetworkCall apiService = ApiClient.getApiService();
        RequestBody Req_car_reg_issue = RequestBody.create(
                MediaType.parse("text/plain"),
                carIssueDate);
        RequestBody Req_car_reg_expire = RequestBody.create(
                MediaType.parse("text/plain"),
                carExpireDate);

        RequestBody empty_file = RequestBody.create(
                MediaType.parse("image/*"),
                "");
        MultipartBody.Part carRegFileToUpload;

        if (!car_reg_path.isEmpty()) {
            //carRegFileToUpload = MultipartBody.Part.createFormData("car_registration", carRegistrationFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), carRegistrationFinalFile));

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(UploadDocs.this.getContentResolver(), carRegistrationUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imageBytes = outputStream.toByteArray();


            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            carRegFileToUpload = MultipartBody.Part.createFormData("car_registration", "car_registration.jpg", requestBody);


        } else {
            carRegFileToUpload = MultipartBody.Part.createFormData("car_registration", "", empty_file);
        }


        Call<UploadDocsResponse> call = apiService.uploadCarRegistration(token, carRegFileToUpload, Req_car_reg_issue, Req_car_reg_expire);
        call.enqueue(new Callback<UploadDocsResponse>() {
            @Override
            public void onResponse(Call<UploadDocsResponse> call, retrofit2.Response<UploadDocsResponse> response) {
                UploadDocsResponse requestResponse = response.body();
                assert requestResponse != null;
                Toast.makeText(UploadDocs.this, requestResponse.getMessage(), Toast.LENGTH_LONG).show();
                SessionManager.setCarStatus(true);
                loading.cancel();
                finish();

            }

            @Override
            public void onFailure(Call<UploadDocsResponse> call, Throwable t) {
                // progressBar.setVisibility(View.GONE);
                loading.cancel();
                Log.d("Failed", "RetrofitFailed");
            }
        });

    }

    //Uploading car documents
    public void uploadInspectionDoc() {

        if (inspection_path.isEmpty()) {
            Toast.makeText(UploadDocs.this, "Please upload car inspection image", Toast.LENGTH_LONG).show();
            return;
        } else if (inspectionIssueDate.isEmpty()) {
            Toast.makeText(UploadDocs.this, "Please select car inspection issue Date", Toast.LENGTH_SHORT).show();
            return;
        } else if (inspectionExpireDate.isEmpty()) {
            Toast.makeText(UploadDocs.this, "Please select car inspection expire Date", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog loading = ProgressDialog.show(this, "", "Uploading....", false, false);
        String token = "Bearer " + SessionManager.getKEY();


        ApiNetworkCall apiService = ApiClient.getApiService();
        RequestBody Req_inspection_issue = RequestBody.create(
                MediaType.parse("text/plain"),
                inspectionIssueDate);
        RequestBody Req_inspection_expire = RequestBody.create(
                MediaType.parse("text/plain"),
                inspectionExpireDate);

        RequestBody empty_file = RequestBody.create(
                MediaType.parse("image/*"),
                "");
        MultipartBody.Part carInspectionFileToUpload;

        if (!inspection_path.isEmpty()) {
            //carInspectionFileToUpload = MultipartBody.Part.createFormData("inspection_document", inspectionFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), inspectionFinalFile));


            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(UploadDocs.this.getContentResolver(), inspectionUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imageBytes = outputStream.toByteArray();


            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            carInspectionFileToUpload = MultipartBody.Part.createFormData("inspection_document", "inspection_document.jpg", requestBody);



        } else {
            carInspectionFileToUpload = MultipartBody.Part.createFormData("inspection_document", "", empty_file);
        }


        Call<UploadDocsResponse> call = apiService.uploadInspection(token, carInspectionFileToUpload, Req_inspection_issue, Req_inspection_expire);
        call.enqueue(new Callback<UploadDocsResponse>() {
            @Override
            public void onResponse(Call<UploadDocsResponse> call, retrofit2.Response<UploadDocsResponse> response) {
                UploadDocsResponse requestResponse = response.body();
                assert requestResponse != null;
                Toast.makeText(UploadDocs.this, requestResponse.getMessage(), Toast.LENGTH_LONG).show();
                SessionManager.setInspectionStatus(true);
                loading.cancel();
                finish();

            }

            @Override
            public void onFailure(Call<UploadDocsResponse> call, Throwable t) {
                // progressBar.setVisibility(View.GONE);
                loading.cancel();
                Log.d("Failed", "RetrofitFailed");
            }
        });

    }

    //uploading Driving Licence
    public void uploadDrivingLicence() {
        if (lic_path.isEmpty()) {
            Toast.makeText(UploadDocs.this, "Please upload Driver's License image", Toast.LENGTH_LONG).show();
            return;
        } else if (licenceIssueDate.isEmpty()) {
            Toast.makeText(UploadDocs.this, "Please select licence issue Date", Toast.LENGTH_SHORT).show();
            return;
        } else if (licenceExpireDate.isEmpty()) {
            Toast.makeText(UploadDocs.this, "Please select licence expire Date", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog loading = ProgressDialog.show(this, "", "Uploading....", false, false);
        String token = "Bearer " + SessionManager.getKEY();

        ApiNetworkCall apiService = ApiClient.getApiService();
        RequestBody Req_licence_reg_issue = RequestBody.create(
                MediaType.parse("text/plain"),
                licenceIssueDate);
        RequestBody Req_licence_reg_expire = RequestBody.create(
                MediaType.parse("text/plain"),
                licenceExpireDate);

        RequestBody empty_file = RequestBody.create(
                MediaType.parse("image/*"),
                "");
        MultipartBody.Part licenceRegFileToUpload;

        if (!lic_path.isEmpty()) {
            //licenceRegFileToUpload = MultipartBody.Part.createFormData("license", drivingLicenceFile.getName(), RequestBody.create(MediaType.parse("image/*"), drivingLicenceFile));


            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(UploadDocs.this.getContentResolver(), licenceUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imageBytes = outputStream.toByteArray();


            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            licenceRegFileToUpload = MultipartBody.Part.createFormData("license", "license.jpg", requestBody);


        } else {
            licenceRegFileToUpload = MultipartBody.Part.createFormData("license", "", empty_file);
        }


        Call<UploadDocsResponse> call = apiService.uploadDrivingLicence(token, licenceRegFileToUpload, Req_licence_reg_issue, Req_licence_reg_expire);
        call.enqueue(new Callback<UploadDocsResponse>() {
            @Override
            public void onResponse(Call<UploadDocsResponse> call, retrofit2.Response<UploadDocsResponse> response) {
                UploadDocsResponse requestResponse = response.body();
                assert requestResponse != null;
                Toast.makeText(UploadDocs.this, requestResponse.getMessage(), Toast.LENGTH_LONG).show();
                SessionManager.setLicenceStatus(true);
                loading.cancel();
                finish();

            }

            @Override
            public void onFailure(Call<UploadDocsResponse> call, Throwable t) {
                // progressBar.setVisibility(View.GONE);
                loading.cancel();
                Log.d("Failed", "RetrofitFailed");
            }
        });

    }


    //uploading car Insurance
    public void uploadInsurance() {
        if (insurance_path.isEmpty()) {
            Toast.makeText(UploadDocs.this, "Please upload insurance image", Toast.LENGTH_LONG).show();
            return;
        } else if (insuranceIssueDate.isEmpty()) {
            Toast.makeText(UploadDocs.this, "Please select insurance issue Date", Toast.LENGTH_SHORT).show();
            return;
        } else if (insuranceExpireDate.isEmpty()) {
            Toast.makeText(UploadDocs.this, "Please select insurance expire Date", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog loading = ProgressDialog.show(this, "", "Uploading....", false, false);
        String token = "Bearer " + SessionManager.getKEY();


        ApiNetworkCall apiService = ApiClient.getApiService();
        RequestBody Req_insurance_reg_issue = RequestBody.create(
                MediaType.parse("text/plain"),
                insuranceIssueDate);
        RequestBody Req_insurance_reg_expire = RequestBody.create(
                MediaType.parse("text/plain"),
                insuranceExpireDate);

        RequestBody empty_file = RequestBody.create(
                MediaType.parse("image/*"),
                "");
        MultipartBody.Part insuranceRegFileToUpload;


        if (!insurance_path.isEmpty()) {
            // insuranceRegFileToUpload = MultipartBody.Part.createFormData("insurance", insurenceFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), insurenceFinalFile));
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(UploadDocs.this.getContentResolver(), insurenceUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            insuranceRegFileToUpload = MultipartBody.Part.createFormData("insurance", "insurance.jpg", requestBody);

        } else {
            insuranceRegFileToUpload = MultipartBody.Part.createFormData("insurance", "", empty_file);
        }


        Call<UploadDocsResponse> call = apiService.uploadInsurance(token, insuranceRegFileToUpload, Req_insurance_reg_issue, Req_insurance_reg_expire);
        call.enqueue(new Callback<UploadDocsResponse>() {
            @Override
            public void onResponse(Call<UploadDocsResponse> call, retrofit2.Response<UploadDocsResponse> response) {
                UploadDocsResponse requestResponse = response.body();
                assert requestResponse != null;
                Toast.makeText(UploadDocs.this, requestResponse.getMessage(), Toast.LENGTH_LONG).show();
                SessionManager.setInsuranceStatus(true);
                loading.cancel();
                finish();

            }

            @Override
            public void onFailure(Call<UploadDocsResponse> call, Throwable t) {
                // progressBar.setVisibility(View.GONE);
                loading.cancel();
                Log.d("Failed", "RetrofitFailed");
            }
        });

    }

    //update vechicale Details
    public void updateVehicleDetails() {
        final ProgressDialog loading = ProgressDialog.show(this, "", "Updating Docs...", false, false);
        String token = "Bearer " + SessionManager.getKEY();

        ApiNetworkCall apiService = ApiClient.getApiService();
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
        RequestBody Req_vehicle_detail_id = RequestBody.create(
                MediaType.parse("text/plain"),
                vehicleDetailId);
        RequestBody Req_insurance_issue = RequestBody.create(
                MediaType.parse("text/plain"),
                insuranceIssueDate);
        RequestBody Req_insurance_expire = RequestBody.create(
                MediaType.parse("text/plain"),
                insuranceExpireDate);

        RequestBody Req_car_reg_issue = RequestBody.create(
                MediaType.parse("text/plain"),
                carIssueDate);
        RequestBody Req_car_reg_expire = RequestBody.create(
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
        MultipartBody.Part insuranceFileToUpload;
        //empty file
        RequestBody empty_file = RequestBody.create(
                MediaType.parse("image/*"),
                "");
        MultipartBody.Part carRegFileToUpload;
        MultipartBody.Part driverLicenceFileToUpload;
        MultipartBody.Part inspectionFileToUpload;

        if (inspectionFinalFile != null && inspectionFinalFile.exists()) {
            inspectionFileToUpload = MultipartBody.Part.createFormData("inspection_document", inspectionFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), inspectionFinalFile));
        } else {
            inspectionFileToUpload = MultipartBody.Part.createFormData("inspection_document", "", empty_file);
        }

        if (insurenceFinalFile != null && insurenceFinalFile.exists()) {
            insuranceFileToUpload = MultipartBody.Part.createFormData("insurance", insurenceFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), insurenceFinalFile));
        } else {
            insuranceFileToUpload = MultipartBody.Part.createFormData("insurance", "", empty_file);
        }


        if (carRegistrationFinalFile != null && carRegistrationFinalFile.exists()) {
            carRegFileToUpload = MultipartBody.Part.createFormData("car_registration", carRegistrationFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), carRegistrationFinalFile));
        } else {
            carRegFileToUpload = MultipartBody.Part.createFormData("car_registration", "", empty_file);
        }

        if (drivingLicenceFile != null && drivingLicenceFile.exists()) {
            driverLicenceFileToUpload = MultipartBody.Part.createFormData("license", drivingLicenceFile.getName(), RequestBody.create(MediaType.parse("image/*"), drivingLicenceFile));
        } else {
            driverLicenceFileToUpload = MultipartBody.Part.createFormData("license", "", empty_file);
        }


        Call<ChangePasswordResponse> call = apiService.updateDocs(token, Request_year, Request_no, Request_brand,
                Request_model, Request_color, Req_type, Req_seat, Req_vehicle_detail_id, insuranceFileToUpload, Req_insurance_issue, Req_insurance_expire,
                carRegFileToUpload, Req_car_reg_issue, Req_car_reg_expire, driverLicenceFileToUpload, Req_licence_issue, Req_licence_expire, Req_inspection_issue, Req_inspection_expire, inspectionFileToUpload);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                ChangePasswordResponse requestResponse = response.body();
                assert requestResponse != null;
                Toast.makeText(UploadDocs.this, "Document uploaded successfully.", Toast.LENGTH_LONG).show();
                loading.cancel();
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


    public void setView() {
        if (docStatus != null) {
            if (!docStatus[0]) {
                binding.licenceTxt.setVisibility(View.GONE);
                binding.licenceImg.setVisibility(View.GONE);
                binding.licenceIssueDate.setVisibility(View.GONE);
                binding.licenceExpireDate.setVisibility(View.GONE);
            }
            if (!docStatus[1]) {
                binding.txtInsurance.setVisibility(View.GONE);
                binding.insuranceImg.setVisibility(View.GONE);
                binding.insuranceIssueDate.setVisibility(View.GONE);
                binding.insuranceExpireDate.setVisibility(View.GONE);
            }
            if (!docStatus[2]) {
                binding.carRegistrationTxt.setVisibility(View.GONE);
                binding.carRegistration.setVisibility(View.GONE);
                binding.carRegistrationIssueDate.setVisibility(View.GONE);
                binding.carRegistrationExpireDate.setVisibility(View.GONE);
            }
            if (!docStatus[3]) {
                binding.carInspectionTxt.setVisibility(View.GONE);
                binding.vehicleInspectionImg.setVisibility(View.GONE);
                binding.carInspectionIssueDate.setVisibility(View.GONE);
                binding.carInspectionExpireDate.setVisibility(View.GONE);
            }
        }
    }


    public void init() {
        insurenceFinalFile = new File("");
        drivingLicenceFile = new File("");
        carRegistrationFinalFile = new File("");
        inspectionFinalFile = new File("");
        binding.swipeRefresh.setOnRefreshListener(() -> binding.swipeRefresh.setRefreshing(false));


        binding.vehicleInspectionImg.setOnClickListener(e -> {
            fileType = "inspection_file";
            showPictureDialog();
        });

        binding.carRegistration.setOnClickListener(e -> {

            fileType = "car_registration_file";
            showPictureDialog();

        });

        binding.licenceImg.setOnClickListener(e -> {

            fileType = "licence_file";
            showPictureDialog();

        });

        binding.insuranceImg.setOnClickListener(e -> {
            fileType = "insurance_file";
            showPictureDialog();
        });


        //calender dialog
        Calendar cal = Calendar.getInstance();
        binding.insuranceIssueDate.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(UploadDocs.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            insuranceIssueDate = format.format(newCalendar.getTime());
                            binding.insuranceIssueDate.setText(insuranceIssueDate);
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

        binding.insuranceExpireDate.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(UploadDocs.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            insuranceExpireDate = format.format(newCalendar.getTime());
                            binding.insuranceExpireDate.setText(insuranceExpireDate);
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


        binding.carInspectionIssueDate.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(UploadDocs.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            inspectionIssueDate = format.format(newCalendar.getTime());
                            binding.carInspectionIssueDate.setText(inspectionIssueDate);
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

        binding.carInspectionExpireDate.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(UploadDocs.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            inspectionExpireDate = format.format(newCalendar.getTime());
                            binding.carInspectionExpireDate.setText(inspectionExpireDate);
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


        binding.licenceIssueDate.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(UploadDocs.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            licenceIssueDate = format.format(newCalendar.getTime());
                            binding.licenceIssueDate.setText(licenceIssueDate);
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

        binding.licenceExpireDate.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(UploadDocs.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            licenceExpireDate = format.format(newCalendar.getTime());
                            binding.licenceExpireDate.setText(licenceExpireDate);
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

        binding.carRegistrationIssueDate.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(UploadDocs.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            carIssueDate = format.format(newCalendar.getTime());
                            binding.carRegistrationIssueDate.setText(carIssueDate);
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

        binding.carRegistrationExpireDate.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder()
                    .context(UploadDocs.this)
                    .callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            carExpireDate = format.format(newCalendar.getTime());
                            binding.carRegistrationExpireDate.setText(carExpireDate);
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


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //getting image from camera
        if (requestCode == IMAGE_RESULT && resultCode == RESULT_OK) {
            switch (fileType) {
                case "insurance_file":
                    insurenceUri = fileUri;
                    Glide.with(UploadDocs.this).load(insurenceUri).into(binding.insuranceImg);
                    insurance_path = insurenceUri.getPath();
                    break;

                case "inspection_file":
                    inspectionUri = fileUri;
                    Glide.with(UploadDocs.this).load(inspectionUri).into(binding.vehicleInspectionImg);
                    inspection_path = inspectionUri.getPath();
                    break;
                case "licence_file":
                    licenceUri = fileUri;
                    Glide.with(UploadDocs.this).load(licenceUri).into(binding.licenceImg);
                    lic_path = licenceUri.getPath();
                    break;
                case "car_registration_file":
                    carRegistrationUri = fileUri;
                    Glide.with(UploadDocs.this).load(carRegistrationUri).into(binding.carRegistration);
                    car_reg_path = carRegistrationUri.getPath();
                    break;

            }
        }

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
                        Glide.with(this).asBitmap().load(bmp).into(binding.insuranceImg);
                        //String insurance_path = getRealPathFromURI(getImageUri(this, bmp));
                        String insurance_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        insurenceFinalFile = new File(insurance_path);
                        break;

                    case "inspection_file":
                        Glide.with(this).asBitmap().load(bmp).into(binding.vehicleInspectionImg);
                        //String insurance_path = getRealPathFromURI(getImageUri(this, bmp));
                        String inspection_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        inspectionFinalFile = new File(inspection_path);
                        break;
                    case "licence_file":
                        Glide.with(this).asBitmap().load(bmp).into(binding.licenceImg);
                        // String lic_path = getRealPathFromURI(getImageUri(this, bmp));
                        String lic_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        drivingLicenceFile = new File(lic_path);
                        break;
                    case "car_registration_file":
                        Glide.with(this).asBitmap().load(bmp).into(binding.carRegistration);
//                        String car_reg_path = getRealPathFromURI(getImageUri(this, bmp));
                        String car_reg_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        carRegistrationFinalFile = new File(car_reg_path);
                        break;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }


    public File compressImage1(String imageUrl) {
        int compressionRatio = 2; //1 == originalImage, 2 = 50% compression, 4=25% compress
        File file = new File(imageUrl);
        try {
            BitmapFactory.Options Options = new BitmapFactory.Options();
            Options.inSampleSize = 4;
            Options.inJustDecodeBounds = false;
//            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), Options);
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

    //picture dialog
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


    //taking photo from Camera
    private void takePhotoFromCamera() {
        //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, MyFileContentProvider.CONTENT_URI);
        file = new File(getExternalCacheDir(),
                String.valueOf(System.currentTimeMillis()) + ".jpg");
        fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, IMAGE_RESULT);
    }

    //checking permission
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


    @Override
    public void passData(boolean[] data) {

        docStatus = data;

    }


}