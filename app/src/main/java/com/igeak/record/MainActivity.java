package com.igeak.record;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.lassana.recorder.AudioRecorder;
import com.github.lassana.recorder.AudioRecorderBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements View.OnClickListener {

    public static final String FILE_PATH = "/sdcard/Recorder";
    private static final int STATE_INIT = 0;
    private static final int STATE_PRE_RECORD = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_RECORDSTOPED = 3;
    //private static final int STATE_PAUSE = 4;
    public static String LOG_TAG = "geak_recorder";
    private String currentFileName;
    private MediaRecorder recorder;

    private ImageView mRecordIv;
    private ImageView mRecordMidTv;
    private ImageView mRecordOutTv;
    //private FrameLayout mRecordFl;
    private Chronometer mTimeTv;
    private RelativeLayout mControlRl;
    private ImageButton mBackIb;
    private ImageButton mSetupIb;
    private ImageButton mPauseIb;
    private int state = STATE_INIT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.round_activity_main);
        mRecordIv = (ImageView) findViewById(R.id.record);
        mRecordMidTv = (ImageView) findViewById(R.id.middlerecord);
        mRecordOutTv = (ImageView) findViewById(R.id.outerrecord);
        //mRecordFl = (FrameLayout) findViewById(R.id.fl_record);
        mTimeTv = (Chronometer) findViewById(R.id.time);
        mControlRl = (RelativeLayout) findViewById(R.id.rlcontrol);
        mBackIb = (ImageButton) findViewById(R.id.record_back);
        mSetupIb = (ImageButton) findViewById(R.id.record_setup);
        mPauseIb = (ImageButton) findViewById(R.id.record_pause);
        mRecordIv.setOnClickListener(MainActivity.this);
        mBackIb.setOnClickListener(MainActivity.this);
        mSetupIb.setOnClickListener(MainActivity.this);
        mPauseIb.setOnClickListener(MainActivity.this);
    }


    @Override
    public void onClick(final View view) {

        if (state == STATE_INIT) {
            if (view.getId() == R.id.record) {
                recordAnimation1();
                final Timer timer = new Timer();

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mTimeTv.post(new Runnable() {
                            @Override
                            public void run() {
                                preRecord();
                                timer.cancel();
                            }
                        });
                    }
                }, 1000);

            }
        } else if (state == STATE_PRE_RECORD) {
            if (view.getId() == R.id.record_setup) {
                record();
            }

        } else if (state == STATE_RECORDING) {
            if (view.getId() == R.id.record_back) {
                finish();
            } else if (view.getId() == R.id.record_pause) {
                pauseRecord();
            } else if (view.getId() == R.id.record_setup) {
                recordAnimation2();
                stopRecord();
            }
        }
    }


    private void reInitRecord() {
        mRecordIv.setVisibility(View.VISIBLE);
        recordAnimation3();
        mRecordOutTv.setVisibility(View.GONE);
        mRecordMidTv.setVisibility(View.GONE);
        mTimeTv.setVisibility(View.GONE);
        mControlRl.setVisibility(View.GONE);
        state = STATE_INIT;
    }

    private void preRecord() {
        mTimeTv.setVisibility(View.VISIBLE);
        mTimeTv.setBase(SystemClock.elapsedRealtime());
        mControlRl.setVisibility(View.VISIBLE);
        mSetupIb.setActivated(false);
        mPauseIb.setActivated(false);
        mBackIb.setActivated(false);
        state = STATE_PRE_RECORD;
    }


    private void record() {
        recordAnimation4();


        mSetupIb.setActivated(true);
        mPauseIb.setActivated(true);
        mBackIb.setActivated(true);
        mediaRecording(); //开始录音
        mTimeTv.setBase(SystemClock.elapsedRealtime());
        mTimeTv.start();
        state = STATE_RECORDING;
    }

    private void pauseRecord() {
        mRecordMidTv.setVisibility(View.GONE);
        mRecordOutTv.setVisibility(View.GONE);
        mControlRl.setVisibility(View.GONE);

        mediaStopRecording();
        mTimeTv.stop();
        deleteFile();
        reInitRecord();
    }


    private void stopRecord() {
        mRecordMidTv.setVisibility(View.GONE);
        mRecordOutTv.setVisibility(View.GONE);
        mControlRl.setVisibility(View.GONE);

        mediaStopRecording();
        mTimeTv.stop();
        //mTimeTv.setBase(SystemClock.elapsedRealtime());
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mTimeTv.post(new Runnable() {
                    @Override
                    public void run() {
                        querySaveRecord();
                        timer.cancel();
                    }
                });
            }
        }, 1000);

    }


    /**
     * 动画1：record大图从大变小，并转换位置
     */
    private void recordAnimation1() {
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(mRecordIv, "scaleY", 0.65384615f);
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(mRecordIv, "scaleX", 0.65384615f);
        ObjectAnimator animation3 = ObjectAnimator.ofFloat(mRecordIv, "translationY", -57.0f);
        //此处的-57一直没搞清楚什么原因，原本应该是-35
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animation1, animation2, animation3);
        animatorSet.setDuration(1000).start();


    }

    /**
     * 动画2：record大图从小变大，转换位置， time 转换位置
     */
    private void recordAnimation2() {
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(mTimeTv, "translationY", 56.0f);
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(mRecordIv, "scaleY", 1.0f);
        ObjectAnimator animation3 = ObjectAnimator.ofFloat(mRecordIv, "scaleX", 1.0f);
        ObjectAnimator animation4 = ObjectAnimator.ofFloat(mRecordIv, "translationY", -18.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animation1, animation2, animation3, animation4);
        animatorSet.setDuration(1000).start();
    }


    /**
     * 动画2：record，转换位置， time 转换位置
     */
    private void recordAnimation3() {
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(mTimeTv, "translationY", 0.0f);
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(mRecordIv, "translationY", 0.0f);
        ObjectAnimator animation3 = ObjectAnimator.ofFloat(mRecordIv, "scaleY", 1.0f);
        ObjectAnimator animation4 = ObjectAnimator.ofFloat(mRecordIv, "scaleX", 1.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animation1, animation2, animation3, animation4);
        animatorSet.setDuration(1000).start();
    }

    /**
     * 动画4：record背景闪烁
     */
    private void recordAnimation4() {
        mRecordMidTv.setVisibility(View.VISIBLE);
        mRecordOutTv.setVisibility(View.VISIBLE);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mRecordMidTv, "alpha", 0f, 1f);
        animator1.setRepeatCount(10);
        animator1.setDuration(1000);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mRecordMidTv, "alpha", 0f, 1f);
        animator2.setRepeatCount(10);
        animator2.setDuration(1000);
        ObjectAnimator.ofFloat(mRecordOutTv, "alpha", 0f, 1f).setRepeatCount(10);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator1,animator2);
        set.start();
    }

    private void querySaveRecord() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_msg_record_end);

        // builder.setTitle("");
        builder.setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
                reInitRecord();
            }
        });
        builder.setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener
                () {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    deleteFile();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "cancel save Record failed " + e.getMessage());
                }
                dialog.dismiss();
                reInitRecord();
            }
        });

        builder.create().show();
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
            String fileName = FILE_PATH + "/REC" + currentDateandTime + ".amr";
            Log.d(LOG_TAG, "filename: " + fileName);
            this.currentFileName = fileName;
            recorder.setOutputFile(fileName);
            recorder.prepare();
            recorder.start();
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
        File file = new File(MainActivity.this.currentFileName);
        if (file.exists()) {
            file.delete();
        }

    }

    @Override
    protected void onDestroy() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        super.onDestroy();
    }
}
