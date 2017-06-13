package com.Yourstylist.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignUpRequest {
    @SerializedName("first_name")
    @Expose
    private String firstName;

    @SerializedName("last_name")
    @Expose
    private String lastName;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("mobile")
    @Expose
    private String mobile;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("device_token")
    @Expose
    private String deviceToken;

    public SignUpRequest(String firstName, String lastName, String email, String mobile, String password, String deviceToken) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.deviceToken = deviceToken;
    }
}
