package co.webnexs.tranznexs.beens;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.webnexs.tranznexs.BaseResponse;

public class Cab_details extends BaseResponse {

    @SerializedName("CabList")
    @Expose
    private CabTypeDetails cabTypeDetails;

    public CabTypeDetails getCabTypeDetails() {
        return cabTypeDetails;
    }

    public void setCabTypeDetails(CabTypeDetails cabTypeDetails) {
        this.cabTypeDetails = cabTypeDetails;
    }

    public class CabTypeDetails{

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("specification")
        @Expose
        private String specification;

        @SerializedName("rate_per_km")
        @Expose
        private String rate_per_km;

        @SerializedName("rate_per_hour")
        @Expose
        private String rate_per_hour;

        @SerializedName("image")
        @Expose
        private String image;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSpecification() {
            return specification;
        }

        public void setSpecification(String specification) {
            this.specification = specification;
        }

        public String getRate_per_km() {
            return rate_per_km;
        }

        public void setRate_per_km(String rate_per_km) {
            this.rate_per_km = rate_per_km;
        }

        public String getRate_per_hour() {
            return rate_per_hour;
        }

        public void setRate_per_hour(String rate_per_hour) {
            this.rate_per_hour = rate_per_hour;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}

