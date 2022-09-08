package co.webnexs.tranznexs.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.victor.loading.rotate.RotateLoading;

import net.gotev.uploadservice.MultipartUploadRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import co.webnexs.tranznexs.R;
import co.webnexs.tranznexs.Constants;
import co.webnexs.tranznexs.ImageFilePath;
import co.webnexs.tranznexs.beens.RegUser;
import co.webnexs.tranznexs.countrypicker.CountryPickerDialog;
import co.webnexs.tranznexs.utils.Common;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    //EditText edit_username;
    EditText edit_name, edit_mobile, edit_email,edit_password, edit_cpassword;
    Uri tempUri;
    CheckBox chk;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private static final int CAMERA_REQUEST = 1888;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    CircleImageView img_add_image;
    TextView alreadyUser,edit_dob;
    Dialog ProgressDialog;
    RotateLoading cusRotateLoading;
    Dialog OpenCameraDialog;
    CountryPickerDialog countryPicker;
    String userImage,name,email,password,phone_no,dob,gender;
    private RegUser regUser = new RegUser();
    Button Register;
    Bitmap bitmap;
    private static final String TAG = "SignUpActivity";
    private ProgressDialog pDialog;
    boolean connected = false;
    DatePickerDialog picker;
    int value;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // LanguageCode = Locale.getDefault().getLanguage();

        Register = (Button) findViewById(R.id.signup);
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_mobile = (EditText) findViewById(R.id.edit_mobile);
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_cpassword = (EditText) findViewById(R.id.edit_cpassword);
        edit_dob = (TextView) findViewById(R.id.edit_dob);
        img_add_image = (CircleImageView) findViewById(R.id.img_add_image);
        alreadyUser = (TextView)findViewById(R.id.alreadyUser);
        radioGroup=(RadioGroup)findViewById(R.id.radiogrup);
        chk=(CheckBox)findViewById(R.id.terms_conditions);

        alreadyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alreadyUser=new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(alreadyUser);
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.male:
                        value = 1;
                       // Toast.makeText(getBaseContext(), "Male"+value, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.female:
                        value = 2;
                       // Toast.makeText(getBaseContext(), "Female"+value, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.other:
                        value = 3;
                      //  Toast.makeText(getBaseContext(), "Other"+value, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
            Register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (edit_name.getText().toString().isEmpty()) {
                        Common.showMkError(SignUpActivity.this, getResources().getString(R.string.please_enter_name));
                        return;
                    }
                    else if (edit_email.getText().toString().isEmpty()) {
                        Common.showMkError(SignUpActivity.this, getResources().getString(R.string.please_enter_email));
                        return;
                    }
                    else if(edit_email.getText().toString().trim().length() != 0 && !isValidEmail(edit_email.getText().toString().trim())){
                        Common.showMkError(SignUpActivity.this, getResources().getString(R.string.please_enter_valid_email));
                        return;
                    }else if (edit_password.getText().toString().isEmpty()) {
                        Common.showMkError(SignUpActivity.this, getResources().getString(R.string.please_enter_password));
                        return;
                    } else if (edit_cpassword.getText().toString().isEmpty()) {
                        Common.showMkError(SignUpActivity.this, getResources().getString(R.string.please_enter_confirm_password));
                        return;
                    }  else if (!edit_cpassword.getText().toString().equals(edit_password.getText().toString())) {
                        Common.showMkError(SignUpActivity.this, "Confirm password is doesn't match");
                        return;
                    }  else if (edit_mobile.getText().toString().isEmpty()) {
                        Common.showMkError(SignUpActivity.this, getResources().getString(R.string.please_enter_mobile));
                        return;
                    } else if (edit_dob.getText().toString().isEmpty()) {
                        Common.showMkError(SignUpActivity.this, getResources().getString(R.string.please_enter_date));
                        return;
                    } else if (chk.isChecked()==false) {
                        Common.showMkError(SignUpActivity.this, getResources().getString(R.string.please_check_terms));
                        return;
                    }else if (bitmap == null) {
                        Common.showMkError(SignUpActivity.this, getResources().getString(R.string.please_check_image));
                        return;
                    }else {
                        signup();
                    }
                }
            });


        //   showDate(year, month+1, day);
        edit_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int pYear = c.get(Calendar.YEAR);
                int pMonth = c.get(Calendar.MONTH);
                int pDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                edit_dob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                               /* final Calendar c = Calendar.getInstance();
                                int mHour = c.get(Calendar.HOUR_OF_DAY);
                                int mMinute = c.get(Calendar.MINUTE);*/
                            }
                        }, pYear, pMonth, pDay);
                datePickerDialog.show();
            }
        });
        img_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
                OpenCameraDialog = new Dialog(SignUpActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                OpenCameraDialog.setContentView(R.layout.camera_dialog_layout);

                RelativeLayout layout_open_camera = (RelativeLayout) OpenCameraDialog.findViewById(R.id.layout_open_camera);
                layout_open_camera.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        OpenCameraDialog.cancel();

                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                        } else {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                    }
                });

                RelativeLayout layout_open_gallery = (RelativeLayout) OpenCameraDialog.findViewById(R.id.layout_open_gallery);
                layout_open_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        OpenCameraDialog.cancel();
                        Intent gi = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        gi.setType("image/*");
                        startActivityForResult(gi, RESULT_LOAD_IMAGE);
                    }
                });

                RelativeLayout layout_open_cancel = (RelativeLayout) OpenCameraDialog.findViewById(R.id.layout_open_cancel);
                layout_open_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OpenCameraDialog.cancel();
                    }
                });

                OpenCameraDialog.show();
            }
        });



        chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //boolean checked = ((CheckBox) v).isChecked();
                chk.setChecked(false);
                // Check which checkbox was clicked

                // Do your coding
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
                alertDialogBuilder.setTitle("Terms & Conditions.");
                alertDialogBuilder.setMessage(R.string.terms_conditions);
                alertDialogBuilder.setPositiveButton("Agree",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Toast.makeText(SignUpActivity.this,"Terms & conditions applied.",Toast.LENGTH_LONG).show();
                                chk.setChecked(true);
                            }
                        });

                alertDialogBuilder.setNegativeButton("Disagree",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        chk.setChecked(false);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }

        });
    }
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    private void signup() {
        pDialog = new ProgressDialog(SignUpActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        pDialog.setIndeterminate(true);
        pDialog.setMessage("Creating Account...");
        pDialog.setCancelable(false);
        pDialog.show();

         name = edit_name.getText().toString();
        password = edit_password.getText().toString();
         email = edit_email.getText().toString();
        phone_no = edit_mobile.getText().toString();
        dob = edit_dob.getText().toString();
        gender = Integer.toString(value);

       // File user_im = new File(userImage);
        //File user_im = new File(getRealPathFromURI(tempUri));
        String image = String.valueOf(userImage);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            try {

                String uploadId = UUID.randomUUID().toString();
                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, Constants.REGISTER)
                        .addFileToUpload(image, "user_image") //Adding file
                        .addParameter("name", name)
                        .addParameter("email_id", email)
                        .addParameter("password", password)
                        .addParameter("phone_no", phone_no)
                        .addParameter("dob", dob)
                        .addParameter("gender",gender)
                        .setMaxRetries(3)
                        .startUpload();//Starting the upload
                Intent intent = new Intent(SignUpActivity.this, OneTimePasswordActivity.class);
                startActivity(intent);
            } catch (FileNotFoundException f) {
                //   Common.showMkError(SignUpActivity.this, getResources().getString(R.string.check_internet));
                Toast.makeText(this, "check internet", Toast.LENGTH_SHORT).show();
                pDialog.cancel();
            } catch (Exception exc) {
                pDialog.cancel();
            }
        }
         else{
                connected = false;
                pDialog.cancel();
                Snackbar.make(findViewById(android.R.id.content), "Check your internet connection.", Snackbar.LENGTH_LONG).show();
            }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            img_add_image.setImageBitmap(bitmap);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try {
                String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpeg";
                File imageFile = new File(mPath);

                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();
                String filePath = imageFile.getPath();
                Bitmap ssbitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                img_add_image.setImageBitmap(ssbitmap);
                userImage = filePath;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                //String path = saveImage(bitmap);
                Toast.makeText(SignUpActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                img_add_image.setImageBitmap(bitmap);
                userImage = ImageFilePath.getPath(SignUpActivity.this, data.getData());

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(SignUpActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}





