package com.Yourstylist.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Like{
    @SerializedName("email")
    @Expose
    private String email;

    public void setEmail(String email){
        this.email=email;
    }

    public String getEmail(){
        return email;
    }
}