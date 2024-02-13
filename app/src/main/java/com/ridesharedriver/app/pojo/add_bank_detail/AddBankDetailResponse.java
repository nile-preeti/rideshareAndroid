package com.ridesharedriver.app.pojo.add_bank_detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddBankDetailResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private BankDetailData data;

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

    public BankDetailData getData() {
        return data;
    }

    public void setData(BankDetailData data) {
        this.data = data;
    }

}