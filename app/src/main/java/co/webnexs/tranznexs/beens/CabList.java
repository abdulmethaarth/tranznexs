package co.webnexs.tranznexs.beens;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CabList {
    @SerializedName("CabList")
    @Expose
    private ArrayList<CarType> carTypes = null;

    public ArrayList<CarType> getCarTypes() {
        return carTypes;
    }

    public void setCarTypes(ArrayList<CarType> carTypes) {
        this.carTypes = carTypes;
    }
}


