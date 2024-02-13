
package com.ridesharedriver.app.pojo.getVehicleDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("vehicle_detail_id")
    @Expose
    private String vehicleDetailId;
    @SerializedName("vehicle_type")
    @Expose
    private String vehicleType;
    @SerializedName("model_name")
    @Expose
    private String modelName;
    @SerializedName("brand_name")
    @Expose
    private String brandName;
    @SerializedName("model_id")
    @Expose
    private String modelId;
    @SerializedName("brand_id")
    @Expose
    private String brandId;
    @SerializedName("year")
    @Expose
    private String year;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("vehicle_no")
    @Expose
    private String vehicleNo;
    @SerializedName("vehicle_type_id")
    @Expose
    private String vehicleTypeId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("seat_no")
    @Expose
    private String seatNo;
    @SerializedName("license_doc")
    @Expose
    private String licenseDoc;
    @SerializedName("license")
    @Expose
    private String license;
    @SerializedName("insurance_doc")
    @Expose
    private String insuranceDoc;
    @SerializedName("insurance")
    @Expose
    private String insurance;
    @SerializedName("permit_doc")
    @Expose
    private String permitDoc;
    @SerializedName("permit")
    @Expose
    private String permit;
    @SerializedName("car_pic")
    @Expose
    private String carPic;
    @SerializedName("car_pic_doc")
    @Expose
    private String carPicDoc;
    @SerializedName("car_registration_doc")
    @Expose
    private String carRegistrationDoc;
    @SerializedName("car_registration")
    @Expose
    private String carRegistration;
    @SerializedName("insurance_issue_date")
    @Expose
    private String insuranceIssueDate;
    @SerializedName("insurance_expiry_date")
    @Expose
    private String insuranceExpiryDate;

    @SerializedName("car_issue_date")
    @Expose
    private String carIssueDate;
    @SerializedName("car_expiry_date")
    @Expose
    private String carExpiryDate;
    @SerializedName("license_issue_date")
    @Expose
    private String licenceIssueDate;
    @SerializedName("license_expiry_date")
    @Expose
    private String licenceExpireDate;
    @SerializedName("premium_facility")
    @Expose
    private String premiumFacility;

    @SerializedName("inspection_document")
    @Expose
    private String inspectionDocument;
    @SerializedName("inspection_issue_date")
    @Expose
    private String inspectionIssueDate;
    @SerializedName("inspection_expiry_date")
    @Expose
    private String inspectionExpireDate;

    public String getInspectionDocument() {
        return inspectionDocument;
    }

    public void setInspectionDocument(String inspectionDocument) {
        this.inspectionDocument = inspectionDocument;
    }

    public String getInspectionIssueDate() {
        return inspectionIssueDate;
    }

    public void setInspectionIssueDate(String inspectionIssueDate) {
        this.inspectionIssueDate = inspectionIssueDate;
    }

    public String getInspectionExpireDate() {
        return inspectionExpireDate;
    }

    public void setInspectionExpireDate(String inspectionExpireDate) {
        this.inspectionExpireDate = inspectionExpireDate;
    }

    public String getLicenceIssueDate() {
        return licenceIssueDate;
    }

    public void setLicenceIssueDate(String licenceIssueDate) {
        this.licenceIssueDate = licenceIssueDate;
    }

    public String getLicenceExpireDate() {
        return licenceExpireDate;
    }

    public void setLicenceExpireDate(String licenceExpireDate) {
        this.licenceExpireDate = licenceExpireDate;
    }

    public String getPremiumFacility() {
        return premiumFacility;
    }

    public void setPremiumFacility(String premiumFacility) {
        this.premiumFacility = premiumFacility;
    }

    public String getCarIssueDate() {
        return carIssueDate;
    }

    public void setCarIssueDate(String carIssueDate) {
        this.carIssueDate = carIssueDate;
    }

    public String getCarExpiryDate() {
        return carExpiryDate;
    }

    public void setCarExpiryDate(String carExpiryDate) {
        this.carExpiryDate = carExpiryDate;
    }

    public String getInsuranceIssueDate() {
        return insuranceIssueDate;
    }

    public void setInsuranceIssueDate(String insuranceIssueDate) {
        this.insuranceIssueDate = insuranceIssueDate;
    }

    public String getInsuranceExpiryDate() {
        return insuranceExpiryDate;
    }

    public void setInsuranceExpiryDate(String insuranceExpiryDate) {
        this.insuranceExpiryDate = insuranceExpiryDate;
    }

    public String getVehicleDetailId() {
        return vehicleDetailId;
    }

    public void setVehicleDetailId(String vehicleDetailId) {
        this.vehicleDetailId = vehicleDetailId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(String vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public String getLicenseDoc() {
        return licenseDoc;
    }

    public void setLicenseDoc(String licenseDoc) {
        this.licenseDoc = licenseDoc;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getInsuranceDoc() {
        return insuranceDoc;
    }

    public void setInsuranceDoc(String insuranceDoc) {
        this.insuranceDoc = insuranceDoc;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getPermitDoc() {
        return permitDoc;
    }

    public void setPermitDoc(String permitDoc) {
        this.permitDoc = permitDoc;
    }

    public String getPermit() {
        return permit;
    }

    public void setPermit(String permit) {
        this.permit = permit;
    }

    public String getCarPic() {
        return carPic;
    }

    public void setCarPic(String carPic) {
        this.carPic = carPic;
    }

    public String getCarPicDoc() {
        return carPicDoc;
    }

    public void setCarPicDoc(String carPicDoc) {
        this.carPicDoc = carPicDoc;
    }

    public String getCarRegistrationDoc() {
        return carRegistrationDoc;
    }

    public void setCarRegistrationDoc(String carRegistrationDoc) {
        this.carRegistrationDoc = carRegistrationDoc;
    }

    public String getCarRegistration() {
        return carRegistration;
    }

    public void setCarRegistration(String carRegistration) {
        this.carRegistration = carRegistration;
    }

}
