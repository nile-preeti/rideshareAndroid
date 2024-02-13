package com.ridesharedriver.app.acitivities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ridesharedriver.app.R;
import com.ridesharedriver.app.Server.Server;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.custom.CheckConnection;
import com.ridesharedriver.app.custom.SetCustomFont;
import com.ridesharedriver.app.custom.Utils;
import com.ridesharedriver.app.pojo.changepassword.ChangePasswordResponse;
import com.ridesharedriver.app.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;

//Login Screen
public class LoginActivity extends ActivityManagePermission {

    private static final String TAG = "FIREBASE_MESSAGE";
    EditText input_email, input_password;
    AppCompatButton login;
    TextView as, txt_createaccount, forgot_password;
    String token;
    Context context;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        bindView();
        //several clickable parts of multiple texts in a single textview
        SpannableString ss = new SpannableString("Not a member? Sign Up");
        final ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // redirect to Register Page.
                Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        };

        ss.setSpan(span1, 14, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_createaccount.setText(ss);
        txt_createaccount.setMovementMethod(LinkMovementMethod.getInstance());
        txt_createaccount.setHighlightColor(Color.TRANSPARENT);

        //Here we get Firebase FCM token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();
                        SessionManager.setGcmToken(token);
                        // Log and toast
                      /*  String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show()*/
                        ;
                    }
                });



       /* FirebaseMessaging.getInstance().getToken().addOnSuccessListener(LoginActivity.this,new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                token = s;
                SessionManager.setGcmToken(token);
            }*/
        // });
      /*  FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

            }
        });*/

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getCurrentFocus();

                if (view != null) {
                    Utils.hideKeyboard(getApplicationContext(), view);
                }
                if (Utils.haveNetworkConnection(getApplicationContext())) {
                    if (validate()) {
                        login(input_email.getText().toString().trim(), input_password.getText().toString().trim());
                        SessionManager.setOldPassword(input_password.getText().toString().trim());
                    } else {
                        // do nothing
                    }
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //LoginApi
    public void login(String email, String password) {

        String deviceId = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        Log.d("deviceId",deviceId);

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        params.put("utype", "2");
        params.put("gcm_token", token);
        params.put("device_type", "android");
        params.put("device_token", deviceId);
        SessionManager.setDeviceId(deviceId);

        Server.post("login", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {

                    if (response.has("status") && response.getBoolean("status")) {
                        CheckConnection checkConnection = new CheckConnection(LoginActivity.this);
                        checkConnection.isAnonymouslyLoggedIn();


                        Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        SessionManager.setKEY(response.getString("token"));
                        Log.d("tokenSaurabh", response.getString("token"));
                        SessionManager.setStatus(response.getBoolean("status"));

                        SessionManager.setLoginTime(response.getJSONObject("data").getString("login_time"));
                        SessionManager.setCurrentTime(response.getJSONObject("data").getString("current_time"));

                        SessionManager.setUserId(response.getJSONObject("data").getString("user_id"));
                        SessionManager.setUserEmail(response.getJSONObject("data").getString("email"));
                        SessionManager.setUserName(response.getJSONObject("data").getString("name"));
                        SessionManager.setUserMobile(response.getJSONObject("data").getString("mobile"));
                        SessionManager.setUserCountryCode(response.getJSONObject("data").getString("country_code"));
                        //  SessionManager.setUserVehicleNo(response.getJSONObject("data").getString("vehicle_no"));
                        boolean isLicenceExpire = false, isInsuranceExpire = false, isCarExpire = false, isInspectionExpire = false;
                        try {
                            isLicenceExpire = response.getJSONObject("data").getBoolean("license_expiry");
                            isInsuranceExpire = response.getJSONObject("data").getBoolean("insurance_expiry");
                            isCarExpire = response.getJSONObject("data").getBoolean("car_expiry");
                            isInspectionExpire = response.getJSONObject("data").getBoolean("insurance_expiry");
                        } catch (Exception ex) {

                        }
                        if (isLicenceExpire || isInsuranceExpire || isCarExpire || isInspectionExpire) {
                            SessionManager.setDocStatus(true);
                            startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
                            finish();
                        } else {
                            SessionManager.setDocStatus(false);
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        }

                        try {
                            SessionManager.setIsLogoutStatus(false);
                        } catch (Exception ex) {

                        }
                    } else {
                        Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                }
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

    public void bindView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        as = (TextView) findViewById(R.id.as);
        txt_createaccount = (TextView) findViewById(R.id.txt_createaccount);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);
        login = (AppCompatButton) findViewById(R.id.login);
        forgot_password = (TextView) findViewById(R.id.txt_forgotpassword);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.haveNetworkConnection(getApplicationContext())) {
                    changepassword_dialog();
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //validation for user inputs
    public Boolean validate() {
        Boolean value = true;
        if (input_email.getText().toString().equals("") && !android.util.Patterns.EMAIL_ADDRESS.matcher(input_email.getText().toString().trim()).matches()) {
            value = false;
            input_email.setError(getString(R.string.email_invalid));
        } else {
            input_email.setError(null);
        }
        if (input_password.getText().toString().trim().equals("")) {
            value = false;
            input_password.setError(getString(R.string.fiels_is_required1));
        } else {
            input_password.setError(null);
        }
        return value;
    }

    public void applyfonts() {
        if (getCurrentFocus() != null) {
            SetCustomFont setCustomFont = new SetCustomFont();
            setCustomFont.overrideFonts(getApplicationContext(), getCurrentFocus());
        } else {
            Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Medium.otf");
            Typeface font1 = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
            input_email.setTypeface(font1);
            input_password.setTypeface(font1);
            login.setTypeface(font);
            txt_createaccount.setTypeface(font);
            forgot_password.setTypeface(font);
        }
    }

    public void resetPassword(String email, final Dialog dialog) {

        RequestParams params = new RequestParams();
        params.put("email", email);
        Server.post(Server.FORGOT_PASSWORD, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        String data = response.getString("data");
                        if (dialog != null) {
                            dialog.cancel();
                        }
                        Toast.makeText(LoginActivity.this, data, Toast.LENGTH_LONG).show();

                    } else {
                        String data = response.getString("data");
                        Toast.makeText(LoginActivity.this, data, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(LoginActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //ChangePassword dialog
    public void changepassword_dialog() {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.password_reset);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView message = (TextView) dialog.findViewById(R.id.message);
        final EditText email = (EditText) dialog.findViewById(R.id.input_email);
        AppCompatButton btn_send_otp = (AppCompatButton) dialog.findViewById(R.id.btn_send_otp);
        AppCompatButton btn_cancel = (AppCompatButton) dialog.findViewById(R.id.btn_cancel);
        dialog.setCancelable(false);

        //  Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Medium.otf");
        Typeface font = Typeface.createFromAsset(getAssets(), "font/montserrat_regular.ttf");
        // Typeface font1 = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
        Typeface font1 = Typeface.createFromAsset(getAssets(), "font/montserrat_bold.ttf");
        Typeface font2 = Typeface.createFromAsset(getAssets(), "font/montserrat_black.ttf");
//        btn_send_otp.setTypeface(font1);
//        btn_cancel.setTypeface(font1);
        email.setTypeface(font);
        title.setTypeface(font1);
        message.setTypeface(font);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        btn_send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LoginActivity.this.getCurrentFocus();
                if (view != null) {
                    Utils.hideKeyboard(LoginActivity.this, view);
                }
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
                    // dialog.cancel();
                    //resetPassword(email.getText().toString().trim(), dialog);

                    if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                        sendOTP(email.getText().toString().trim(), dialog);
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
                    }
                } else {
                    email.setError(getString(R.string.email_invalid));
                    // Toast.makeText(LoginActivity.this, "email is invalid", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show();
    }

    //resetPassword Api
    public void resetPassword(String email, String OTP, String pass, final Dialog dialog) {
        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Resetting Password.....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Map<String, String> details = new HashMap<>();
        details.put("email", email);
        details.put("otp", OTP);
        details.put("password", pass);

        ApiNetworkCall apiService = ApiClient.getApiService();
        Call<ChangePasswordResponse> call = apiService.resetPassword(details);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                ChangePasswordResponse jsonResponse = response.body();
                if (jsonResponse.getStatus()) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.cancel();
                    //  resetpassword_dialog();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    // dialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                progressDialog.dismiss();
                dialog.cancel();
            }
        });
    }

    //send otp Api
    private void sendOTP(String email, final Dialog dialog) {
        Map<String, String> details = new HashMap<>();
        details.put("email", email);
        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Sending OTP.....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<ChangePasswordResponse> call = apiService.sendOTP(details);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                ChangePasswordResponse jsonResponse = response.body();
                if (jsonResponse.getStatus()) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.cancel();
                    resetpassword_dialog(email);

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    // dialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                progressDialog.dismiss();
                dialog.cancel();
            }
        });
    }

    //ResetPasswordDialog
    public void resetpassword_dialog(String userEmail) {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_reset_password);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        final EditText email = (EditText) dialog.findViewById(R.id.input_email);
        email.setText(userEmail);

        final EditText otp = (EditText) dialog.findViewById(R.id.input_otp);
        final EditText password = (EditText) dialog.findViewById(R.id.input_Password);
        final EditText cpassword = (EditText) dialog.findViewById(R.id.input_confirmPassword);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView message = (TextView) dialog.findViewById(R.id.message);

        AppCompatButton btn_reset_password = (AppCompatButton) dialog.findViewById(R.id.btn_reset_password);
        AppCompatButton btn_cancel = (AppCompatButton) dialog.findViewById(R.id.btn_cancel);

        // Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Medium.otf");
        Typeface font = Typeface.createFromAsset(getAssets(), "font/montserrat_regular.ttf");
        // Typeface font1 = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
        Typeface font1 = Typeface.createFromAsset(getAssets(), "font/montserrat_bold.ttf");
//        btn_reset_password.setTypeface(font1);
//        btn_cancel.setTypeface(font1);
        email.setTypeface(font);
        title.setTypeface(font1);
        message.setTypeface(font);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        btn_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LoginActivity.this.getCurrentFocus();
                if (view != null) {
                    CheckConnection.hideKeyboard(LoginActivity.this, view);
                }

                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
                    //dialog.cancel();
                    // resetPassword(email.getText().toString().trim(), dialog);
                    if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                        resetPassword(email.getText().toString().trim(), otp.getText().toString().trim(), password.getText().toString().trim(), dialog);
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
                    }

                } else if (!(otp.getText().toString().trim()).equals(otp.getText().toString().trim())) {
                    Toast.makeText(LoginActivity.this, "Please enter OTP", Toast.LENGTH_LONG).show();
                    return;
                } else if (password.getText().toString().trim().isEmpty() || password.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(LoginActivity.this, "Password and Confirm password does not matches", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    email.setError(getString(R.string.email_invalid));
                    // Toast.makeText(LoginActivity.this, "email is invalid", Toast.LENGTH_LONG).show();
                }


            }
        });
        dialog.show();

    }
}
