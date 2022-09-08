package co.webnexs.tranznexs.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.webnexs.tranznexs.Api;
import co.webnexs.tranznexs.ArrivingDriver;
import co.webnexs.tranznexs.BaseResponse;
import co.webnexs.tranznexs.Constants;
import co.webnexs.tranznexs.DriverConstans;
import co.webnexs.tranznexs.beens.DriverTrack;
import co.webnexs.tranznexs.RetrofitClient;
import co.webnexs.tranznexs.beens.EndTrip;
import co.webnexs.tranznexs.gpsLocation.GPSTracker;
import co.webnexs.tranznexs.map.DirectionObject;
import co.webnexs.tranznexs.map.GsonRequest;
import co.webnexs.tranznexs.map.Helper;
import co.webnexs.tranznexs.map.LegsObject;
import co.webnexs.tranznexs.map.PolylineObject;
import co.webnexs.tranznexs.map.RouteObject;
import co.webnexs.tranznexs.map.StepsObject;
import co.webnexs.tranznexs.map.VolleySingleton;
import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.here.android.mpa.mapping.MapView;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import co.webnexs.tranznexs.utils.AllTripFeed;
import co.webnexs.tranznexs.utils.CircleTransform;
import co.webnexs.tranznexs.utils.Common;
import co.webnexs.tranznexs.utils.DistanceUtil;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.util.EntityUtils;
import io.socket.emitter.Emitter;
import co.webnexs.tranznexs.R;
import retrofit2.Call;
import retrofit2.Callback;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.os.Build.VERSION_CODES.M;
import static com.google.android.gms.maps.model.JointType.ROUND;
import static co.webnexs.tranznexs.Constants.image;

public class TrackTruckActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    RelativeLayout layout_header, rl_info;
    RelativeLayout layout_main_drv_car;
    LinearLayout three_options;
    RelativeLayout layout_location_heading;
    TextView txt_driver_name;
    CardView cancell_Reson;
    TextView txt_mob_num;
    TextView txt_car_name;
    TextView txt_number_plate;
    TextView txt_pickup_address;
    TextView txt_drop_address;
    TextView txt_contact,cancelResonCancelBtn,cencel_reason_done;
    TextView txt_share_eta,confirmOtp;
    TextView txt_cancel, txt_location, changed_mind, afraid_to_Travel, just_Dont_go, rude_call,driver_late;
    ImageView img_drv_car_img/*,addBtn,close_edt_Btn*/,ex_img;
    ImageView img_drv_img, centerImageRipple;
    LinearLayout layout_main_linear,footer,layout_dirvier_car,layout_contact,layout_cancel,layout_share,otpLayout;
    RelativeLayout end_Trip,header_cancel_Trip,arrived_homeLayout;
    FloatingActionButton reCenter_fab,sos_btn;
    LatLng OldLatLog;
    Typeface OpenSans_Regular, OpenSans_Bold, Roboto_Regular, Roboto_Medium, Roboto_Bold, OpenSans_Semi_Bold;

    private GoogleMap googleMap;
    private GoogleMap mMap;
    private MapStyleOptions mapStyle;
    LatLng latLng;
    TrackTruckActivity mListener;
    final String TAG = "GPS";
    double x;
    double y;
    private GPSTracker gpsTracker;
    protected LocationManager locationManager;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    boolean canGetLocation = true;
    boolean isGPS = false;
    boolean isNetwork = false;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    Location loc;
    int devise_height;
    SupportMapFragment mapFragment;

   // boolean animationStart = true;
    boolean MapLayoutClick = false;
    boolean FooterMapLayoutClick = true;

    private ArrayList<LatLng> arrayPoints = null;
    MarkerOptions markerOption;
    MarkerOptions DriverMarkerOption;
    Marker DriverMarker;

    int markerPositon;

    Socket mSocket;

    Animation slideUpAnimation, slideDownAnimation;

    Dialog ShareDialog,header_cancel_Trip_dialog,arrived_dialog;
    String DriverPhNo, DriverMobileNumber, from, to, driver_lat,CabModelName,driver_lng, DriverName,
            DriverImg, bikeNum,
            ride_id,otp,stringLatitude,stringLongitude, Road_lng,Road_lat;
    String Driver_Id = "0";
    CircleImageView driverImg;
    SharedPreferences userPref;
    BroadcastReceiver receiver;

    AllTripFeed allTripFeed;
    int LatLngCom = 0;
    float googleZoom = 0;
    double CoverdValue = 0;
    MapView mMapView;
    double PickupLatitude, Driver_lat, Driver_lng;
    double PickupLongtude;
    double DropLatitude;
    double DropLongtude;
    PolylineOptions lineOptions, graylineOptions;
    Polyline polyline, greyPolyLine;
    String distanceFromApi, pickup_lat, pickup_lng, drop_lat, drop_lng, user_id, request_Time,
            cancellation_Resson;
    int driverAccept = 3 * 1000; //Here period is 1 mint
    int driver_Start_Track = 5 * 1000; //Here period is 1 mint
    int driver_Start = 6 * 1000; //Here period is 1 mint
    int trackInterval = 7 * 1000; //Here period is 1 mint
    Timer acceptTimer, driverStartTrack,startDriverSide, endTripDriverSide;
    TimerTask timerTaskAsyn;
    private static TrackTruckActivity instance;
    RippleBackground rippleBackground;
    EditText addDestination;
    boolean connected = false;
    CountDownTimer countDownTimer;
    TextView counttime,heading_txt,adition_txt;
    Button done;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    protected void onStart() {
        super.onStart();
        reCenter();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_truck);

        SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(Constants.KEY_ONTRIP,true);
        editor.apply();


        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        OpenSans_Regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular_0.ttf");
        OpenSans_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold_0.ttf");
        OpenSans_Semi_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Semibold_0.ttf");
        Roboto_Regular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Medium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        Roboto_Bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        rippleBackground = (RippleBackground) findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM");
        SimpleDateFormat dfs = new SimpleDateFormat("dd");

        SharedPreferences prefs = getSharedPreferences(DriverConstans.MY_Driver_PREFS_NAME, MODE_PRIVATE);
        pickup_lng = prefs.getString(DriverConstans.USER_PICKUP_LNG, "");
        pickup_lat = prefs.getString(DriverConstans.USER_PICKUP_LAT, "");
        drop_lat = prefs.getString(DriverConstans.USER_DROP_LAT, "");
        // DropLatitude = Double.parseDouble(drop_lat);
        drop_lng = prefs.getString(DriverConstans.USER_DROP_LNG, "");
        //DropLongtude = Double.parseDouble(drop_lng);
        from = prefs.getString(DriverConstans.USER_FROM_LOCATION, "");
        to = prefs.getString(DriverConstans.USER_TO_LOCATION, "");
        DriverMobileNumber = prefs.getString(DriverConstans.Driver_mobno, "");
        DriverName = prefs.getString(DriverConstans.Driver_fname, "");
        DriverImg = prefs.getString(DriverConstans.Driver_img, "");
        bikeNum = prefs.getString(DriverConstans.Driver_BIKE_NO, "");
        Driver_Id = prefs.getString(DriverConstans.Driver_Id, "");
        driver_lat = prefs.getString(DriverConstans.Driver_lat, "");
        driver_lng = prefs.getString(DriverConstans.Driver_log, "");
        CabModelName = prefs.getString(DriverConstans.CabName, "");
        request_Time = prefs.getString(DriverConstans.tempDateTime, "");
        otp = prefs.getString(DriverConstans.otp, "");
        Road_lng = prefs.getString(DriverConstans.Road_lng, "");
        Road_lat = prefs.getString(DriverConstans.Road_lat, "");
        ride_id = prefs.getString(DriverConstans.ride_id, "");

        SharedPreferences pref = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        distanceFromApi = pref.getString(Constants.distance, "");
        user_id = pref.getString(Constants.user_id, "");

        txt_mob_num = (TextView) findViewById(R.id.txt_mob_num);
        txt_mob_num.setText(DriverMobileNumber);
        txt_driver_name = (TextView) findViewById(R.id.txt_driver_name);
        txt_driver_name.setText("Driver "+DriverName);
        txt_driver_name.setTypeface(OpenSans_Bold);
        txt_car_name = (TextView) findViewById(R.id.txt_car_name);
        txt_car_name.setText(CabModelName);
        txt_number_plate = (TextView) findViewById(R.id.txt_number_plate);
        txt_number_plate.setText(bikeNum);



        if(prefs.getBoolean(Constants.ARRIVED, false)){
            DriverAccepting();
        }

        centerImageRipple = (ImageView) findViewById(R.id.centerImageRipple);
        Picasso.get()
                .load(Uri.parse(pref.getString(image, "")))
                .placeholder(R.drawable.unknown_driver)
                .transform(new CircleTransform())
                .into(centerImageRipple);

        TextView txtfromView = (TextView) findViewById(R.id.txt_pickup_address);
        txtfromView.setText(from);

        TextView txtToView = (TextView) findViewById(R.id.txt_drop_address);
        txtToView.setText(to);

        layout_header = (RelativeLayout) findViewById(R.id.layout_header);
        rl_info = (RelativeLayout) findViewById(R.id.rl_info);
       /* layout_main_drv_car = (RelativeLayout) findViewById(R.id.layout_main_drv_car);*/
        three_options = (LinearLayout) findViewById(R.id.three_options);
        footer = (LinearLayout)findViewById(R.id.footer);
        /*layout_location_heading = (RelativeLayout) findViewById(R.id.layout_location_heading);*/
        txt_pickup_address = (TextView) findViewById(R.id.txt_pickup_address);
        txt_drop_address = (TextView) findViewById(R.id.txt_drop_address);
        txt_contact = (TextView) findViewById(R.id.txt_contact);
        txt_share_eta = (TextView) findViewById(R.id.txt_share_eta);
       // txt_cancel = (TextView) findViewById(R.id.txt_cancel);
        img_drv_car_img = (ImageView) findViewById(R.id.img_drv_car_img);
        img_drv_img = (ImageView) findViewById(R.id.img_drv_img);
        layout_main_linear = (LinearLayout) findViewById(R.id.layout_main_linear);
        layout_contact = (LinearLayout) findViewById(R.id.layout_contact);
        layout_share = (LinearLayout) findViewById(R.id.layout_share);
        confirmOtp = (TextView) findViewById(R.id.confirmOtp);
        //layout_cancel = (LinearLayout) findViewById(R.id.layout_cancel);
        otpLayout = (LinearLayout) findViewById(R.id.otpLayout);
        header_cancel_Trip = (RelativeLayout) findViewById(R.id.header_cancel_Trip);
       /* addBtn = (ImageView) findViewById(R.id.addBtn);
        close_edt_Btn = (ImageView) findViewById(R.id.close_edt_Btn);*/
        addDestination = (EditText) findViewById(R.id.addDestination);
        end_Trip = (RelativeLayout) findViewById(R.id.end_Trip);

        /* arrivingDriver = (RelativeLayout) findViewById(R.id.arrivingDriver);*/

        acceptTimer = new Timer();
        timerTaskAsyn = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DriverAccepting();
                        Log.d("repeat", "after each 3 sec");
                    }
                });
            }
        };
        acceptTimer.schedule(timerTaskAsyn,0,driverAccept);

        header_cancel_Trip_dialog = new Dialog(TrackTruckActivity.this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        header_cancel_Trip_dialog.setContentView(R.layout.cancel_reson_dialog);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            header_cancel_Trip_dialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        header_cancel_Trip_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        header_cancel_Trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                header_cancel_Trip_dialog.show();
            }
        });
        changed_mind = (TextView)header_cancel_Trip_dialog.findViewById(R.id.changed_mindset);
        changed_mind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cencel_reason_done.setVisibility(View.VISIBLE);
                changed_mind.setBackgroundResource(R.drawable.btn_accent_circle_white_outlined);
                afraid_to_Travel.setBackgroundResource(0);
                just_Dont_go.setBackgroundResource(0);
                rude_call.setBackgroundResource(0);
                driver_late.setBackgroundResource(0);
                cancellation_Resson = changed_mind.getText().toString();
            }
        });
        afraid_to_Travel = (TextView)header_cancel_Trip_dialog.findViewById(R.id.afraid_to_Travel);
        afraid_to_Travel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cencel_reason_done.setVisibility(View.VISIBLE);
                changed_mind.setBackgroundResource(0);
                afraid_to_Travel.setBackgroundResource(R.drawable.btn_accent_circle_white_outlined);
                just_Dont_go.setBackgroundResource(0);
                rude_call.setBackgroundResource(0);
                driver_late.setBackgroundResource(0);
                cancellation_Resson = afraid_to_Travel.getText().toString();
            }
        });
        just_Dont_go = (TextView)header_cancel_Trip_dialog.findViewById(R.id.just_Dont_go);
        just_Dont_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cencel_reason_done.setVisibility(View.VISIBLE);
                changed_mind.setBackgroundResource(0);
                afraid_to_Travel.setBackgroundResource(0);
                just_Dont_go.setBackgroundResource(R.drawable.btn_accent_circle_white_outlined);
                rude_call.setBackgroundResource(0);
                driver_late.setBackgroundResource(0);
                cancellation_Resson = just_Dont_go.getText().toString();
            }
        });
        rude_call = (TextView)header_cancel_Trip_dialog.findViewById(R.id.rude_call);
        rude_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cencel_reason_done.setVisibility(View.VISIBLE);
                changed_mind.setBackgroundResource(0);
                afraid_to_Travel.setBackgroundResource(0);
                just_Dont_go.setBackgroundResource(0);
                rude_call.setBackgroundResource(R.drawable.btn_accent_circle_white_outlined);
                driver_late.setBackgroundResource(0);
                cancellation_Resson = rude_call.getText().toString();

            }
        });
        driver_late = (TextView)header_cancel_Trip_dialog.findViewById(R.id.driver_late);
        driver_late.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cencel_reason_done.setVisibility(View.VISIBLE);
                changed_mind.setBackgroundResource(0);
                afraid_to_Travel.setBackgroundResource(0);
                just_Dont_go.setBackgroundResource(0);
                rude_call.setBackgroundResource(0);
                driver_late.setBackgroundResource(R.drawable.btn_accent_circle_white_outlined);
                cancellation_Resson = driver_late.getText().toString();

            }
        });
        cancelResonCancelBtn = (TextView)header_cancel_Trip_dialog.findViewById(R.id.cancelResonCancelBtn);
        cancelResonCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                header_cancel_Trip_dialog.dismiss();
            }
        });
        cencel_reason_done = (TextView)header_cancel_Trip_dialog.findViewById(R.id.cencel_reason_done);
        cencel_reason_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cacellation();
            }
        });

        arrived_dialog = new Dialog(TrackTruckActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        arrived_dialog.setContentView(R.layout.arrived_dialog);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            arrived_dialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        arrived_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
        heading_txt = (TextView)arrived_dialog.findViewById(R.id.heading_txt);
        adition_txt = (TextView)arrived_dialog.findViewById(R.id.adition_txt);
        ex_img = (ImageView)arrived_dialog.findViewById(R.id.ex_img);
        arrived_homeLayout = (RelativeLayout)arrived_dialog.findViewById(R.id.arrived_homeLayout);
        done = (Button)arrived_dialog.findViewById(R.id.done);

        arrived_homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrived_dialog.cancel();
                layout_contact.setVisibility(View.GONE);
                header_cancel_Trip.setVisibility(View.GONE);
            }
        });
        counttime=findViewById(R.id.counttime);
        countDownTimer= new CountDownTimer(150000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                counttime.setText(f.format(min) + ":" + f.format(sec));
                // counter++;
            }
            @Override
            public void onFinish() {
                counttime.setText("00:00");
                noDrivers();
            }
        }.start();

       /*  addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDestination.setVisibility(View.VISIBLE);
                close_edt_Btn.setVisibility(View.VISIBLE);
                txt_drop_address.setVisibility(View.GONE);
                addBtn.setVisibility(View.GONE);
            }
        });

        close_edt_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_drop_address.setVisibility(View.VISIBLE);
                addDestination.setVisibility(View.GONE);
                close_edt_Btn.setVisibility(View.GONE);
                addBtn.setVisibility(View.VISIBLE);
            }
        });

       LinearLayout navigates = (LinearLayout)findViewById(R.id.navigate);
        navigates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrivingDriver.setVisibility(View.GONE);
                navigate();
            }
        });*/



        /*final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                arrivingDriver.setVisibility(View.GONE);
            }
        }, 60000);*/



        end_Trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HeaderEndTrip();
            }
        });

        allTripFeed = Common.allTripFeeds;
        Log.e("Okayswiss", "Userprofile :" + allTripFeed);

        arrayPoints = new ArrayList<LatLng>();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        devise_height = displaymetrics.heightPixels;

       // txt_remaining.setTypeface(OpenSans_Regular);
       // txt_covered.setTypeface(OpenSans_Regular);
       // txt_total_km.setTypeface(OpenSans_Regular);
       // txt_remaining_val.setTypeface(Roboto_Medium);
       // txt_total_km_val.setTypeface(Roboto_Medium);
        txt_driver_name.setTypeface(OpenSans_Regular);
       // txt_mob_num.setTypeface(OpenSans_Regular);
        txt_car_name.setTypeface(OpenSans_Regular);
        txt_number_plate.setTypeface(OpenSans_Regular);
        txt_pickup_address.setTypeface(OpenSans_Regular);
        txt_drop_address.setTypeface(OpenSans_Regular);

        txt_contact.setTypeface(OpenSans_Semi_Bold);
        txt_share_eta.setTypeface(OpenSans_Semi_Bold);
       // txt_cancel.setTypeface(OpenSans_Semi_Bold);

        reCenter_fab = (FloatingActionButton) findViewById(R.id.reCenter_fab);
        sos_btn = (FloatingActionButton) findViewById(R.id.sos_Btn);
        reCenter_fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reCenter();
            }
        });
        sos_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + "100"));
                startActivity(callIntent);
            }
        });

       /* layout_contact.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FooterMapLayoutClick = false;
                return false;
            }
        });*/
        layout_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + DriverMobileNumber));
                        startActivity(callIntent);
                    }
                }, 100);

            }
        });

       /* layout_share.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FooterMapLayoutClick = false;
                return false;
            }
        });*/

        layout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bitmapimage = BitmapFactory.decodeResource(getResources(),
                        R.drawable.logo);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/jpeg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmapimage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = new File(Environment.getExternalStorageDirectory()
                        + File.separator + "temporary_file.jpg");

                try {
                    f.createNewFile();
                    new FileOutputStream(f).write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }


                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg"));
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hello everyone");
                startActivity(Intent.createChooser(sharingIntent, "Share image using"));
            }
               /* ShareDialog = new Dialog(TrackTruckActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                ShareDialog.setContentView(R.layout.camera_dialog_layout);

                TextView Whatsapp = (TextView)ShareDialog.findViewById(R.id.txt_open_camera);
                Whatsapp.setText("Whatsapp Share");

                TextView Facebook = (TextView)ShareDialog.findViewById(R.id.txt_open_gallery);
                Facebook.setText("Facebook Share");

                RelativeLayout layout_open_camera = (RelativeLayout) ShareDialog.findViewById(R.id.layout_open_camera);
                layout_open_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShareDialog.cancel();
                        DropDownAnim anim = new DropDownAnim(layout_main_drv_car, (int) (devise_height * 0.55), animationStart);
                        anim.setDuration(50);
                        layout_main_drv_car.startAnimation(anim);
                        animationStart = true;
                        layout_dirvier_car.setBackgroundResource(R.drawable.track_truck_footer_bg);

                    }
                });

                RelativeLayout layout_open_gallery = (RelativeLayout) ShareDialog.findViewById(R.id.layout_open_gallery);
                layout_open_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShareDialog.cancel();
                        DropDownAnim anim = new DropDownAnim(layout_main_drv_car, (int) (devise_height * 0.55), animationStart);
                        anim.setDuration(50);
                        layout_main_drv_car.startAnimation(anim);
                        animationStart = true;
                        layout_dirvier_car.setBackgroundResource(R.drawable.track_truck_footer_bg);

                    }
                });

                RelativeLayout layout_open_cancel = (RelativeLayout) ShareDialog.findViewById(R.id.layout_open_cancel);
                layout_open_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShareDialog.cancel();
                        FooterMapLayoutClick = true;
                    }
                });

                ShareDialog.show();
            }*/
        });

        /*layout_cancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FooterMapLayoutClick = false;
                cancell_Reson.setVisibility(View.VISIBLE);
                return false;
            }
        });*/
      /*  layout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               *//* animationStart = false;
                DropDownAnim anim = new DropDownAnim(layout_main_drv_car, (int) (devise_height * 0.30), animationStart);
                anim.setDuration(100);
                layout_main_drv_car.startAnimation(anim);
                animationStart = true;*//*
            }
        });*/


      /*  layout_main_drv_car.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!FooterMapLayoutClick)
                    FooterMapLayoutClick = true;
                return false;
            }
        });*/

       /* layout_main_drv_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FooterMapLayoutClick)
                    FooterMapLayoutClick = true;
            }
        });*/

       /* layout_dirvier_car.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FooterMapLayoutClick = false;
                return false;
            }
        });*/

        /*layout_dirvier_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // layout_dirvier_car.setEnabled(false);
                *//*DropDownAnim anim = new DropDownAnim(layout_main_drv_car, (int) (devise_height * 0.30), animationStart);
                anim.setDuration(100);
                layout_main_drv_car.startAnimation(anim);*//*

               *//* if (animationStart) {
                    animationStart = false;
                    //MapLayoutClick = false;
                    layout_dirvier_car.setBackgroundResource(0);
                } else {
                    //animationStart = true;
                    // MapLayoutClick = true;
                }*//*

            }
        });*/

    }

    private void noDrivers() {
        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<BaseResponse> call = api.noDriver(user_id, request_Time);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
                BaseResponse user = response.body();

                if (user.status.equalsIgnoreCase("true")) {
                    acceptTimer.cancel();
                    acceptTimer.purge();
                    driverAccept = 0;
                    header_cancel_Trip_dialog.dismiss();
                    countDownTimer.cancel();
                    Toast.makeText(TrackTruckActivity.this, user.getMessage(), Toast.LENGTH_LONG).show();

                    SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean(Constants.KEY_ONTRIP, false);
                    editor.putString(Constants.amount, "0.00");
                    editor.apply();
                    driver_Start = 0;
                    SharedPreferences prefs = getSharedPreferences(DriverConstans.MY_Driver_PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.clear();
                    edit.apply();
                    Intent intent = new Intent(TrackTruckActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                   /* Toast.makeText(TrackTruckActivity.this, "Cancel_trip_false.", Toast.LENGTH_SHORT).show();*/
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                if (Build.VERSION.SDK_INT >= M) {
                    return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
                }
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }



    public void navigate() {
        reCenter();
        mMap.clear();
        PickupLongtude = Double.parseDouble(pickup_lng);
        PickupLatitude = Double.parseDouble(pickup_lat);
        Driver_lat = Double.parseDouble(driver_lat);
        Driver_lng = Double.parseDouble(driver_lng);
        LatLng Pickup = new LatLng(PickupLatitude, PickupLongtude);
        mMap.addMarker(new MarkerOptions().position(Pickup).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.black_location_pin)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(Driver_lat, Driver_lng)).title("Driver Location.").icon(BitmapDescriptorFactory.fromResource(R.drawable.cartop)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Pickup));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(Pickup)
                .zoom(7)
                .build();
        String directionApiPath = Helper.getUrl(String.valueOf(PickupLatitude), String.valueOf(PickupLongtude),
                String.valueOf(driver_lat), String.valueOf(driver_lat));
        //Log.d(TAG, "Path " + directionApiPath);
        getDirectionFromDirectionApiServer(directionApiPath);

    }


    private void DriverAccepting() {
        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<ArrivingDriver> call = api.ArrivingDriverDetails(user_id, request_Time);
        call.enqueue(new Callback<ArrivingDriver>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(Call<ArrivingDriver> call, retrofit2.Response<ArrivingDriver> response) {
                ArrivingDriver user = response.body();

                if (user.status.equalsIgnoreCase("true")) {
                    sos_btn.setVisibility(View.VISIBLE);
                    countDownTimer.cancel();
                    counttime.setVisibility(View.GONE);
                    reCenter_fab.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.VISIBLE);
                    rippleBackground.stopRippleAnimation();
                    centerImageRipple.setVisibility(View.GONE);
                    acceptTimer.cancel();
                    driverAccept = 0;
                    acceptTimer.purge();
                    confirmOtp.setText(otp);
                    ArrivingDriver.ArrivingDriverDetails userData = user.getDriverDetails();
                    Driver_Id = userData.getDriver_id();
                    ride_id = userData.getRide_id();
                    DriverName = userData.getFirstname();
                    DriverMobileNumber = userData.getPhone_no();
                    DriverImg = userData.getDriver_image();
                    bikeNum = userData.getNumber_plate();
                    driver_lat = userData.getLatitude();
                    driver_lng = userData.getLongitude();
                    CabModelName = userData.getBrand();
                    SharedPreferences.Editor editor = getSharedPreferences(DriverConstans.MY_Driver_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString(DriverConstans.Driver_fname, userData.getFirstname());
                    editor.putString(DriverConstans.Driver_Id, userData.getDriver_id());
                    editor.putString(DriverConstans.Driver_mobno, userData.getPhone_no());
                    editor.putString(DriverConstans.Driver_img, userData.getDriver_image());
                    editor.putString(DriverConstans.Driver_BIKE_NO, userData.getNumber_plate());
                    editor.putString(DriverConstans.Driver_lat, userData.getLatitude());
                    editor.putString(DriverConstans.Driver_log, userData.getLongitude());
                    editor.putString(DriverConstans.CabName, userData.getBrand());
                    editor.apply();
                    txt_number_plate.setText(userData.getNumber_plate());
                    txt_driver_name.setText("Driver "+userData.getFirstname());
                    txt_mob_num.setText(userData.getPhone_no());
                    txt_car_name.setText(userData.getBrand());

                    Picasso.get()
                            .load(Uri.parse(userData.getDriver_image()))
                            .placeholder(R.drawable.mail_defoult)
                            .transform(new CircleTransform())
                            .into(img_drv_img);

                    driverStartTrack = new Timer();
                    TimerTask timerTaskAsyn = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DriverTracking();
                                }
                            });
                        }
                    };
                    driverStartTrack.schedule(timerTaskAsyn,0, driver_Start_Track);

                    startDriverSide = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    StartRideAtDriverSide();
                                }
                            });
                        }
                    };
                    startDriverSide.schedule(timerTask,0, driver_Start);

                    /* navigate();*/
                    Toast.makeText(TrackTruckActivity.this, "driver shortly arriving", Toast.LENGTH_SHORT).show();

                }
                else if(user.status.equalsIgnoreCase("arrived")){
                    if(rippleBackground.isRippleAnimationRunning()){
                        rippleBackground.stopRippleAnimation();
                    }
                    acceptTimer.cancel();
                    driverAccept = 0;
                    centerImageRipple.setVisibility(View.GONE);
                    header_cancel_Trip.setVisibility(View.GONE);
                    reCenter_fab.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.VISIBLE);
                    sos_btn.setVisibility(View.VISIBLE);
                    confirmOtp.setText(otp);
                    counttime.setVisibility(View.GONE);
                    txt_number_plate.setText(bikeNum);
                    txt_driver_name.setText("Driver "+DriverName);
                    txt_mob_num.setText(DriverMobileNumber);
                    txt_car_name.setText(CabModelName);

                    Picasso.get()
                            .load(Uri.parse(DriverImg))
                            .placeholder(R.drawable.mail_defoult)
                            .transform(new CircleTransform())
                            .into(img_drv_img);


                    PickupLongtude = Double.parseDouble(pickup_lng);
                    PickupLatitude = Double.parseDouble(pickup_lat);
                    DropLatitude = Double.parseDouble(drop_lat);
                    DropLongtude = Double.parseDouble(drop_lng);
                    LatLng Pickup = new LatLng(PickupLatitude, PickupLongtude);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(Pickup).title("Pickup from").icon(BitmapDescriptorFactory.fromResource(R.drawable.black_location_pin)));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(DropLatitude,DropLongtude)).title("Drop At.").icon(BitmapDescriptorFactory.fromResource(R.drawable.black_location_pin)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(Pickup));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(Pickup)
                            .zoom(15)
                            .build();
                    String directionApiPath = Helper.getUrl(String.valueOf(PickupLatitude), String.valueOf(PickupLongtude),
                            String.valueOf(DropLatitude), String.valueOf(DropLongtude));
                    //Log.d(TAG, "Path " + directionApiPath);
                    getDirectionFromDirectionApiServer(directionApiPath);

                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            arrived_dialog.cancel();
                            layout_contact.setVisibility(View.GONE);
                            header_cancel_Trip.setVisibility(View.GONE);
                        }
                    });
                    arrived_homeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            arrived_dialog.cancel();
                            layout_contact.setVisibility(View.GONE);
                            header_cancel_Trip.setVisibility(View.GONE);
                        }
                    });
                    startDriverSide = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    StartRideAtDriverSide();
                                }
                            });
                        }
                    };
                    startDriverSide.schedule(timerTask,0, driver_Start);
                }else if(user.status.equalsIgnoreCase("started")){

                    centerImageRipple.setVisibility(View.GONE);
                    header_cancel_Trip.setVisibility(View.GONE);
                    reCenter_fab.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.VISIBLE);
                    sos_btn.setVisibility(View.VISIBLE);
                    PickupLongtude = Double.parseDouble(pickup_lng);
                    PickupLatitude = Double.parseDouble(pickup_lat);
                    DropLatitude = Double.parseDouble(drop_lat);
                    DropLongtude = Double.parseDouble(drop_lng);
                    LatLng Pickup = new LatLng(PickupLatitude, PickupLongtude);
                    mMap.clear();
                    otpLayout.setVisibility(View.GONE);
                    mMap.addMarker(new MarkerOptions().position(Pickup).title("Pickup from").icon(BitmapDescriptorFactory.fromResource(R.drawable.black_location_pin)));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(DropLatitude, DropLongtude)).title("Drop At.").icon(BitmapDescriptorFactory.fromResource(R.drawable.black_location_pin)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(Pickup));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                    if(rippleBackground.isRippleAnimationRunning()){
                        rippleBackground.stopRippleAnimation();
                        centerImageRipple.setVisibility(View.GONE);
                    }
                    acceptTimer.cancel();
                    driverAccept = 0;
                    footer.setVisibility(View.VISIBLE);
                    header_cancel_Trip.setVisibility(View.GONE);
                    reCenter_fab.setVisibility(View.VISIBLE);
                    counttime.setVisibility(View.GONE);
                    sos_btn.setVisibility(View.VISIBLE);
                    txt_number_plate.setText(bikeNum);
                    txt_driver_name.setText("Driver "+DriverName);
                    txt_mob_num.setText(DriverMobileNumber);
                    txt_car_name.setText(CabModelName);
                    Picasso.get()
                            .load(Uri.parse(DriverImg))
                            .placeholder(R.drawable.mail_defoult)
                            .transform(new CircleTransform())
                            .into(img_drv_img);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(Pickup)
                            .zoom(15)
                            .build();
                    String directionApiPath = Helper.getUrl(String.valueOf(PickupLatitude), String.valueOf(PickupLongtude),
                            String.valueOf(DropLatitude), String.valueOf(DropLongtude));
                    //Log.d(TAG, "Path " + directionApiPath);
                    getDirectionFromDirectionApiServer(directionApiPath);

                    arrived_dialog.show();
                    heading_txt.setText("Your ride started.");
                    adition_txt.setText("Happy journey "+DriverName);
                    ex_img.setImageResource(R.drawable.rental_image);
                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            arrived_dialog.cancel();
                            layout_contact.setVisibility(View.GONE);
                            header_cancel_Trip.setVisibility(View.GONE);
                        }
                    });


                    if (ActivityCompat.checkSelfPermission(TrackTruckActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackTruckActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(PickupLatitude, PickupLongtude), 15.0f));
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mMap.setMyLocationEnabled(true);
                    endTripDriverSide = new Timer();
                    TimerTask timerTaskAsyn = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    endTripAtDriverSide();
                                }
                            });
                        }
                    };
                    endTripDriverSide.schedule(timerTaskAsyn, 0, trackInterval);

                }else if(user.status.equalsIgnoreCase("search")){

                }
                else {
                    countDownTimer.cancel();
                     Toast.makeText(TrackTruckActivity.this, user.getMessage(), Toast.LENGTH_LONG).show();
                    acceptTimer.cancel();
                    acceptTimer.purge();
                    driverAccept = 0;
                    SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean(Constants.KEY_ONTRIP, false);
                    editor.putString(Constants.amount, "0.00");
                    editor.apply();
                    driver_Start = 0;
                    SharedPreferences prefs = getSharedPreferences(DriverConstans.MY_Driver_PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.clear();
                    edit.apply();
                    Intent intent = new Intent(TrackTruckActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void onFailure(Call<ArrivingDriver> call, Throwable t) {
                Toast.makeText(TrackTruckActivity.this, "Check your internet.!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void DriverTracking() {
        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<DriverTrack> call = api.driverTracking(Driver_Id,ride_id,pickup_lat,pickup_lng);
        call.enqueue(new Callback<DriverTrack>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(Call<DriverTrack> call, retrofit2.Response<DriverTrack> response) {
                DriverTrack users = response.body();

                if (users.status.equalsIgnoreCase("true")) {
                    DriverTrack.DriverLocation userData = users.getDriverLocation();
                    acceptTimer.cancel();
                    mMap.clear();
                    SharedPreferences.Editor editor = getSharedPreferences(DriverConstans.MY_Driver_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString(DriverConstans.Road_lat, userData.getRoad_lat());
                    editor.putString(DriverConstans.Road_lng, userData.getRoad_lng());
                    editor.apply();
                    PickupLongtude = Double.parseDouble(pickup_lng);
                    PickupLatitude = Double.parseDouble(pickup_lat);
                    Double Driverlat = Double.parseDouble(userData.getRoad_lat());
                    Double Driverlng = Double.parseDouble(userData.getRoad_lng());
                    LatLng Driver = new LatLng(Driverlat, Driverlng);
                    mMap.addMarker(new MarkerOptions().position(new LatLng(PickupLatitude, PickupLongtude)).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.black_location_pin)));
                    mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).flat(true).position(new LatLng(Driverlat, Driverlng)).title("Driver Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.cartop))

                    );
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(Driver));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(Driver)
                            .zoom(15)
                            .build();
                    String directionApiPath = Helper.getUrl(String.valueOf(PickupLatitude), String.valueOf(PickupLongtude),
                            String.valueOf(Driverlat), String.valueOf(Driverlng));
                    getDirectionFromDirectionApiServer(directionApiPath);
                    //Toast.makeText(TrackTruckActivity.this, "driver tracking", Toast.LENGTH_SHORT).show();
                }
                else if(users.status.equalsIgnoreCase("arrived")){

                    SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean(Constants.ARRIVED,true);
                    editor.apply();

                    arrived_dialog.show();
                    driverStartTrack.cancel();
                    driver_Start_Track=0;
                    if(rippleBackground.isRippleAnimationRunning()){
                        rippleBackground.stopRippleAnimation();
                    }
                    header_cancel_Trip.setVisibility(View.GONE);
                    confirmOtp.setText(otp);
                    counttime.setVisibility(View.GONE);
                    sos_btn.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.VISIBLE);
                    txt_number_plate.setText(bikeNum);
                    txt_driver_name.setText("Driver "+DriverName);
                    txt_mob_num.setText(DriverMobileNumber);
                    txt_car_name.setText(CabModelName);

                    Picasso.get()
                            .load(Uri.parse(DriverImg))
                            .placeholder(R.drawable.mail_defoult)
                            .transform(new CircleTransform())
                            .into(img_drv_img);

                    LatLng Pickup = new LatLng(PickupLatitude, PickupLongtude);
                   Double lat = Double.parseDouble(driver_lat);
                   Double lng = Double.parseDouble(driver_lng);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(Pickup).title("Pickup from").icon(BitmapDescriptorFactory.fromResource(R.drawable.black_location_pin)));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title("Drop At.").icon(BitmapDescriptorFactory.fromResource(R.drawable.black_location_pin)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(Pickup));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(Pickup)
                            .zoom(15)
                            .build();
                    String directionApiPath = Helper.getUrl(String.valueOf(PickupLatitude), String.valueOf(PickupLongtude),
                            String.valueOf(lat), String.valueOf(lng));
                    //Log.d(TAG, "Path " + directionApiPath);
                    getDirectionFromDirectionApiServer(directionApiPath);

                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            arrived_dialog.cancel();
                            layout_contact.setVisibility(View.GONE);
                            header_cancel_Trip.setVisibility(View.GONE);
                        }
                    });
                    arrived_homeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            arrived_dialog.cancel();
                            layout_contact.setVisibility(View.GONE);
                            header_cancel_Trip.setVisibility(View.GONE);
                        }
                    });
                }else if(users.status.equalsIgnoreCase("again_search")){
                    driverStartTrack.cancel();
                    startDriverSide.cancel();
                    driver_Start_Track=0;
                    driver_Start=0;
                    Intent i = getBaseContext().getPackageManager().
                            getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
                else if(users.status.equalsIgnoreCase("driver_cancel")){
                    driverStartTrack.cancel();
                    startDriverSide.cancel();
                    driver_Start_Track=0;
                    driver_Start=0;
                    SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean(Constants.KEY_ONTRIP, false);
                    editor.putString(Constants.amount, "0.00");
                    editor.apply();
                    Toast.makeText(TrackTruckActivity.this, users.getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TrackTruckActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(TrackTruckActivity.this, "driver tracking false.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<DriverTrack> call, Throwable t) {
                Toast.makeText(TrackTruckActivity.this, "Check your internet connection...", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void cancelTracking() {
        if(driverStartTrack != null) {
            driverStartTrack.cancel();
            driverStartTrack = null;
        }
    }

    private void cacellation() {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String Cancelletion_Time = formatter.format(date);

                Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
                    Call<BaseResponse> call = api.cancelRide(ride_id,Cancelletion_Time,cancellation_Resson);
                call.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
                        BaseResponse user = response.body();

                        if (user.status.equalsIgnoreCase("true")) {
                            acceptTimer.cancel();
                            countDownTimer.cancel();
                            header_cancel_Trip_dialog.dismiss();
                            Toast.makeText(TrackTruckActivity.this, "Cancelation succeed.", Toast.LENGTH_SHORT).show();

                            SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putBoolean(Constants.KEY_ONTRIP, false);
                            editor.putString(Constants.amount, "0.00");
                            editor.apply();
                            driver_Start = 0;
                            SharedPreferences prefs = getSharedPreferences(DriverConstans.MY_Driver_PREFS_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = prefs.edit();
                            edit.clear();
                            edit.apply();
                            Intent intent = new Intent(TrackTruckActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();


                        } else {
                            Toast.makeText(TrackTruckActivity.this, "Cancel_trip_false.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {

                    }
                });
    }

    private void HeaderEndTrip() {
        acceptTimer.cancel();
        /*SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(Constants.KEY_ONPAYMENT, false);
        editor.putBoolean(Constants.KEY_ONTRIP, false);
        editor.apply();*/
        SharedPreferences prefs = getSharedPreferences(DriverConstans.MY_Driver_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.apply();
        startActivity(new Intent(TrackTruckActivity.this, Payments.class));
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        IntentFilter filter = new IntentFilter("come.texi.user.activity.TrackTruckActivity");
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Common.is_pusnotification = 1;
                }
            };
            registerReceiver(receiver, filter);
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        mMap = googleMap;

        mapStyle = MapStyleOptions.loadRawResourceStyle(this,R.raw.style_json_silver);
        mMap.setMapStyle(mapStyle);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        GPSTracker gpsTracker = new GPSTracker(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 15.0f));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);

        dp10 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                MapLayoutClick = true;

            }
        });

        googleMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {
                // Toast.makeText(TrackTruckActivity.this,"Zoom Cancel",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getLocation() {
        gpsTracker = new GPSTracker(TrackTruckActivity.this);
        if (gpsTracker.canGetLocation()) {
            x = gpsTracker.getLatitude();
            y = gpsTracker.getLongitude();
            Toast.makeText(this, "New Example lat : " + x + " " + y, Toast.LENGTH_SHORT).show();
        }
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
        x = loc.getLatitude();
        y = loc.getLongitude();
        // tvTime.setText(DateFormat.getTimeInstance().format(loc.getTime()));
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        updateUI(location);


        if (mListener != null) {
            mListener.onLocationChanged(location);

            //Move the camera to the user's location and zoom in!
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void getDirectionFromDirectionApiServer(String url) {
        GsonRequest<DirectionObject> serverRequest = new GsonRequest<DirectionObject>(
                Request.Method.GET,
                url,
                DirectionObject.class,
                createRequestSuccessListener(),
                createRequestErrorListener());
        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(serverRequest);
    }

    private com.android.volley.Response.Listener<DirectionObject> createRequestSuccessListener() {
        return new com.android.volley.Response.Listener<DirectionObject>() {
            @Override
            public void onResponse(DirectionObject response) {
               /* try {
                    Log.d("JSON Response", response.toString());*/
                if (response.getStatus().equalsIgnoreCase("OK")) {
                    List<LatLng> mDirections = getDirectionPolylines(response.getRoutes());
                    drawRouteOnMap(googleMap, mDirections);
                }
                else {
                    Toast.makeText(TrackTruckActivity.this, "server error", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions) {
        graylineOptions = new PolylineOptions();
        graylineOptions.color(Color.GRAY);
        graylineOptions.width(5);
        graylineOptions.startCap(new SquareCap());
        graylineOptions.endCap(new SquareCap());
        graylineOptions.jointType(ROUND);
        graylineOptions.addAll(positions);
        greyPolyLine = mMap.addPolyline(graylineOptions);

        lineOptions = new PolylineOptions();
        lineOptions.width(5);
        lineOptions.color(Color.BLACK).geodesic(true);
        lineOptions.startCap(new SquareCap());
        lineOptions.endCap(new SquareCap());
        lineOptions.jointType(ROUND);
        polyline = map.addPolyline(lineOptions);
        ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
        polylineAnimator.setDuration(2000);
        polylineAnimator.setInterpolator(new LinearInterpolator());
        polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                List<LatLng> points = greyPolyLine.getPoints();
                int percentValue = (int) valueAnimator.getAnimatedValue();
                int size = points.size();
                int newPoints = (int) (size * (percentValue / 100.0f));
                List<LatLng> p = points.subList(0, newPoints);
                polyline.setPoints(p);
            }

        });
        polylineAnimator.start();
    }

    private List<LatLng> getDirectionPolylines(List<RouteObject> routes) {
        List<LatLng> directionList = new ArrayList<LatLng>();
        for (RouteObject route : routes) {
            List<LegsObject> legs = route.getLegs();
            for (LegsObject leg : legs) {
                String routeDistance = leg.getDistance().getText();
                String routeDuration = leg.getDuration().getText();
                setRouteDistanceAndDuration(routeDistance, routeDuration);
                List<StepsObject> steps = leg.getSteps();
                for (StepsObject step : steps) {
                    PolylineObject polyline = step.getPolyline();
                    String points = polyline.getPoints();
                    List<LatLng> singlePolyline = decodePoly(points);
                    for (LatLng direction : singlePolyline) {
                        directionList.add(direction);
                    }
                }
            }
        }
        return directionList;
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private void setRouteDistanceAndDuration(String distance, String duration) {
        TextView durations = (TextView)findViewById(R.id.duration);
        TextView dist = (TextView)findViewById(R.id.dist);
        dist.setText(distance);
        durations.setText(duration);
        /*txtduration.setText(duration);*/
    }

    private com.android.volley.Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

   /* @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("MapLayoutClick", "MapLayoutClick Down= " + MapLayoutClick + "==" + FooterMapLayoutClick);
//                        if (MapLayoutClick) {
//                            layout_booking_detail.setAlpha(0);
//                            layout_main_drv_car.setAlpha(0);
//                        }
                        if (FooterMapLayoutClick) {

                            FooterMapLayoutClick = false;
                            DropDownAnim anim = new DropDownAnim(layout_main_drv_car, (int) (devise_height * 0.30), animationStart);
                            anim.setDuration(500);
                            layout_main_drv_car.startAnimation(anim);
                            animationStart = true;

                        }
                    }
                }, 20);

                break;
            case MotionEvent.ACTION_UP:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("MapLayoutClick", "MapLayoutClick Up = " + MapLayoutClick);
                        if (MapLayoutClick) {
                            slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                                    R.anim.slide_up_map);
                            layout_main_drv_car.startAnimation(slideUpAnimation);
                            layout_main_linear.startAnimation(slideUpAnimation);
                            MapLayoutClick = false;
                        }
                        //if(MapLayoutClick) {
                        // layout_main_drv_car.setAlpha(1);
                        // layout_booking_detail.setAlpha(1);
                        //}
                    }
                }, 50);
                break;
        }
        return super.dispatchTouchEvent(event);
    }*/

    private boolean checkReady() {
        if (googleMap == null) {
            // Toast.makeText(this, "Google Map not ready", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    /*Add marker function*/
    public void MarkerAdd() {

        if (checkReady()) {

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            if (allTripFeed.getPickupLarLng() != null) {
                markerOption = new MarkerOptions()
                        .position(allTripFeed.getPickupLarLng())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_path_point));
                googleMap.addMarker(markerOption);
                builder.include(markerOption.getPosition());

            }

            if (allTripFeed.getDropLarLng() != null) {
                markerOption = new MarkerOptions()
                        .position(allTripFeed.getDropLarLng())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_path_point));
                googleMap.addMarker(markerOption);
                builder.include(markerOption.getPosition());

                DriverMarkerOption = new MarkerOptions()
                        .position(allTripFeed.getDriverLarLng())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker));
                DriverMarker = googleMap.addMarker(DriverMarkerOption);
                builder.include(DriverMarker.getPosition());

            }

            // .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon))

            if (allTripFeed.getDriverLarLng() != null || allTripFeed.getPickupLarLng() != null) {
                LatLngBounds bounds = builder.build();

                Log.d("areBoundsTooSmall", "areBoundsTooSmall = " + areBoundsTooSmall(bounds, 300));
                if (areBoundsTooSmall(bounds, 300)) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10));
                } else {

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                    googleMap.animateCamera(cu, new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
                            googleMap.animateCamera(zout);
                        }

                        @Override
                        public void onCancel() {
//                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
//                            googleMap.animateCamera(zout);
                        }
                    });

                }
            }

            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                private float currentZoom = -1;

                @Override
                public void onCameraChange(CameraPosition pos) {
                    if (pos.zoom != currentZoom) {
                        currentZoom = pos.zoom;
                        googleZoom = currentZoom;
                        Log.d("currentZoom", "currentZoom = " + currentZoom);
                        // do you action here
                    }
                }
            });

            Log.e("Okayswiss", "googleZoom = " + googleZoom);

            // googleZoom = googleMap.getCameraPosition().zoom;
        }
    }

    public void WayMarker(LatLng WayDriverLarLng, String booking_status) {
        if (DriverMarker != null)
            DriverMarker.remove();
        if (checkReady()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            DriverMarker = googleMap.addMarker(new MarkerOptions()
                    .position(WayDriverLarLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
            builder.include(DriverMarker.getPosition());

            LatLngBounds bounds = builder.build();

            Log.d("areBoundsTooSmall", "areBoundsTooSmall = " + googleZoom + "==" + areBoundsTooSmall(bounds, 300));
            //if (areBoundsTooSmall(bounds, 300)) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), googleZoom));
            /*} else {

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                googleMap.animateCamera(cu, new GoogleMap.CancelableCallback() {

                    @Override
                    public void onFinish() {
                        CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
                        googleMap.animateCamera(zout);
                    }

                    @Override
                    public void onCancel() {
                        CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -1.0);
                        googleMap.animateCamera(zout);
                    }
                });

            }*/
        }


        Log.d("DriverLarLng", "DriverLarLng = " + allTripFeed.getDriverLarLng().latitude + "==" + allTripFeed.getDriverLarLng().longitude + "==" + WayDriverLarLng.latitude + "==" + WayDriverLarLng.longitude);
        Log.d("booking_status", "booking_status = " + booking_status);
        if (booking_status != null && !booking_status.equals("") && booking_status.equals("begin trip")) {
            Log.d("LatLngCom", "LatLngCom = " + LatLngCom);
//            if(LatLngCom == 0)
//                new DistanceTwoLatLng(allTripFeed.getPickupLarLng(),WayDriverLarLng).execute();

            System.out.println(getDistance(new LatLng(OldLatLog.latitude, OldLatLog.longitude), new LatLng(WayDriverLarLng.latitude, WayDriverLarLng.longitude)) + " Metre Nautical Miles Test\n");

            CoverdValue = CoverdValue + getDistance(new LatLng(OldLatLog.latitude, OldLatLog.longitude), new LatLng(WayDriverLarLng.latitude, WayDriverLarLng.longitude));

            float TotalKmMet = (float) (Double.parseDouble(allTripFeed.getKm()) * 1000);
            if (TotalKmMet > CoverdValue) {
                double FinalRemValue = TotalKmMet - CoverdValue;

                String RemValueStr = "";
                if (FinalRemValue > 1000) {
                    FinalRemValue = FinalRemValue / 1000;
                    //System.out.println();
                    RemValueStr = FinalRemValue + "";
                    RemValueStr = String.format("%.2f", Float.parseFloat(RemValueStr)) + getResources().getString(R.string.km);
                } else {
                    RemValueStr = FinalRemValue + "";
                    RemValueStr = String.format("%.2f", Float.parseFloat(RemValueStr)) + " M";
                }
               // txt_remaining_val.setText(RemValueStr);
                Log.d("CoverdValue ", "CoverdValue =" + CoverdValue + "==" + TotalKmMet + "==" + RemValueStr);
            }

            String CoverValueStr = "";
            if (CoverdValue > 1000) {
                double CoverValueKm = CoverdValue / 1000;
                //System.out.println();
                CoverValueStr = CoverValueKm + "";
                CoverValueStr = String.format("%.2f", Float.parseFloat(CoverValueStr)) + getResources().getString(R.string.km);
            } else {
                CoverValueStr = CoverdValue + "";
                CoverValueStr = String.format("%.2f", Float.parseFloat(CoverValueStr)) + " M";
            }
            Log.d("CoverdValue ", "CoverdValue =" + CoverdValue + "==" + TotalKmMet + "==" + CoverValueStr);
           // txt_covered_val.setText(CoverValueStr);

            OldLatLog = WayDriverLarLng;
        }

    }

    public void MarkerMoveTimer(int totalTimer) {
        new CountDownTimer(totalTimer, 2000) { //40000 milli seconds is total time, 1000 milli seconds is time interval

            public void onTick(long millisUntilFinished) {

                Log.d("arrayPoints", "arrayPoints = " + arrayPoints.size() + "==" + markerPositon);
                LatLng latLng = arrayPoints.get(markerPositon);
                WayMarker(latLng, "begin trip");

                Log.d("LatLngCom", "LatLngCom = " + LatLngCom);
//                if(LatLngCom == 0)
//                    new DistanceTwoLatLng(allTripFeed.getPickupLarLng(),latLng).execute();

                //System.out.println(distance(OldLatLog.latitude,OldLatLog.longitude,latLng.latitude,latLng.longitude, "K") + " Kilometers\n");
                System.out.println(getDistance(new LatLng(OldLatLog.latitude, OldLatLog.longitude), new LatLng(latLng.latitude, latLng.longitude)) + " Metre Nautical Miles Test\n");

                CoverdValue = CoverdValue + getDistance(new LatLng(OldLatLog.latitude, OldLatLog.longitude), new LatLng(latLng.latitude, latLng.longitude));

                float TotalKmMet = (float) (Double.parseDouble(allTripFeed.getKm()) * 1000);
                double FinalRemValue = TotalKmMet - CoverdValue;

                String RemValueStr = "";
                if (FinalRemValue > 1000) {
                    FinalRemValue = FinalRemValue / 1000;
                    //System.out.println();
                    RemValueStr = FinalRemValue + "";
                    RemValueStr = String.format("%.2f", Float.parseFloat(RemValueStr)) + getResources().getString(R.string.km);
                }
//                else{
//                    RemValueStr = FinalRemValue+"";
//                    RemValueStr = String.format("%.2f", Float.parseFloat(RemValueStr))+" M";
//                }

                String CoverValueStr = "";
                if (CoverdValue > 1000) {
                    double CoverValueKm = CoverdValue / 1000;
                    //System.out.println();
                    CoverValueStr = CoverValueKm + "";
                    CoverValueStr = String.format("%.2f", Float.parseFloat(CoverValueStr)) + getResources().getString(R.string.km);
                }
//                else{
//                    CoverValueStr = CoverdValue+"";
//                    CoverValueStr = String.format("%.2f", Float.parseFloat(CoverValueStr))+" "+getResources().getString(R.string.km);
//                }
                Log.d("CoverdValue ", "CoverdValue =" + CoverdValue + "==" + TotalKmMet + "==" + RemValueStr + "==" + CoverValueStr);
               // txt_covered_val.setText(CoverValueStr);
               // txt_remaining_val.setText(RemValueStr);
                OldLatLog = latLng;

                markerPositon = markerPositon - 1;
            }

            public void onFinish() {
                LatLng latLng = arrayPoints.get(0);
                WayMarker(latLng, "begin trip");
            }
        }.start();

    }

    private void StartRideAtDriverSide() {
        PickupLongtude = Double.parseDouble(pickup_lng);
        PickupLatitude = Double.parseDouble(pickup_lat);
        DropLatitude = Double.parseDouble(drop_lat);
        DropLongtude = Double.parseDouble(drop_lng);
        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<BaseResponse> call = api.startRide(ride_id);
        call.enqueue(new Callback<BaseResponse>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
                BaseResponse user = response.body();

                if (user.status.equalsIgnoreCase("true")) {
                   // startDriverSide.cancel();
                    driver_Start = 0;
                    if(startDriverSide != null){
                        startDriverSide.cancel();
                        startDriverSide.purge();
                        driver_Start = 0;
                    }
                    reCenter();
                    LatLng Pickup = new LatLng(PickupLatitude, PickupLongtude);
                    mMap.clear();
                    otpLayout.setVisibility(View.GONE);
                    mMap.addMarker(new MarkerOptions().position(Pickup).title("Pickup from").icon(BitmapDescriptorFactory.fromResource(R.drawable.black_location_pin)));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(DropLatitude, DropLongtude)).title("Drop At.").icon(BitmapDescriptorFactory.fromResource(R.drawable.black_location_pin)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(Pickup));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                    if(rippleBackground.isRippleAnimationRunning()){
                        rippleBackground.stopRippleAnimation();
                        centerImageRipple.setVisibility(View.GONE);
                    }
                    footer.setVisibility(View.VISIBLE);
                    header_cancel_Trip.setVisibility(View.GONE);
                    reCenter_fab.setVisibility(View.VISIBLE);
                    counttime.setVisibility(View.GONE);
                    sos_btn.setVisibility(View.VISIBLE);
                    txt_number_plate.setText(bikeNum);
                    txt_driver_name.setText("Driver "+DriverName);
                    txt_mob_num.setText(DriverMobileNumber);
                    txt_car_name.setText(CabModelName);
                    Picasso.get()
                            .load(Uri.parse(DriverImg))
                            .placeholder(R.drawable.mail_defoult)
                            .transform(new CircleTransform())
                            .into(img_drv_img);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(Pickup)
                            .zoom(15)
                            .build();
                    String directionApiPath = Helper.getUrl(String.valueOf(PickupLatitude), String.valueOf(PickupLongtude),
                            String.valueOf(DropLatitude), String.valueOf(DropLongtude));
                    //Log.d(TAG, "Path " + directionApiPath);
                    getDirectionFromDirectionApiServer(directionApiPath);

                    arrived_dialog.show();
                    heading_txt.setText("Your ride started.");
                    adition_txt.setText("Happy journey "+DriverName);
                    ex_img.setImageResource(R.drawable.rental_image);
                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            arrived_dialog.cancel();
                            layout_contact.setVisibility(View.GONE);
                            header_cancel_Trip.setVisibility(View.GONE);
                        }
                    });


                    if (ActivityCompat.checkSelfPermission(TrackTruckActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackTruckActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(PickupLatitude, PickupLongtude), 15.0f));
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mMap.setMyLocationEnabled(true);
                    endTripDriverSide = new Timer();
                    TimerTask timerTaskAsyn = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    endTripAtDriverSide();
                                }
                            });
                        }
                    };
                    endTripDriverSide.schedule(timerTaskAsyn, 0, trackInterval);

                } else {
                    // Toast.makeText(TrackTruckActivity.this, "Start_trip_false.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(TrackTruckActivity.this, "Check your internet.", Toast.LENGTH_SHORT).show();
                //Util.noInternetDialog(ctx, false);
            }
        });

    }

    private void endTripAtDriverSide() {
        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<EndTrip> call = api.endTrip(ride_id);
        call.enqueue(new Callback<EndTrip>() {
            @Override
            public void onResponse(Call<EndTrip> call, retrofit2.Response<EndTrip> response) {
                BaseResponse user = response.body();

                if (user.status.equalsIgnoreCase("true")) {
                    endTripDriverSide.cancel();
                    driver_Start_Track = 0;
                    trackInterval = 0;
                  //  acceptTimer.cancel();
          //          startDriverSide.purge();
                 //   driverStartTrack.cancel();

                    EndTrip.EndTripDetails data = ((EndTrip) user).getRideData();

                    SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString(Constants.amount,data.getFare());
                    editor.putString(Constants.payment_type,data.getPaymentMethod());
                    editor.putString(Constants.Payment_method,data.getPayment_method());
                    editor.putBoolean(Constants.KEY_ONTRIP, false);
                    editor.apply();
                    Toast.makeText(TrackTruckActivity.this, "payment_type "+data.getPaymentMethod(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TrackTruckActivity.this, Payments.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(TrackTruckActivity.this, "Your ride has been finished", Toast.LENGTH_SHORT).show();

                } else {
                     // Toast.makeText(TrackTruckActivity.this, "trip_false.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EndTrip> call, Throwable t) {
                Toast.makeText(TrackTruckActivity.this, "Check your internet.", Toast.LENGTH_SHORT).show();
                //Util.noInternetDialog(ctx, false);

            }
        });
    }

    public void drawDashedPolyLine(GoogleMap mMap, ArrayList<LatLng> listOfPoints, int color) {
        /* Boolean to control drawing alternate lines */
        boolean added = false;
        Log.e("Okayswiss", "listOfPoints = " + listOfPoints.size());
        for (int i = 0; i < listOfPoints.size() - 1; i++) {

            /* Get distance between current and next point */
            double distance = getConvertedDistance(listOfPoints.get(i), listOfPoints.get(i + 1));

            /* If distance is less than 0.020 kms */
            if (distance < 0.020) {
                if (!added) {
                    mMap.addPolyline(new PolylineOptions()
                            .add(listOfPoints.get(i))
                            .add(listOfPoints.get(i + 1))
                            .color(color));
                    added = true;
                } else {/* Skip this piece */
                    added = false;
                }
            } else {
                /* Get how many divisions to make of this line */
                int countOfDivisions = (int) ((distance / 0.020));

                /* Get difference to add per lat/lng */
                double latdiff = (listOfPoints.get(i + 1).latitude - listOfPoints
                        .get(i).latitude) / countOfDivisions;
                double lngdiff = (listOfPoints.get(i + 1).longitude - listOfPoints
                        .get(i).longitude) / countOfDivisions;

                /* Last known indicates start point of polyline. Initialized to ith point */
                LatLng lastKnowLatLng = new LatLng(listOfPoints.get(i).latitude, listOfPoints.get(i).longitude);
                for (int j = 0; j < countOfDivisions; j++) {

                    /* Next point is point + diff */
                    LatLng nextLatLng = new LatLng(lastKnowLatLng.latitude + latdiff, lastKnowLatLng.longitude + lngdiff);
                    if (!added) {
                        mMap.addPolyline(new PolylineOptions()
                                .add(lastKnowLatLng)
                                .add(nextLatLng)
                                .color(color));
                        added = true;
                    } else {
                        added = false;
                    }
                    lastKnowLatLng = nextLatLng;
                }
            }
        }
    }

    private double getConvertedDistance(LatLng latlng1, LatLng latlng2) {
        double distance = DistanceUtil.distance(latlng1.latitude,
                latlng1.longitude,
                latlng2.latitude,
                latlng2.longitude);
        BigDecimal bd = new BigDecimal(distance);
        BigDecimal res = bd.setScale(3, RoundingMode.DOWN);
        return res.doubleValue();
    }

    private boolean areBoundsTooSmall(LatLngBounds bounds, int minDistanceInMeter) {
        float[] result = new float[1];
        Location.distanceBetween(bounds.southwest.latitude, bounds.southwest.longitude, bounds.northeast.latitude, bounds.northeast.longitude, result);
        return result[0] < minDistanceInMeter;
    }

    private Emitter.Listener onSocketConnectionListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // handle the response args
//                    Toast.makeText(TrackTruckActivity.this,"Driver on the way..",Toast.LENGTH_LONG).show();
                    JSONObject data = (JSONObject) args[0];
                    Log.d("data", "connected data = " + data);
                    try {
                        if (data.getString("success").equals("true")) {
                            JSONArray dataArray = new JSONArray(data.getString("data"));
                            Log.d("dataArray", "dataArray = " + dataArray);
                            for (int di = 0; di < dataArray.length(); di++) {
                                JSONObject dataObj = dataArray.getJSONObject(di);
                                String Lotlon = dataObj.getString("loc");
                                JSONArray LotLanArray = new JSONArray(dataObj.getString("loc"));
                                String[] SplLotlon = Lotlon.split("\\,");

                                String DrvLat = SplLotlon[0].replace("[", "");
                                String DrvLng = SplLotlon[1].replace("]", "");

                                allTripFeed.setDriverLarLng(new LatLng(Double.parseDouble(DrvLat), Double.parseDouble(DrvLng)));
                                WayMarker(allTripFeed.getDriverLarLng(), dataObj.getString("booking_status").toString());
                                Log.d("Lotlon", "connected Lotlon = " + DrvLng + "==" + DrvLat);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    };

    /**
     * Listener for socket connection error.. listener registered at the time of socket connection
     */
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mSocket != null)
                        if (mSocket.connected() == false) {
                            Log.d("connected", "connected two= " + mSocket.connected());
                            //socketConnection();
                        } else {
                            Log.d("connected", "connected three= " + mSocket.connected());
                        }
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        //txt_remaining = null;
        //txt_remaining_val = null;
       // txt_covered = null;
        //txt_covered_val = null;
       // txt_total_km = null;
       // txt_total_km_val = null;
        layout_main_drv_car = null;
      //  layout_location_heading = null;
        //layout_footer = null;
       // layout_dirvier_car = null;
        txt_driver_name = null;
        //txt_mob_num = null;
        txt_car_name = null;
        txt_number_plate = null;
        txt_pickup_address = null;
        txt_drop_address = null;
        txt_contact = null;
        txt_share_eta = null;
       // txt_cancel = null;

        //  mSocket.disconnect();

        unregisterReceiver(receiver);
    }

    public void call(View v) {
        Toast.makeText(TrackTruckActivity.this, "Your call will be initiated in a few moments.", Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:"+DriverMobileNumber));
                startActivity(i);
            }
        }, 100);
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    private double getDistance(LatLng my_latlong, LatLng frnd_latlong) {
        Location l1 = new Location("One");
        l1.setLatitude(my_latlong.latitude);
        l1.setLongitude(my_latlong.longitude);

        Location l2 = new Location("Two");
        l2.setLatitude(frnd_latlong.latitude);
        l2.setLongitude(frnd_latlong.longitude);

        float distance = l1.distanceTo(l2);
        float dist = distance;

        if (distance > 1000.0f) {
            distance = distance / 1000.0f;
            dist = distance;
        }
        return dist;
    }


    public class DistanceTwoLatLng extends AsyncTask<String, Integer, String> {

        private String content = null;

        String LatLonUrl = "";

        public DistanceTwoLatLng(LatLng DriverLatLng, LatLng WayDriverLarLng) {
            LatLngCom = 1;
            LatLonUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + DriverLatLng.latitude + "," + DriverLatLng.longitude + "&destinations=" + WayDriverLarLng.latitude + "," + WayDriverLarLng.longitude + "&mode=driving";
        }

        protected void onPreExecute() {
            //Toast.makeText(TrackTruckActivity.this,"Start",Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpParams HttpParams = client.getParams();
                HttpConnectionParams.setConnectionTimeout(HttpParams, 60 * 60 * 1000);
                HttpConnectionParams.setSoTimeout(HttpParams, 60 * 60 * 1000);
                Log.d("DrowLocUrl", "DrowLocUrl = " + LatLonUrl);
                HttpGet getMethod = new HttpGet(LatLonUrl);
                //getMethod.setEntity(entity);
                client.execute(getMethod, new ResponseHandler<String>() {
                    @Override
                    public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {

                        HttpEntity httpEntity = httpResponse.getEntity();
                        content = EntityUtils.toString(httpEntity);
                        Log.d("Result >>>", "DrowLocUrl Result One" + content);
                        return null;
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Indiaries", "DrowLocUrl Result error" + e);
                return e.getMessage();
            }
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(TrackTruckActivity.this,"End = "+result,Toast.LENGTH_LONG).show();
            LatLngCom = 0;
            boolean isStatus = Common.ShowHttpErrorMessage(TrackTruckActivity.this, result);
            if (isStatus) {
                try {

                    JSONObject resObj = new JSONObject(result);
                    if (resObj.getString("status").toLowerCase().equals("ok")) {

                        JSONArray rowArray = new JSONArray(resObj.getString("rows"));
                        for (int li = 0; li < rowArray.length(); li++) {
                            JSONObject eleObj = rowArray.getJSONObject(li);

                            JSONArray eleArray = new JSONArray(eleObj.getString("elements"));
                            for (int ei = 0; ei < eleArray.length(); ei++) {

                                JSONObject elementObj = eleArray.getJSONObject(li);
                                Log.d("Status", "Status = " + elementObj.getString("status"));
                                if (elementObj.getString("status").equals("OK")) {

                                    JSONObject distanceObj = new JSONObject(elementObj.getString("distance"));

                                    int DistanceVal = Integer.parseInt(distanceObj.getString("value")) / 1000;
                                    Log.d("DistanceVal", "DistanceVal" + DistanceVal);
                                    /*if (txt_covered_val != null)
                                        txt_covered_val.setText(DistanceVal + " " + getResources().getString(R.string.km));*/


                                  /*  double remDis = Double.parseDouble(allTripFeed.getKm()) - DistanceVal;
                                    if (txt_remaining_val != null)
                                        txt_remaining_val.setText(String.format("%.2f " + getResources().getString(R.string.km), remDis))*/;
                                }
                            }
                        }


                    } else {
                        Toast.makeText(TrackTruckActivity.this, getResources().getString(R.string.location_long), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.really_exit))
                .setMessage(getResources().getString(R.string.are_you_sure))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }
                }).create().show();
    }

    int dp10 = 0;
    JSONObject bookingData = null;
    private Marker destMarker = null;

    public void reCenter() {
        GPSTracker gpsTracker = new GPSTracker(this);
        stringLatitude = String.valueOf(gpsTracker.getLatitude());
        stringLongitude = String.valueOf(gpsTracker.getLongitude());
       // Toast.makeText(this, "locaion sis  "+stringLatitude+" "+stringLongitude, Toast.LENGTH_SHORT).show();
        double a=Double.parseDouble(stringLatitude);
        double b=Double.parseDouble(stringLongitude);


        if (mMap == null) {
            return;
        }
        Polyline polyline = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(x, y));
//        builder.include(marker.getPosition());
        if (polyline!= null) {
            List<LatLng> lats = polyline.getPoints();
            for (LatLng lat : lats) {
                builder.include(lat);
            }
        }
        CameraUpdate cau = CameraUpdateFactory.newLatLngZoom(new LatLng(a,b), 15);
        mMap.animateCamera(cau);
    }
       /* if (bookingData != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(a,b));
            builder.include(destMarker.getPosition());
            if (polyline != null) {
                List<LatLng> lats = polyline.getPoints();
                for (LatLng lat : lats) {
                    builder.include(lat);
                }
            }
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 20);
            mMap.animateCamera(cu);
        } else {
           *//* CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(a, b), 16);*//*
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 15.0f));

        }*/
        // location();
/*

        if (gpsTracker.getIsGPSTrackingEnabled()) {
            stringLatitude = String.valueOf(gpsTracker.getLatitude());
            stringLongitude = String.valueOf(gpsTracker.getLongitude());
            Toast.makeText(this, "locationssss "+stringLatitude, Toast.LENGTH_SHORT).show();
            latLng = new LatLng(gpsTracker.getLatitude(),gpsTracker.getLatitude());
            double a=Double.parseDouble(stringLatitude);
            double b=Double.parseDouble(stringLongitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(a,b), 16));
            mMap.getCameraPosition().target;
        }
        Polyline polyline = null;
*/

    }

