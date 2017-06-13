
package com.Yourstylist.retrofit;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotolistResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<Photo> data = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public PhotolistResponse() {
    }

    /**
     * 
     * @param data
     * @param success
     */
    public PhotolistResponse(Boolean success, List<Photo> data) {
        super();
        this.success = success;
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Photo> getData() {
        return data;
    }

    public void setData(List<Photo> data) {
        this.data = data;
    }

}
