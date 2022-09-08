package co.webnexs.tranznexs;

import co.webnexs.tranznexs.beens.Cab_details;
import co.webnexs.tranznexs.beens.DriverTrack;
import co.webnexs.tranznexs.beens.Drivers;
import co.webnexs.tranznexs.beens.EndTrip;
import co.webnexs.tranznexs.beens.CabList;
import co.webnexs.tranznexs.beens.OvertripDetailsList;
import co.webnexs.tranznexs.beens.RatePer;
import co.webnexs.tranznexs.beens.SingleTripDetails;
import co.webnexs.tranznexs.beens.Users;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {

    @GET("login_screens")
    Call<Banners> getBanners();

    @FormUrlEncoded
    @POST("otp_login")
    Call<Users> getUser(@Field("phone_no") String phone_no);

    @POST("email_login")
    @FormUrlEncoded
    Call<Users> login(@Field("email_id") String email_id,
                      @Field("password") String password);

    @POST("change_password")
    @FormUrlEncoded
    Call<BaseResponse> changePassword(@Field("rider_id") String rider_id,
                                      @Field("oldpassword") String oldpassword,
                                      @Field("newpassword") String newpassword);

    @POST("forgot_password")
    @FormUrlEncoded
    Call<BaseResponse> forgotPassword(@Field("phone_no") String phone_no,
                                      @Field("newpassword") String newpassword
    );

    @GET("cab_types")
    Call<CabList> getCabList();


    @FormUrlEncoded
    @POST("single_cab_detail")
    Call<Cab_details> getCabDetails(@Field("cabtype") String cabtype);


    @POST("nearbydrivers")
    @FormUrlEncoded
    Call<Drivers> nearBy(@Field("rider_id") String rider_id,
                         @Field("latitude") String latitude,
                         @Field("longitude") String longitude,
                         @Field("cabtype") String cabtype
    );
    @POST("rateperkm")
    @FormUrlEncoded
    Call<RatePer> rate(@Field("cabtype") String cabtype,
                       @Field("pickup_lat") String pickup_lat,
                       @Field("pickup_lng") String pickup_lng,
                       @Field("drop_lat") String drop_lat,
                       @Field("drop_lng") String drop_lng);

    @POST("twoway")
    @FormUrlEncoded
    Call<RatePer> twoWay(@Field("cabtype") String cabtype,
                         @Field("pickup_lat") String pickup_lat,
                         @Field("pickup_lng") String pickup_lng,
                         @Field("drop_lat") String drop_lat,
                         @Field("drop_lng") String drop_lng);

    @POST("book_ride")
    @FormUrlEncoded //this arriving driver class is just using comen class purpose
    Call<ArrivingDriver> postBooking(@Field("rider_id") String rider_id,
                                   @Field("pickup_lat") String pickup_lat,
                                   @Field("pickup_lng") String pickup_lng,
                                   @Field("pickup_loc_name") String pickup_loc_name,
                                   @Field("cabtype") String cabtype,
                                   @Field("drop_lat") String drop_lat,
                                   @Field("drop_lng") String drop_lng,
                                   @Field("drop_loc_name") String drop_loc_name,
                                   @Field("fare") String fare,
                                   @Field("distance") String distance,
                                   @Field("request_time") String request_time,
                                   @Field("ride_otp") String ride_otp,
                                   @Field("payment_method") String payment_method,
                                   @Field("ride_type") String ride_type);


    @FormUrlEncoded
    @POST("cancel_ride")
    Call<BaseResponse> cancelRide(@Field("ride_id") String ride_id,
                                  @Field("cancellation_time") String cancellation_time,
                                  @Field("cancellation_reason") String cancellation_reason);

   @FormUrlEncoded
    @POST("no_driver")
    Call<BaseResponse> noDriver(@Field("rider_id") String rider_id,
                                  @Field("request_time") String request_time);

    @FormUrlEncoded
    @POST("rideaccept_details")
    Call<ArrivingDriver> ArrivingDriverDetails(@Field("rider_id") String rider_id,
                                               @Field("request_time") String request_time);

    @FormUrlEncoded
    @POST("driver_tracking")
    Call<DriverTrack> driverTracking(@Field("driver_id") String driver_id,
                                     @Field("ride_id") String ride_id,
                                     @Field("latitude") String latitude,
                                     @Field("longitude") String longitude
                                     );

    @FormUrlEncoded
    @POST("start_trip")
    Call<BaseResponse> startRide(@Field("ride_id") String user_id);


    @FormUrlEncoded
    @POST("end_trip")
    Call<EndTrip> endTrip(@Field("ride_id") String ride_id);

    @FormUrlEncoded
    @POST("stripe_pay")
    Call<BaseResponse> payStripe(@Field("amount") String amount,
                               @Field("token") String token,
                               @Field("rider_id") String rider_id,
                               @Field("email_id") String email_id,
                               @Field("ride_id") String ride_id);

    @FormUrlEncoded
    @POST("razor_pay")
    Call<BaseResponse> payRazor(@Field("amount") String amount,
                               @Field("rider_id") String rider_id,
                               @Field("ride_id") String ride_id,
                               @Field("status") String status,
                               @Field("transaction_id") String transaction_id
    );

    @FormUrlEncoded
    @POST("ride_rating")
    Call<BaseResponse> ratings(@Field("rating") String rating,
                                @Field("review") String review,
                                @Field("ride_id") String ride_id);

    @FormUrlEncoded
    @POST("change_payment")
    Call<EndTrip> changePayment(@Field("ride_id") String ride_id,
                                     @Field("prev_method") String prev_method);

    @FormUrlEncoded
    @POST("my_trips")
    Call<OvertripDetailsList> getTripDetails(@Field("rider_id") String rider_id);

    @FormUrlEncoded
    @POST("single_trip")
    Call<SingleTripDetails> getSingleTripDetail(@Field("rider_id") String rider_id,
                                                @Field("ride_id") String ride_id
                                             );
}
