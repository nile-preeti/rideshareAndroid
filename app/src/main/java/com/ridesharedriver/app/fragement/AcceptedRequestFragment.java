package com.ridesharedriver.app.fragement;

import android.media.MediaPlayer;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.ridesharedriver.app.adapter.AcceptedRequestAdapter;
import com.ridesharedriver.app.custom.Utils;
import com.ridesharedriver.app.pojo.PendingRequestPojo;
import com.ridesharedriver.app.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.ridesharedriver.app.adapter.AcceptedRequestAdapter.mediaPlayer;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

//Accept Request
public class AcceptedRequestFragment extends Fragment {

    private View view;
    RecyclerView recyclerView;
    String userid = "";
    TextView txt_error;
    String status;
    SwipeRefreshLayout swipeRefreshLayout;
    AcceptedRequestAdapter adapter;
    LinearLayoutManager layoutManager;
    ImageView noDataImg;
    private List<PendingRequestPojo> data_lists = new ArrayList<>();
    boolean flag;
    private boolean loading = true;
   private int visibleItemCount, totalItemCount, pastVisibleItems;
   private int pageNumber = 1, perPage = 10, totalNumberOfPage = 0;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.myswitch);
        item.setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.accepted_request_fragment, container, false);
        setHasOptionsMenu(true);
        bindView();
        Log.d("ondetach", "call");
        return view;
    }

    public void bindView() {
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.accepted_request));
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        recyclerView = view.findViewById(R.id.recyclerview);
        txt_error = view.findViewById(R.id.txt_error);
        noDataImg = view.findViewById(R.id.noDataImg);

        userid = SessionManager.getUserId();

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new AcceptedRequestAdapter(getActivity(), data_lists, AcceptedRequestFragment.this);
        recyclerView.setAdapter(adapter);
        Bundle bundle = getArguments();
               if (bundle != null) {
            status = bundle.getString("status");
            ((HomeActivity) getActivity()).fontToTitleBar(setTitle(status));
        }
               pageNumber = 1;perPage=10;
        if (Utils.haveNetworkConnection(requireActivity())) {
            getAcceptedRequest(status, String.valueOf(perPage), String.valueOf(pageNumber));
        } else {
            Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int size = totalNumberOfPage / perPage;
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            if (totalNumberOfPage > data_lists.size())
                            {
                                if (Utils.haveNetworkConnection(requireActivity())) {
                                    getAcceptedRequest(status, String.valueOf(perPage), String.valueOf(pageNumber));
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


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //media player stop
    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    //acceptingRide
    public void getAcceptedRequest(String status, String perPage, String pageNo) {
        swipeRefreshLayout.setRefreshing(true);

        String url = Server.BASE_URL.concat("rides").concat("?status=").concat(status).concat("&per_page=").concat(perPage).concat("&page_no=").concat(pageNo)
                .concat("&from=").concat("").concat("&to=").concat("");

        /*String url = Server.BASE_URL.concat("rides?id=").concat(SessionManager.getUserId()).concat("&status=").concat(status)
                .concat("&utype=2");*/

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data = new JSONObject(response);

//                    /*JSONArray obj=data.getJSONObject("data").getJSONArray("audio");
//                    for(int j=0;j<obj.length();j++) {
//                        JSONObject jsonObject = obj.getJSONObject(j);
//                        Audio audio = new Audio();
//                        audio.setAudio(jsonObject.getString("audio"));
//                    }*/
                    Log.e("Request_Response", "onResponse = \n " + perPage+", "+pageNumber);
                   // data_lists.clear();


                    if (data.has("status") && data.getBoolean("status")) {
                        JSONArray jsonArray = data.getJSONArray("data");
                        //JSONArray obj=data.getJSONObject("data").getJSONArray("audio");
                        try {
                            totalNumberOfPage = data.getInt("total_record");

                        } catch (Exception ex) {

                        }
                        if (data.getJSONArray("data").length() == 0 && totalNumberOfPage==0) {
                            txt_error.setVisibility(View.VISIBLE);
                            noDataImg.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            txt_error.setVisibility(View.GONE);
                            noDataImg.setVisibility(View.GONE);

                            List<PendingRequestPojo> pendingRequestPojoList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                PendingRequestPojo lists = new PendingRequestPojo();
                                List<String> a = new ArrayList<>();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

//                                JSONArray obj = jsonObject.getJSONArray("audio");
//                                if (obj.length() == 0) {
//                                    flag = false;
//                                } else {
//                                    flag = true;
//                                    for (int j = 0; j < obj.length(); j++) {
//                                        JSONObject jsonObject1 = obj.getJSONObject(j);
//                                        a.add(jsonObject1.getString("audio"));
//                                    }
//                                }
//                                lists.setAudio(a);
                                lists.setUser_name(jsonObject.getString("user_name"));
                                lists.setUser_lastname(jsonObject.getString("user_lastname"));
                                lists.setTime(jsonObject.getString("time"));

                                lists.setPickup_adress(jsonObject.getString("pickup_adress"));
//                                try {
//                                    lists.setPickup_adress(Utils.getMinimumAddress(getContext(),Double.parseDouble(jsonObject.getString("pickup_lat")),
//                                            Double.parseDouble(jsonObject.getString("pickup_long"))));
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }

                                lists.setDrop_address(jsonObject.getString("drop_address"));
                                lists.setRide_id(jsonObject.getString("ride_id"));
                                if(status.equalsIgnoreCase("CANCELLED")) {
                                    lists.setStatus("CANCELLED");
                                } else {
                                    lists.setStatus(jsonObject.getString("status"));
                                }
                                lists.setPickup_lat(jsonObject.getString("pickup_lat"));
                                lists.setPickup_long(jsonObject.getString("pickup_long"));
                                lists.setDrop_lat(jsonObject.getString("drop_lat"));
                                lists.setDrop_long(jsonObject.getString("drop_long"));
                                lists.setUser_mobile(jsonObject.getString("user_mobile"));
                                lists.setDistance(jsonObject.getString("distance"));
                                lists.setAmount(jsonObject.getString("amount"));
                                lists.setPayment_status(jsonObject.getString("payment_status"));

                                try {
                                    lists.setTip_amount(jsonObject.getString("tip_amount"));

                                }
                                catch (Exception ex)
                                {
                                    lists.setTip_amount("0.0");
                                }

                                pendingRequestPojoList.add(lists);

                            }
                            data_lists.addAll(pendingRequestPojoList);
                            if (totalNumberOfPage >= data_lists.size()) {

                                adapter.setData(data_lists);
                                pageNumber++;
                                loading = true;

                            }
                            //temporary change
//                            else {
//                                data_lists.clear();
//                                data_lists.addAll(pendingRequestPojoList);
//                                adapter.setData(data_lists);
//                            }

                        }

                        swipeRefreshLayout.setRefreshing(false);

                    }
                    else {
                        Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), getString(R.string.error_occurred) +e.toString(), Toast.LENGTH_LONG).show();
                    Log.e("COM_ERROR",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response ", "" + error.getMessage());
                swipeRefreshLayout.setRefreshing(false);
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
        RetryPolicy policy =
                new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    //media player detacchement
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


//set title according to Ride status
    private String setTitle(String s) {
        String title = null;
        switch (s) {
            case "ACCEPTED":
                title = getString(R.string.accepted_request);
                break;
            case "PENDING":
                title = getString(R.string.pending_request);
                break;
            case "CANCELLED":
                title = getString(R.string.cancelled_request);
                break;
            case "COMPLETED":
                title = getString(R.string.completed_request);
                break;
        }
        return title;
    }
}
