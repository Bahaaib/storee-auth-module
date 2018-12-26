package com.example.microsoft.auth.Root;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserModel implements Serializable {
    @SerializedName("name_en")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String mobile;

    @SerializedName("token")
    private String token;

    private boolean male;


    public UserModel() {
        //Required Empty Constructor
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobil) {
        this.mobile = mobil;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isMale() {
        return male;
    }

    public void setIsMale(boolean isMale){
        this.male = isMale;
    }

}
