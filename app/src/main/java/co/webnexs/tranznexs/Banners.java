package co.webnexs.tranznexs;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import co.webnexs.tranznexs.beens.Banner;

public class Banners    {

    @SerializedName("screenList")
    private ArrayList<Banner> banners;

    public Banners(){ }

    public ArrayList<Banner> getBanners(){
        return banners;
    }
}
