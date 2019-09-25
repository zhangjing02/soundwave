package com.ansen.waveview.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ansen.waveview.R;
import com.ansen.waveview.SoundWaveView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MediaRecorder mMediaRecorder;
    private boolean isRun = true;
    private SoundWaveView voiceLineView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = mMediaRecorder.getMaxAmplitude() / 100;
            double db = 0;
            if (i > 1)
                db = 20 * Math.log10(i);
            voiceLineView.setVolume((int) db);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        voiceLineView = (SoundWaveView) findViewById(R.id.voicLine);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 0);
            return;
        }

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath(), "cache");
            if (!file.exists()) {
                file.createNewFile();
            }
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            mMediaRecorder.setMaxDuration(1000 * 60 * 5);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRun) {
                    handler.sendEmptyMessage(0);
                    SystemClock.sleep(100);
                }
            }
        }).start();

    }
}
