package co.webnexs.tranznexs.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//import com.google.firebase.iid.FirebaseInstanceId;
import com.wang.avi.AVLoadingIndicatorView;

import co.webnexs.tranznexs.Constants;
import co.webnexs.tranznexs.utils.Common;
import co.webnexs.tranznexs.R;

public class SplashActivity extends AppCompatActivity {

    Common common = new Common();
    private static int SPLASH_TIME_OUT = 1000;
    static boolean running = false;
    AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        avi = (AVLoadingIndicatorView)findViewById(R.id.avi);
        avi.show();
        }

    @Override
    protected void onStart() {
        super.onStart();
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled){
            Intent viewIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(viewIntent);
        }
        else if (ContextCompat.checkSelfPermission(SplashActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            // Check if enabled and if not send user to the GPS settings

            if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

            else{
                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                /* GetActivity();*/
            }

        }else{
            GetActivity();
        }
        running = true;
    }

    private void GetActivity() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //getLocation();
                SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
                if(prefs.getBoolean(Constants.KEY_OTP_LOGGED_IN, false)){
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                    if(prefs.getBoolean(Constants.KEY_LOGGED_IN, false)){
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }if(prefs.getBoolean(Constants.KEY_ONPAYMENT, false)){
                        startActivity(new Intent(SplashActivity.this, Payments.class));
                        finish();
                    }else{
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        finish();
                    }if(prefs.getBoolean(Constants.KEY_ONTRIP, false)){
                        startActivity(new Intent(SplashActivity.this, TrackTruckActivity.class));
                        finish();
                    }else{
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginOptionActivity.class));
                    finish();
                }
            }
        },SPLASH_TIME_OUT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(SplashActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        GetActivity();

                    }
                }else{
                   // Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (ContextCompat.checkSelfPermission(SplashActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION)){
                            ActivityCompat.requestPermissions(SplashActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        }else{
                            ActivityCompat.requestPermissions(SplashActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        }
                    }
                }
                return;
            }
        }
    }

}