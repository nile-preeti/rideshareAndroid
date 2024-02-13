
package com.ridesharedriver.app.pojo.driverEarning;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DriverEarningResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("current_day_earning")
    @Expose
    private String currentDayEarning;
    @SerializedName("data")
    @Expose
    private List<Data> data = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCurrentDayEarning() {
        return currentDayEarning;
    }

    public void setCurrentDayEarning(String currentDayEarning) {
        this.currentDayEarning = currentDayEarning;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }


}
