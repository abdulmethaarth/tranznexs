package co.webnexs.tranznexs.beens;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.webnexs.tranznexs.BaseResponse;

public class DriverTrack extends BaseResponse {

    @SerializedName("Driverlocation")
    @Expose
    private  DriverLocation driverLocation;

    public DriverLocation getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(DriverLocation driverLocation) {
        this.driverLocation = driverLocation;
    }

    public class DriverLocation{
        @SerializedName("road_lat")
        @Expose
        private String road_lat;

        @SerializedName("road_lng")
        @Expose
        private String road_lng;

        public String getRoad_lat() {
            return road_lat;
        }

        public void setRoad_lat(String road_lat) {
            this.road_lat = road_lat;
        }

        public String getRoad_lng() {
            return road_lng;
        }

        public void setRoad_lng(String road_lng) {
            this.road_lng = road_lng;
        }
    }
}
