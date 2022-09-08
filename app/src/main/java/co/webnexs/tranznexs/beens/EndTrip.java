package co.webnexs.tranznexs.beens;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.webnexs.tranznexs.BaseResponse;

public class EndTrip extends BaseResponse {

    @SerializedName("rideData")
    @Expose
    EndTripDetails rideData;

    public EndTripDetails getRideData() {
        return rideData;
    }

    public void setRideData(EndTripDetails rideData) {
        this.rideData = rideData;
    }

    public class EndTripDetails{

        @SerializedName("fare")
        @Expose
        private String fare;

        @SerializedName("PaymentMethod")
        @Expose
        private String PaymentMethod;

        @SerializedName("payment_method")
        @Expose
        private String payment_method;

        public String getFare() {
            return fare;
        }

        public void setFare(String fare) {
            this.fare = fare;
        }

        public String getPaymentMethod() {
            return PaymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            PaymentMethod = paymentMethod;
        }

        public String getPayment_method() {
            return payment_method;
        }

        public void setPayment_method(String payment_method) {
            this.payment_method = payment_method;
        }
    }
}

