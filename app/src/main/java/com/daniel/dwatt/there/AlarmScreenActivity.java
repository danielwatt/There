package com.daniel.dwatt.there;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;


public class AlarmScreenActivity extends Activity {

    public final String TAG = this.getClass().getSimpleName();

    private PowerManager.WakeLock mWakeLock;
    private MediaPlayer mPlayer;
    Vibrator v;
    private static final int WAKELOCK_TIMEOUT = 60 * 1000; //60 secs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup layout
        this.setContentView(R.layout.activity_alarm);

        String shortName = getIntent().getStringExtra(AlarmManagerHelper.SHORTNAME);
        String uriRingtoneString = getIntent().getStringExtra(AlarmManagerHelper.RINGTONELOCATION);
        boolean vibrate = getIntent().getBooleanExtra(AlarmManagerHelper.VIBRATE, false);

        TextView shortNameText = (TextView) findViewById(R.id.shortNameText);
        shortNameText.setText(shortName);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        ImageButton dismissButton = (ImageButton) findViewById(R.id.dismissButton);
        dismissButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mPlayer.stop();
                v.cancel();
                finish();
            }
        });

        //Play alarm tone
        mPlayer = new MediaPlayer();
        try {
            if (uriRingtoneString != null && !uriRingtoneString.equals("")) {
                Uri toneUri = Uri.parse(uriRingtoneString);
                if (toneUri != null) {
                    mPlayer.setDataSource(this, toneUri);
                    mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mPlayer.setLooping(true);
                    mPlayer.prepare();
                    mPlayer.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (vibrate)
        {
            long[] pattern = {0, 2000, 2000};
            v.vibrate(pattern,0);
        }

        //Ensure wakelock release
        Runnable releaseWakelock = new Runnable() {

            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

                if (mWakeLock != null && mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
            }
        };

        new Handler().postDelayed(releaseWakelock, WAKELOCK_TIMEOUT);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();

        // Set the window to keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // Acquire wakelock
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
            mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
        }

        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
            Log.i(TAG, "Wakelock aquired!!");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }
}
