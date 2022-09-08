package co.webnexs.tranznexs.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;

import co.webnexs.tranznexs.Api;
import co.webnexs.tranznexs.Constants;
import co.webnexs.tranznexs.beens.OvertripDetailsList;
import co.webnexs.tranznexs.RetrofitClient;
import co.webnexs.tranznexs.beens.TripDetails;
import co.webnexs.tranznexs.adapter.TripDetailsAdapter;
import co.webnexs.tranznexs.utils.Common;
import co.webnexs.tranznexs.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllTripActivity extends AppCompatActivity {

    TextView txt_all_trip,txtNextRide;
    RecyclerView recycle_all_trip;
    RelativeLayout layout_no_recourd_found,newRide,layout_back_arrow;
    LinearLayout layout_recycleview;
    SharedPreferences userPref;
    Typeface OpenSans_Bold,OpenSans_Regular,Roboto_Bold;
    Dialog ProgressDialog;
    RotateLoading cusRotateLoading;
    Common common = new Common();
    private ArrayList<TripDetails> tripDetailsList;
    private RecyclerView recyclerView;
    private TripDetailsAdapter eAdapter;
    String user_ID,ride_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trip);

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        user_ID = prefs.getString(Constants.user_id, "");

        txt_all_trip = (TextView)findViewById(R.id.txt_all_trip);
        txtNextRide = (TextView)findViewById(R.id.txtNextRide);
        recycle_all_trip = (RecyclerView)findViewById(R.id.recycle_all_trip);
        layout_no_recourd_found = (RelativeLayout)findViewById(R.id.layout_no_recourd_found);
        newRide = (RelativeLayout)findViewById(R.id.newRide);

        layout_recycleview = (LinearLayout)findViewById(R.id.layout_recycleview);
        layout_back_arrow = (RelativeLayout)findViewById(R.id.layout_back_arrow);

        OpenSans_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold_0.ttf");
        OpenSans_Regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular_0.ttf");
        Roboto_Bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        txt_all_trip.setTypeface(OpenSans_Bold);
        txtNextRide.setTypeface(OpenSans_Bold);
        newRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (AllTripActivity.this, co.webnexs.tranznexs.activity.HomeActivity.class);
                startActivity(intent);
            }
        });

        ProgressDialog = new Dialog(AllTripActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading)ProgressDialog.findViewById(R.id.rotateloading_register);
        ProgressDialog.show();
        cusRotateLoading.start();


        Api api = RetrofitClient.getApiService();

        //String id = "23";

        Call<OvertripDetailsList> call = api.getTripDetails(user_ID);

        call.enqueue(new Callback<OvertripDetailsList>() {
            @Override
            public void onResponse(Call<OvertripDetailsList> call, Response<OvertripDetailsList> response) {
                OvertripDetailsList  users = response.body();
                if (users.status.equalsIgnoreCase("true")) {
                    ProgressDialog.cancel();
                    cusRotateLoading.stop();
                    tripDetailsList = response.body().getTripDetails();
                    recyclerView = (RecyclerView) findViewById(R.id.recycle_all_trip);
                    eAdapter = new TripDetailsAdapter(tripDetailsList);
                    RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getApplicationContext());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AllTripActivity.this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(eAdapter);
                    recyclerView.addOnItemTouchListener(new AllTripActivity.RecyclerTouchListener(AllTripActivity.this,
                            recyclerView, new AllTripActivity.ClickListener() {
                        @Override
                        public void onClick(View view, final int position) {
                            ride_id = tripDetailsList.get(position).getRide_id();
                           Intent intent = new Intent(AllTripActivity.this,SingleTripDetailsActivity.class);
                           intent.putExtra("ride_id",ride_id);
                            startActivity(intent);
                        }
                        @Override
                        public void onLongClick(View view, int position) {
                        }
                    }));
                }
                else{
                    ProgressDialog.cancel();
                    cusRotateLoading.stop();
                    layout_no_recourd_found.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<OvertripDetailsList> call, Throwable t) {
                ProgressDialog.cancel();
                cusRotateLoading.stop();
                layout_no_recourd_found.setVisibility(View.VISIBLE);
            }
        });


        layout_back_arrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
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

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.really_exit))
                .setMessage(getResources().getString(R.string.are_you_sure))
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }
                }).create().show();
    }


    //Recycler view clicking functions
    public static interface ClickListener{
        public void onClick(View view,int position);
        public void onLongClick(View view,int position);
    }


    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private AllTripActivity.ClickListener clicklistener;
        private GestureDetector gestureDetector;
        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final AllTripActivity.ClickListener clicklistener){
            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }
            return false;
        }
        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }
        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}
