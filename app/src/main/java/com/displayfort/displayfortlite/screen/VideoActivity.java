package com.displayfort.displayfortlite.screen;

/**
 * Created by pc on 26/03/2019 14:09.
 * DisplayFortLite
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.VideoView;

import com.displayfort.displayfortlite.R;
import com.displayfort.displayfortlite.widgets.MyVideoView;

import java.io.File;

public class VideoActivity extends Activity {
    public final static String TAG = "LibVLCAndroidSample/VideoActivity";
    public final static String LOCATION = "com.compdigitec.libvlcandroidsample.VideoActivity.location";
    private String mFilePath;

    // display surface
    private SurfaceView mSurface;
    private SurfaceHolder holder;

    // media player
    private int mVideoWidth;
    private int mVideoHeight;
    private final static int VideoSizeChanged = -1;
    private MyVideoView playerView;

    private Context context;

    /*************
     * Activity
     *
     *************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);
        context = this;
        // Receive path to play from intent
        Intent intent = getIntent();
        mFilePath = new File(Environment.getExternalStorageDirectory() + File.separator + "ads/Footboys.mov").getAbsolutePath();
//                intent.getExtras().getString(LOCATION);

        Log.d("TAG", "Playing back " + mFilePath);

        init();
    }

    private void init() {
        playerView = findViewById(R.id.vlc_videoview);
        playerView.setVideoPath(mFilePath);
        playerView.setVideoSize(1080, 640);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        playerView.setVideoSize(1080, 1920);
    }


    /*************
     * Surface
     *************/
    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        if (holder == null || mSurface == null)
            return;

        // get screen size
        int w = getWindow().getDecorView().getWidth();
        int h = getWindow().getDecorView().getHeight();

        // getWindow().getDecorView() doesn't always take orientation into
        // account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        // force surface buffer size
        holder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        LayoutParams lp = mSurface.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }

    /*************
     * Player
     *************/


}
