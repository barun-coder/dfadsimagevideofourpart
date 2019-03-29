package com.displayfort.displayfortlite.old;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.displayfort.displayfortlite.R;
import com.displayfort.displayfortlite.receiver.StartService;
import com.displayfort.displayfortlite.screen.BaseSupportActivity;
import com.displayfort.displayfortlite.screen.LoginActivity;
import com.displayfort.displayfortlite.widgets.FullScreenVideoView;
import com.netcompss.ffmpeg4android.Prefs;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URLConnection;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

//import com.daasuu.mp4compose.Rotation;
//import com.daasuu.mp4compose.composer.Mp4Composer;


/**
 * Created by pc on 09/01/2019 10:59.
 * MyApplication
 */
public class PotraitPlayAdsFromUsbUniversalActivityInactive extends BaseSupportActivity implements SurfaceHolder.Callback {
    private String TAG = "USBCatch";
    private String TAGs = "FileTranspose";
    String macId;
    private ImageView mDefaultIV;
    private ImageView displayImageView;
    private File[] completFileList;
    private int currentAdvertisementNo = 0;
    private int Orientation = ExifInterface.ORIENTATION_NORMAL;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFERENCE_NAME = "DFADSPreference";
    //    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;
    private UniversalVideoView videoView;
    private RelativeLayout mUvVideoRl;
    private File filePath;
    private MyReceiver myReceiver;
    private int height, width;
    private FullScreenVideoView myVideoView;
    private WebView webview;
    private boolean isChecking = false;
    private Thread thread;
    private File currentWorkingFile = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels * 2;
        width = displayMetrics.widthPixels * 2;
        Log.d("SIZE", width + ":" + height);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_universal_offline_main);
        PermisionRequest();
        RegisterUpdateReceiver();
        this.sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Orientation = ExifInterface.ORIENTATION_UNDEFINED;
        init();
//        SHowMNT();
    }


    private void init() {
        mUvVideoRl = (RelativeLayout) findViewById(R.id.uv_video_rl);
        mUvVideoRl.setVisibility(View.GONE);
        mDefaultIV = (ImageView) findViewById(R.id.default_iv);
        displayImageView = (ImageView) findViewById(R.id.imageView2);
        videoView = findViewById(R.id.videoView);
        initializeWebView();
        UniversalMediaController mMediaController = new UniversalMediaController(this);
        videoView.setMediaController(mMediaController);
        myVideoView = findViewById(R.id.myVide);
//        myVideoView.setVideoSize(250,500);
        Log.d("Secure", LoginActivity.getSerialNumber());


    }

    private void initializeWebView() {
        webview = findViewById(R.id.webview);
        webview.setVisibility(View.VISIBLE);
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadUrl("about:blank");
        webview.loadUrl("file:///storage/emulated/0/ads/nVertical_Display_4K_Underwater_Stock_Footage_Demo_Reel_2160_x_4096,_60p.mp4");
    }

    ///data/user/0/displayfort.nirmit.com.myapplication/files
    private void SHowMNT() {
        isChecking = false;
        mDefaultIV.setVisibility(View.VISIBLE);
        mUvVideoRl.setVisibility(View.INVISIBLE);
        webview.setVisibility(View.INVISIBLE);
        displayImageView.setVisibility(View.INVISIBLE);
        webview.clearCache(true);
        webview.clearHistory();
        webview.destroy();
        showLog("FILEPATH", "\n\n");
        File[] fileList;
        File file = new File("mnt");
        if (file.exists()) {
            showLog("FILEPATH-MNT", "mnt exist");
            fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                showLog("FILEPATH-MNT", i + ":-" + fileList[i].getAbsolutePath());
            }
            showLog("FILEPATH", "MNT over \n");
            File file1 = new File(getApplicationContext().getFilesDir().getPath());
            if (!isNotMobile() && isIMEIAvailable()) {
                if (Orientation == ExifInterface.ORIENTATION_UNDEFINED) {
                    file1 = new File(Environment.getExternalStorageDirectory() + File.separator + "ads");
                } else {
                    file1 = new File(getApplicationContext().getFilesDir().getPath().replace("files", "img"));
                }
                if (file1.exists()) {
                    showLog("FILEPATH-MNT", file1 + " mnt/usb exist");
                    startFIlePlay(file1);
                }
            } else {
                file1 = new File(file.getAbsoluteFile() + File.separator + "usb");
                fileList = file1.listFiles();
                if (fileList != null && fileList.length > 0) {
                    for (int i = 0; i < fileList.length; i++) {
                        showLog("FILEPATH-MNT", i + ":--" + fileList[i].getAbsolutePath());
                        if (fileList[i].exists()) {
                            startFIlePlay(fileList[i]);
                            return;
                        }
                    }

                } else {
                    file1 = new File("storage");
                    fileList = file1.listFiles();
                    if (fileList != null && fileList.length > 0) {
                        for (int i = 0; i < fileList.length; i++) {
                            if (!fileList[i].getAbsolutePath().contains("emulated") && !fileList[i].getAbsolutePath().contains("self")) {
                                if (fileList[i].exists()) {
                                    startFIlePlay(fileList[i]);
                                    return;
                                }
                            }
                        }
                    } else {
                        file1 = new File(file.getAbsoluteFile() + File.separator + "sdcard");
                        fileList = file1.listFiles();
                        if (fileList != null && fileList.length > 0) {
//                            for (int i = 0; i < fileList.length; i++) {
                            startFIlePlay(file1);
                            return;
                            //                            }

                        }
                    }
                }
            }

        }
    }


    ///storage/emulated/0/ads
    private void startFIlePlay(File file) {
        filterName(file);
        completFileList = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if ((new File(dir, name)).isFile()) {
                    return true;
                }
                return false;
            }
        });
        if (completFileList != null && completFileList.length > 0) {
            mDefaultIV.setVisibility(View.GONE);
            showLog("FILEPATH", completFileList.length + " completFileList \n");
            currentAdvertisementNo = 0;
            currentWorkingFile = file;
            showCurrentAdvertisements();
        } else {
            webview.setVisibility(View.INVISIBLE);
            displayImageView.setVisibility(View.INVISIBLE);
        }
    }


    private boolean isFileExistAndEmpty(File transposeFile) {
        if (transposeFile.exists()) {
            if (transposeFile.length() <= 1) {
                transposeFile.delete();
                return true;
            }
        } else {
            return true;
        }
        return false;
    }


    private void filterName(File file) {
        filePath = file;
        completFileList = file.listFiles();
        if (completFileList != null && completFileList.length > 0) {
            for (File FronFile : completFileList) {
                if (FronFile.getAbsolutePath().contains(" ")) {
                    File from = new File(file, FronFile.getName());
                    File to = new File(file, FronFile.getName().replace(" ", "_"));
                    boolean isRename = from.renameTo(to);
                    showLog("FILEPATH", to.getAbsolutePath() + ":" + isRename);
                }
            }
        }
    }


    private void showCurrentAdvertisements() {
        isChecking = true;
        StartWhileloopForcontinouscheck();
        showCurrentAd();

    }

    private void StartWhileloopForcontinouscheck() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isChecking) {
//                    ShowToast("isChecking start ");
                    if (currentWorkingFile != null && currentWorkingFile.exists()) {
                        ShowLog("Exist File");
                    } else {
                        ShowLog("File Not Exist");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SHowMNT();
                            }
                        });
                    }
                }
            }
        });
        thread.start();
    }

    private void showCurrentAd() {
        int interval = -1;
        try {

            final File file = completFileList[currentAdvertisementNo];
            webview.loadUrl("about:blank");
            if (file.exists()) {

                showLog("FILEPATH", "Run File " + file.getAbsolutePath() + "\n");
                String type = URLConnection.guessContentTypeFromName(file.getName());
                showLog("type", "type" + type);
                if (file.isFile() && type != null) {
                    if (type.toLowerCase().contains("gif")) {
                        mUvVideoRl.setVisibility(View.INVISIBLE);
                        displayImageView.setVisibility(View.VISIBLE);
                        webview.setVisibility(View.INVISIBLE);
                        String photoPath = file.getAbsolutePath();
                        Glide.with(this).load(photoPath).into(displayImageView);
                        Log.d("ADVERTISEMENT", photoPath.toString() + "");
//                        displayImageView.setRotation(270f);
                        interval = 5000;
                    } else if (type.toLowerCase().contains("image")) {
                        mUvVideoRl.setVisibility(View.INVISIBLE);
                        displayImageView.setVisibility(View.VISIBLE);
                        webview.setVisibility(View.INVISIBLE);
//                        displayImageView.setRotation(270f);
                        //displayImageView
                        String photoPath = file.getAbsolutePath();
                        Glide.with(this).load(photoPath).into(displayImageView);
                        System.gc();
                        Log.d("ADVERTISEMENT", photoPath.toString() + "");
                        interval = 5000;
                    } else if (type.toLowerCase().contains("video") && !(type.contains("quicktime") || type.contains("null"))) {
                        if (file.exists()) {
                            mUvVideoRl.setVisibility(View.INVISIBLE);
                            displayImageView.setVisibility(View.INVISIBLE);
                            webview.setVisibility(View.VISIBLE);
                            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//                            webview.loadUrl("file:///" + getHMLDATA(file.getAbsolutePath(), type));
                            webview.loadUrl("file:///" + file.getAbsolutePath());
//                            webview.loadUrl("https://play.google.com/store/apps/details?id=com.displayfort.displayfortlite");oncre
                            interval = getMiliseconds(file);
                        }
                    } else if (type.toLowerCase().contains("avnvideo")) {

                        mUvVideoRl.setVisibility(View.INVISIBLE);
//                        File newfile = file;// new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Prefs.FOLDER + file.getName());// new File(getApplicationInfo().dataDir + File.separator + file.getName());
                        File newfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Prefs.FOLDER + "_" + file.getName());// new File(getApplicationInfo().dataDir + File.separator + file.getName());
                        boolean isFileFound = newfile.exists();
                        if (!isFileFound) {
                            Log.d(" VIDEPATH", "isFileFound" + isFileFound);
                            interval = -1; // 1 Second
                        } else {
                            mUvVideoRl.setVisibility(View.VISIBLE);
                            displayImageView.setVisibility(View.INVISIBLE);
                            Log.d("VIDEPATH", "isFileFound" + isFileFound);
                            Log.d("VIDEPATH", "PlayAdsFromUsbActivity.hashSet" + true);
                            String photoPath = newfile.getAbsolutePath();
                            interval = getMiliseconds(newfile);

//                            myVideoView.setVideoURI(Uri.parse(photoPath));
//                            myVideoView.start();
                            if (interval != 0) {
                                Log.i("PostActivity", "Video List is " + getFilesDir().getPath());
                                Uri myUri = Uri.parse(photoPath); // initialize Uri here
                                String url = photoPath;
                                try {
                                    videoView.setVideoURI(Uri.parse(photoPath));
                                    videoView.start();
                                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            Log.d("VIDEPATH", "onCompletion");
                                        }
                                    });
                                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                        @Override
                                        public boolean onError(MediaPlayer mp, int what, int extra) {
                                            Log.d("VIDEPATH", "onError");
                                            videoView.stopPlayback();
                                            if (currentAdvertisementNo < (completFileList.length - 1)) {
                                                currentAdvertisementNo++;
                                            } else {
                                                currentAdvertisementNo = 0;
                                            }
                                            showCurrentAd();
                                            return false;
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                interval = -1;
                            }

                        }
                        // 1 Second
                    }
                }

                if (interval != 0) {
                    Log.d("interval", "interval  :  " + interval + " " + file.getName());
                    Handler handler = new Handler();
                    // final MainActivity MyActivity=this;
                    Runnable runnable = new Runnable() {
                        public void run() {
                            /* hit api on stop*/
                            showCurrentAd();
                        }
                    };
                    handler.postDelayed(runnable, interval);
                    if (currentAdvertisementNo < (completFileList.length - 1)) {
                        currentAdvertisementNo++;
                    } else {
                        currentAdvertisementNo = 0;
                    }
                }
            } else {
                SHowMNT();
            }

        } catch (
                Exception e) {
            e.printStackTrace();
            Log.e("Handeledexception", "E: " + e.getMessage());
            mUvVideoRl.setVisibility(View.INVISIBLE);
            displayImageView.setVisibility(View.VISIBLE);
            if (currentAdvertisementNo < (completFileList.length - 1)) {
                currentAdvertisementNo++;
            } else {
                currentAdvertisementNo = 0;
            }
            showCurrentAd();
        }
    }

    private String getHMLDATA(String absolutePath, String type) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getFileDetail("p1.txt"));
        stringBuffer.append(" <source src=\"" + absolutePath + "\" type=\"" + type + "\">");
        stringBuffer.append(getFileDetail("p2.txt"));
        showLog("HTML", stringBuffer.toString());
        File myFile = null;
        try {
            myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + StartService.FOLDER);
            if (!myFile.exists()) {
                myFile.mkdirs();
            }
            myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + StartService.FOLDER, "videoHtml.html");
            if (myFile.exists()) {
                myFile.delete();
            }
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(stringBuffer.toString());
            myOutWriter.close();
            fOut.close();
            return myFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e("ERRR", "Could not create file", e);
        }

        return "";
    }

    private String getFileDetail(String filename) {
        InputStream input;
        String text = "";
        try {
            AssetManager assetManager = getAssets();
            input = assetManager.open(filename);

            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            // byte buffer into a string
            text = new String(buffer);
            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }


    public Bitmap changeOrientation(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream outputStream = null;
        int orientation = ExifInterface.ORIENTATION_ROTATE_270;
        Bitmap imageRotate = rotateBitmap(bitmap, orientation);

        return imageRotate;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    private int getMiliseconds(File file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(this, Uri.fromFile(file));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = 0;
        try {
            timeInMillisec = Long.parseLong(time);
            return (int) timeInMillisec;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 1500;
    }

    private boolean isNotMobile() {
        Display display = ((Activity) this).getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        float widthInches = metrics.widthPixels / metrics.xdpi;
        float heightInches = metrics.heightPixels / metrics.ydpi;
        double diagonalInches = Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));
        return diagonalInches >= 7.0;
    }

    private boolean isIMEIAvailable() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return true;
        }
        if (telephonyManager.getDeviceId() != null) {
            return true;
        }
        return false;
    }

    private void showLog(String tag, String deviceName) {
        Log.d(TAG, tag + ":" + deviceName);
//        Toast.makeText(this, "" + deviceName, Toast.LENGTH_SHORT).show();
//        try {
//            textView.append(deviceName + "\n");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

//    @Override
//    public void onPause() {
//        showLog("onPause","onFinishAPP");
//        onFinishAPP();
//        super.onPause();
//    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void onFinishAPP() {
        try {
            if (webview != null) {
                webview.destroy();
            }
            showLog("MediaMount onFinishAPP", "Registerd");
            unregisterReceiver(myReceiver);
            showLog("MediaMount", "UNRegisterd");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (videoView != null && videoView.isPlaying()) {
                videoView.stopPlayback();
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        showLog("onStop", "onFinishAPP");
        onFinishAPP();
        super.onStop();

    }

    @Override
    public void onDestroy() {
        showLog("onDestroy", "onFinishAPP");
        onFinishAPP();
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer = new MediaPlayer();
        mSurfaceHolder = holder;
        mMediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            ShowToast(action);
            if (action.equals("android.intent.action.MEDIA_MOUNTED")) {
                SHowMNT();
            } else {
                SHowMNT();
            }

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                ShowToast("attached");


            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                ShowToast("deattached");
            }
        }

    }

    private void ShowToast(String attached) {
        Log.i("ShowToast", attached);
        if ((0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE))) {
            Toast.makeText(PotraitPlayAdsFromUsbUniversalActivityInactive.this, attached, Toast.LENGTH_SHORT).show();
        }
    }

    private void ShowLog(String attached) {
        Log.i("ShowToast", attached);

    }

    private void RegisterUpdateReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intentFilter.addDataScheme("file");
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        myReceiver = new MyReceiver();
        this.registerReceiver(myReceiver, intentFilter);
        showLog("MediaMount", "Registerd");
    }

    public void mute(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int mute_volume = 0;
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mute_volume, 0);
    }

    public void unmute(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int unmute_volume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, unmute_volume, 0);
    }
}
