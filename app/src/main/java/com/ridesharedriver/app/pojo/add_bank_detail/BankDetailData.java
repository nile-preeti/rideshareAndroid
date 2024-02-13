package com.ridesharedriver.app.pojo.add_bank_detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankDetailData {

    @SerializedName("account_holder_name")
    @Expose
    private String accountHolderName;
    @SerializedName("bank_name")
    @Expose
    private String bankName;
    @SerializedName("routing_number")
    @Expose
    private String routingNumber;
    @SerializedName("account_number")
    @Expose
    private String accountNumber;
    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("user_id")
    @Expose
    private String userId;

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
