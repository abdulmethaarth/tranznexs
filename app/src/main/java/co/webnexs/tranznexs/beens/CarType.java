package co.webnexs.tranznexs.beens;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.webnexs.tranznexs.BaseResponse;

public class CarType extends BaseResponse {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("icon")
    @Expose
    private String icon;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
