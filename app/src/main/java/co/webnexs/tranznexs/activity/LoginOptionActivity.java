package co.webnexs.tranznexs.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterViewFlipper;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;

import co.webnexs.tranznexs.R;
import co.webnexs.tranznexs.Api;
import co.webnexs.tranznexs.Banners;
import co.webnexs.tranznexs.RetrofitClient;
import co.webnexs.tranznexs.beens.Banner;
import co.webnexs.tranznexs.adapter.FlipperAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginOptionActivity extends AppCompatActivity {

    RelativeLayout option_layout_signin,option_layout_signup;
    private AdapterViewFlipper adapterViewFlipper;
    RotateLoading cusRotateLoading;
    Dialog ProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_option);

        option_layout_signin = (RelativeLayout)findViewById(R.id.option_layout_signin);
        option_layout_signup = (RelativeLayout)findViewById(R.id.option_layout_signup);
        adapterViewFlipper = (AdapterViewFlipper) findViewById(R.id.adapterViewFlipper);

        ProgressDialog = new Dialog(LoginOptionActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        cusRotateLoading = (RotateLoading)ProgressDialog.findViewById(R.id.rotateloading_register);
        ProgressDialog.show();
        getBanners();

        option_layout_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginOptionActivity.this, OneTimePasswordActivity.class));
                finish();
            }
        });

        option_layout_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginOptionActivity.this, SignUpActivity.class));
                finish();
            }
        });
    }

    private void getBanners() {
        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<Banners> call = api.getBanners();
        call.enqueue(new Callback<Banners>() {
            @Override
            public void onResponse(Call<Banners> call, Response<Banners> response) {
                ProgressDialog.dismiss();

                ArrayList<Banner> banners = response.body().getBanners();

                FlipperAdapter adapter = new FlipperAdapter(getApplicationContext(), banners);

                //adding it to adapterview flipper
                adapterViewFlipper.setAdapter(adapter);
                adapterViewFlipper.setFlipInterval(2000);
                adapterViewFlipper.startFlipping();
                adapterViewFlipper.startFlipping();
            }

            @Override
            public void onFailure(Call<Banners> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage()+"Server error, check your internet", Toast.LENGTH_LONG).show();
                ProgressDialog.dismiss();
            }
        });
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
}
