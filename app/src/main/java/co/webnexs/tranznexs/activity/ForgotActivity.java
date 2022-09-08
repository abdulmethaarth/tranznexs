package co.webnexs.tranznexs.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.victor.loading.rotate.RotateLoading;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import co.webnexs.tranznexs.Api;
import co.webnexs.tranznexs.BaseResponse;
import co.webnexs.tranznexs.RetrofitClient;
import co.webnexs.tranznexs.beens.Users;
import co.webnexs.tranznexs.countrypicker.Country;
import co.webnexs.tranznexs.countrypicker.CountryPickerCallbacks;
import co.webnexs.tranznexs.countrypicker.CountryPickerDialog;
import co.webnexs.tranznexs.utils.Common;
import co.webnexs.tranznexs.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotActivity extends AppCompatActivity {

    TextView txt_forgot_password,txt_for_pass_logo,txt_retrive_password;
    RelativeLayout layout_back_arrow,layout_retrive_password;
    EditText etPhoneNumber /*etOTP,*/;
    CountryPickerDialog countryPicker;
    String phoneNumber, otp,TxtViewCountryCode ;
    FirebaseAuth auth;
    LinearLayout otpPasswordLayout;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;
    RotateLoading cusRotateLoading;
    Dialog ProgressDialog;
    TextView loginWithEmail,edit_country_code;
    Api myApi;
    LinearLayout otpLayout;
    PinEntryEditText otpText;
    Typeface OpenSans_Bold,Roboto_Bold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        ProgressDialog = new Dialog(ForgotActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading)ProgressDialog.findViewById(R.id.rotateloading_register);
        otpLayout = (LinearLayout)findViewById(R.id.otpLayout);
        otpText = (PinEntryEditText) findViewById(R.id.forgotOtp);
        myApi = RetrofitClient.getRetrofitInstance().create(Api.class);
        layout_back_arrow = (RelativeLayout)findViewById(R.id.layout_back_arrow);
        layout_retrive_password=findViewById(R.id.layout_retrive_password);
        etPhoneNumber=(EditText)findViewById(R.id.et_number);
        edit_country_code = (TextView) findViewById(R.id.edit_country_code);
        TxtViewCountryCode = edit_country_code.getText().toString();
        otpPasswordLayout = (LinearLayout)findViewById(R.id.otpLayout) ;

        StartFirebaseLogin();
        layout_retrive_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etPhoneNumber.getText().toString().isEmpty()){
                    Toast.makeText(ForgotActivity.this,"Please enter your Mobile number",Toast.LENGTH_SHORT).show();
                }
                else{
                    String Number = etPhoneNumber.getText().toString();
                    Call<Users> call = myApi.getUser(Number);
                    call.enqueue(new Callback<Users>() {
                        @Override
                        public void onResponse(Call<Users> call, Response<Users> response) {
                            Users users = response.body();

                            if (users.message.equalsIgnoreCase("user login successfully")) {

                                new AlertDialog.Builder(ForgotActivity.this)
                                        .setTitle(getResources().getString(R.string.edit_number))
                                        .setNegativeButton(getResources().getString(R.string.yes), null)
                                        .setPositiveButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                ProgressDialog.show();
                                                cusRotateLoading.start();
                                                phoneNumber = edit_country_code.getText().toString()+ etPhoneNumber.getText().toString();
                                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                                        phoneNumber,                     // Phone number to verify
                                                        60,                           // Timeout duration
                                                        TimeUnit.SECONDS,                // Unit of timeout
                                                        ForgotActivity.this,        // Activity (for callback binding)
                                                        mCallback);
                                            }
                                        }).create().show();
                            } else {

                                Toast.makeText(ForgotActivity.this, "Incorrect mobile number.", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<Users> call, Throwable t) {
                            Toast.makeText(ForgotActivity.this, "Check your internet connection...", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

        otpText.requestFocus();
        otpText.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(final CharSequence otp_pin) {
                if (otp_pin.length() == 6) {
                    ProgressDialog.show();
                    cusRotateLoading.start();
                    //this only checking purpose so remove only this commend line
                    otp = otpText.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                    SigninWithPhone(credential);
                }
            }
        });


        edit_country_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countryPicker =
                        new CountryPickerDialog(ForgotActivity.this, new CountryPickerCallbacks() {
                            @Override
                            public void onCountrySelected(Country country, int flagResId) {
                                // TODO handle callback
                                Log.d("country", country.getDialingCode());
                                Log.d("country", new Locale(getResources().getConfiguration().locale.getLanguage(),
                                        country.getIsoCode()).getDisplayCountry());
                                edit_country_code.setText("+" + country.getDialingCode());
                                //+new Locale(getResources().getConfiguration().locale.getLanguage(),country.getIsoCode()).getDisplayCountry()
                                Common.CountryCode = country.getDialingCode();
                                etPhoneNumber.requestFocus();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.showSoftInput(etPhoneNumber, InputMethodManager.SHOW_IMPLICIT);
                                    }
                                }, 100);
                            }
                        });

                countryPicker.show();
            }
        });


        ProgressDialog = new Dialog(ForgotActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading)ProgressDialog.findViewById(R.id.rotateloading_register);


        layout_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ProgressDialog.cancel();
                            cusRotateLoading.stop();

                            Api myApi = RetrofitClient.getRetrofitInstance().create(Api.class);
                            Call<BaseResponse> call = myApi.forgotPassword(etPhoneNumber.getText().toString(),otpText.getText().toString());
                            call.enqueue(new Callback<BaseResponse>() {
                                @Override
                                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                                    BaseResponse users = response.body();
                                    if (users.status.equalsIgnoreCase("true")) {
                                        ProgressDialog.cancel();
                                        cusRotateLoading.stop();
                                        Toast.makeText(ForgotActivity.this, "Your password has been changed.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ForgotActivity.this, LoginActivity.class));

                                    } else {
                                        ProgressDialog.cancel();
                                        cusRotateLoading.stop();
                                        Toast.makeText(ForgotActivity.this, "Please try again.", Toast.LENGTH_SHORT).show();
                                    }

                                    // loader(false);
                                }

                                @Override
                                public void onFailure(Call<BaseResponse> call, Throwable t) {
                                    ProgressDialog.cancel();
                                    cusRotateLoading.stop();
                                    Toast.makeText(ForgotActivity.this, "Sorry something went wrong...", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            ProgressDialog.cancel();
                            cusRotateLoading.stop();
                            Snackbar.make(findViewById(android.R.id.content), "Incorrect OTP.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }
    private void StartFirebaseLogin() {
        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Snackbar.make(findViewById(android.R.id.content), "verification completed.", Snackbar.LENGTH_LONG).show();
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                Snackbar.make(findViewById(android.R.id.content), "Check your mobile Number.", Snackbar.LENGTH_LONG).show();
                ProgressDialog.cancel();
                cusRotateLoading.stop();
            }
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                otpPasswordLayout.setVisibility(View.VISIBLE);
                ProgressDialog.cancel();
                cusRotateLoading.stop();
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Snackbar.make(findViewById(android.R.id.content), "Code sent.", Snackbar.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        txt_forgot_password = null;
        layout_back_arrow = null;
        layout_retrive_password = null;

    }
}
