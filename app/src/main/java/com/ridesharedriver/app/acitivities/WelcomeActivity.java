package com.ridesharedriver.app.acitivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ridesharedriver.app.R;
import com.ridesharedriver.app.Server.Server;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.custom.CheckConnection;
import com.ridesharedriver.app.databinding.ActivityWelcomeBinding;
import com.ridesharedriver.app.pojo.upload_docs.DocStatusResponse;
import com.ridesharedriver.app.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;

//Welcome Screen
public class WelcomeActivity extends AppCompatActivity  {

    ActivityWelcomeBinding binding;
    private boolean carStatus= false,insuranceStatus = false, licenceStatus=false, inspectionStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.txtUserName.setText(String.format("Welcome, %s", SessionManager.getUserName()));

        updateloginlogout();

        binding.goCarRegImg.setOnClickListener(e->{
            Intent intent = new Intent(WelcomeActivity.this,UploadDocs.class);
            intent.putExtra("welcome", true);
            intent.putExtra("car",true);
            intent.putExtra("licence",false);
            intent.putExtra("insurance",false);
            intent.putExtra("inspection",false);
            startActivity(intent);
        });

        binding.goDriveLicenceImg.setOnClickListener(e->{
            Intent intent = new Intent(WelcomeActivity.this,UploadDocs.class);
            intent.putExtra("welcome", true);
            intent.putExtra("licence",true);
            intent.putExtra("car",false);
            intent.putExtra("insurance",false);
            intent.putExtra("inspection",false);
            startActivity(intent);
        });

        binding.goCarInsuranceImg.setOnClickListener(e->{
            Intent intent = new Intent(WelcomeActivity.this,UploadDocs.class);
            intent.putExtra("welcome", true);
            intent.putExtra("insurance",true);
            intent.putExtra("licence",false);
            intent.putExtra("car",false);
            intent.putExtra("inspection",false);
            startActivity(intent);
        });

        binding.goInspectionDocImg.setOnClickListener(e->{
            Intent intent = new Intent(WelcomeActivity.this,UploadDocs.class);
            intent.putExtra("welcome", true);
            intent.putExtra("insurance",false);
            intent.putExtra("licence",false);
            intent.putExtra("car",false);
            intent.putExtra("inspection",true);
            startActivity(intent);
        });
    }



    //here we check documents status
    public void getDocsStatus() {

        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<DocStatusResponse> call = apiService.getDocsStatus("Bearer " + SessionManager.getKEY());
        call.enqueue(new Callback<DocStatusResponse>() {
            @Override
            public void onResponse(Call<DocStatusResponse> call, retrofit2.Response<DocStatusResponse> response) {
                DocStatusResponse jsonResponse = response.body();
                if (jsonResponse != null) {
                    if (jsonResponse.getStatus()) {
                       if(!jsonResponse.getCarExpiry())
                       {
                           carStatus = true;
                           SessionManager.setCarStatus(true);
                       }

                        if(!jsonResponse.getInspectionExpiry())
                        {
                            inspectionStatus = true;
                            SessionManager.setInspectionStatus(true);
                        }
                       if(!jsonResponse.getInsuranceExpiry())
                       {
                           insuranceStatus= true;
                           SessionManager.setInsuranceStatus(true);
                       }
                       if(!jsonResponse.getLicenseExpiry())
                       {
                           licenceStatus = true;
                           SessionManager.setLicenceStatus(true);
                       }


                        if(SessionManager.getCarStatus() ||carStatus)
                        {
                            Log.e("Welcome","car");
                            binding.txtCarRegAttention.setText(getString( R.string.uploaded_successfully));
                            binding.txtCarRegAttention.setTextColor( ResourcesCompat.getColor( getResources(), R.color.green4,null));
                            binding.attentionImg.setColorFilter( ResourcesCompat.getColor( getResources(), R.color.green4,null));
                            binding.attentionImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_thumb,null));
                        }
                        if(SessionManager.getInsuranceStatus() || insuranceStatus)
                        {
                            Log.e("Welcome","insurance");
                            binding.txtCarInsuranceAttention.setText(getString( R.string.uploaded_successfully));
                            binding.txtCarInsuranceAttention.setTextColor( ResourcesCompat.getColor( getResources(), R.color.green4,null));
                            binding.attention2Img.setColorFilter( ResourcesCompat.getColor( getResources(), R.color.green4,null));
                            binding.attention2Img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_thumb,null));
                        }
                        if(SessionManager.getLicenceStatus() || licenceStatus)
                        {
                            Log.e("Welcome","licence");
                            binding.txtDrivingLicenceAttention.setText(getString( R.string.uploaded_successfully));
                            binding.txtDrivingLicenceAttention.setTextColor( ResourcesCompat.getColor( getResources(), R.color.green4,null));
                            binding.attention1Img.setColorFilter( ResourcesCompat.getColor( getResources(), R.color.green4,null));
                            binding.attention1Img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_thumb,null));

                        }

                        if(SessionManager.getInspectionStatus() ||inspectionStatus)
                        {
                            Log.e("Welcome","car");
                            binding.txtInspectionDocAttention.setText(getString( R.string.uploaded_successfully));
                            binding.txtInspectionDocAttention.setTextColor( ResourcesCompat.getColor( getResources(), R.color.green4,null));
                            binding.attention3Img.setColorFilter( ResourcesCompat.getColor( getResources(), R.color.green4,null));
                            binding.attention3Img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_thumb,null));
                        }

                        if((SessionManager.getCarStatus()&& SessionManager.getLicenceStatus() && SessionManager.getInsuranceStatus()&&SessionManager.getInspectionStatus())||
                                (carStatus && licenceStatus && insuranceStatus && inspectionStatus))
                        {
                            SessionManager.setDocStatus(false);
                            startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                            finish();
                        }


                    }
                }
            }

            @Override
            public void onFailure(Call<DocStatusResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
            }
        });
    }

    //update login Logout api
    public void updateloginlogout() {

        RequestParams params = new RequestParams();
        params.put("status", "1");

        Server.post("updateloginlogout", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {

                    if (response.has("status") && response.getBoolean("status")) {
                        CheckConnection checkConnection = new CheckConnection(WelcomeActivity.this);
                        checkConnection.isAnonymouslyLoggedIn();


                    } else {
                        Toast.makeText(WelcomeActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(WelcomeActivity.this, getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                //swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDocsStatus();
        updateloginlogout();
    }


}