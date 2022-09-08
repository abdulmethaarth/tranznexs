package co.webnexs.tranznexs.beens;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.webnexs.tranznexs.BaseResponse;

public class RatePer extends BaseResponse {

    @SerializedName("Rate_Details")
    @Expose
    private RateDetails Rate_Details;

    public RateDetails getRate_Details() {
        return Rate_Details;
    }

    public void setRate_Details(RateDetails rate_Details) {
        Rate_Details = rate_Details;
    }

    public class RateDetails {

        @SerializedName("RatePerKm")
        @Expose
        private String RatePerKm;

        @SerializedName("Amount")
        @Expose
        private String Amount;

        @SerializedName("Distance")
        @Expose
        private String Distance;

        @SerializedName("cab_name")
        @Expose
        private String cab_name;

        public String getRatePerKm() {
            return RatePerKm;
        }

        public void setRatePerKm(String ratePerKm) {
            RatePerKm = ratePerKm;
        }

        public String getAmount() {
            return Amount;
        }

        public void setAmount(String amount) {
            Amount = amount;
        }

        public String getDistance() {
            return Distance;
        }

        public void setDistance(String distance) {
            Distance = distance;
        }

        public String getCab_name() {
            return cab_name;
        }

        public void setCab_name(String cab_name) {
            this.cab_name = cab_name;
        }
    }
}
