package co.webnexs.tranznexs.activity;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.victor.loading.rotate.RotateLoading;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import co.webnexs.tranznexs.Api;
import co.webnexs.tranznexs.ArrivingDriver;
import co.webnexs.tranznexs.Constants;
import co.webnexs.tranznexs.DriverConstans;
import co.webnexs.tranznexs.beens.RatePer;
import co.webnexs.tranznexs.RetrofitClient;
import co.webnexs.tranznexs.map.DirectionObject;
import co.webnexs.tranznexs.map.GsonRequest;
import co.webnexs.tranznexs.map.Helper;
import co.webnexs.tranznexs.map.LegsObject;
import co.webnexs.tranznexs.map.PolylineObject;
import co.webnexs.tranznexs.map.RouteObject;
import co.webnexs.tranznexs.map.StepsObject;
import co.webnexs.tranznexs.map.VolleySingleton;

import co.webnexs.tranznexs.R;
import retrofit2.Call;
import retrofit2.Callback;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class BookingDetailActivity extends FragmentActivity implements OnMapReadyCallback {
    NotificationManager notificationManager;
    private GoogleMap mMap;
    private MapStyleOptions mapStyle;
    MarkerOptions options;
    TextView txt_car_name;

    ImageView img_car_image;
    RelativeLayout layout_back_arrow,layout_cancel,payment_dialog_layout;
    TextView txt_confirm_request,txtdistace,txtduration,txt_cancel_ride;

    Typeface OpenSans_Regular,Roboto_Regular,Roboto_Medium,Roboto_Bold,OpenSans_Semibold;
    String pickup_point;
    String drop_point;
    String pickup_lat;
    String pickup_lng;
    String ride_type="ride";
    String truckType,truckTypeArb;
    String CabId,user_id,cabtypeeNumer,bikename,distances,mobile_no,name;
    String AreaId;
    Float distance;
    Float totlePrice;
    String booking_date;
    double PickupLatitude;
    double PickupLongtude;
    double DropLatitude;
    double DropLongtude;
    String DayNight;
    String comment;
    String pickup_date_time;
    String transfertype;
    String PaymentType;
    String person,RideDuration;
    String transaction_id;
    String BookingType;
    String ride_otp,payment_method;
    String amount = "0";
    Button done;
    SharedPreferences userPref;
    Dialog ProgressDialog,SetUpPayment;
    Api myApi;
    RotateLoading cusRotateLoading;
    TextView txtfromView,txtToView,nomalRide_btn,ride_msg,hourly_btn,txtTotal,setPayment,oneway,twoway,personal;
    String paymentType = "0";
    RelativeLayout confirmTripBooking,oneWayTwoway,layout_cash,deb_crd_card_layout;
    LinearLayout toAddressLayout,km_dur_layout;
    ImageView edt_loc_icon;
    PolylineOptions lineOptions,graylineOptions;
    Polyline polyline,greyPolyLine;
    TableRow tableRow;
    CheckBox chk;
    View oneway_line,twoway_line;
    Typeface  OpenSans_Bold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        toAddressLayout = (LinearLayout) findViewById(R.id.toAddressLayout);
        km_dur_layout = (LinearLayout) findViewById(R.id.km_dur_layout);
        oneWayTwoway = (RelativeLayout) findViewById(R.id.oneWayTwoway);
        layout_cancel = (RelativeLayout) findViewById(R.id.layout_cancel);
        layout_back_arrow = (RelativeLayout) findViewById(R.id.layout_back_arrow);
        edt_loc_icon = (ImageView) findViewById(R.id.edt_loc_icon);
        personal = (TextView)findViewById(R.id.personal);
        setPayment = (TextView)findViewById(R.id.setPayment);
        oneway_line = (View)findViewById(R.id.oneway_line);
        twoway_line = (View)findViewById(R.id.twoway_line);
        //chk=(CheckBox)findViewById(R.id.handicap);

        OpenSans_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold_0.ttf");
        myApi = RetrofitClient.getRetrofitInstance().create(Api.class);
        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        user_id = prefs.getString(Constants.user_id, "");
        bikename = prefs.getString(Constants.bikeName, "");//"No name defined" is the default value
        distances = prefs.getString(Constants.distance, "");
        mobile_no = prefs.getString(Constants.mobileno, "");
        name = prefs.getString(Constants.firstname, "");
        amount = prefs.getString(Constants.amount, "");


        Bundle bundle = getIntent().getExtras();
        final String from = bundle.getString("from");
        final String to = bundle.getString("to");
        PickupLatitude = getIntent().getExtras().getDouble("PickupLatitude");
        PickupLongtude = getIntent().getExtras().getDouble("PickupLongtude");
        DropLatitude = getIntent().getExtras().getDouble("DropLatitude");
        DropLongtude = getIntent().getExtras().getDouble("DropLongtude");
        cabtypeeNumer = getIntent().getExtras().getString("cabtypeeNumer");

        /* Toast.makeText(this, "Ans : "+user_id+" "+bikeType+" "+PickupLatitude+" "+PickupLongtude+" "+DropLatitude+" "+DropLongtude, Toast.LENGTH_LONG).show();*/

        txtfromView = (TextView) findViewById(R.id.fromLocation);
        txtfromView.setText(from);

        txtToView = (TextView) findViewById(R.id.toLocation);
        txtToView.setText(to);

        /*txtDateView = (TextView) findViewById(R.id.bookingDate);
        txtDateView.setText(date);
*/
        txtTotal = (TextView) findViewById(R.id.totalAmount);
        txtTotal.setText("$"+amount);

        txtdistace = (TextView) findViewById(R.id.distaceTrip);
        txtdistace.setText(distances + "km");
        txtduration = (TextView) findViewById(R.id.durationTrip);

        hourly_btn = (TextView)findViewById(R.id.hourly_btn);
        nomalRide_btn = (TextView)findViewById(R.id.nomalRide_btn);
        /*biketypeName = (TextView)findViewById(R.id.biketypeName);
        biketypeName.setText(bikename);*/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ride_msg = (TextView)findViewById(R.id.ride_msg);



        SetUpPayment = new Dialog(BookingDetailActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        SetUpPayment.setContentView(R.layout.activity_payment_type);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            SetUpPayment.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        SetUpPayment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        deb_crd_card_layout = (RelativeLayout) SetUpPayment.findViewById(R.id.deb_crd_card_layout);
        payment_dialog_layout = (RelativeLayout) SetUpPayment.findViewById(R.id.payment_dialog_layout);
        layout_cash = (RelativeLayout) SetUpPayment.findViewById(R.id.layout_cash);
        deb_crd_card_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deb_crd_card_layout.setBackgroundResource(R.drawable.btn_accent_circle_white_outlined);
                layout_cash.setBackgroundResource(0);
                paymentType = "1";
                SetUpPayment.cancel();
            }
        });
        layout_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_cash.setBackgroundResource(R.drawable.btn_accent_circle_white_outlined);
                deb_crd_card_layout.setBackgroundResource(0);
                paymentType = "0";
                SetUpPayment.cancel();
            }
        });

        payment_dialog_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetUpPayment.cancel();
            }
        });
        hourly_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomalRide_btn.setVisibility(View.VISIBLE);
                hourly_btn.setVisibility(View.GONE);
                oneWayTwoway.setVisibility(View.GONE);
                km_dur_layout.setVisibility(View.GONE);
                // distanceTimeLayout.setVisibility(View.GONE);
                edt_loc_icon.setVisibility(View.GONE);
                ride_msg.setText("Go on normal ride?");
                toAddressLayout.setVisibility(View.GONE);
                txtTotal.setText("$0.0");
                ride_type = "hour";
                SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString(Constants.amount, "0.00");
                editor.apply();
                mMap.clear();
                LatLng PickUp = new LatLng(PickupLatitude,PickupLongtude);
                mMap.addMarker(new MarkerOptions().position(PickUp).title("Pickup Location.").icon(BitmapDescriptorFactory.fromResource(R.drawable.black_location_pin)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(PickUp));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                Dialog arrived_dialog = new Dialog(BookingDetailActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                arrived_dialog.setContentView(R.layout.arrived_dialog);
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    arrived_dialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
                }

                arrived_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {

                    }
                });

                done = (Button)arrived_dialog.findViewById(R.id.done);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrived_dialog.cancel();
                    }
                });

                TextView heading_txt = (TextView)arrived_dialog.findViewById(R.id.heading_txt);
                heading_txt.setText("Hourly Conditions.");
                TextView adition_txt = (TextView)arrived_dialog.findViewById(R.id.adition_txt);
                adition_txt.setText(R.string.hourly_title);
                ImageView ex_img = (ImageView)arrived_dialog.findViewById(R.id.ex_img);
                ex_img.setImageResource(R.drawable.rental_image);
                arrived_dialog.show();
                /*CameraPosition cameraPositions = new CameraPosition.Builder()
                        .target(PickUp)
                        .zoom(15)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositions));*/

                /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BookingDetailActivity.this);
                alertDialogBuilder.setTitle("Hourly Conditions.");
                alertDialogBuilder.setMessage(R.string.hourly_title);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();*/
            }
        });

        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, 1);
               /* Intent intent= new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);*/
            }
        });
        setPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetUpPayment.show();
            }
        });

        nomalRide_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomalRide_btn.setVisibility(View.GONE);
                hourly_btn.setVisibility(View.VISIBLE);
                km_dur_layout.setVisibility(View.VISIBLE);
                oneWayTwoway.setVisibility(View.VISIBLE);
                toAddressLayout.setVisibility(View.VISIBLE);
                edt_loc_icon.setVisibility(View.VISIBLE);
                txtTotal.setText("$"+amount);
                ride_msg.setText("Need a cab entire day..?");
                ride_type = "ride";
                // cediIcon.setVisibility(View.VISIBLE);
                // hourlyTxt.setVisibility(View.GONE);
                //txtTotal.setVisibility(View.VISIBLE);
                //  tableRow.setVisibility(View.VISIBLE);
                mMap.clear();
                LatLng Pickup = new LatLng(PickupLatitude,PickupLongtude);
                mMap.addMarker(new MarkerOptions().position(Pickup).title("Pickup Location.").icon(BitmapDescriptorFactory.fromResource(R.drawable.black_location_pin)));
                mMap.addMarker(new MarkerOptions().position(new LatLng(DropLatitude,DropLongtude)).title("Drop Location.").icon(BitmapDescriptorFactory.fromResource(R.drawable.black_location_pin)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(Pickup));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                /*CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(Pickup)
                        .zoom(10)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
                String directionApiPath = Helper.getUrl(String.valueOf(PickupLatitude), String.valueOf(PickupLongtude),
                        String.valueOf(DropLatitude), String.valueOf(DropLongtude));
                //Log.d(TAG, "Path " + directionApiPath);
                getDirectionFromDirectionApiServer(directionApiPath);
            }
        });

        oneway = (TextView)findViewById(R.id.oneway);
        oneway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneway_line.setVisibility(View.VISIBLE);
                twoway_line.setVisibility(View.GONE);
                twoway.setTextColor(Color.GRAY);
                oneway.setTextColor(Color.WHITE);
                // Toast.makeText(getApplicationContext(), "Selected button one  " + index, Toast.LENGTH_SHORT).show();
                txtTotal.setText("$"+amount);
                txtdistace.setText(distances+"km");
                txtduration.setText(RideDuration);
            }
        });

        twoway = (TextView)findViewById(R.id.twoway);
        twoway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneway_line.setVisibility(View.GONE);
                twoway_line.setVisibility(View.VISIBLE);
                oneway.setTextColor(Color.GRAY);
                twoway.setTextColor(Color.WHITE);
                String pikLat = String.valueOf(PickupLatitude);
                String pikLng = String.valueOf(PickupLongtude);
                String dropLat = String.valueOf(DropLatitude);
                String dropLng = String.valueOf(DropLongtude);
                String twoway = "twoway";

                Call<RatePer> call = myApi.twoWay(cabtypeeNumer,pikLat,pikLng,dropLat,dropLng);
                call.enqueue(new Callback<RatePer>() {
                    @Override
                    public void onResponse(Call<RatePer> call, retrofit2.Response<RatePer> response) {
                        RatePer users = response.body();
                        if (users.status.equalsIgnoreCase("true")) {
                            ProgressDialog.cancel();
                            cusRotateLoading.stop();
                            RatePer.RateDetails userData = users.getRate_Details();
                            txtTotal.setText("$"+userData.getAmount());
                            txtdistace.setText(userData.getDistance()+"km");
                            txtduration.setText("based on coming back"+new String(Character.toChars(0x1F60A)));
                        } else {
                            Toast.makeText(BookingDetailActivity.this, "Failed your data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<RatePer> call, Throwable t) {
                        ProgressDialog.cancel();
                        cusRotateLoading.stop();
                        Toast.makeText(BookingDetailActivity.this, "Check Your internet connection...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
       /* chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //boolean checked = ((CheckBox) v).isChecked();
                chk.setChecked(false);
                // Check which checkbox was clicked

                // Do your coding
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BookingDetailActivity.this);
                alertDialogBuilder.setTitle("Handicap Accessibility.");
                alertDialogBuilder.setMessage(R.string.handipcap_access);
                alertDialogBuilder.setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Toast.makeText(BookingDetailActivity.this,"Handicap wheel chair has attached.",Toast.LENGTH_LONG).show();
                                chk.setChecked(true);
                            }
                        });

                alertDialogBuilder.setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        chk.setChecked(false);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }

        });*/
        confirmTripBooking = (RelativeLayout)findViewById(R.id.layout_confirm_request) ;
        confirmTripBooking.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                ProgressDialog.show();
                cusRotateLoading.start();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String CurrentTime = formatter.format(date);

                pickup_lat = Double.toString(PickupLatitude);
                pickup_lng = Double.toString(PickupLongtude);
                String drop_lat = Double.toString(DropLatitude);
                String drop_lng = Double.toString(DropLongtude);

                Random random = new Random();
                String otp = String.format("%04d", random.nextInt(10000));
                ride_otp =String.valueOf(otp);
               // Toast.makeText(BookingDetailActivity.this, "resRandom "+otp, Toast.LENGTH_SHORT).show();

                Call<ArrivingDriver> call = myApi.postBooking(user_id,pickup_lat,pickup_lng,from,cabtypeeNumer,drop_lat,drop_lng,to,amount,distances,CurrentTime,ride_otp,paymentType,ride_type);
                call.enqueue(new Callback<ArrivingDriver>() {
                    @Override
                    public void onResponse(Call<ArrivingDriver> call, retrofit2.Response<ArrivingDriver> response) {
                        ArrivingDriver users = response.body();
                        if (users.status.equalsIgnoreCase("true")) {
                            ArrivingDriver.ArrivingDriverDetails userData = users.getDriverDetails();
                            ProgressDialog.cancel();
                            cusRotateLoading.stop();
                            issueNotification();
                            SharedPreferences.Editor editor = getSharedPreferences(DriverConstans.MY_Driver_PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putString(DriverConstans.USER_FROM_LOCATION, from);
                            editor.putString(DriverConstans.USER_TO_LOCATION,to);
                            editor.putString(DriverConstans.otp,ride_otp);
                            editor.putString(DriverConstans.ride_id,userData.getRide_id());
                            editor.putString(DriverConstans.tempDateTime, CurrentTime);
                            editor.apply();
                            startActivity(new Intent(BookingDetailActivity.this, TrackTruckActivity.class));

                        } else {
                            ProgressDialog.cancel();
                            cusRotateLoading.stop();
                            Toast.makeText(BookingDetailActivity.this, "false...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrivingDriver> call, Throwable t) {
                        ProgressDialog.cancel();
                        cusRotateLoading.stop();
                        Toast.makeText(BookingDetailActivity.this, "Check Your internet connection...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        txt_confirm_request = (TextView)findViewById(R.id.txt_confirm_request);
        txt_cancel_ride = (TextView)findViewById(R.id.txt_cancel_ride);
        txt_confirm_request.setTypeface(OpenSans_Bold);
        txt_cancel_ride.setTypeface(OpenSans_Bold);
        ProgressDialog = new Dialog(BookingDetailActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading)ProgressDialog.findViewById(R.id.rotateloading_register);

        userPref = PreferenceManager.getDefaultSharedPreferences(BookingDetailActivity.this);
        layout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (BookingDetailActivity.this, HomeActivity.class);
                startActivity(intent);
                mMap.clear();
            }
        });


        layout_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected", false);
                setResult(100, resultIntent);
                finish();

            }
        });
    }

    private void issueNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel("CHANNEL_1", "Example channel", NotificationManager.IMPORTANCE_DEFAULT);
        }
        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this, "CHANNEL_1");
        notification
                .setSmallIcon(R.mipmap.ic_launcher) // can use any other icon
                .setContentTitle("Booking Details!")
                .setContentText("Your booking is confirmed...")
                .setDefaults(Notification.DEFAULT_ALL) // must requires VIBRATE permission
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.base_car_color))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Dear customer your booking has recevied \n" + name + "\n" + mobile_no + "\n" + bikename))
                .setNumber(1); // this shows a number in the notification dots

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(1, notification.build());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeNotificationChannel(String id, String name, int importance)
    {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true); // set false to disable badges, Oreo exclusive

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1) && (resultCode == RESULT_OK)) {
            Cursor cursor = null;
            try {
                Uri uri = data.getData();
                cursor = getContentResolver().query(uri, new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER }, null, null, null);
                if (cursor != null && cursor.moveToNext()) {
                    String phone = cursor.getString(0);
                    TextView contact_number = (TextView)findViewById(R.id.contact_number);
                    contact_number.setText(phone);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selected", false);
        setResult(102, resultIntent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        txt_car_name = null;
        img_car_image = null;
        layout_back_arrow = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapStyle = MapStyleOptions.loadRawResourceStyle(this,R.raw.style_json_silver);
        mMap.setMapStyle(mapStyle);
        LatLng Pickup = new LatLng(PickupLatitude,PickupLongtude);
        mMap.addMarker(new MarkerOptions().position(Pickup).title("Pickup Location.").icon(BitmapDescriptorFactory.fromResource(R.drawable.black_location_pin)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(DropLatitude,DropLongtude)).title("Drop Location.").icon(BitmapDescriptorFactory.fromResource(R.drawable.black_location_pin)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Pickup));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(Pickup)
                .zoom(15)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        String directionApiPath = Helper.getUrl(String.valueOf(PickupLatitude), String.valueOf(PickupLongtude),
                String.valueOf(DropLatitude), String.valueOf(DropLongtude));
        //Log.d(TAG, "Path " + directionApiPath);
        getDirectionFromDirectionApiServer(directionApiPath);
    }

    private void getDirectionFromDirectionApiServer(String url){
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
                if(response.getStatus().equalsIgnoreCase("OK")){
                    List<LatLng> mDirections = getDirectionPolylines(response.getRoutes());
                    drawRouteOnMap(mMap, mDirections);


                }
                else{
                    Toast.makeText(BookingDetailActivity.this, "server error", Toast.LENGTH_SHORT).show();
                }
            };
        };
    }
    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions){
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
    private List<LatLng> getDirectionPolylines(List<RouteObject> routes){
        List<LatLng> directionList = new ArrayList<LatLng>();
        for(RouteObject route : routes){
            List<LegsObject> legs = route.getLegs();
            for(LegsObject leg : legs){
                String routeDistance = leg.getDistance().getText();
                String routeDuration = leg.getDuration().getText();
                setRouteDistanceAndDuration(routeDistance, routeDuration);
                List<StepsObject> steps = leg.getSteps();
                for(StepsObject step : steps){
                    PolylineObject polyline = step.getPolyline();
                    String points = polyline.getPoints();
                    List<LatLng> singlePolyline = decodePoly(points);
                    for (LatLng direction : singlePolyline){
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
    private void setRouteDistanceAndDuration(String distance, String duration){
        //txtdistace.setText(distance);
        txtduration.setText(duration);
        RideDuration = duration;
    }
    private com.android.volley.Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

}
