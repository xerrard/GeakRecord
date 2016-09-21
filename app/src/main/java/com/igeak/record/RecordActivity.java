package com.igeak.record;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class RecordActivity extends Activity implements View.OnClickListener {

    public static final String FILE_PATH = "/sdcard/Recorder";
    private static final int STATE_PRE_RECORD = 0;
    private static final int STATE_RECORDING_DURING_2 = 1; //还在2s中
    private static final int STATE_RECORDING = 2;
    public static String LOG_TAG = "geak_recorder";
    private static final long MEMORY_LIMIT = 200; //单位MB，保证存储空间大于200M
    private String currentFileName;
    private MediaRecorder recorder;

    private Chronometer mTimeTv;
    private ImageButton mSetupIb;
    private ImageButton mListIb;
    private int state = STATE_PRE_RECORD;
    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }


    };

    private int BASE = 1;
    private int SPACE = 500;// 间隔取样时间

    private void updateMicStatus() {
        if (recorder != null) {
            double ratio = (double)recorder.getMaxAmplitude() /BASE;
            double db = 0;// 分贝
            if (ratio > 1)
                db = 20 * Math.log10(ratio);
            Log.d(LOG_TAG,"分贝值："+db);
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.record_new_style);

        mTimeTv = (Chronometer) findViewById(R.id.time);
        mSetupIb = (ImageButton) findViewById(R.id.record_setup);
        mListIb = (ImageButton) findViewById(R.id.record_list);
        mSetupIb.setOnClickListener(RecordActivity.this);
        mListIb.setOnClickListener(RecordActivity.this);
        mTimeTv.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                String time = chronometer.getText().toString();
                if ("6:00:00".equals(time)) {
                    mTimeTv.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string
                                    .toast_record_over_6hours), Toast.LENGTH_LONG).show();
                            mTimeTv.stop();
                            mTimeTv.setBase(SystemClock.elapsedRealtime());
                            mediaStopRecording();
                        }
                    });
                }
            }
        });
        preRecord();
    }


    @Override
    public void onClick(final View view) {

        if (state == STATE_PRE_RECORD) {
            if (view.getId() == R.id.record_list) {
                startActivity(new Intent(this, WearListActivity.class));
            } else if (view.getId() == R.id.record_setup) {
                long SDAvailableSize = MemorySpaceCheck.getSDAvailableSize() / 1024 / 1024;
                if (SDAvailableSize > MEMORY_LIMIT) {
                    record();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string
                            .toast_memory_not_enough), Toast.LENGTH_LONG).show();
                }

            }
        } else if (state == STATE_RECORDING) {
            if (view.getId() == R.id.record_setup) {
                stopRecordToQuerySave();
            }
        }
    }

    private void preRecord() {
        mListIb.setActivated(false);
        mSetupIb.setActivated(false);
        mTimeTv.setBase(SystemClock.elapsedRealtime());
        state = STATE_PRE_RECORD;
    }

    /**
     * 进入录音状态，开启闪烁动画
     */
    private void record() {
        mListIb.setActivated(true);
        mSetupIb.setActivated(true);
        mTimeTv.setBase(SystemClock.elapsedRealtime());
        mTimeTv.start();
        mediaRecording(); //开始录音
        state = STATE_RECORDING_DURING_2;
        mTimeTv.postDelayed(new Runnable() {
            @Override
            public void run() {
                state = STATE_RECORDING;
            }
        }, 2000);

    }


    /**
     * 停止录音，
     */
    private void stopRecordToQuerySave() {
        mediaStopRecording();
        mTimeTv.stop();
        mListIb.setActivated(false);
        mSetupIb.setActivated(false);
        querySaveRecord();
    }

    private void querySaveRecord() {
        Intent intent = new Intent(this, QuerySave.class);
        startActivityForResult(intent, Const.REQUESTCODE_QUERY_SAVE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Const.REQUESTCODE_QUERY_SAVE) {
            if (resultCode == Const.RESULTCODE_OK) {
                startActivityForResult(new Intent(this, WearListActivity.class), Const
                        .REQUESTCODE_LIST);
            } else if (resultCode == Const.RESULTCODE_CANCEL || resultCode == 0) {
                try {
                    deleteFile();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "cancel save Record failed " + e.getMessage());
                }
            }
            state = STATE_PRE_RECORD;
            preRecord();

        } else if (requestCode == Const.REQUESTCODE_LIST) {
        }

    }


    private void setNameDataBase(long nameindex) {
        SharedPreferences sharedPreferences = getSharedPreferences("record_name", Context
                .MODE_PRIVATE); //私有数据
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putLong("name", nameindex);
        editor.commit();//提交修改
    }

    private long getNameDataBase() {
        SharedPreferences sharedPreferences = getSharedPreferences("record_name", Context
                .MODE_PRIVATE); //私有数据
        long currentName = sharedPreferences.getLong("name", 0);
        return currentName;
    }

    private void mediaRecording() {
        createFilePath();
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentDateandTime = sdf.format(new Date());
            long currentNameIndex = getNameDataBase() + 1; //文件名中加一个参数
            String fileName = FILE_PATH + "/REC" + currentDateandTime + "_record" + currentNameIndex
                    + ".amr";
            setNameDataBase(currentNameIndex);
            Log.d(LOG_TAG, "filename: " + fileName);
            this.currentFileName = fileName;
            recorder.setOutputFile(fileName);
            recorder.prepare();
            recorder.start();
            recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {

                }
            });
            updateMicStatus();
        } catch (Exception e) {
            Log.e(LOG_TAG, "startRecording failed " + e.getMessage());
        }

    }

    private void mediaStopRecording() {
        try {
            recorder.stop();
            recorder.release();
            recorder = null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "stopRecording failed " + e.getMessage());
        }
    }


    private void createFilePath() {
        File Dir = new File(FILE_PATH);
        if (!Dir.exists()) {
            Dir.mkdir();
        }
    }


    private void deleteFile() {
        File file = new File(RecordActivity.this.currentFileName);
        if (file.exists()) {
            file.delete();
            setNameDataBase(getNameDataBase() - 1); //如果不保存，要恢复原有数据index
        }

    }

    @Override
    protected void onDestroy() {

        if (state == STATE_RECORDING) {
            mediaStopRecording();
            Toast.makeText(getApplicationContext(), getString(R.string.toast_recording_destroy),
                    Toast.LENGTH_LONG).show();
        }
        else if(state == STATE_RECORDING_DURING_2){
            mediaStopRecording();
            deleteFile();
        }

        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        super.onDestroy();
    }


}
