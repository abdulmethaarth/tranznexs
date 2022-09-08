package co.webnexs.tranznexs.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.victor.loading.rotate.RotateLoading;
import co.webnexs.tranznexs.R;
import co.webnexs.tranznexs.Api;
import co.webnexs.tranznexs.Constants;
import co.webnexs.tranznexs.RetrofitClient;
import co.webnexs.tranznexs.beens.Users;
import co.webnexs.tranznexs.utils.Common;
//import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edit_username;
    EditText edit_password;
    RelativeLayout layout_signin,layout_forgotPass,layout_back_arrow;
    TextView txt_change_password,txt_forgot_password;
    TextView txt_signin, txt_sign_in_logo;
    LinearLayout layout_login_main;
    Button Skip;
    Api myApi;
    Typeface OpenSans_Regular, OpenSans_Bold, regularRoboto, Roboto_Bold;

    Dialog ProgressDialog;
    RotateLoading cusRotateLoading;
    Common common = new Common();
    String email,password;

    //Error Alert
    RelativeLayout rlMainView;
    TextView tvTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        myApi = RetrofitClient.getRetrofitInstance().create(Api.class);
        rlMainView = (RelativeLayout) findViewById(R.id.rlMainView);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        layout_login_main = (LinearLayout) findViewById(R.id.layout_login_main);
        edit_username = (EditText) findViewById(R.id.username);
        edit_password = (EditText) findViewById(R.id.password);
        layout_signin = (RelativeLayout) findViewById(R.id.layout_signin);
        layout_forgotPass = (RelativeLayout) findViewById(R.id.layout_forgotPass);
        layout_back_arrow = (RelativeLayout) findViewById(R.id.layout_back_arrow);

        txt_forgot_password = (TextView) findViewById(R.id.txt_forgot_password);
        txt_sign_in_logo = (TextView) findViewById(R.id.txt_sign_in_logo);

        ProgressDialog = new Dialog(LoginActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading) ProgressDialog.findViewById(R.id.rotateloading_register);

        OpenSans_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold_0.ttf");
        OpenSans_Regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular_0.ttf");
        regularRoboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        edit_username.setTypeface(OpenSans_Regular);
        edit_password.setTypeface(OpenSans_Regular);
        txt_forgot_password.setTypeface(OpenSans_Regular);
        txt_sign_in_logo.setTypeface(Roboto_Bold);

        layout_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, OneTimePasswordActivity.class));
            }
        });

        layout_forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(intent);
            }
        });

        layout_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edit_username.getText().toString().isEmpty()) {
                    // showMKPanelError(LoginActivity.this, getResources().getString(R.string.please_enter_email), rlMainView, tvTitle, regularRoboto);
                    Common.showMkError(LoginActivity.this, getResources().getString(R.string.please_enter_email));

                    return;
                }
                else if(edit_username.getText().toString().trim().length() != 0 && !isValidEmail(edit_username.getText().toString().trim())){
                    Common.showMkError(LoginActivity.this, getResources().getString(R.string.please_enter_valid_email));
                    return;
                }
                else if (edit_password.getText().toString().isEmpty()) {
                    // showMKPanelError(LoginActivity.this, getResources().getString(R.string.please_enter_password), rlMainView, tvTitle, regularRoboto);
                    Common.showMkError(LoginActivity.this, getResources().getString(R.string.please_enter_password));
                    return;
                } else {
                    email = edit_username.getText().toString();
                    password = edit_password.getText().toString();
                    ProgressDialog.show();
                    cusRotateLoading.start();
                    Call<Users> call = myApi.login(email,password);
                    call.enqueue(new Callback<Users>() {
                        @Override
                        public void onResponse(Call<Users> call, Response<Users> response) {
                            Users users = response.body();
                            if (users.status.equalsIgnoreCase("true")) {
                                ProgressDialog.cancel();
                                cusRotateLoading.stop();
                                Users.LoginUserDetails userData = users.getUserDetails();
                                SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString(Constants.user_id, userData.getId());
                                editor.putString(Constants.firstname, userData.getName());
                                editor.putString(Constants.email_id, userData.getEmail_id());
                                editor.putString(Constants.mobileno, userData.getPhone_no());
                                editor.putString(Constants.image, userData.getUser_image());
                                editor.putString(Constants.password, edit_password.getText().toString());
                                editor.putBoolean(Constants.KEY_LOGGED_IN,true);
                                editor.apply();
                                startActivity(new Intent(LoginActivity.this, SplashActivity.class));

                            }else if(users.status.equalsIgnoreCase("wrong_pass")){
                                ProgressDialog.cancel();
                                cusRotateLoading.stop();
                                Snackbar.make(findViewById(android.R.id.content),"Wrong password", Snackbar.LENGTH_LONG).show();
                                Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                ProgressDialog.cancel();
                                cusRotateLoading.stop();
                                Toast.makeText(LoginActivity.this, "invalid email password...", Toast.LENGTH_SHORT).show();
                            }

                            // loader(false);
                        }

                        @Override
                        public void onFailure(Call<Users> call, Throwable t) {
                            ProgressDialog.cancel();
                            cusRotateLoading.stop();
                            Toast.makeText(LoginActivity.this, "Sorry something went wrong...", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                /*
                 *//* if (isNetworkAvailable(LoginActivity.this)) {
                    String BASE_URL = null;
                    try {
                        BASE_URL = RetrofitClient.getRetrofitInstance()+"?email="+ URLEncoder.encode(edit_username.getText().toString().trim(), "UTF-8")+"&password="+edit_password.getText().toString().trim();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Log.d("BASE_URL", "BASE_URL " + BASE_URL);
                    new Common.LoginCallHttp(LoginActivity.this, ProgressDialog, cusRotateLoading, edit_password.getText().toString().trim(), "", BASE_URL).execute();
                } else {
                    showInternetInfo(LoginActivity.this, "network not available.");
                }*//*


            }
        });*/

     /*   layout_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fi = new Intent(LoginActivity.this,ForgotActivity.class);
                startActivity(fi);
            }
        });*/


       /* layout_login_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rlMainView.getVisibility() == View.VISIBLE){
                    if(!isFinishing()){
                        TranslateAnimation slideUp = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -100);
                        slideUp.setDuration(10);
                        slideUp.setFillAfter(true);
                        rlMainView.startAnimation(slideUp);
                        slideUp.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                rlMainView.setVisibility(View.GONE);
                            }
                        });

                    }
                }
            }
        });*/

       /* Common.ValidationGone(LoginActivity.this,rlMainView,edit_username);
        Common.ValidationGone(LoginActivity.this,rlMainView,edit_password);*/

            }

   /* private void loginUser(String email, String password) {
        compositeDisposable.add(myApi.loginUser(email,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>(){
                    @Override
                    public void accept(String s)throws  Exception {
                        if(s.contains("password"))
                            Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(LoginActivity.this,""+s,Toast.LENGTH_SHORT).show();
                    }
                })

        );
    }*/


                /*private void createSession(LoginUserDetails email) {
                    sessionManager.createLoginSession(email.getEmail(), email.getId());
                    Intent intent = new Intent(LoginActivity.this, OneTimePasswordActivity.class);
                    startActivity(intent);
                }*/
        });
    }

    @Override
    public void onBackPressed() {

      /*  if(slidingMenu.isMenuShowing()){
            slidingMenu.toggle();
        }else {*/
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
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
