package co.webnexs.tranznexs.beens;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.webnexs.tranznexs.BaseResponse;

public class  Users extends BaseResponse {

    @SerializedName("riderDetails")
    @Expose
    private LoginUserDetails userDetails;

    public LoginUserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(LoginUserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public class LoginUserDetails{

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("name")
        @Expose
        private String name;


        @SerializedName("email_id")
        @Expose
        private String email_id;

        @SerializedName("phone_no")
        @Expose
        private String phone_no;

        @SerializedName("user_image")
        @Expose
        private String user_image;

        @SerializedName("dob")
        @Expose
        private String dob;


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

        public String getEmail_id() {
            return email_id;
        }

        public void setEmail_id(String email_id) {
            this.email_id = email_id;
        }

        public String getPhone_no() {
            return phone_no;
        }

        public void setPhone_no(String phone_no) {
            this.phone_no = phone_no;
        }

        public String getUser_image() {
            return user_image;
        }

        public void setUser_image(String user_image) {
            this.user_image = user_image;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }
    }
}

