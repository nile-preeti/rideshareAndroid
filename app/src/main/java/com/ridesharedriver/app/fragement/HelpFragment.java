package com.ridesharedriver.app.fragement;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ridesharedriver.app.R;
import com.ridesharedriver.app.acitivities.HomeActivity;
import com.ridesharedriver.app.adapter.HelpQuestionAdapter;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.databinding.FragmentHelpBinding;
import com.ridesharedriver.app.pojo.help_question.QuestionData;
import com.ridesharedriver.app.pojo.help_question.QuestionResponse;
import com.ridesharedriver.app.session.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


public class HelpFragment extends Fragment implements HelpQuestionAdapter.ItemClicked {


    private static final int REQUEST_CODE = 1009;
    private FragmentHelpBinding helpBinding;
    private HelpQuestionAdapter mAdapter;
    private List<QuestionData> questionDataList;
    View rootView;
    private TextView textViewMail, textViewPhone;
    public HelpFragment() {

    }

    public static HelpFragment newInstance(String param1, String param2) {
        HelpFragment fragment = new HelpFragment();
        return fragment;
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        helpBinding = FragmentHelpBinding.inflate(getLayoutInflater(),container,false);
        rootView = helpBinding.getRoot();
        //rootView= inflater.inflate(R.layout.fragment_help, container, false);
        textViewMail = helpBinding.tvMailHelp;
        textViewPhone = helpBinding.tvPhoneHelp;

        helpBinding.questionHelpRv.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));

        getQuestions();

        textViewPhone.setOnClickListener(e->{
            if (ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.CALL_PHONE},
                        REQUEST_CODE);

            } else {

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", textViewPhone.getText().toString(), null));
                startActivity(intent);

            }
        });
        textViewMail.setOnClickListener(e->{
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",textViewMail.getText().toString(), null));

            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });
        setHasOptionsMenu(true);
        ((HomeActivity) getActivity()).fontToTitleBar("Help");

        return  rootView;
    }


    //getting question
    public void getQuestions() {

        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<QuestionResponse> call = apiService.getHelpQuestions("Bearer " + SessionManager.getKEY());
        call.enqueue(new Callback<QuestionResponse>() {
            @Override
            public void onResponse(Call<QuestionResponse> call, retrofit2.Response<QuestionResponse> response) {
                QuestionResponse jsonResponse = response.body();
                if (jsonResponse != null) {
                    if (jsonResponse.getStatus()) {
                      questionDataList = jsonResponse.getData();
                        mAdapter = new HelpQuestionAdapter(questionDataList,HelpFragment.this);

                        helpBinding.questionHelpRv.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();


                    }
                }
            }

            @Override
            public void onFailure(Call<QuestionResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
            }
        });
    }




    @Override
    public void onItemClicked(int id) {
        HelpFragmentQuestionCategory helpFragmentQuestionCategory = new HelpFragmentQuestionCategory();
        Bundle args = new Bundle();
        args.putInt("id", id);
        helpFragmentQuestionCategory.setArguments(args);
        ((HomeActivity)requireActivity()).changeFragment(helpFragmentQuestionCategory,"Help_Question");
    }
}