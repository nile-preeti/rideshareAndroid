package com.ridesharedriver.app.pojo.driverstatus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateDriverStatus {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("login_time")
    @Expose
    private String loginTime;
    @SerializedName("current_time")
    @Expose
    private String currentTime;

    @SerializedName("total_working_hour")
    @Expose
    private String total_working_hour;

    public String gettotal_working_hour() {
        return total_working_hour;
    }

    public void settotal_working_hour(String total_working_hour) {
        this.total_working_hour = total_working_hour;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
