package co.webnexs.tranznexs.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.victor.loading.rotate.RotateLoading;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.webnexs.tranznexs.Api;
import co.webnexs.tranznexs.BaseResponse;
import co.webnexs.tranznexs.Constants;
import co.webnexs.tranznexs.RetrofitClient;
import co.webnexs.tranznexs.utils.Common;
import co.webnexs.tranznexs.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    Pattern letter;

    Pattern digit = Pattern.compile("[0-9]");
    Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

    TextView txt_change_pass;
    EditText edit_current_pass;
    EditText edit_new_pass;
    EditText edit_con_pass;
    RelativeLayout layout_change_password_button, layout_back_arrow;
    RelativeLayout layout_menu;

    Typeface OpenSans_Regular, OpenSans_Bold, regularRoboto, Roboto_Bold;
    String OldPassword, user_id, email_id;
    //SlidingMenu slidingMenu;

    Dialog ProgressDialog;
    RotateLoading cusRotateLoading;

    //Error Alert
    RelativeLayout rlMainView;
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        rlMainView = (RelativeLayout) findViewById(R.id.rlMainView);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        OldPassword = prefs.getString(Constants.password, "");
        user_id = prefs.getString(Constants.user_id, "");
        email_id = prefs.getString(Constants.email_id, "");

       /* RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) getResources().getDimension(R.dimen.height_50), 0, 0);
        rlMainView.setLayoutParams(params);*/

        edit_current_pass = (EditText) findViewById(R.id.edit_current_pass);
        edit_new_pass = (EditText) findViewById(R.id.edit_new_pass);
        edit_con_pass = (EditText) findViewById(R.id.edit_con_pass);
        layout_change_password_button = (RelativeLayout) findViewById(R.id.layout_change_password_button);
        layout_back_arrow = (RelativeLayout) findViewById(R.id.layout_back_arrow);
        layout_menu = (RelativeLayout) findViewById(R.id.layout_menu);

        txt_change_pass = (TextView) findViewById(R.id.txt_change_pass);

        ProgressDialog = new Dialog(ChangePasswordActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading) ProgressDialog.findViewById(R.id.rotateloading_register);


        OpenSans_Regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular_0.ttf");
        OpenSans_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold_0.ttf");
        regularRoboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        edit_new_pass.setTypeface(OpenSans_Regular);
        edit_con_pass.setTypeface(OpenSans_Regular);
        edit_current_pass.setTypeface(OpenSans_Regular);
        txt_change_pass.setTypeface(Roboto_Bold);

        layout_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected", false);
                setResult(100, resultIntent);
                finish();

            }
        });
        layout_change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Log.d("password","Login password = "+userPref.getString("password",""));
                if (edit_current_pass.getText().toString().trim().length() == 0) {
                    Common.showMkError(ChangePasswordActivity.this, getResources().getString(R.string.please_enter_current_password));
                    edit_current_pass.requestFocus();
                    return;
                } else if (!edit_current_pass.getText().toString().equals(OldPassword)) {
                    Common.showMkError(ChangePasswordActivity.this, getResources().getString(R.string.please_current_password));
                    edit_current_pass.requestFocus();
                    return;
                } else if (edit_new_pass.getText().toString().trim().length() == 0) {
                    Common.showMkError(ChangePasswordActivity.this, getResources().getString(R.string.please_enter_new_password));
                    edit_new_pass.requestFocus();
                    return;
                } else if (edit_new_pass.getText().toString().trim().length() < 6 || edit_new_pass.getText().toString().trim().length() > 32) {
                    Common.showMkError(ChangePasswordActivity.this, getResources().getString(R.string.password_new_length));
                    edit_new_pass.requestFocus();
                    return;
                }
//                else if(!PasswordValidaton(edit_new_pass.getText().toString())){
//                    showMKPanelError(ChangePasswordActivity.this, getResources().getString(R.string.password_valid),rlMainView,tvTitle,regularRoboto);
//                    edit_new_pass.requestFocus();
//                    return;
//                }
                else if (edit_con_pass.getText().toString().trim().length() == 0) {
                    Common.showMkError(ChangePasswordActivity.this, getResources().getString(R.string.please_enter_confirm_password));
                    edit_con_pass.requestFocus();
                    return;
                } else if (!edit_new_pass.getText().toString().equals(edit_con_pass.getText().toString())) {
                    Common.showMkError(ChangePasswordActivity.this, getResources().getString(R.string.password_new_confirm));
                    edit_con_pass.requestFocus();
                    return;
                }

                Changpassword();
            }
        });
    }

    private void Changpassword() {

        ProgressDialog.show();
        cusRotateLoading.start();
        Api myApi = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<BaseResponse> call = myApi.changePassword(user_id,OldPassword,edit_new_pass.getText().toString());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse users = response.body();
                if (users.status.equalsIgnoreCase("true")) {
                    ProgressDialog.cancel();
                    cusRotateLoading.stop();
                    Toast.makeText(ChangePasswordActivity.this, "Your password has been changed.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString(Constants.password, edit_new_pass.getText().toString());
                    editor.apply();
                    startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));

                } else {
                    ProgressDialog.cancel();
                    cusRotateLoading.stop();
                    Toast.makeText(ChangePasswordActivity.this, "invalid email password...", Toast.LENGTH_SHORT).show();
                }

                // loader(false);
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                ProgressDialog.cancel();
                cusRotateLoading.stop();
                Toast.makeText(ChangePasswordActivity.this, "Sorry something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public final boolean PasswordValidaton(String password) {
        Matcher hasLetter = letter.matcher(password);
        Matcher hasDigit = digit.matcher(password);
        Matcher hasSpecial = special.matcher(password);

        return hasLetter.find() && hasDigit.find() && hasSpecial.find();
    }
}

