package com.displayfort.displayfortlite.screen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.displayfort.displayfortlite.DFPrefrence;
import com.displayfort.displayfortlite.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
//                setFile();
                Intent intent;
                if (new DFPrefrence(context).IsLogin()) {
                    intent = new Intent(context, PotraitPlayAdsFromUsbUniversalActivity.class);
                } else {
                    intent = new Intent(context, LoginActivity.class);
                }
//                intent = new Intent(context, PotraitPlayAdsFromUsbUniversalActivity.class);
                startActivity(intent);
                finish();

            }
        }, 2500);
    }

    private void setFile() {
        String ret = "";
        try {
///system/build.prop
            File sdcard = Environment.getExternalStorageDirectory();
            sdcard = new File("/system/build.prop");
            FileWriter writer = new FileWriter(sdcard);
            //Get the text file
            File file = new File(sdcard, "build.prop");


            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            if (bufferedReader != null) {


                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

    }


}
