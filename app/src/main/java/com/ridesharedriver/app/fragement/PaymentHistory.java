package com.ridesharedriver.app.fragement;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.ridesharedriver.app.adapter.PaymentAdapter;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.custom.CheckConnection;
import com.ridesharedriver.app.custom.Utils;
import com.ridesharedriver.app.custom.chart.DayAxisValueFormatter;
import com.ridesharedriver.app.custom.chart.MyAxisValueFormatter;
import com.ridesharedriver.app.databinding.PaymentHistoryAlertLayoutBinding;
import com.ridesharedriver.app.pojo.PendingRequestPojo;
import com.ridesharedriver.app.pojo.driverEarning.Data;
import com.ridesharedriver.app.pojo.driverEarning.DriverEarningResponse;
import com.ridesharedriver.app.pojo.paymenthistory.PaymentHistoryData;
import com.ridesharedriver.app.pojo.paymenthistory.PaymentHistoryResponse;
import com.ridesharedriver.app.session.SessionManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

import static com.ridesharedriver.app.session.SessionManager.COLOR;
import static com.ridesharedriver.app.session.SessionManager.getKEY;

/**
 * Created by android on 15/4/17.
 */

//Payment history
public class PaymentHistory extends Fragment {

    View view;
    TextView txt_error, txt_earning;
    ImageView datePicker;
    ImageView noDataImg;
    private Typeface tfLight;
    private MenuItem calenderView;
    private List<Data> earningData = null;
    private List<PendingRequestPojo> pendingRequestPojoList = new ArrayList<>();
    private double totalEarning = 0;
    private boolean loading = true;
    private int visibleItemCount, totalItemCount, pastVisibleItems;
    private BarChart chart;
    private int pageNumber = 1;
    private final int perPage = 10;
    private int totalNumberOfPage = 0;
    private RecyclerView recyclerView;
    private String toDate = "", fromDate = "";
    private PaymentAdapter paymentAdapter;
    private RadioButton rdbYear, rdbMonth, rdbWeek;
    private LinearLayout llStepsLayout;
    private ImageView nextImg, prevImg;
    private Utils utils = new Utils();
    private int valueCounter = 0;
    private CircularProgressIndicator progressIndicator;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private Calendar calendar;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.myswitch);
        item.setVisible(false);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.payement_history, container, false);
        setHasOptionsMenu(true);
        tfLight = Typeface.createFromAsset(requireActivity().getAssets(), "font/montserrat_regular.ttf");

        calendar = Calendar.getInstance();
        txt_error = (TextView) view.findViewById(R.id.txt_error);
        noDataImg = view.findViewById(R.id.noDataImg);
        txt_earning = view.findViewById(R.id.txt_total_earning);
        datePicker = view.findViewById(R.id.datePicker);

        rdbYear = view.findViewById(R.id.rdb_year);
        rdbMonth = view.findViewById(R.id.rdb_month);
        rdbWeek = view.findViewById(R.id.rdb_week);
        progressIndicator = view.findViewById(R.id.earning_progress);

        llStepsLayout = view.findViewById(R.id.ll_steps);
        llStepsLayout.setVisibility(View.GONE);
        nextImg = view.findViewById(R.id.img_next);
        prevImg = view.findViewById(R.id.img_prev);

        //chart
        chart = view.findViewById(R.id.chart1);
        rdbYear.setChecked(true);
        try {
            initChart();
            getEarningInfo("year", String.valueOf(calendar.get(Calendar.YEAR)));
        }
        catch(Exception ex)
        {

        }



        nextImg.setOnClickListener(e -> {
            if (valueCounter == 0) {
                prevImg.setVisibility(View.GONE);
            } else {
                prevImg.setVisibility(View.VISIBLE);
            }

            if (rdbMonth.isChecked()) {
                if (valueCounter == 12) {
                    nextImg.setVisibility(View.GONE);
                }
                if (valueCounter < 12) {
                    ++valueCounter;
                    getEarningInfo("month", String.valueOf(valueCounter));
                }

            }

           if (rdbWeek.isChecked()) {
                if (valueCounter < 4) {
                    ++valueCounter;
                    getEarningInfo("week", String.valueOf(valueCounter));
                }
                if (valueCounter == 4) {
                    nextImg.setVisibility(View.GONE);
                }
            }
        });

        prevImg.setOnClickListener(e -> {

            if (valueCounter == 0) {
                prevImg.setVisibility(View.GONE);
            }
            if (rdbMonth.isChecked()) {
                if (valueCounter >1) {
                    --valueCounter;
                    getEarningInfo("month", String.valueOf(valueCounter));
                }
                else{
                    valueCounter=0;
                }

            }
            if (rdbWeek.isChecked()) {
                if (valueCounter >1) {
                    --valueCounter;
                    getEarningInfo("week", String.valueOf(valueCounter));
                }
                else
                {
                    valueCounter=0;
                }
            }
        });

        rdbMonth.setOnClickListener(e -> {
            llStepsLayout.setVisibility(View.VISIBLE);
            valueCounter = 1;
            nextImg.setVisibility(View.VISIBLE);
            prevImg.setVisibility(View.GONE);
            getEarningInfo("month","1");
        });
        rdbWeek.setOnClickListener(e -> {
            llStepsLayout.setVisibility(View.VISIBLE);
            valueCounter = 1;
            nextImg.setVisibility(View.VISIBLE);
            prevImg.setVisibility(View.GONE);
            getEarningInfo("week","1");
        });

        rdbYear.setOnClickListener(e-> {
            llStepsLayout.setVisibility(View.GONE);
            valueCounter=0;
            getEarningInfo("year", String.valueOf(calendar.get(Calendar.YEAR)));
        });

        datePicker.setOnClickListener(e -> {
            showDatePickerDialog();
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        paymentAdapter = new PaymentAdapter(pendingRequestPojoList);
        recyclerView.setAdapter(paymentAdapter);
        paymentAdapter.notifyDataSetChanged();


        txt_earning.setText(DecimalFormat.getCurrencyInstance(Locale.US).format(totalEarning));
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.payment_history));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int size = totalNumberOfPage / perPage;
                if (dy > 0) {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            if (totalNumberOfPage > pendingRequestPojoList.size()) {
                                if (Utils.haveNetworkConnection(requireActivity())) {
                                    getPaymentHistory(fromDate, toDate, String.valueOf(perPage), String.valueOf(pageNumber));
                                } else {
                                    Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                                }

                            }
                            // loading = true;
                            //Do pagination.. i.e. fetch new data

                        }


                    }
                }


            }

        });

        if (Utils.haveNetworkConnection(getActivity())) {
            getPaymentHistory(fromDate, toDate, String.valueOf(perPage), String.valueOf(pageNumber));
        } else {
            Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();

        }
        return view;
    }

    //chart
    private void initChart() {
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setMaxVisibleValueCount(14);
        // chart.setVisibleXRangeMinimum(14);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        chart.setFitBars(true);


    }

    //setting chart
    private void setChart() {
        ValueFormatter xAxisFormatter;
        if (rdbWeek.isChecked() || rdbMonth.isChecked())
            xAxisFormatter = new DayAxisValueFormatter(chart, earningData, true);
        else
            xAxisFormatter = new DayAxisValueFormatter(chart, earningData, false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);

        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(earningData.size());
        xAxis.setLabelRotationAngle(70);
        xAxis.setDrawLabels(true);
        xAxis.setAvoidFirstLastClipping(false);


        ValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f);


        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        l.setFormToTextSpace(30);


        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < earningData.size(); i++) {
            values.add(new BarEntry(i, (float) earningData.get(i).getAmount(), ResourcesCompat.getDrawable(getResources(), R.drawable.detail_button, null)));
        }

        BarDataSet set1;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.setValueTextColor(Color.WHITE);
            chart.setNoDataTextColor(Color.WHITE);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "Earning Analysis");

            set1.setDrawIcons(false);
            set1.setValueTextColor(Color.WHITE);
            set1.setColors(ResourcesCompat.getColor(getResources(), R.color.app_clr, null),ResourcesCompat.getColor(getResources(), R.color.app_clr, null),ResourcesCompat.getColor(getResources(), R.color.app_clr, null));
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.WHITE);
            data.setValueTypeface(tfLight);
            data.setBarWidth(0.5f);


            chart.setData(data);
        }

    }


    //getting earning
    public void getEarningInfo(String param, String value) {
        if (CheckConnection.haveNetworkConnection(requireContext())) {
            progressIndicator.setVisibility(View.VISIBLE);

            String url = Server.BASE_URL.concat("earn?").concat(param).concat("=").concat(value);
            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            requestQueue.getCache().clear();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        if (earningData != null) {
                            earningData.clear();
                        }

                        JSONObject data = new JSONObject(response);
                        Log.e("Response", "EarningonResponse = \n " + response);

                        if (data.has("status") && data.getBoolean("status")) {

                            Gson gson = new Gson();
                            DriverEarningResponse driverEarningResponse = gson.fromJson(data.toString(), DriverEarningResponse.class);
                            try {
                                initChart();
                            }
                            catch (Exception ex)
                            {

                            }
                            earningData = driverEarningResponse.getData();
                            try {
                                chart.clear();
                            } catch (Exception ex) {

                            }
                            try {
                                setChart();
                                chart.invalidate();
                            }
                            catch (Exception ex)
                            {

                            }
                            progressIndicator.setVisibility(View.GONE);
                        } else {
//                        Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();

                            progressIndicator.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {

                        progressIndicator.setVisibility(View.GONE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Response ", "" + error.getMessage());

                    progressIndicator.setVisibility(View.GONE);
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
            Toast.makeText(requireContext(), getString(R.string.network), Toast.LENGTH_LONG).show();

        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.payment_history_menu, menu);
        calenderView = menu.findItem(R.id.calender);
        calenderView.setVisible(false);


        calenderView.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                if(list!=null)
//                {

//                    dateEditText.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
                // showAlert();
//                        }
//                    });
//


                //   }
                return true;
            }
        });
    }

//getting Payment History
    private void getPaymentHistory(String from, String to, String perPage, String pageNo) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching Payment History......");
        progressDialog.show();

        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<PaymentHistoryResponse> call = apiService.getPaymentHistory("Bearer " + SessionManager.getKEY(), from, to, perPage, pageNo);
        call.enqueue(new Callback<PaymentHistoryResponse>() {
            @Override
            public void onResponse(Call<PaymentHistoryResponse> call, retrofit2.Response<PaymentHistoryResponse> response) {
                PaymentHistoryResponse jsonResponse = response.body();
                assert jsonResponse != null;
                if (jsonResponse.getStatus()) {

                    Log.e("earning", SessionManager.getKEY());
                    //Toast.makeText(requireContext(),response.body().toString(),Toast.LENGTH_LONG).show();

                    List<PaymentHistoryData> list = new ArrayList<>();
                    assert response.body() != null;
                    list = response.body().getData();

                    try {
                        totalNumberOfPage = jsonResponse.getTotalRecord();

                    } catch (Exception ex) {

                    }
                    List<PendingRequestPojo> tempList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        PendingRequestPojo pojo = new PendingRequestPojo();
                        pojo.setAmount(list.get(i).getPayoutAmount());
                        pojo.setDistance(list.get(i).getDistance());
                        pojo.setScreen("payment");
                        pojo.setPickup_adress(list.get(i).getPickupAdress());
//                        try {
//                            pojo.setPickup_adress(Utils.getMinimumAddress(requireContext(), Double.parseDouble(list.get(i).getPickupLat()), Double.parseDouble(list.get(i).getPickupLong())));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        pojo.setDrop_address(list.get(i).getDropAddress());
                        pojo.setPayment_status(list.get(i).getPaymentStatus());
                        pojo.setStatus(list.get(i).getStatus());
                        pojo.setTime(list.get(i).getTime());
                        pojo.setDriver_name(list.get(i).getDriverName());
                        pojo.setDrop_lat(list.get(i).getDropLat());
                        pojo.setDrop_long(list.get(i).getDropLong());
                        pojo.setPickup_lat(list.get(i).getPickupLat());
                        pojo.setPickup_long(list.get(i).getPickupLong());
                        pojo.setTip_amount(list.get(i).getTipAmount());
                        pojo.setUser_name(list.get(i).getUserName());
                        pojo.setUser_lastname(list.get(i).getUserLastname());

                        tempList.add(pojo);
                        Timber.e("Payment History....Pending pojo");
                        //calculating total Earning
                        // if(list.get(i).getPaymentStatus().equalsIgnoreCase("Completed"))
                        //  totalEarning += Double.parseDouble(list.get(i).getPayoutAmount());

                    }

                    totalEarning += Double.parseDouble(response.body().getTotalPayout());
                    txt_earning.setText(DecimalFormat.getCurrencyInstance(Locale.US).format(totalEarning));
                    Log.e("SIZE_LIST", jsonResponse.getTotalPayout() + "");
                    txt_error.setVisibility(View.GONE);
                    noDataImg.setVisibility(View.GONE);
                    progressDialog.cancel();

                    pendingRequestPojoList.addAll(tempList);
                    if (totalNumberOfPage >= pendingRequestPojoList.size()) {
                        paymentAdapter.setData(pendingRequestPojoList);
                        pageNumber++;
                        loading = true;

                    }

                    Log.e("SIZE_LIST", pendingRequestPojoList.size() + "");

                } else {
                    //Toast.makeText(requireContext(), "Error while getting payment history.", Toast.LENGTH_LONG).show();
                    totalEarning = 0;
                    txt_error.setVisibility(View.VISIBLE);
                    noDataImg.setVisibility(View.VISIBLE);
                    txt_earning.setText(DecimalFormat.getCurrencyInstance(Locale.US).format(totalEarning));
                    progressDialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<PaymentHistoryResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                Toast.makeText(requireContext(),
                        "Failure while getting payment history.", Toast.LENGTH_LONG).show();
                progressDialog.cancel();
            }
        });


    }

    //showing date picker
    public void showDatePickerDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        dialogBuilder.setCancelable(false);
        PaymentHistoryAlertLayoutBinding binding = PaymentHistoryAlertLayoutBinding.inflate(requireActivity().getLayoutInflater());
        View dialogView = binding.getRoot();
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();


        Calendar formCalender = Calendar.getInstance();
        fromDate = "";
        toDate = "";

        binding.tvSelectFromDate.setOnClickListener(e -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    formCalender.set(year, month, dayOfMonth, 0, 0);
                    fromDate = format.format(formCalender.getTime());
                    binding.tvSelectFromDate.setText((utils.getCurrentDateInSpecificFormat(fromDate)));
                }
            }, formCalender.get(Calendar.YEAR), formCalender.get(Calendar.MONTH), formCalender.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        Calendar toCalender = Calendar.getInstance();
        binding.tvSelectToDate.setOnClickListener(e -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    toCalender.set(year, month, dayOfMonth, 0, 0);
                    toDate = format.format(toCalender.getTime());
                    binding.tvSelectToDate.setText((utils.getCurrentDateInSpecificFormat(toDate)));
                }
            }, toCalender.get(Calendar.YEAR), toCalender.get(Calendar.MONTH), toCalender.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();

        });


        binding.btnCancelPaymentHistory.setOnClickListener(e -> {

            alertDialog.dismiss();
        });


        binding.btnSubmitPaymentHistory.setOnClickListener((e -> {
            if (fromDate.isEmpty()) {
                Toast.makeText(requireContext(), "Please Select from date", Toast.LENGTH_SHORT).show();
            } else if (toDate.isEmpty()) {
                Toast.makeText(requireContext(), "Please Select to date", Toast.LENGTH_SHORT).show();
            } else {
                pendingRequestPojoList.clear();
                pageNumber = 1;
                totalEarning = 0;
                txt_earning.setText("$0.00");
                getPaymentHistory(fromDate, toDate, String.valueOf(perPage), String.valueOf(pageNumber));
                alertDialog.dismiss();
            }
        }));

        alertDialog.show();
    }


}




