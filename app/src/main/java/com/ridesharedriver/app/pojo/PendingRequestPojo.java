package com.ridesharedriver.app.pojo;

import java.io.Serializable;
import java.util.List;

public class PendingRequestPojo implements Serializable {

    private String ride_id;
    private String user_id;
    private String driver_id;
    private String pickup_adress;
    private String drop_address;
    private String pikup_location;
    private String drop_locatoin;
    private String distance;
    private String status;
    private String payment_status;
    private String amount;
    private String time;
    private String user_mobile;
    private String user_avatar;
    private String driver_avatar;
    private String user_name;
    private String user_lastname;
    private String driver_mobile;
    private String driver_name;
    private String pickup_lat;
    private String tip_amount;
    private String screen="";


    private List<String> audio = null;

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public String getTip_amount() {
        return tip_amount;
    }

    public void setTip_amount(String tip_amount) {
        this.tip_amount = tip_amount;
    }

    public List<String> getAudio() {
        return audio;
    }

    public void setAudio(List<String> audio) {
        this.audio = audio;
    }

    public String getPickup_lat() {
        return pickup_lat;
    }

    public void setPickup_lat(String pickup_lat) {
        this.pickup_lat = pickup_lat;
    }

    public String getPickup_long() {
        return pickup_long;
    }

    public void setPickup_long(String pickup_long) {
        this.pickup_long = pickup_long;
    }

    public String getDrop_lat() {
        return drop_lat;
    }

    public void setDrop_lat(String drop_lat) {
        this.drop_lat = drop_lat;
    }

    public String getDrop_long() {
        return drop_long;
    }

    public void setDrop_long(String drop_long) {
        this.drop_long = drop_long;
    }

    private String pickup_long;
    private String drop_lat;



    private String drop_long;

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    private String payment_mode;

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getDriver_avatar() {
        return driver_avatar;
    }

    public void setDriver_avatar(String driver_avatar) {
        this.driver_avatar = driver_avatar;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDriver_mobile() {
        return driver_mobile;
    }

    public void setDriver_mobile(String driver_mobile) {
        this.driver_mobile = driver_mobile;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public PendingRequestPojo() {
    }

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getPickup_adress() {
        return pickup_adress;
    }

    public void setPickup_adress(String pickup_adress) {
        this.pickup_adress = pickup_adress;
    }

    public String getDrop_address() {
        return drop_address;
    }

    public void setDrop_address(String drop_address) {
        this.drop_address = drop_address;
    }

    public String getPikup_location() {
        return pikup_location;
    }

    public void setPikup_location(String pikup_location) {
        this.pikup_location = pikup_location;
    }

    public String getDrop_locatoin() {
        return drop_locatoin;
    }

    public void setDrop_locatoin(String drop_locatoin) {
        this.drop_locatoin = drop_locatoin;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser_lastname() {
        return user_lastname;
    }

    public void setUser_lastname(String user_lastname) {
        this.user_lastname = user_lastname;
    }
}
