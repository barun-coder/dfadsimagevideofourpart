package com.displayfort.displayfortlite.screen;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.displayfort.RetroFit.BaseRequest;
import com.displayfort.RetroFit.RequestReceiver;

import com.displayfort.displayfortlite.DFPrefrence;
import com.displayfort.displayfortlite.R;
import com.displayfort.displayfortlite.validation.ValidationUtils;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseSupportActivity {

    private Context context;
    public EditText securityPinEt;
    public Button mLoginBtn;
    public TextView mUniqueIdTv;
    private String uniqueId = "", securityPin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_login);
        context = this;
        init();
        setUpt();

    }

    private void setUpt() {
        String serial = Build.SERIAL;
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String myKey = serial + android_id;
        Log.d("Secure", serial);
        Log.d("Secure", android_id);
        Log.d("Secure", getSerialNumber());
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        String uniqueId = android_id;
        if (uniqueId.equalsIgnoreCase("unknown")) {
            uniqueId = ObtainMAC().trim();
        }
//        mUniqueIdTv.setText(ObtainMAC().trim());
//        mUniqueIdTv.append(getSerialNumber());
//        mUniqueIdTv.append("\n" + ObtainMAC().trim());
//        mUniqueIdTv.append("\n" + serial);
        mUniqueIdTv.setText(android_id);
//        mUniqueIdTv.append("\n" + macAddress);
    }


    public static String getSerialNumber() {
        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            // (?) Lenovo Tab (https://stackoverflow.com/a/34819027/1276306)
            serialNumber = (String) get.invoke(c, "gsm.sn1");

            if (serialNumber.equals(""))
                // Samsung Galaxy S5 (SM-G900F) : 6.0.1
                // Samsung Galaxy S6 (SM-G920F) : 7.0
                // Samsung Galaxy Tab 4 (SM-T530) : 5.0.2
                // (?) Samsung Galaxy Tab 2 (https://gist.github.com/jgold6/f46b1c049a1ee94fdb52)
                serialNumber = (String) get.invoke(c, "ril.serialnumber");

            if (serialNumber.equals(""))
                // Archos 133 Oxygen : 6.0.1
                // Google Nexus 5 : 6.0.1
                // Hannspree HANNSPAD 13.3" TITAN 2 (HSG1351) : 5.1.1
                // Honor 5C (NEM-L51) : 7.0
                // Honor 5X (KIW-L21) : 6.0.1
                // Huawei M2 (M2-801w) : 5.1.1
                // (?) HTC Nexus One : 2.3.4 (https://gist.github.com/tetsu-koba/992373)
                serialNumber = (String) get.invoke(c, "ro.serialno");

            if (serialNumber.equals(""))
                // (?) Samsung Galaxy Tab 3 (https://stackoverflow.com/a/27274950/1276306)
                serialNumber = (String) get.invoke(c, "sys.serialnumber");

            if (serialNumber.equals(""))
                // Archos 133 Oxygen : 6.0.1
                // Hannspree HANNSPAD 13.3" TITAN 2 (HSG1351) : 5.1.1
                // Honor 9 Lite (LLD-L31) : 8.0
                // Xiaomi Mi 8 (M1803E1A) : 8.1.0
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(""))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
    }

    private void init() {
        securityPinEt = (EditText) findViewById(R.id.security_pin_et);
        mLoginBtn = (Button) findViewById(R.id.email_sign_in_button);
        mUniqueIdTv = (TextView) findViewById(R.id.uniquid_tv);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    loginRequest();
                }
            }
        });

        securityPinEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == R.id.email_sign_in_button)) {
                    if (isValid()) {
                        loginRequest();
                    }
                }
                return false;
            }
        });

    }

    private boolean isValid() {
        uniqueId = mUniqueIdTv.getText().toString();
        securityPin = securityPinEt.getText().toString();
        if (!ValidationUtils.isValidString(uniqueId, 2)) {
            ShowToast("Not a valid Username.", "Please enter valid Username");
            return false;
        }

        if (!ValidationUtils.isValidString(securityPin, 8)) {
            ShowToast("Not a valid Pin.", "Please enter valid 8 Digit Pin");
            return false;
        }

        return true;

    }

    private void ShowToast(String s, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public String ObtainMAC() {
//        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = manager.getConnectionInfo();
        String macId = getMacAddr();
//        ShowToast(",", "toast");
        if (macId == null || macId.length() <= 0) {
            macId = getanotherUniqueId();
        }
        return macId.toUpperCase();
    }

    private String getanotherUniqueId() {
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        String deviceId = telephonyManager.getDeviceId();
        String subscriberId = telephonyManager.getSubscriberId();
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "";
    }

    private void loginRequest() {
        BaseRequest baseRequest = new BaseRequest(this);
        baseRequest.setBaseRequestListner(new RequestReceiver() {

            @Override
            public void onSuccess(int requestCode, String fullResponse, Object dataObject, int StatusCode) {
                if (StatusCode == 200) {
                    try {
                        JSONObject object = new JSONObject(fullResponse);
                        new DFPrefrence(context).setIsLogin(true);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(context, PotraitPlayAdsFromUsbUniversalActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String message = "Something went wrong.";
                    try {
                        JSONObject jsonObject = new JSONObject(fullResponse);
                        message = jsonObject.optString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("Not Allowed")
                            .setMessage(message)

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
//                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            }

            @Override
            public void onFailure(int requestCode, String errorCode, String message) {

            }

            @Override
            public void onNetworkFailure(int requestCode, String message) {

            }
        });
        final com.google.gson.JsonObject jsonObject = getJsonObject(
                "mac_id", uniqueId,
                "spin", securityPin
        );

        baseRequest.callAPIPost(1, jsonObject, "login");
    }

    public static JsonObject getJsonObject(String... nameValuePair) {
        JsonObject HashMap = null;
        if (null != nameValuePair && nameValuePair.length % 2 == 0) {
            HashMap = new JsonObject();
            int i = 0;
            while (i < nameValuePair.length) {
                HashMap.addProperty(nameValuePair[i], nameValuePair[i + 1]);
                i += 2;
            }

        }

        return HashMap;
    }
}

