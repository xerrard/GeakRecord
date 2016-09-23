package com.igeak.record;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by xuqiang on 16-6-29.
 */
public class PlayerNewActivity extends Activity implements View.OnClickListener, MediaPlayer
        .OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    File[] files;
    int currentIndex = 0;
    TextView mCurrentFileName;
    TextView mCurrentFileDate;
    TextView mCurrentFileTime;
    TextView mCurrentFileDuration;
    ImageButton mSetupIb;
    ImageButton mDeleteIb;
    //CircleSrcView mCircle;
    //boolean isPlaying = false;
    File currentFile;
    MediaPlayer player;
    MediaObserver task;
    int durationInt = 0;
    private static final int STATE_INIT = 1001;
    private static final int STATE_PLAYING = 1002;
    private static final int STATE_PAUSE = 1003;
    private int state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_new);
        initRes();
        Intent intent = getIntent();
        currentIndex = intent.getIntExtra("index", currentIndex);
        files = getAllRecordFile();
        currentFile = files[currentIndex];
        showFileInfo(currentFile);
        state = STATE_INIT;

    }

    private void initRes() {
        mCurrentFileName = (TextView) findViewById(R.id.play_name);
        mCurrentFileDate = (TextView) findViewById(R.id.play_date);
        mCurrentFileTime = (TextView) findViewById(R.id.play_time);
        mCurrentFileDuration = (TextView) findViewById(R.id.play_duration);
        mSetupIb = (ImageButton) findViewById(R.id.play_setup);
        mDeleteIb = (ImageButton) findViewById(R.id.play_delete);
        //mCircle = (CircleSrcView) findViewById(R.id.circle);
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

        String dateString = file.getName().substring(3, 17);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        //SimpleDateFormat tf = new SimpleDateFormat("mm:ss");


        try {
            Date date = sdf.parse(dateString);
            System.out.println(date);

            // SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");
            String dateText = sdfDate.format(date);


            SimpleDateFormat sdfName = new SimpleDateFormat(" HH:mma");
            String timeText = sdfName.format(date);

            player = MediaPlayer.create(this, Uri.fromFile(file));
            if (player == null) {
                delete();
                Toast.makeText(getApplicationContext(), R.string.toast_bad_file_deleted, Toast.LENGTH_LONG).show();
                finish();
            }
            player.setOnPreparedListener(PlayerNewActivity.this);
            player.setOnErrorListener(PlayerNewActivity.this);
            player.setOnCompletionListener(PlayerNewActivity.this);
            //player.prepare();
            durationInt = player.getDuration();
            //player.reset();
            //player.stop();

            SimpleDateFormat tf = new SimpleDateFormat("mm:ss");
            tf.setTimeZone(TimeZone.getTimeZone("UTC"));

            if (durationInt > 3600000) {
                tf.applyPattern("HH:mm:ss");
            }
            String duration = tf.format(durationInt);


//            String name = getString(R.string.record)
//                    + " " + String.format("%02d", currentIndex + 1);

            if (file.getName().length() > 21) {
                String str = file.getName().substring(24);
                String[] strings = str.split("\\.");
                String nameIndexString = strings[0];
                long nameIndex = Long.parseLong(nameIndexString);
                String name = getString(R.string.record)
                        + " " + String.format("%02d", nameIndex);
                mCurrentFileName.setText(name);
            } else {
                String name = getString(R.string.record);
                mCurrentFileName.setText(name);
            }

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
            if (state == STATE_PLAYING) {
                pauseMusic();
            } else if (state == STATE_PAUSE) {

                resumeMusic();
            } else if (state == STATE_INIT) {
                playMusic();
            }
        } else if (view.getId() == R.id.play_delete) {
            queryDelete();
        }
    }


    private void queryDelete() {
        Intent intent = new Intent(this, QueryDelete.class);
        startActivityForResult(intent, Const.REQUESTCODE_QUERY_DELETE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Const.REQUESTCODE_QUERY_DELETE) {
            if (resultCode == Const.RESULTCODE_OK) {
                try {
                    if (state == STATE_PLAYING) {
                        pauseMusic(); //先暂停播放
                    }
                    delete();
                } catch (Exception e) {
                    Log.e(MainActivity.LOG_TAG, "delete Record failed " + e.getMessage());
                }
                finish();
            } else if (resultCode == Const.RESULTCODE_CANCEL || requestCode == 0) {

            }
        }

    }


    @Override
    protected void onDestroy() {
        if (player != null) {
            player.release(); //释放播放器资源
            player = null;
        }
        super.onDestroy();
    }

    private void resumeMusic() {
        if (!isBlueToothHeadsetConnected()) {
            Toast.makeText(PlayerNewActivity.this, R.string.toast_play_bt_headset, Toast.LENGTH_LONG)
                    .show();
            return;
        }
        player.start();
        task = new MediaObserver();
        task.execute();
        //task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        mSetupIb.setActivated(true);
        //isPlaying = true;
        state = STATE_PLAYING;
    }

    private void playMusic() {
        if (!isBlueToothHeadsetConnected()) {
            Toast.makeText(PlayerNewActivity.this, R.string.toast_play_bt_headset, Toast.LENGTH_LONG)
                    .show();
            return;
        }
        player.start();
        task = new MediaObserver();
        task.execute();
        //task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        mSetupIb.setActivated(true);
        //isPlaying = true;
        state = STATE_PLAYING;
    }

    private void pauseMusic() {
        player.pause();
        if (!task.isCancelled()) {
            task.cancel(true);
        }
        mSetupIb.setActivated(false);
        //isPlaying = false;
        state = STATE_PAUSE;
    }

    private void delete() {
        if (currentFile.exists()) {
            currentFile.delete();
        }
        setResult(Const.RESULTCODE_UPDATE);
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
        state = STATE_INIT;
//        if (!task.isCancelled()) {
//            task.cancel(true);
//        }
//        mCircle.setProgress(1.0f);
//        mCircle.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mCircle.setVisibility(View.GONE);
//            }
//        }, 500);
        mSetupIb.setActivated(false);
        //isPlaying = false;

    }

    private class MediaObserver extends AsyncTask<Void, Float, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                int currentPosition = player.getCurrentPosition();
                int lastPosition = currentPosition;
                while (((currentPosition = player.getCurrentPosition())
                        < durationInt) && state == STATE_PLAYING) {
                    if((currentPosition/1000) > (lastPosition/1000)){
                        publishProgress((float)currentPosition);
                    }
                    lastPosition = currentPosition;
                }
                if (state == STATE_INIT) {
                    publishProgress(0.0f);
                }
            } catch (Exception e) {
                Log.e(MainActivity.LOG_TAG, "prepare() failed");
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
            int duration = durationInt - Math.round(values[0]);

            SimpleDateFormat tf = new SimpleDateFormat("mm:ss");
            tf.setTimeZone(TimeZone.getTimeZone("UTC"));

            if (duration > 3600000) {
                tf.applyPattern("HH:mm:ss");
            }
            String durationStr = tf.format(duration);
            mCurrentFileDuration.setText(durationStr);

            //mCircle.setVisibility(View.VISIBLE);
            //mCircle.setProgress(values[0]);
        }
    }

}
