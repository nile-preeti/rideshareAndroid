package com.ridesharedriver.app.utils;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ridesharedriver.app.R;
import com.ridesharedriver.app.adapter.ServiceAlertAdapter;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.custom.CheckConnection;
import com.ridesharedriver.app.pojo.changeStatus.ChangeVehicleStatusResponse;
import com.ridesharedriver.app.pojo.getVehicleDetails.GetAddedVehicleResponse;
import com.ridesharedriver.app.pojo.getVehicleDetails.VehicleData;
import com.ridesharedriver.app.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

//Showing servuce alert
public class ShowServiceAlert implements ServiceAlertAdapter.OnServiceSelect {
    private CheckBox chkSedanX, chkOcoryXL, chkOcoryPremium;
    private List<VehicleData> vehicleDataList = new ArrayList<>();
    private RecyclerView serviceRecylcerView;
    private ServiceAlertAdapter mAdapter;
    private Context serviceContext;
    private Dialog alertDialog;

    //Showing alert
    public void showAlert(Context context, String vehicle_id, String status) {

        this.serviceContext = context;
        alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.select_service_layout);
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        serviceRecylcerView = alertDialog.findViewById(R.id.recyclerview_service);

        serviceRecylcerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));


        chkSedanX = alertDialog.findViewById(R.id.chk_sedan_x);
        chkOcoryXL = alertDialog.findViewById(R.id.chk_sedan_xl);
        chkOcoryPremium = alertDialog.findViewById(R.id.chk_sedan_premium);
        ProgressBar progressBar = alertDialog.findViewById(R.id.progress);
        Button btnCancel = alertDialog.findViewById(R.id.btn_cancel_select_services);
        Button btnSubmit = alertDialog.findViewById(R.id.btn_submit_select_services);

        btnCancel.setOnClickListener(e -> {
//            vehicleDataList.clear();
            alertDialog.dismiss();
        });

//        chkSedanX.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked)
//                {
//                    onCheckAddService(context,"1",0);
//                }
//                else
//                {
//                    onCheckAddService(context,"2",0);
//                }
//            }
//        });
//
//        chkOcoryXL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked)
//                {
//                    onCheckAddService(context,"1",0);
//                }
//                else
//                {
//                    onCheckAddService(context,"2",0);
//                }
//            }
//        });
//
//        chkOcoryPremium.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked)
//                {
//                    onCheckAddService(context,"1",0);
//                }
//                else
//                {
//                    onCheckAddService(context,"2",0);
//                }
//            }
//        });
        btnSubmit.setOnClickListener(e -> {
//            String id1 = "", id2 = "", id3 = "";
//            if (chkSedanX.isChecked()) {
//                id1 = "1";
//            }
//            if (chkOcoryXL.isChecked()) {
//                id2 = "2";
//            }
//            if (chkOcoryPremium.isChecked()) {
//                id3 = "3";
//            }
//
//            if (CheckConnection.haveNetworkConnection(context)) {
//                addService(context, id1 + "," + id2 + "," + id3, "");
//            }
//            else
//            {
//                Toast.makeText(ApplicationProvider.getApplicationContext(), getApplicationContext().getString(R.string.network), Toast.LENGTH_LONG).show();
//
//            }
//            vehicleDataList.clear();
            alertDialog.dismiss();


        });


        if (status.equals("1")) {
            if (CheckConnection.haveNetworkConnection(context)) {
                vehicleDataList.clear();
                getService(context);
                alertDialog.show();
            } else {
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.network), Toast.LENGTH_LONG).show();
            }
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    for (progressBarStatus = 0; progressBarStatus <= 20; progressBarStatus++) {
//
//                        progressBarbHandler.post(new Runnable() {
//                            @RequiresApi(api = Build.VERSION_CODES.M)
//                            public void run() {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                    progressBar.setProgress(progressBarStatus,true);
//                                }
//                                else
//                                {
//                                    progressBar.setProgress(progressBarStatus);
//                                }
//                                if(progressBarStatus==10)
//                                    progressBar.setProgressTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
//                            }
//                        });
//
//
//                        try {
//                            Thread.sleep(1000);
//                        } catch (Exception ex) {
//                        }
//                    }
//                }
//            }).start();

        } else {
            vehicleDataList.clear();
            alertDialog.dismiss();
        }
    }


    private void onCheckAddService(Context context, String status, String service_id) {
        if (CheckConnection.haveNetworkConnection(context)) {

            try {
                addService(context, service_id, status);
            } catch (IndexOutOfBoundsException ex) {

            }

        } else {
            Toast.makeText(context, context.getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }


    private static int progressBarStatus = 0;
    private static Handler progressBarbHandler = new Handler();

    private static void addService(Context context, String id, String status) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading......");
        progressDialog.show();
        Map<String, String> details = new HashMap<>();
        details.put("service_id", id);
        details.put("status", status);

        String token = "Bearer " + SessionManager.getKEY();
        Log.d("token", token);

        ApiNetworkCall apiService = ApiClient.getApiService();
        Call<ChangeVehicleStatusResponse> call = apiService.selectService(token, details);
        call.enqueue(new Callback<ChangeVehicleStatusResponse>() {
            @Override
            public void onResponse(Call<ChangeVehicleStatusResponse> call, retrofit2.Response<ChangeVehicleStatusResponse> response) {
                ChangeVehicleStatusResponse jsonResponse = response.body();
                if (jsonResponse.getStatus()) {

                    progressDialog.cancel();
                    Log.e("Service Status ", "Success");
                } else {

                    progressDialog.cancel();
                    Log.e("Service Status ", "Error");
                }
            }

            @Override
            public void onFailure(Call<ChangeVehicleStatusResponse> call, Throwable t) {
                Log.e("Service Status ", "Failure");
                progressDialog.cancel();
            }
        });


    }


    private void getService(Context context) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading......");
        progressDialog.show();

        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<GetAddedVehicleResponse> call = apiService.getAddedVehicles("Bearer " + SessionManager.getKEY());
        call.enqueue(new Callback<GetAddedVehicleResponse>() {
            @Override
            public void onResponse(Call<GetAddedVehicleResponse> call, retrofit2.Response<GetAddedVehicleResponse> response) {
                GetAddedVehicleResponse jsonResponse = response.body();
                assert jsonResponse != null;
                if (jsonResponse.getStatus()) {

                    vehicleDataList = jsonResponse.getData();
//                    disableVehicleType();
                    if (vehicleDataList.size() > 0) {
                        mAdapter = new ServiceAlertAdapter(vehicleDataList, ShowServiceAlert.this, context);
                        serviceRecylcerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
//                        for (int i = 0; i < vehicleDataList.size(); i++) {
//                            if(vehicleDataList.get(i).getVehicleTypeId().equals("1") )
//                                chkSedanX.setVisibility(View.VISIBLE);
//                            if(vehicleDataList.get(i).getVehicleTypeId().equals("2") )
//                                chkOcoryXL.setVisibility(View.VISIBLE);
//                            if(vehicleDataList.get(i).getVehicleTypeId().equals("3") )
//                                chkOcoryPremium.setVisibility(View.VISIBLE);
//
//                            if (vehicleDataList.get(i).getStatus() != null) {
//                                if (vehicleDataList.get(i).getVehicleTypeId().equals("1") &&
//                                        vehicleDataList.get(i).getStatus().equalsIgnoreCase("1")) {
//                                    chkSedanX.setChecked(true);
//                                    chkSedanX.setEnabled(true);
//
//                                }
//                            }
//                                if (vehicleDataList.get(i).getVehicleTypeId().equals("1") &&
//                                        vehicleDataList.get(i).getStatus().equals("0")) {
//                                    chkSedanX.setChecked(false);
//                                    chkSedanX.setEnabled(true);
//                                }
////
//
//
//                            if (vehicleDataList.get(i).getStatus() != null) {
//                                if (vehicleDataList.get(i).getVehicleTypeId().equals("2") &&
//                                        vehicleDataList.get(i).getStatus().equalsIgnoreCase("1")) {
//                                    chkOcoryXL.setChecked(true);
//                                    chkOcoryXL.setEnabled(true);
//                                }
//                            }
//                                if (vehicleDataList.get(i).getVehicleTypeId().equals("2") &&
//                                        vehicleDataList.get(i).getStatus().equals("0")) {
//                                    chkOcoryXL.setChecked(false);
//                                    chkOcoryXL.setEnabled(true);
//                                }
////
//
//
//                            if (vehicleDataList.get(i).getStatus() != null) {
//                                if (vehicleDataList.get(i).getVehicleTypeId().equals("3") &&
//                                        vehicleDataList.get(i).getStatus().equalsIgnoreCase("1")) {
//                                    chkOcoryPremium.setChecked(true);
//                                    chkOcoryPremium.setEnabled(true);
//
//                                }
//                            }
//                            if (vehicleDataList.get(i).getVehicleTypeId().equals("3") &&
//                                    vehicleDataList.get(i).getStatus().equalsIgnoreCase("0")) {
//                                chkOcoryPremium.setChecked(false);
//                                chkOcoryPremium.setEnabled(true);
//                            }
//
//                        }
                    }

                    progressDialog.cancel();


                } else {
//                    disableVehicleType();
                    if(alertDialog!=null)
                    {
                        if(alertDialog.isShowing())
                            alertDialog.dismiss();
                    }
                    Toast.makeText(context, "Please activate a vehicle in profile.", Toast.LENGTH_LONG).show();
                    progressDialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<GetAddedVehicleResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                Toast.makeText(context, "Failure while getting vehicle data.", Toast.LENGTH_LONG).show();
                progressDialog.cancel();
            }
        });


    }

//    private void disableVehicleType() {
//
//        chkSedanX.setChecked(false);
//        chkSedanX.setEnabled(false);
//        chkOcoryXL.setChecked(false);
//        chkOcoryXL.setEnabled(false);
//        chkOcoryPremium.setChecked(false);
//        chkOcoryPremium.setEnabled(false);
//        chkOcoryPremium.setVisibility(View.GONE);
//        chkOcoryXL.setVisibility(View.GONE);
//        chkSedanX.setVisibility(View.GONE);
//    }

    @Override
    public void onItemClicked(String service_id, boolean status) {
        if (status)
            onCheckAddService(serviceContext, "1", service_id);
        else {
            onCheckAddService(serviceContext, "2", service_id);
        }

    }
}
