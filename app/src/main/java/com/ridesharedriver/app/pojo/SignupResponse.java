package com.ridesharedriver.app.pojo;

public class SignupResponse {
    private boolean status;
    private String message;
    private Data data;
    private String token;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static class Data {
        private int user_id;
        private String utype;
        private String username;
        private String email;
        private String country_code;
        private String mobile;
        private String otp;
        private String status;
        private boolean is_card;
        private boolean add_card;

        public int getUserId() {
            return user_id;
        }

        public void setUserId(int user_id) {
            this.user_id = user_id;
        }

        public String getUtype() {
            return utype;
        }

        public void setUtype(String utype) {
            this.utype = utype;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCountryCode() {
            return country_code;
        }

        public void setCountryCode(String country_code) {
            this.country_code = country_code;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public boolean isCard() {
            return is_card;
        }

        public void setCard(boolean is_card) {
            this.is_card = is_card;
        }

        public boolean isAddCard() {
            return add_card;
        }

        public void setAddCard(boolean add_card) {
            this.add_card = add_card;
        }
    }
}
