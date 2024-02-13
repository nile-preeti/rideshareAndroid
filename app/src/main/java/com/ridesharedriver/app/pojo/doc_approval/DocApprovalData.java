package com.ridesharedriver.app.pojo.doc_approval;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DocApprovalData {
    @SerializedName("license_approve_status")
    @Expose
    private String licenseApproveStatus;
    @SerializedName("insurance_approve_status")
    @Expose
    private String insuranceApproveStatus;
    @SerializedName("car_registration_approve_status")
    @Expose
    private String carRegistrationApproveStatus;
    @SerializedName("inspection_approval_status")
    @Expose
    private String inspectionApprovalStatus;
    @SerializedName("inspection_expiry")
    @Expose
    private Boolean inspectionExpiry;
    @SerializedName("change_vehicle")
    @Expose
    private Boolean changeVehicle;
    @SerializedName("car_expiry")
    @Expose
    private Boolean carExpiry;
    @SerializedName("insurance_expiry")
    @Expose
    private Boolean insuranceExpiry;
    @SerializedName("license_expiry")
    @Expose
    private Boolean licenseExpiry;
    @SerializedName("identification_expiry")
    @Expose
    private Boolean identificationExpiry;

    public Boolean getIdentificationExpiry() {
        return identificationExpiry;
    }

    public void setIdentificationExpiry(Boolean identificationExpiry) {
        this.identificationExpiry = identificationExpiry;
    }

    public String getLicenseApproveStatus() {
        return licenseApproveStatus;
    }

    public void setLicenseApproveStatus(String licenseApproveStatus) {
        this.licenseApproveStatus = licenseApproveStatus;
    }

    public String getInsuranceApproveStatus() {
        return insuranceApproveStatus;
    }

    public void setInsuranceApproveStatus(String insuranceApproveStatus) {
        this.insuranceApproveStatus = insuranceApproveStatus;
    }

    public String getCarRegistrationApproveStatus() {
        return carRegistrationApproveStatus;
    }

    public void setCarRegistrationApproveStatus(String carRegistrationApproveStatus) {
        this.carRegistrationApproveStatus = carRegistrationApproveStatus;
    }

    public String getInspectionApprovalStatus() {
        return inspectionApprovalStatus;
    }

    public void setInspectionApprovalStatus(String inspectionApprovalStatus) {
        this.inspectionApprovalStatus = inspectionApprovalStatus;
    }

    public Boolean getInspectionExpiry() {
        return inspectionExpiry;
    }

    public void setInspectionExpiry(Boolean inspectionExpiry) {
        this.inspectionExpiry = inspectionExpiry;
    }

    public Boolean getChangeVehicle() {
        return changeVehicle;
    }

    public void setChangeVehicle(Boolean changeVehicle) {
        this.changeVehicle = changeVehicle;
    }

    public Boolean getCarExpiry() {
        return carExpiry;
    }

    public void setCarExpiry(Boolean carExpiry) {
        this.carExpiry = carExpiry;
    }

    public Boolean getInsuranceExpiry() {
        return insuranceExpiry;
    }

    public void setInsuranceExpiry(Boolean insuranceExpiry) {
        this.insuranceExpiry = insuranceExpiry;
    }

    public Boolean getLicenseExpiry() {
        return licenseExpiry;
    }

    public void setLicenseExpiry(Boolean licenseExpiry) {
        this.licenseExpiry = licenseExpiry;
    }
}
