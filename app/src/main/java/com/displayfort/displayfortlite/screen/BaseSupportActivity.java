package com.displayfort.displayfortlite.screen;

import android.os.Bundle;

import java.util.HashSet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by pc on 10/01/2019 13:14.
 * MyApplication
 */
public class BaseSupportActivity extends AppCompatActivity {
    public static HashSet<String> hashSet = new HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.d("keyCode", "" + keyCode + " : " + getCurrentFocus());
//        keycodeTv.append(keyCode + " , ");
//
//
//        return super.onKeyDown(keyCode, event);
//
//    }
}
