package co.webnexs.tranznexs.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import androidx.appcompat.widget.AppCompatButton;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.victor.loading.rotate.RotateLoading;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

import co.webnexs.tranznexs.R;
import co.webnexs.tranznexs.Api;
import co.webnexs.tranznexs.Constants;
import co.webnexs.tranznexs.RetrofitClient;
import co.webnexs.tranznexs.beens.Users;
import co.webnexs.tranznexs.countrypicker.Country;
import co.webnexs.tranznexs.countrypicker.CountryPickerCallbacks;
import co.webnexs.tranznexs.countrypicker.CountryPickerDialog;
import co.webnexs.tranznexs.utils.Common;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OneTimePasswordActivity extends AppCompatActivity {
    SharedPreferences pref;
    Context ctx;
    Button  btnGenerateOTP;
    EditText etPhoneNumber /*etOTP,*/;
    CountryPickerDialog countryPicker;
    String phoneNumber, otp,TxtViewCountryCode ;
    FirebaseAuth auth;
    LinearLayout otpPasswordLayout;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;
    RotateLoading cusRotateLoading;
    RelativeLayout layout_back_arrow;
    Dialog ProgressDialog;
    TextView loginWithEmail,edit_country_code;
    Api myApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_time_password);

        ProgressDialog = new Dialog(OneTimePasswordActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading)ProgressDialog.findViewById(R.id.rotateloading_register);
        loginWithEmail = (TextView)findViewById(R.id.loginWithEmail);
        otpPasswordLayout = (LinearLayout)findViewById(R.id.otpPasswordLayout);
        layout_back_arrow = (RelativeLayout) findViewById(R.id.layout_back_arrow);
        final PinEntryEditText otpText = (PinEntryEditText) findViewById(R.id.otp);
        myApi = RetrofitClient.getRetrofitInstance().create(Api.class);

        btnGenerateOTP=findViewById(R.id.btn_generate_otp);
        etPhoneNumber=(EditText)findViewById(R.id.et_phone_number);
        edit_country_code = (TextView) findViewById(R.id.country_code);
        TxtViewCountryCode = edit_country_code.getText().toString();

            StartFirebaseLogin();
            btnGenerateOTP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(etPhoneNumber.getText().toString().isEmpty()){
                        Toast.makeText(OneTimePasswordActivity.this,"Please enter your Mobile number",Toast.LENGTH_SHORT).show();
                    }
                    else{
                       String Number = etPhoneNumber.getText().toString();
                        Call<Users> call = myApi.getUser(Number);
                        call.enqueue(new Callback<Users>() {
                            @Override
                            public void onResponse(Call<Users> call, Response<Users> response) {
                                Users users = response.body();

                                if (users.status.equalsIgnoreCase("true")) {
                                    Users.LoginUserDetails userData = users.getUserDetails();
                                    SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
                                    editor.putString(Constants.user_id, userData.getId());
                                    editor.putString(Constants.firstname, userData.getName());
                                    editor.putString(Constants.email_id, userData.getEmail_id());
                                    editor.putString(Constants.mobileno, userData.getPhone_no());
                                    editor.putString(Constants.image, userData.getUser_image());
                                    editor.apply();

                                    new AlertDialog.Builder(OneTimePasswordActivity.this)
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
                                                            OneTimePasswordActivity.this,        // Activity (for callback binding)
                                                            mCallback);
                                                }
                                            }).create().show();
                                } else {

                                    Toast.makeText(OneTimePasswordActivity.this, "Your not register user.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(OneTimePasswordActivity.this, SignUpActivity.class));
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<Users> call, Throwable t) {
                                Toast.makeText(OneTimePasswordActivity.this, "Check your internet connection...", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            });

        layout_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OneTimePasswordActivity.this, LoginOptionActivity.class));
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
                        new CountryPickerDialog(OneTimePasswordActivity.this, new CountryPickerCallbacks() {
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


        ProgressDialog = new Dialog(OneTimePasswordActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading)ProgressDialog.findViewById(R.id.rotateloading_register);

        loginWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OneTimePasswordActivity.this, LoginActivity.class);
                startActivity(intent);
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
                            startActivity(new Intent(OneTimePasswordActivity.this, HomeActivity.class));
                            finish();
                            SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putBoolean(Constants.KEY_OTP_LOGGED_IN,true);
                            editor.apply();
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
        public void onBackPressed() {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
    }
}