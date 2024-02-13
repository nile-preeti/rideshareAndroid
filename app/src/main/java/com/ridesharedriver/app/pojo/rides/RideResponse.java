package com.ridesharedriver.app.pojo.rides;

import com.ridesharedriver.app.pojo.PendingRequestPojo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RideResponse {
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("total_record")
    @Expose
    private Integer totalRecord;
    @SerializedName("total_earning")
    @Expose
    private String totalEarning;
    @SerializedName("data")
    @Expose
    private List<PendingRequestPojo> data = null;

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

    public List<PendingRequestPojo> getData() {
        return data;
    }

    public void setData(List<PendingRequestPojo> data) {
        this.data = data;
    }

}
