package com.ridesharedriver.app.pojo.upload_docs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DocStatusResponse {
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("license_expiry")
    @Expose
    private Boolean licenseExpiry;
    @SerializedName("insurance_expiry")
    @Expose
    private Boolean insuranceExpiry;
    @SerializedName("car_expiry")
    @Expose
    private Boolean carExpiry;
    @SerializedName("driver_rest_time")
    @Expose
    private Boolean driverRestTime;
    @SerializedName("change_vehicle")
    @Expose
    private Boolean changeVehicle;
    @SerializedName("identification_expiry")
    @Expose
    private Boolean identificationExpiry;
    @SerializedName("inspection_expiry")
    @Expose
    private Boolean inspectionExpiry;


    public Boolean getInspectionExpiry() {
        return inspectionExpiry;
    }

    public void setInspectionExpiry(Boolean inspectionExpiry) {
        this.inspectionExpiry = inspectionExpiry;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public Boolean getDriverRestTime() {
        return driverRestTime;
    }

    public void setDriverRestTime(Boolean driverRestTime) {
        this.driverRestTime = driverRestTime;
    }

    public Boolean getChangeVehicle() {
        return changeVehicle;
    }

    public void setChangeVehicle(Boolean changeVehicle) {
        this.changeVehicle = changeVehicle;
    }

    public Boolean getIdentificationExpiry() {
        return identificationExpiry;
    }

    public void setIdentificationExpiry(Boolean identificationExpiry) {
        this.identificationExpiry = identificationExpiry;
    }

}
