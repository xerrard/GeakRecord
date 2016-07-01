package com.igeak.record;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by xuqiang on 16-6-29.
 */
public class PlayerActivity extends Activity implements View.OnClickListener, MediaPlayer
        .OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    File[] files;
    int currentIndex = 0;
    TextView mCurrentFileName;
    TextView mCurrentFileDate;
    TextView mCurrentFileTime;
    TextView mCurrentFileDuration;
    ImageButton mSetupIb;
    ImageButton mDeleteIb;
    CircleSrcView mCircle;
    boolean isPlaying = false;
    File currentFile;
    MediaPlayer player;
    MediaObserver task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        initRes();
        Intent intent = getIntent();
        currentIndex = intent.getIntExtra("index", currentIndex);
        files = getAllRecordFile();
        currentFile = files[currentIndex];
        showFileInfo(currentFile);

    }

    private void initRes() {
        mCurrentFileName = (TextView) findViewById(R.id.play_name);
        mCurrentFileDate = (TextView) findViewById(R.id.play_date);
        mCurrentFileTime = (TextView) findViewById(R.id.play_time);
        mCurrentFileDuration = (TextView) findViewById(R.id.play_duration);
        mSetupIb = (ImageButton) findViewById(R.id.play_setup);
        mDeleteIb = (ImageButton) findViewById(R.id.play_delete);
        mCircle = (CircleSrcView) findViewById(R.id.circle);
    }


    private File[] getAllRecordFile() {
        File mfile = new File(MainActivity.FILE_PATH);
        File[] filelist = mfile.listFiles();
        Arrays.sort(filelist, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                return file.lastModified() < t1.lastModified() ? 1 : -1;
            }
        });
        return filelist;
    }


    private void showFileInfo(File file) {

        String dateString = file.getName().substring(3);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        SimpleDateFormat tf = new SimpleDateFormat("mm:ss");


        try {
            Date date = sdf.parse(dateString);
            System.out.println(date);

            // SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");
            String dateText = sdfDate.format(date);


            SimpleDateFormat sdfName = new SimpleDateFormat("a HH:mm");
            String timeText = sdfName.format(date);

            player = MediaPlayer.create(this, Uri.fromFile(file));
            player.setOnPreparedListener(PlayerActivity.this);
            player.setOnErrorListener(PlayerActivity.this);
            player.setOnCompletionListener(PlayerActivity.this);
            //player.prepare();
            int time = player.getDuration();

            String duration = tf.format(new Date(time));

            String name = getString(R.string.record)
                    + " " + String.format("%02d", currentIndex + 1);
            mCurrentFileName.setText(name);
            mCurrentFileDate.setText(dateText);
            mCurrentFileTime.setText(timeText);
            mCurrentFileDuration.setText(duration);
            mSetupIb.setOnClickListener(this);
            mDeleteIb.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.play_setup) {
            if (isPlaying) {
                pauseMusic();

            } else {
                playMusic();

            }
        } else if (view.getId() == R.id.play_delete) {
            queryDelete();
        }
    }


    private void queryDelete() {
        Intent intent = new Intent(this, QueryDelete.class);
        startActivityForResult(intent,Const.REQUESTCODE_QUERY_DELETE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Const.REQUESTCODE_QUERY_DELETE){
            if(resultCode==Const.RESULTCODE_OK){
                try {
                    pauseMusic(); //先暂停播放
                    delete();
                } catch (Exception e) {
                    Log.e(MainActivity.LOG_TAG, "delete Record failed " + e.getMessage());
                }
                finish();
            }
            else if(resultCode==Const.RESULTCODE_CANCEL){

            }
        }

    }


    @Override
    protected void onDestroy() {
        player.release(); //释放播放器资源
        player = null;
        super.onDestroy();
    }

    private void playMusic() {
//        if (!isBlueToothHeadsetConnected()) {
//            Toast.makeText(PlayerActivity.this, R.string.toast_play_bt_headset, Toast.LENGTH_LONG)
//                    .show();
//            return;
//        }
        player.start();
        task = new MediaObserver();
        task.execute();
        mSetupIb.setActivated(true);
        isPlaying = true;

    }

    private void pauseMusic() {
        player.pause();
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        mSetupIb.setActivated(false);
        isPlaying = false;
    }

    private void delete() {
        if (currentFile.exists()) {
            currentFile.delete();
        }
    }


    private boolean isBlueToothHeadsetConnected() {
        try {
            int stateHeadset = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(
                    android.bluetooth.BluetoothProfile.HEADSET);
            int stateA2dp = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(
                    android.bluetooth.BluetoothProfile.A2DP);

            return stateHeadset == android.bluetooth.BluetoothProfile.STATE_CONNECTED
                    || stateA2dp == android.bluetooth.BluetoothProfile.STATE_CONNECTED;
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        mCircle.setVisibility(View.GONE);
        mSetupIb.setActivated(false);
        isPlaying = false;
    }

    private class MediaObserver extends AsyncTask<Void, Float, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                int duration;
                int currentPosition;
                float currentRatio = 0.0f;
                while (((currentPosition = player.getCurrentPosition())
                        < (duration = player.getDuration())) && (currentRatio <= 0.99f)) {
                    float ratio = duration == 0 ? 0 : (float) currentPosition / (float) duration;
                    if (ratio >= currentRatio) {
                        currentRatio = currentRatio + 0.01f;
                        publishProgress(currentRatio);
                    }
                }
            } catch (Exception e) {
                Log.e(MainActivity.LOG_TAG, "prepare() failed");
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
            mCircle.setVisibility(View.VISIBLE);
            mCircle.setProgress(values[0]);
        }
    }

}
