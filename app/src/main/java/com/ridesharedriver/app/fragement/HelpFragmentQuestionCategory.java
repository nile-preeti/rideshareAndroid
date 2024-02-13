package com.ridesharedriver.app.fragement;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ridesharedriver.app.R;
import com.ridesharedriver.app.acitivities.HomeActivity;
import com.ridesharedriver.app.adapter.CompleteRideAdapter;
import com.ridesharedriver.app.adapter.HelpQuestionCategoryAdapter;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.custom.Utils;
import com.ridesharedriver.app.databinding.FragmentHelpQuestionCategoryBinding;
import com.ridesharedriver.app.pojo.PendingRequestPojo;
import com.ridesharedriver.app.pojo.help_question.QuestionCategoryData;
import com.ridesharedriver.app.pojo.help_question.QuestionCategoryResponse;
import com.ridesharedriver.app.pojo.rides.RideResponse;
import com.ridesharedriver.app.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


//help question category
public class HelpFragmentQuestionCategory extends Fragment implements CompleteRideAdapter.OnRideSelect {

    private FragmentHelpQuestionCategoryBinding binding;
    private List<QuestionCategoryData> questionDataList;
    private HelpQuestionCategoryAdapter mAdapter;


    //complete rides
    CompleteRideAdapter adapter;
    private String status = "COMPLETED";
    private List<PendingRequestPojo> data_lists = new ArrayList<>();
    private int visibleItemCount, totalItemCount, pastVisibleItems;
    private int pageNumber = 1, perPage = 10, totalNumberOfPage = 0;
    LinearLayoutManager layoutManager;
    private boolean loading = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.myswitch);
        item.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Question Category");

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        binding = FragmentHelpQuestionCategoryBinding.inflate(getLayoutInflater(), container, false);
        binding.questionHelpRv.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
        ));


        //complete ride
        layoutManager = new LinearLayoutManager(getActivity());
        binding.completeRideRv.setLayoutManager(layoutManager);
        binding.completeRideRv.setItemAnimator(new DefaultItemAnimator());
        adapter = new CompleteRideAdapter(requireContext(), data_lists, this);
        binding.completeRideRv.setAdapter(adapter);
        binding.completeRideRv.setVisibility(View.GONE);

        if (getArguments() != null) {
            if (Utils.haveNetworkConnection(requireActivity())) {

                getQuestionsCategory(String.valueOf(getArguments().getInt("id")));
            } else {
                Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            }
        }


        binding.completeRideRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            if (totalNumberOfPage > data_lists.size()) {
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

        return binding.getRoot();
    }





//getting accepted rides
    private void getAcceptedRequest(String status,String perPage, String pageNo) {
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Fetching Ride Data......");
        progressDialog.show();
        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<RideResponse> call = apiService.getRides("Bearer " + SessionManager.getKEY(),status, "","",perPage, pageNo);
        call.enqueue(new Callback<RideResponse>() {
            @Override
            public void onResponse(Call<RideResponse> call, retrofit2.Response<RideResponse> response) {
                RideResponse jsonResponse = response.body();
                assert jsonResponse != null;
                if (jsonResponse.getStatus()) {
                    progressDialog.cancel();
                    Log.e("earning", SessionManager.getKEY());
                    //Toast.makeText(requireContext(),response.body().toString(),Toast.LENGTH_LONG).show();

                    List<PendingRequestPojo> list = new ArrayList<>();
                    assert response.body() != null;
                    list = response.body().getData();

                    try {
                        totalNumberOfPage = jsonResponse.getTotalRecord();

                    } catch (Exception ex) {

                    }
                    data_lists.addAll(list);
                    if (totalNumberOfPage >= data_lists.size()) {
                        adapter.setData(data_lists);
                        pageNumber++;
                        loading = true;

                    }



                } else {
                    //Toast.makeText(requireContext(), "Error while getting payment history.", Toast.LENGTH_LONG).show();

                    progressDialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<RideResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                Toast.makeText(requireContext(),
                        "Failure while getting ride data.", Toast.LENGTH_LONG).show();
                progressDialog.cancel();
            }
        });


    }


    //getting question category
    public void getQuestionsCategory(String id) {

        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<QuestionCategoryResponse> call = apiService.getHelpQuestionCategory("Bearer " + SessionManager.getKEY(), id);
        call.enqueue(new Callback<QuestionCategoryResponse>() {
            @Override
            public void onResponse(Call<QuestionCategoryResponse> call, retrofit2.Response<QuestionCategoryResponse> response) {
                QuestionCategoryResponse jsonResponse = response.body();
                if (jsonResponse != null) {
                    if (jsonResponse.getStatus()) {
                        questionDataList = jsonResponse.getData();
                        mAdapter = new HelpQuestionCategoryAdapter(questionDataList, new HelpQuestionCategoryAdapter.ItemClicked() {
                            @Override
                            public void onItemClicked(int id, String question, String email) {
                                Bundle bundle = new Bundle();
                                HelpSubmitFragment helpSubmitFragment = new HelpSubmitFragment();
                                bundle.putInt("id",id);
                                bundle.putString("question",question);
                                bundle.putString("email",email);
                                bundle.putString("ride_id","");
                                helpSubmitFragment.setArguments(bundle);
                                ((HomeActivity)requireContext()).changeFragment(helpSubmitFragment,"submit_question");
                            }
                        });

                        binding.questionHelpRv.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();

                        if (id.equalsIgnoreCase("1")) {
                            if (Utils.haveNetworkConnection(requireActivity())) {
                                getAcceptedRequest(status, String.valueOf(perPage), String.valueOf(pageNumber));
                                binding.completeRideRv.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                            }

                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<QuestionCategoryResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
            }
        });
    }


    //ride selection
    @Override
    public void onRideSelect(String ride_id) {
        Bundle bundle = new Bundle();
        bundle.putString("question","Ride Id: "+ride_id);
        bundle.putInt("id",1);
        bundle.putString("ride_id",ride_id);
        HelpSubmitFragment helpSubmitFragment = new HelpSubmitFragment();
        helpSubmitFragment.setArguments(bundle);
        ((HomeActivity)requireActivity()).changeFragment(helpSubmitFragment,"submit_question");
    }
}