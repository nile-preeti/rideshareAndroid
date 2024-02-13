
package com.ridesharedriver.app.pojo.driverEarning;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("amount")
    @Expose
    private double amount;
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("month")
    @Expose
    private String month;

    public Data(double amount, String day) {
        this.amount = amount;
        this.day = day;
    }
    public Data()
    {

    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

}
