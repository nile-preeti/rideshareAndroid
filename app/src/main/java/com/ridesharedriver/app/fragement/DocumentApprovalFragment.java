package com.ridesharedriver.app.fragement;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ridesharedriver.app.R;
import com.ridesharedriver.app.acitivities.HomeActivity;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.databinding.FragmentDocumentApprovalBinding;
import com.ridesharedriver.app.pojo.doc_approval.DocApprovalResponse;
import com.ridesharedriver.app.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;


//Document Approval
public class DocumentApprovalFragment extends Fragment {


    private FragmentDocumentApprovalBinding binding;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.myswitch);
        item.setVisible(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.document_approval_status));
        // Inflate the layout for this fragment
            binding = FragmentDocumentApprovalBinding.inflate(getLayoutInflater(),container,false);
            View view = binding.getRoot();
            getDocumentApprovedStatus();
        return view;
    }



    //Document status
    public void getDocumentApprovedStatus() {
        final ProgressDialog loading = ProgressDialog.show(requireContext(),
                "", "Please wait...", false, false);
        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<DocApprovalResponse> call = apiService.getDocumentApprovalStatus("Bearer " + SessionManager.getKEY());
        call.enqueue(new Callback<DocApprovalResponse>() {
            @Override
            public void onResponse(Call<DocApprovalResponse> call, retrofit2.Response<DocApprovalResponse> response) {
                DocApprovalResponse jsonResponse = response.body();
                if (jsonResponse != null) {
                    if (jsonResponse.getStatus()) {

                        try {
                            if (jsonResponse.getVerificationIdApprovalAtatus().equalsIgnoreCase("1"))
                            {
                                binding.attention3Img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_thumb,null));
                                binding.attention3Img.setColorFilter( ResourcesCompat.getColor( getResources(), R.color.app_clr,null));
                                binding.txtIdentityDocAttention.setText(getString(R.string.document_approved));
                                binding.txtIdentityDocAttention.setTextColor(ResourcesCompat.getColor(getResources(),R.color.green4,null));
                            }
                            if (jsonResponse.getData().getIdentificationExpiry())
                            {
                                binding.attention3Img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_warning,null));
                                binding.attention3Img.setColorFilter( ResourcesCompat.getColor( getResources(), R.color.red2,null));
                                binding.txtIdentityDocAttention.setText(getString(R.string.expired));
                                binding.txtIdentityDocAttention.setTextColor(ResourcesCompat.getColor(getResources(),R.color.red2,null));
                            }


                            if (jsonResponse.getBackgroundApprovalStatus().equalsIgnoreCase("1"))
                            {
                                binding.attention4Img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_thumb,null));
                                binding.attention4Img.setColorFilter( ResourcesCompat.getColor( getResources(), R.color.app_clr,null));
                                binding.txtBackgroundDocAttention.setText(getString(R.string.document_approved));
                                binding.txtBackgroundDocAttention.setTextColor(ResourcesCompat.getColor(getResources(),R.color.green4,null));

                            }

                            if(jsonResponse.getData().getInsuranceApproveStatus().equalsIgnoreCase("1"))
                            {
                                binding.attention2Img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_thumb,null));
                                binding.attention2Img.setColorFilter( ResourcesCompat.getColor( getResources(), R.color.app_clr,null));
                                binding.txtCarInsuranceAttention.setText(getString(R.string.document_approved));
                                binding.txtCarInsuranceAttention.setTextColor(ResourcesCompat.getColor(getResources(),R.color.green4,null));
                            }
                            if(jsonResponse.getData().getInsuranceExpiry())
                            {
                                binding.attention2Img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_warning,null));
                                binding.attention2Img.setColorFilter( ResourcesCompat.getColor( getResources(), R.color.red2,null));
                                binding.txtCarInsuranceAttention.setText(getString(R.string.expired));
                                binding.txtCarInsuranceAttention.setTextColor(ResourcesCompat.getColor(getResources(),R.color.red2,null));
                            }

                            if(jsonResponse.getData().getLicenseApproveStatus().equalsIgnoreCase("1"))
                            {
                                binding.attention1Img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_thumb,null));
                                binding.attention1Img.setColorFilter( ResourcesCompat.getColor( getResources(), R.color.app_clr,null));
                                binding.txtDrivingLicenceAttention.setText(getString(R.string.document_approved));
                                binding.txtDrivingLicenceAttention.setTextColor(ResourcesCompat.getColor(getResources(),R.color.green4,null));
                            }
                            if(jsonResponse.getData().getLicenseExpiry())
                            {
                                binding.attention1Img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_warning,null));
                                binding.attention1Img.setColorFilter( ResourcesCompat.getColor( getResources(), R.color.red2,null));
                                binding.txtDrivingLicenceAttention.setText(getString(R.string.expired));
                                binding.txtDrivingLicenceAttention.setTextColor(ResourcesCompat.getColor(getResources(),R.color.red2,null));
                            }

                            if(jsonResponse.getData().getCarRegistrationApproveStatus().equalsIgnoreCase("1"))
                            {
                                binding.attentionImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_thumb,null));
                                binding.attentionImg.setColorFilter( ResourcesCompat.getColor( getResources(), R.color.app_clr,null));
                                binding.txtCarRegAttention.setText(getString(R.string.document_approved));
                                binding.txtCarRegAttention.setTextColor(ResourcesCompat.getColor(getResources(),R.color.green4,null));
                            }
                            if(jsonResponse.getData().getCarExpiry())
                            {
                                binding.attentionImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_warning,null));
                                binding.attentionImg.setColorFilter( ResourcesCompat.getColor( getResources(), R.color.red2,null));
                                binding.txtCarRegAttention.setText(getString(R.string.expired));
                                binding.txtCarRegAttention.setTextColor(ResourcesCompat.getColor(getResources(),R.color.red2,null));
                            }

                            if(jsonResponse.getData().getInspectionApprovalStatus().equalsIgnoreCase("1"))
                            {
                                binding.attention5Img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_thumb,null));
                                binding.attention5Img.setColorFilter( ResourcesCompat.getColor( getResources(), R.color.app_clr,null));
                                binding.txtInspectionDocAttention.setText(getString(R.string.document_approved));
                                binding.txtInspectionDocAttention.setTextColor(ResourcesCompat.getColor(getResources(),R.color.green4,null));
                            }

                            if(jsonResponse.getData().getInspectionExpiry())
                            {
                                binding.attention5Img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_warning,null));
                                binding.attention5Img.setColorFilter( ResourcesCompat.getColor( getResources(), R.color.red2,null));
                                binding.txtInspectionDocAttention.setText(getString(R.string.expired));
                                binding.txtInspectionDocAttention.setTextColor(ResourcesCompat.getColor(getResources(),R.color.red2,null));
                            }
                        }
                        catch (Exception ex)
                        {
                            loading.cancel();
                        }
                        loading.cancel();
                    } else {
                        loading.cancel();

                    }
                }
            }

            @Override
            public void onFailure(Call<DocApprovalResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                loading.cancel();
            }
        });
    }


}