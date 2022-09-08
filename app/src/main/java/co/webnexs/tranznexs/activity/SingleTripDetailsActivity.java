package co.webnexs.tranznexs.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.webnexs.tranznexs.R;
import co.webnexs.tranznexs.Api;
import co.webnexs.tranznexs.Constants;
import co.webnexs.tranznexs.RetrofitClient;
import co.webnexs.tranznexs.map.DirectionObject;
import co.webnexs.tranznexs.map.GsonRequest;
import co.webnexs.tranznexs.map.Helper;
import co.webnexs.tranznexs.map.LegsObject;
import co.webnexs.tranznexs.map.PolylineObject;
import co.webnexs.tranznexs.map.RouteObject;
import co.webnexs.tranznexs.map.StepsObject;
import co.webnexs.tranznexs.map.VolleySingleton;
import co.webnexs.tranznexs.utils.SimpleRatingBar;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class SingleTripDetailsActivity extends FragmentActivity implements OnMapReadyCallback {

    LinearLayout layoutFb,layoutMail,layoutWhatsApp;
    FloatingActionButton fabSettings;
    private LocationManager locManager;
    private boolean fabExpanded = false;
    String ride_id,user_id;
    TextView date,booking_id,fare,paymentType,from_Location,to_Location,
            driver_name,cabName_numPlate,tripFare,sub_total,total,total_payable;
    CircleImageView driverImg,stamp;
    SimpleRatingBar userRating;
    private GoogleMap mMap;
    PolylineOptions lineOptions,graylineOptions;
    Polyline polyline,greyPolyLine;
    RelativeLayout layout_back_arrow,newRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        ride_id = getIntent().getExtras().getString("ride_id");
        SharedPreferences pref = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        user_id = pref.getString(Constants.user_id, "");
        singleRideDetails();

        date = (TextView)findViewById(R.id.date);
        booking_id = (TextView)findViewById(R.id.booking_id);
        fare = (TextView)findViewById(R.id.fare);
        paymentType = (TextView)findViewById(R.id.paymentType);
        from_Location = (TextView)findViewById(R.id.from_Location);
        to_Location = (TextView)findViewById(R.id.to_Location);
        driver_name = (TextView)findViewById(R.id.driver_name);
        cabName_numPlate = (TextView)findViewById(R.id.cabName_numPlate);
        tripFare = (TextView)findViewById(R.id.tripFare);
        sub_total = (TextView)findViewById(R.id.sub_total);
        total = (TextView)findViewById(R.id.total);
        total_payable = (TextView)findViewById(R.id.total_payable);
        driverImg = (CircleImageView)findViewById(R.id.driverImg);
        stamp = (CircleImageView)findViewById(R.id.stamp);
        userRating = (SimpleRatingBar)findViewById(R.id.userRating);
        layout_back_arrow = (RelativeLayout)findViewById(R.id.layout_back_arrow);
        newRide = (RelativeLayout)findViewById(R.id.newRide);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        layout_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intnt = new Intent(SingleTripDetailsActivity.this, AllTripActivity.class);
                startActivity(intnt);
            }
        });

        layout_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intnt = new Intent(SingleTripDetailsActivity.this, HomeActivity.class);
                startActivity(intnt);
            }
        });

        fabSettings = (FloatingActionButton) this.findViewById(R.id.fabSetting);
        layoutFb = (LinearLayout) this.findViewById(R.id.layoutFb);
        layoutFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layoutMail = (LinearLayout) this.findViewById(R.id.layoutMail);
        layoutMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layoutWhatsApp = (LinearLayout) this.findViewById(R.id.layoutWhatsApp);
        layoutWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });
    }

    private void singleRideDetails() {
        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<co.webnexs.tranznexs.beens.SingleTripDetails> userCall = api.getSingleTripDetail(user_id,ride_id);
        userCall.enqueue(new Callback<co.webnexs.tranznexs.beens.SingleTripDetails>() {
            @Override
            public void onResponse(Call<co.webnexs.tranznexs.beens.SingleTripDetails> call, retrofit2.Response<co.webnexs.tranznexs.beens.SingleTripDetails> response) {

                co.webnexs.tranznexs.beens.SingleTripDetails baseResponse = response.body();
                if (baseResponse.status.equalsIgnoreCase("true")) {
                    co.webnexs.tranznexs.beens.SingleTripDetails.SingleTrip data = baseResponse.getDetails();
                    date.setText(data.getAccept_date());
                    booking_id.setText("Booking Id TRNZ_"+ride_id);
                    fare.setText("$"+data.getFare());
                    paymentType.setText(data.getPayment());
                    from_Location.setText(data.getPickup_loc());
                    to_Location.setText(data.getDrop_loc());
                    cabName_numPlate.setText(data.getBrand()+" "+data.getNumber_plate());
                    tripFare.setText("$"+data.getFare());
                    sub_total.setText("$"+data.getFare());
                    total.setText("$"+data.getFare());
                    total_payable.setText("$"+data.getFare());
                    driver_name.setText(data.getFirstname()+" "+data.getLastname());

                   // int rating=Integer.parseInt(data.getRating());
                    userRating.setRating(3);
                    userRating.setEnabled(false);
                    Picasso.get()
                            .load(Uri.parse(data.getDriver_image()))
                            .placeholder(R.drawable.mail_defoult)
                            .into(driverImg);
                    Picasso.get()
                            .load(Uri.parse(data.getStatus_stamp()))
                            .placeholder(R.drawable.mail_defoult)
                            .into(stamp);

                    double PickupLatitude=Double.parseDouble(data.getPickup_lat());
                    double PickupLongtude=Double.parseDouble(data.getPickup_lng());
                    double DropLatitude=Double.parseDouble(data.getDrop_lat());
                    double DropLongtude=Double.parseDouble(data.getDrop_lng());

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
                    getDirectionFromDirectionApiServer(directionApiPath);

                } else {
                    //Toast.makeText(DriverHomeActivity.this, "update_false", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<co.webnexs.tranznexs.beens.SingleTripDetails> call, Throwable t) {
                Log.d("onFailure", t.toString());
                //Toast.makeText(DriverHomeActivity.this, "Check your internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openSubMenusFab() {
        layoutFb.setVisibility(View.VISIBLE);
        layoutMail.setVisibility(View.VISIBLE);
        layoutWhatsApp.setVisibility(View.VISIBLE);
        getWindow().setBackgroundDrawableResource(R.color.gray_light);
        //Change settings icon to 'X' icon
        fabSettings.setImageResource(R.drawable.fab_close_btn);
        fabExpanded = true;
    }

    private void closeSubMenusFab() {
        layoutFb.setVisibility(View.INVISIBLE);
        layoutMail.setVisibility(View.INVISIBLE);
        layoutWhatsApp.setVisibility(View.INVISIBLE);
        fabSettings.setImageResource(R.drawable.share_btn);
        fabExpanded = false;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
        this.mMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.mMap.setMyLocationEnabled(true);

        mMap = googleMap;
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
                    Toast.makeText(SingleTripDetailsActivity.this, "server error", Toast.LENGTH_SHORT).show();
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
