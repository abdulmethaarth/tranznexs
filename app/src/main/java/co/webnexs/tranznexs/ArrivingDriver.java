package co.webnexs.tranznexs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArrivingDriver extends BaseResponse{

    @SerializedName("driverDeatils")
    @Expose
    private ArrivingDriverDetails driverDetails;

    public ArrivingDriverDetails getDriverDetails() {
        return driverDetails;
    }

    public void setDriverDetails(ArrivingDriverDetails driverDetails) {
        this.driverDetails = driverDetails;
    }

    public class ArrivingDriverDetails{

        @SerializedName("firstname")
        @Expose
        private String firstname;

        @SerializedName("lastname")
        @Expose
        private String lastname;

        @SerializedName("phone_no")
        @Expose
        private String phone_no;

        @SerializedName("brand")
        @Expose
        private String brand;

        @SerializedName("number_plate")
        @Expose
        private String number_plate;

        @SerializedName("latitude")
        @Expose
        private String latitude;

        @SerializedName("longitude")
        @Expose
        private String longitude;

        @SerializedName("ride_id")
        @Expose
        private String ride_id;

        @SerializedName("driver_id")
        @Expose
        private String driver_id;

        @SerializedName("driver_image")
        @Expose
        private String driver_image;

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getPhone_no() {
            return phone_no;
        }

        public void setPhone_no(String phone_no) {
            this.phone_no = phone_no;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getNumber_plate() {
            return number_plate;
        }

        public void setNumber_plate(String number_plate) {
            this.number_plate = number_plate;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getRide_id() {
            return ride_id;
        }

        public void setRide_id(String ride_id) {
            this.ride_id = ride_id;
        }

        public String getDriver_id() {
            return driver_id;
        }

        public void setDriver_id(String driver_id) {
            this.driver_id = driver_id;
        }

        public String getDriver_image() {
            return driver_image;
        }

        public void setDriver_image(String driver_image) {
            this.driver_image = driver_image;
        }
    }
}
