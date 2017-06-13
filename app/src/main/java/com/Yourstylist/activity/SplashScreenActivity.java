package com.Yourstylist.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.Yourstylist.R;
import com.Yourstylist.Utils;

import static com.Yourstylist.Utils.tag;


public class SplashScreenActivity extends AppCompatActivity {
    private ImageView imgAppIcon;
    private static final int PERMISSION_REQUEST_CODE =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initializeViews();
        continueExecution();
    }

    private void initializeViews() {
        imgAppIcon=(ImageView)findViewById(R.id.imgAppIcon);
    }

    private void continueExecution() {
        Animation animation=new AlphaAnimation(0f,1f);
        animation.setDuration(1000);
        imgAppIcon.startAnimation(animation);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(SplashScreenActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.SYSTEM_ALERT_WINDOW}, PERMISSION_REQUEST_CODE);
            } else {
                proceed();
            }
        } else {
            proceed();
        }
    }

    private void proceed() {
        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation=new AlphaAnimation(1f,0f);
                animation.setDuration(1500);
                imgAppIcon.postOnAnimationDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences=getSharedPreferences(Utils.USER_SHARED_PREFERENCE,MODE_PRIVATE);
                        if(sharedPreferences.contains(Utils.USER_EMAIL)){
                            Intent intent=new Intent(SplashScreenActivity.this,HomeScreenActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Intent intent=new Intent(SplashScreenActivity.this,LoginScreenActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                },1200);

                imgAppIcon.startAnimation(animation);
            }
        },2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(tag,"Permissions Granted");
                    proceed();
                } else {
                    // Permission Denied
                    Toast.makeText(SplashScreenActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
