package co.webnexs.tranznexs.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import co.webnexs.tranznexs.R;

public class Emergency_contact extends AppCompatActivity {
    RelativeLayout layout_back_arrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

        layout_back_arrow = (RelativeLayout)findViewById(R.id.layout_back_arrow);

        layout_back_arrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //MapLayoutClick = false;
                return false;
            }
        });

        layout_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
