package co.webnexs.tranznexs;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    @SerializedName("message")
    public String message;

    @SerializedName("status")
    public String status;

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

}
