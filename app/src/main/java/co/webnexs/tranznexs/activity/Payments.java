package co.webnexs.tranznexs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import co.webnexs.tranznexs.R;
import co.webnexs.tranznexs.Api;
import co.webnexs.tranznexs.BaseResponse;
import co.webnexs.tranznexs.Constants;
import co.webnexs.tranznexs.DriverConstans;
import co.webnexs.tranznexs.RetrofitClient;
import co.webnexs.tranznexs.beens.EndTrip;
import co.webnexs.tranznexs.utils.SimpleRatingBar;
import retrofit2.Call;
import retrofit2.Callback;

public class Payments extends AppCompatActivity implements PaymentResultListener {
    private static final String TAG ="";
    RelativeLayout layout_back_arrow,pmntGateway_dialog_layout,layout_stripepay,layuot_razorpay;
    Dialog success,RatingDialog,PaymentDialog,PaymentGatwatyDialog;
    RelativeLayout cashLayout,addCardLayout;
    ImageView closeBtn;
    Button payBtn;
    EditText cardNumber,cvvPin,expiryDate;
    TextView fare,creditCardText;
    String amount,ride_id,user_id;
    String payment_type ="0";
    CardInputWidget cardInputWidget;
    Card card;
    CardView creditcard;
    String stripe_token,getLast4,cardtype,email_id,mobileno;
    Dialog ProgressDialog;
    RotateLoading cusRotateLoading;
    TimerTask timerTaskAsync;
    Timer timerAsync;
    int changePayment = 3 * 1000; //Here period is 1 mint


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(Constants.KEY_ONPAYMENT,true);
        editor.apply();

        SharedPreferences prefs = getSharedPreferences(DriverConstans.MY_Driver_PREFS_NAME, MODE_PRIVATE);
        ride_id = prefs.getString(DriverConstans.ride_id, "");


        SharedPreferences sharedPreferences = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        amount = sharedPreferences.getString(Constants.amount, "");
        payment_type = sharedPreferences.getString(Constants.Payment_method, "");
        user_id = sharedPreferences.getString(Constants.user_id, "");
        email_id = sharedPreferences.getString(Constants.email_id, "");
        mobileno = sharedPreferences.getString(Constants.mobileno, "");
        //Razor apy code
        Checkout.preload(getApplicationContext());

        cashLayout =(RelativeLayout)findViewById(R.id.cash_layout);
        addCardLayout = (RelativeLayout)findViewById(R.id.addCardLayout);

        if(payment_type == "0"){
            cashLayout.setVisibility(View.VISIBLE);
            addCardLayout.setVisibility(View.GONE);
        }else if(payment_type == "1") {
            cashLayout.setVisibility(View.GONE);
            addCardLayout.setVisibility(View.VISIBLE);
        }

        fare = (TextView) findViewById(R.id.fare);
        fare.setText("$"+amount);

        ProgressDialog = new Dialog(Payments.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading) ProgressDialog.findViewById(R.id.rotateloading_register);

        PaymentDialog = new Dialog(Payments.this, android.R.style.Theme_Translucent_NoTitleBar);
        PaymentDialog.setContentView(R.layout.strip_payment);
        cardInputWidget = (CardInputWidget)PaymentDialog.findViewById(R.id.card_input_widget);
        payBtn = (Button)PaymentDialog.findViewById(R.id.payBtn);
        creditcard = (CardView)PaymentDialog.findViewById(R.id.credit_card);

        PaymentGatwatyDialog = new Dialog(Payments.this, android.R.style.Theme_Translucent_NoTitleBar);
        PaymentGatwatyDialog.setContentView(R.layout.payment_gatway_dialog);
        layuot_razorpay = (RelativeLayout)PaymentGatwatyDialog.findViewById(R.id.layuot_razorpay);
        pmntGateway_dialog_layout = (RelativeLayout)PaymentGatwatyDialog.findViewById(R.id.pmntGateway_dialog_layout);
        layout_stripepay = (RelativeLayout)PaymentGatwatyDialog.findViewById(R.id.layout_stripepay);

        pmntGateway_dialog_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentGatwatyDialog.cancel();
            }
        });


        layuot_razorpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment();
                PaymentGatwatyDialog.cancel();
                PaymentDialog.cancel();
            }
        });

        layout_stripepay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentDialog.show();
                PaymentGatwatyDialog.cancel();
            }
        });

        RatingDialog = new Dialog(Payments.this, android.R.style.Theme_Translucent_NoTitleBar);
        RatingDialog.setContentView(R.layout.rating_dialog);


        success = new Dialog(Payments.this, android.R.style.Theme_Translucent_NoTitleBar);
        success.setContentView(R.layout.success);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            success.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

            Button done = (Button)RatingDialog.findViewById(R.id.done);
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText feedBack = (EditText) RatingDialog.findViewById(R.id.feedBack);
                    SimpleRatingBar rate = (SimpleRatingBar) RatingDialog.findViewById(R.id.userRating);
                    String rating = String.valueOf(rate.getRating());
                    if (rate.getRating() == 0.0) {
                        Toast.makeText(Payments.this, "Please give rating", Toast.LENGTH_LONG).show();
                        return;

                    } else if (feedBack.getText().toString().isEmpty()) {
                        Toast.makeText(Payments.this, "Please give your feedback", Toast.LENGTH_LONG).show();
                    } else {
                        String review = feedBack.getText().toString();
                        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
                        Call<BaseResponse> userCall = api.ratings(rating,review,ride_id);
                        userCall.enqueue(new Callback<BaseResponse>() {
                            @Override
                            public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {

                                BaseResponse baseResponse = response.body();

                                if (baseResponse.status.equalsIgnoreCase("true")) {

                                    RatingDialog.cancel();
                                    Toast.makeText(Payments.this, rating + " Thanks for your rating. " + new String(Character.toChars(0x1F60A)), Toast.LENGTH_LONG).show();

                                } else {
                                    //Toast.makeText(DriverHomeActivity.this, "update_false", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<BaseResponse> call, Throwable t) {
                                Log.d("onFailure", t.toString());
                                //Toast.makeText(DriverHomeActivity.this, "Check your internet.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        success.show();
                    }
                }
            });

        timerAsync = new Timer();
        timerTaskAsync = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        ChangePaymnetMethod();
                    }
                });
            }
        };  timerAsync.schedule(timerTaskAsync, 0,changePayment);

        addCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentGatwatyDialog.show();
            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCard();
            }
        });

        cashLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Payments.this);
                alertDialogBuilder.setTitle("Cash");
                alertDialogBuilder.setMessage(R.string.cashdialog);
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                timerAsync.cancel();
                                timerAsync.purge();
                                changePayment = 0;
                                RatingDialog.show();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        LinearLayout layout_pay_success = (LinearLayout) success.findViewById(R.id.success_ok);
        layout_pay_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                success.cancel();
                SharedPreferences pref = getSharedPreferences(DriverConstans.MY_Driver_PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.clear();
                edit.apply();
                SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean(Constants.KEY_ONPAYMENT,false);
                editor.apply();
                Intent intent = new Intent(Payments.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("GO", false);
                startActivity(intent);
            }
        });
    }

    private void ChangePaymnetMethod() {
        cashLayout =(RelativeLayout)findViewById(R.id.cash_layout);
        addCardLayout = (RelativeLayout)findViewById(R.id.addCardLayout);
        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<EndTrip> userCall = api.changePayment(ride_id,payment_type);
        userCall.enqueue(new Callback<EndTrip>() {
            @Override
            public void onResponse(Call<EndTrip> call, retrofit2.Response<EndTrip> response) {
                EndTrip baseResponse = response.body();
                if (baseResponse.status.equalsIgnoreCase("true")) {
                    EndTrip.EndTripDetails data = baseResponse.getRideData();
                    payment_type=data.getPayment_method();
                    if(payment_type == "0"){

                        cashLayout.setVisibility(View.VISIBLE);
                        addCardLayout.setVisibility(View.GONE);
                    }
                    else if(payment_type == "1"){

                        addCardLayout.setVisibility(View.VISIBLE);
                        cashLayout.setVisibility(View.GONE);
                        Toast.makeText(Payments.this, "card", Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(Payments.this, baseResponse.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    /*Toast.makeText(Payments.this, baseResponse.getMessage(), Toast.LENGTH_LONG).show();*/
                }
            }
            @Override
            public void onFailure(Call<EndTrip> call, Throwable t) {
                Log.d("onFailure", t.toString());
                //Toast.makeText(DriverHomeActivity.this, "Check your internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        double a = Double.parseDouble(amount);
        double payment = a*100;
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Tranznexs");
            options.put("description", "Total Amount");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://tranznexs.com/images/app_icon.png");
            options.put("currency", "INR");
            options.put("amount", payment);

            JSONObject preFill = new JSONObject();
            preFill.put("email", email_id);
            preFill.put("contact", mobileno);

            options.put("prefill", preFill);
           // Toast.makeText(this, "response "+preFill+ " "+options, Toast.LENGTH_LONG).show();

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
            Call<BaseResponse> userCall = api.payRazor(amount,user_id,ride_id,"Success",razorpayPaymentID);
            userCall.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {

                    BaseResponse baseResponse = response.body();

                    if (baseResponse.status.equalsIgnoreCase("true")) {
                        ProgressDialog.cancel();
                        cusRotateLoading.stop();
                        timerAsync.cancel();
                        timerAsync.purge();
                        changePayment = 0;
                        Toast.makeText(Payments.this, "Your payment succesfull", Toast.LENGTH_SHORT).show();
                        PaymentDialog.cancel();
                        RatingDialog.show();

                    } else {
                        //Toast.makeText(DriverHomeActivity.this, "update_false", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    Log.d("onFailure", t.toString());
                    Toast.makeText(Payments.this, "Check your internet.", Toast.LENGTH_SHORT).show();
                    ProgressDialog.cancel();
                    cusRotateLoading.stop();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String razorpayPaymentID) {
        try {
            Toast.makeText(this, "Payment failed:" + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }

    private void addCard() {
        card =  cardInputWidget.getCard();
        if(card == null){
            Toast.makeText(getApplicationContext(),"Invalid card",Toast.LENGTH_SHORT).show();
        }else {
            if (!card.validateCard()) {
                // Do not continue token creation.
                Toast.makeText(getApplicationContext(), "Invalid card", Toast.LENGTH_SHORT).show();
            } else {
                CreateToken(card);
            }
        }
    }

    private void CreateToken( Card card) {
        ProgressDialog.show();
        cusRotateLoading.start();
        Stripe stripe = new Stripe(getApplicationContext(), getString(R.string.key));
        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        // Send token to your server
                        Log.e("Stripe Token", token.getId());
                        String str_token = token.getId();
                        new StripeCharge(token.getId()).execute();
                        Intent intent = new Intent();
                        getLast4 = token.getCard().getLast4();
                        stripe_token = token.getId();
                        cardtype = token.getCard().getBrand();
                        setResult(0077,intent);
                        Toast.makeText(Payments.this, "Token "+str_token, Toast.LENGTH_SHORT).show();

                        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
                        Call<BaseResponse> userCall = api.payStripe(amount,str_token,user_id,email_id,ride_id);
                        userCall.enqueue(new Callback<BaseResponse>() {
                            @Override
                            public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {

                                BaseResponse baseResponse = response.body();

                                if (baseResponse.status.equalsIgnoreCase("succeeded")) {
                                    ProgressDialog.cancel();
                                    cusRotateLoading.stop();
                                    timerAsync.cancel();
                                    timerAsync.purge();
                                    changePayment = 0;
                                    PaymentGatwatyDialog.cancel();
                                    Toast.makeText(Payments.this, "Your payment succesfull", Toast.LENGTH_SHORT).show();
                                    PaymentDialog.cancel();
                                    RatingDialog.show();

                                } else {
                                    ProgressDialog.cancel();
                                    cusRotateLoading.stop();
                                    Toast.makeText(Payments.this, "payment false", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<BaseResponse> call, Throwable t) {
                                Log.d("onFailure", t.toString());
                                Toast.makeText(Payments.this, "Check your internet.", Toast.LENGTH_SHORT).show();
                                ProgressDialog.cancel();
                                cusRotateLoading.stop();
                            }
                        });
                    }
                    public void onError(Exception error) {

                        ProgressDialog.cancel();
                        cusRotateLoading.stop();
                        Toast.makeText(getApplicationContext(),
                                error.getLocalizedMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.really_exit))
                .setMessage(getResources().getString(R.string.are_you_sure))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        //  HomeActivity.super.onBackPressed();
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }
                }).create().show();
    }

    public class StripeCharge extends AsyncTask<String, Void, String> {
        String token;

        public StripeCharge(String token) {
            this.token = token;
        }

        @Override
        protected String doInBackground(String... params) {
            new Thread() {
                @Override
                public void run() {
                    //postData(token,""+"30");
                }
            }.start();
            return "Done";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Result",s);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == resultCode){
            creditcard.setVisibility(View.VISIBLE);
            if(stripe_token.length()>1)
            creditCardText.setText(cardtype+" "+getLast4);
        }
    }
}
