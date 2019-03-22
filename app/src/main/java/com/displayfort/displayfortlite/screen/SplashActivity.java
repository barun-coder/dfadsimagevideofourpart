package com.displayfort.displayfortlite.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.displayfort.displayfortlite.DFPrefrence;
import com.displayfort.displayfortlite.R;

import java.util.ArrayList;

public class SplashActivity extends BaseSupportActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        PermisionRequest();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (new DFPrefrence(context).IsLogin()) {
                    intent = new Intent(context, PotraitPlayAdsFromUsbUniversalActivity.class);
                } else {
                    intent = new Intent(context, LoginActivity.class);
                }
                startActivity(intent);
                finish();

            }
        }, 2500);
    }


}
