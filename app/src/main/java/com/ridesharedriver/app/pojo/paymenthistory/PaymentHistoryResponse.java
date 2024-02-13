package com.ridesharedriver.app.pojo.paymenthistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaymentHistoryResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("total_record")
    @Expose
    private Integer totalRecord;
    @SerializedName("total_earning")
    @Expose
    private String totalEarning;
    @SerializedName("total_payout")
    @Expose
    private String totalPayout;
    @SerializedName("data")
    @Expose
    private List<PaymentHistoryData> data = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }

    public String getTotalEarning() {
        return totalEarning;
    }

    public void setTotalEarning(String totalEarning) {
        this.totalEarning = totalEarning;
    }

    public String getTotalPayout() {
        return totalPayout;
    }

    public void setTotalPayout(String totalPayout) {
        this.totalPayout = totalPayout;
    }

    public List<PaymentHistoryData> getData() {
        return data;
    }

    public void setData(List<PaymentHistoryData> data) {
        this.data = data;
    }

}