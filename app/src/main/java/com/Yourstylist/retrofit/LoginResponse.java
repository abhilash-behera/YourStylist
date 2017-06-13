
package com.Yourstylist.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private LoginResponseData loginResponseData;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public LoginResponseData getLoginResponseData() {
        return loginResponseData;
    }

    public void setLoginResponseData(LoginResponseData loginResponseData) {
        this.loginResponseData = loginResponseData;
    }

}
