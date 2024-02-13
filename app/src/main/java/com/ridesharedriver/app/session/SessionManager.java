package com.ridesharedriver.app.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ridesharedriver.app.acitivities.LoginActivity;
import com.ridesharedriver.app.pojo.User;

public class SessionManager {

    static SharedPreferences pref;
    public static final String KEY_NAME = "name";
    public static final String AVATAR = "avatar";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_COUNTRY_CODE = "countryCode";
    public static final String KEY_VEHICLE_NO = "vehicle_no";
    public static final String KEY_PAYPALID = "paypal_id";
    public static final String KEY_VEHICLE = "vehicle";
    public static final String GCM_TOKEN = "gcm_token";
    public static final String KEY_EMAIL = "email";
    public static final String FARE_UNIT = "unit";
    public static final String LOGIN_AS = "login_as";
    public static final String USER_ID = "user_id";
    public static final String IS_ONLINE = "false";
    public static final String USER = "user";
    public static final String BRAND = "brand";
    public static final String MODEL = "model";
    public static final String YEAR = "year";
    public static final String COLOR = "color";
    public static final String DRivingLicence = "licence";
    public static final String VehicleInsurance = "insurance";
    public static final String VehicleNo = "no";
    public static final String VehiclePermit = "permit";
    public static final String VehicleRegistartion = "registration";
    private static final String PREF_NAME = "taxiapp";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY = "key";
    public static final String SWITCH_STATE="state";
    public static final String ACTIVE_RIDE_ID="active_ride_id";
    public static final String OLD_PASSWORD="old_password";
    public static final String DOC_STATUS="doc_status";
    public static final String CAR_STATUS="car_status";
    public static final String INSURANCE_STATUS="insurance_status";
    public static final String LICENCE_STATUS="licence_status";
    public static final String INSPECTION_STATUS="inspection_status";
    public static final String LOGIN_TIME="login_time";
    public static final String TOTAL_TIME="total_time";
    public static final String CURRENT_TIME="current_time";
    public static final String OFFLINE_ALERT="offline_alert";
    public static final String Is_LOGOUT="is_logout";
    public static final String LAST_TIME="last_time";
    public static final String DEVICE_ID="device_id";

    public SessionManager() {
    }

    public static void initialize(Context context) {
        if (pref == null)
            pref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static void setIsLogoutStatus(Boolean status) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putBoolean(Is_LOGOUT, status);
        prefsEditor.commit();
    }

    public static Boolean getIsLogoutStatus() {
        return pref.getBoolean(Is_LOGOUT, true);
    }

    public static void setOfflineAlertStatus(Boolean alertStatus) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putBoolean(OFFLINE_ALERT, alertStatus);
        prefsEditor.commit();
    }

    public static Boolean getOfflineAlertStatus() {
        return pref.getBoolean(OFFLINE_ALERT, false);
    }


    public static void setDeviceId(String id) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(DEVICE_ID, id);
        editor.commit();
    }

    public static String getDeviceId() {
        return pref.getString(DEVICE_ID,"");
    }



    public static void setCarStatus(Boolean docstatus) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putBoolean(CAR_STATUS, docstatus);
        prefsEditor.commit();
    }

    public static Boolean getCarStatus() {
        return pref.getBoolean(CAR_STATUS, false);
    }

    public static void setLicenceStatus(Boolean docstatus) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putBoolean(LICENCE_STATUS, docstatus);
        prefsEditor.commit();
    }
    public static Boolean getInspectionStatus() {
        return pref.getBoolean(INSPECTION_STATUS, false);
    }
    public static void setInspectionStatus(Boolean docstatus) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putBoolean(INSPECTION_STATUS, docstatus);
        prefsEditor.commit();
    }

    public static Boolean getLicenceStatus() {

        return pref.getBoolean(LICENCE_STATUS, false);
    }

    public static void setInsuranceStatus(Boolean docstatus) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putBoolean(INSURANCE_STATUS, docstatus);
        prefsEditor.commit();
    }

    public static Boolean getInsuranceStatus() {
        return pref.getBoolean(INSURANCE_STATUS, false);
    }

    public static void setDocStatus(Boolean docstatus) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putBoolean(DOC_STATUS, docstatus);
        prefsEditor.commit();
    }

    public static Boolean getDocStatus() {
        return pref.getBoolean(DOC_STATUS, false);
    }

    public static void setLoginTime(String loginTime) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(LOGIN_TIME, loginTime);
        prefsEditor.commit();
    }

    public static String getLoginTime() {
        return pref.getString(LOGIN_TIME, null);
    }


    public static void setTotalTime(String totaltime) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(TOTAL_TIME, totaltime);
        prefsEditor.commit();
    }

    public static String getTotalTime() {
        return pref.getString(TOTAL_TIME, null);
    }



    public static void setCurrentTime(String currentTime) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(CURRENT_TIME, currentTime);
        prefsEditor.commit();
    }

    public static String getCurrentTime() {
        return pref.getString(CURRENT_TIME, null);
    }


    public static void setGcmToken(String gcmToken) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(GCM_TOKEN, gcmToken);
        prefsEditor.commit();
    }

    public String getGcmToken() {
        return pref.getString(GCM_TOKEN, null);
    }

    public static void setStatus(Boolean staus) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_LOGIN, staus);
        editor.commit();
    }
    public static void setOldPassword(String password)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(OLD_PASSWORD, password);
        editor.commit();
    }

    public static String getOldPassword()
    {
        return pref.getString(OLD_PASSWORD, null);
    }

    public static void setUserMobile(String mobile) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_MOBILE, mobile);
        editor.commit();
    }
    public static String getUserMobile() {
        //return getUser() == null ? "" : getUser().getUser_id();
        return pref.getString(KEY_MOBILE, null);
    }

    public static String getUserCountryCode() {
        //return getUser() == null ? "" : getUser().getUser_id();
        return pref.getString(KEY_COUNTRY_CODE, null);
    }

    public static void setUserCountryCode(String CountryCode) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_COUNTRY_CODE, CountryCode);
        editor.commit();
    }



    public static void setUserVehicleNo(String vehicleNo) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_VEHICLE_NO, vehicleNo);
        editor.commit();
    }

    public static String getUserVehicleNo() {
        //return getUser() == null ? "" : getUser().getUser_id();
        return pref.getString(KEY_VEHICLE_NO, null);
    }


    public static void setSwitchState(boolean state) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(SWITCH_STATE, state);
        editor.commit();
    }

    public static boolean getSwitchState(boolean state) {
        //return getUser() == null ? "" : getUser().getUser_id();
        return pref.getBoolean(SWITCH_STATE, state);
    }

    public static void setActiveRideId(String ride_id) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ACTIVE_RIDE_ID, ride_id);
        editor.commit();
    }

    public static String getActiveRideId() {
        //return getUser() == null ? "" : getUser().getUser_id();
        return pref.getString(ACTIVE_RIDE_ID, null);
    }

    public static Boolean getStatus() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public static void setUserId(String userid) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_ID, userid);
        editor.commit();
    }

    public static String getUserId() {
        //return getUser() == null ? "" : getUser().getUser_id();
        return pref.getString(USER_ID, null);
    }

    public static void setUser(String user) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(USER, user);
        prefsEditor.commit();
    }

    public static User getUser() {
        Gson gson = new Gson();
        return gson.fromJson(pref.getString(USER, null), User.class);
    }

    public static void setKEY(String k) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(KEY, k);
        prefsEditor.commit();
    }

    public static String getKEY() {
        //return getUser() == null ? "" : getUser().getKey();
        return pref.getString(KEY, null);
    }


    public static void setLastTime(String time) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(LAST_TIME, time);
        prefsEditor.commit();
    }

    public static String getLastTime() {
        return pref.getString(LAST_TIME, null);
    }



    public static void setUnit(String unit) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(FARE_UNIT, unit);
        prefsEditor.commit();
    }

    public static void setUserEmail(String email) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public static String getUserEmail() {
        //return getUser() == null ? "" : getUser().getUser_id();
        return pref.getString(KEY_EMAIL, null);
    }

    public static void setUserName(String email) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_NAME, email);
        editor.commit();
    }

    public static String getUserName() {
        //return getUser() == null ? "" : getUser().getUser_id();
        return pref.getString(KEY_NAME, null);
    }

    public static String getUnit() {
        return pref.getString(FARE_UNIT, null);
    }

    public void setpaypalId(String k) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(KEY_PAYPALID, k);
        prefsEditor.commit();
    }

    public String getPaypalId() {
        return getUser().getPaypal_id();
    }

    public static void setAvatar(String avatar) {
    }

    public static String getAvatar() {
        return getUser() == null ? "" : getUser().getAvatar();
    }




    /*--------------------------------------------------------------------------------------------------------------------------------------------*/
    public static void logoutUser(Context _context) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putBoolean(IS_LOGIN, false);
        prefsEditor.clear();
        prefsEditor.apply();
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _context.startActivity(i);
    }

    public static String getName() {
        return getUser() == null ? "" : getUser().getName();
    }

    public String getEmail() {
        return getUser() == null ? "" : getUser().getEmail();
    }

    public String getMobile() {
        return getUser().getMobile();
    }
}
