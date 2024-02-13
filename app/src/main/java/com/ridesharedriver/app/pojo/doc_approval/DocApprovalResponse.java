package com.ridesharedriver.app.pojo.doc_approval;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DocApprovalResponse {
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("data")
    @Expose
    private DocApprovalData data;
    @SerializedName("verification_id_approval_atatus")
    @Expose
    private String verificationIdApprovalAtatus;
    @SerializedName("background_approval_status")
    @Expose
    private String backgroundApprovalStatus;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public DocApprovalData getData() {
        return data;
    }

    public void setData(DocApprovalData data) {
        this.data = data;
    }

    public String getVerificationIdApprovalAtatus() {
        return verificationIdApprovalAtatus;
    }

    public void setVerificationIdApprovalAtatus(String verificationIdApprovalAtatus) {
        this.verificationIdApprovalAtatus = verificationIdApprovalAtatus;
    }

    public String getBackgroundApprovalStatus() {
        return backgroundApprovalStatus;
    }

    public void setBackgroundApprovalStatus(String backgroundApprovalStatus) {
        this.backgroundApprovalStatus = backgroundApprovalStatus;
    }

}
