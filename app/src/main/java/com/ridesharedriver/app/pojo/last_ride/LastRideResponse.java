package com.ridesharedriver.app.pojo.last_ride;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastRideResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private LastRideData data;

    @SerializedName("license_expiry")
    @Expose
    private Boolean licenseExpiry;
    @SerializedName("user_status")
    @Expose
    private Boolean userStatus;
    @SerializedName("insurance_expiry")
    @Expose
    private Boolean insuranceExpiry;
    @SerializedName("car_expiry")
    @Expose
    private Boolean carExpiry;
    @SerializedName("driver_rest_time")
    @Expose
    private Boolean isDriverRestTime;
    @SerializedName("change_vehicle")
    @Expose
    private Boolean isChangeVehicle;
    @SerializedName("identification_expiry")
    @Expose
    private Boolean isIdentityExpire;
    @SerializedName("inspection_expiry")
    @Expose
    private Boolean inspectionExpiry;

    public Boolean getInspectionExpiry() {
        return inspectionExpiry;
    }

    public void setInspectionExpiry(Boolean inspectionExpiry) {
        this.inspectionExpiry = inspectionExpiry;
    }

    public Boolean getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Boolean userStatus) {
        this.userStatus = userStatus;
    }

    public Boolean getIdentityExpire() {
        return isIdentityExpire;
    }

    public void setIdentityExpire(Boolean identityExpire) {
        isIdentityExpire = identityExpire;
    }

    public Boolean getChangeVehicle() {
        return isChangeVehicle;
    }

    public void setChangeVehicle(Boolean changeVehicle) {
        isChangeVehicle = changeVehicle;
    }

    public Boolean getDriverRestTime() {
        return isDriverRestTime;
    }

    public void setDriverRestTime(Boolean driverRestTime) {
        isDriverRestTime = driverRestTime;
    }

    public Boolean getLicenseExpiry() {
        return licenseExpiry;
    }

    public void setLicenseExpiry(Boolean licenseExpiry) {
        this.licenseExpiry = licenseExpiry;
    }

    public Boolean getInsuranceExpiry() {
        return insuranceExpiry;
    }

    public void setInsuranceExpiry(Boolean insuranceExpiry) {
        this.insuranceExpiry = insuranceExpiry;
    }

    public Boolean getCarExpiry() {
        return carExpiry;
    }

    public void setCarExpiry(Boolean carExpiry) {
        this.carExpiry = carExpiry;
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

    public LastRideData getData() {
        return data;
    }

    public void setData(LastRideData data) {
        this.data = data;
    }

}