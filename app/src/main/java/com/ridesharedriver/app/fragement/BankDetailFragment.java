package com.ridesharedriver.app.fragement;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ridesharedriver.app.R;
import com.ridesharedriver.app.acitivities.HomeActivity;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.custom.CheckConnection;
import com.ridesharedriver.app.pojo.add_bank_detail.AddBankDetailResponse;
import com.ridesharedriver.app.pojo.add_bank_detail.BankDetailData;
import com.ridesharedriver.app.session.SessionManager;
import com.ridesharedriver.app.utils.ShowAlert;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

//Bank Detail
public class BankDetailFragment extends Fragment {

    private View rootView;
    private EditText et_name, et_bank_name, et_account_number, et_routing_number;
    private Button btnAddBankAccount;
    private ImageView textViewEditDetails;
    private BankDetailData bankDetailData = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    private String name = "", bankName = "", routingNumber = "", accountNumber = "", user_id="";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        ((HomeActivity) getActivity()).fontToTitleBar("Bank Detail");
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_bank_detail, container, false);

        bindView(savedInstanceState);

        textViewEditDetails.setOnClickListener(e -> {

            setKeyboardFocus(et_bank_name);
            et_bank_name.setFocusableInTouchMode(true);
            InputMethodManager imm = (InputMethodManager) requireContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            textViewEditDetails.setColorFilter(getResources().getColor(R.color.red1));

            et_name.setEnabled(true);
            et_bank_name.setEnabled(false);
            et_routing_number.setEnabled(false);
            et_account_number.setEnabled(false);
            btnAddBankAccount.setEnabled(true);
        });

        btnAddBankAccount.setOnClickListener(e -> {
            name = et_name.getText().toString();
//            bankName = et_bank_name.getText().toString();
            bankName = "Republic Bank & Trust";
//            routingNumber = et_routing_number.getText().toString();
            routingNumber = "264171241";
            accountNumber = et_account_number.getText().toString();
            textViewEditDetails.setColorFilter(getResources().getColor(R.color.app_clr));
            if (validate()) {
                if (CheckConnection.haveNetworkConnection(requireActivity())) {
                    addBankDetail(name, bankName, routingNumber, accountNumber);
                } else {
                    Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                }
            }
        });
        try {

            MasterKey masterKeyAlias = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                // masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                masterKeyAlias = new MasterKey.Builder(requireActivity().getApplicationContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();
            }
            assert masterKeyAlias != null;
            sharedPreferences = EncryptedSharedPreferences.create(requireActivity().getApplicationContext(),
                    "secret_shared_prefs_bank_detail",
                    masterKeyAlias,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getBankDetails();

        return rootView;
    }

    public static void setKeyboardFocus(final EditText primaryTextField) {
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                primaryTextField.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                primaryTextField.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
            }
        }, 100);
    }

    private void bindView(Bundle savedInstanceState) {
        et_account_number = rootView.findViewById(R.id.account_number);
        et_bank_name = rootView.findViewById(R.id.et_bank_name);
        et_name = rootView.findViewById(R.id.et_account_holder);
        et_routing_number = rootView.findViewById(R.id.et_routing_number);
        btnAddBankAccount = rootView.findViewById(R.id.btn_submit);
        textViewEditDetails = rootView.findViewById(R.id.tv_edit_bank_detail);
        textViewEditDetails.setColorFilter(getResources().getColor(R.color.app_clr));
    }

    private SharedPreferences sharedPreferences = null;

    @Override
    public void onResume() {
        super.onResume();
        textViewEditDetails.setColorFilter(getResources().getColor(R.color.app_clr));
//        if (sharedPreferences != null) {
//            textViewEditDetails.setVisibility(View.VISIBLE);
//            Gson gson = new Gson();
//            String json = sharedPreferences.getString("bank_detail", "");
//            BankDetailData bankDetailData = gson.fromJson(json, BankDetailData.class);
//            if (bankDetailData != null) {
//                et_name.setText(bankDetailData.getAccountHolderName());
//                et_bank_name.setText(bankDetailData.getBankName());
//                et_account_number.setText(bankDetailData.getAccountNumber());
//                et_routing_number.setText(bankDetailData.getRoutingNumber());
//                disableView();
//
//            }
//        } else {
//            textViewEditDetails.setVisibility(View.GONE);
//        }
    }

    private void disableView() {
        et_name.setEnabled(false);
        et_bank_name.setEnabled(false);
        et_routing_number.setEnabled(false);
        et_account_number.setEnabled(false);
        btnAddBankAccount.setEnabled(false);
    }

    //adding bank details
    public void addBankDetail(String name, String bankName, String routingNumber, String accountNumber) {
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading......");
        progressDialog.show();
        Map<String, String> details = new HashMap<>();
        details.put("account_holder_name", name.trim());
        details.put("bank_name", bankName.trim());
        details.put("routing_number", routingNumber.trim());
        details.put("account_number", accountNumber.trim());
//        details.put("user_id", user_id.trim());

        String token = "Bearer " + SessionManager.getKEY();
        Log.d("token", token);

        ApiNetworkCall apiService = ApiClient.getApiService();
        Call<AddBankDetailResponse> call = apiService.addBankAccount(token, details);
        call.enqueue(new Callback<AddBankDetailResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<AddBankDetailResponse> call, retrofit2.Response<AddBankDetailResponse> response) {
                AddBankDetailResponse jsonResponse = response.body();
                if (jsonResponse.getStatus()) {
                    bankDetailData = jsonResponse.getData();
                    Toast.makeText(requireContext(), jsonResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    et_bank_name.setText(bankDetailData.getBankName());
                    et_name.setText(bankDetailData.getAccountHolderName());
                    et_account_number.setText(bankDetailData.getAccountNumber());
                    et_routing_number.setText(bankDetailData.getRoutingNumber());
//                    if (sharedPreferences != null) {
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        Gson gson = new Gson();
//                        String json = gson.toJson(bankDetailData);
//                        editor.putString("bank_detail", json);
//                        editor.apply();
//                        disableView();
//                    }
                    progressDialog.cancel();
                    Log.e("Bank Status", "Success" + jsonResponse.getMessage());
                } else {

                    progressDialog.cancel();
                    Toast.makeText(requireContext(), jsonResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Bank Status", "Error" + jsonResponse.getMessage());
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onFailure(Call<AddBankDetailResponse> call, Throwable t) {

                validate();
                Log.e("Bank Status", "Failure " + t.getMessage());
                progressDialog.cancel();
            }
        });

    }

    //validation for user inputs
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public boolean validate() {
        if (name.isEmpty()) {
            new ShowAlert().showAlert(requireContext(), "Please Enter Account Holder Name");
            return false;
        }
//        else if (bankName.isEmpty()) {
//            new ShowAlert().showAlert(requireContext(), "Please Enter Bank Name");
//            return false;
//        }
//        else if (accountNumber.isEmpty()) {
//            new ShowAlert().showAlert(requireContext(), "Please Enter Account Number");
//            return false;
//        }
//        else if (routingNumber.isEmpty()) {
//            new ShowAlert().showAlert(requireContext(), "Please Enter Routing Number");
//            return false;
//        }
        return true;
    }

    //fetching bank details
    private void getBankDetails() {
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading......");
        progressDialog.show();

        String token = "Bearer " + SessionManager.getKEY();
        Log.d("token", token);

        ApiNetworkCall apiService = ApiClient.getApiService();
        Call<AddBankDetailResponse> call = apiService.fetchBankAccount(token);
        call.enqueue(new Callback<AddBankDetailResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<AddBankDetailResponse> call, retrofit2.Response<AddBankDetailResponse> response) {
                AddBankDetailResponse jsonResponse = response.body();
                if (jsonResponse != null) {
                    if (jsonResponse.getStatus()) {
                        bankDetailData = jsonResponse.getData();
//                        Toast.makeText(requireContext(), jsonResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        et_bank_name.setText(bankDetailData.getBankName());
                        et_name.setText(bankDetailData.getAccountHolderName());
                        et_account_number.setText(bankDetailData.getAccountNumber());
                        et_routing_number.setText(bankDetailData.getRoutingNumber());
                        user_id=bankDetailData.getUserId();
                        disableView();
                        progressDialog.cancel();
                        Log.e("Bank Status", "Success" + jsonResponse.getMessage());
                    } else {
                        progressDialog.cancel();
                        Toast.makeText(requireContext(), jsonResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Bank Status", "Error" + jsonResponse.getMessage());
                    }
                } else {
                    progressDialog.cancel();
                    Toast.makeText(requireContext(), "Some Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onFailure(Call<AddBankDetailResponse> call, Throwable t) {

                Log.e("Bank Status", "Failure " + t.getMessage());
                progressDialog.cancel();
            }
        });


    }

}