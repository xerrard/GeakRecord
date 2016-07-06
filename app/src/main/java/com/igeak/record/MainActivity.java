package com.igeak.record;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
    //private static final int STATE_RECORDSTOPED = 3;
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
    private ImageButton mListIb;
    private int state = STATE_INIT;

    AnimatorSet animatorSet3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (STATE_RECORDSTOPED == state) {
//            reInitRecord();
//        }
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
        mListIb = (ImageButton) findViewById(R.id.record_list);
        mRecordIv.setOnClickListener(MainActivity.this);
        mBackIb.setOnClickListener(MainActivity.this);
        mSetupIb.setOnClickListener(MainActivity.this);
        mListIb.setOnClickListener(MainActivity.this);
        mListIb.setActivated(true);
        initRecord();

    }


    @Override
    public void onClick(final View view) {
        if (view.getId() == R.id.record_list) {
            if (state != STATE_RECORDING) {
                startActivity(new Intent(this, WearListActivity.class));
            }
        } else if (state == STATE_PRE_RECORD) {
            if (view.getId() == R.id.record_setup) {
                record();
            }
        } else if (state == STATE_RECORDING) {
            if (view.getId() == R.id.record_back) {
                queryStopRecord();
            } else if (view.getId() == R.id.record_setup) {
                stopRecordToQuerySave();
            }
        }
    }


    /**
     * 第一次进入，保持1s时间，然后动画切换到pre模式
     */
    private void initRecord() {
        final Timer timer = new Timer();
        state = STATE_INIT;
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
        }, Const.ANIMATION_LONG_1000);

    }

    private void reInitRecord() {
        state = STATE_INIT;
        preRecord();

    }

    private void reInitRecordnoAnimation() {
        state = STATE_INIT;

        mTimeTv.setBase(SystemClock.elapsedRealtime());
        mControlRl.setVisibility(View.VISIBLE);
        mSetupIb.setActivated(false);
        //mListIb.setActivated(false);
        mBackIb.setActivated(false);
        state = STATE_PRE_RECORD;

    }

    /**
     * pre模式，主要做好动画，此时的事件只支持录音（见onclick）
     */
    private void preRecord() {

        //mTimeTv.setVisibility(View.VISIBLE);
        mTimeTv.setBase(SystemClock.elapsedRealtime());
        mControlRl.setVisibility(View.VISIBLE);
        mSetupIb.setActivated(false);
        //mListIb.setActivated(false);
        mBackIb.setActivated(false);
        recordAnimation1();
        recordAnimation2();
        state = STATE_PRE_RECORD;
    }

    /**
     * 进入录音状态，开启闪烁动画
     */
    private void record() {
        mListIb.setActivated(false);
        mRecordMidTv.setVisibility(View.VISIBLE);
        mRecordOutTv.setVisibility(View.VISIBLE);
        recordAnimation3();
        mSetupIb.setActivated(true);
        //mListIb.setActivated(true);
        mBackIb.setActivated(true);
        mTimeTv.setBase(SystemClock.elapsedRealtime());
        mTimeTv.start();
        mediaRecording(); //开始录音
        state = STATE_RECORDING;
    }

//    private void pauseRecord() {
//        mRecordMidTv.setVisibility(View.GONE);
//        mRecordOutTv.setVisibility(View.GONE);
//        mControlRl.setVisibility(View.GONE);
//
//        mediaStopRecording();
//        mTimeTv.stop();
//        deleteFile();
//        //state = STATE_RECORDSTOPED;
//        reInitRecord();
//    }


    /**
     * 停止录音，处理好动画
     */
    private void stopRecordToQuerySave() {
        Animator.AnimatorListener listener = new MyAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
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
                }, Const.ANIMATION_LONG_1000);
            }
        };

        recordAnimation4(listener);  //图从小变大，
        animatorSet3.cancel();
        mRecordMidTv.setVisibility(View.GONE);
        mRecordOutTv.setVisibility(View.GONE);
        mControlRl.setVisibility(View.GONE);
        mTimeTv.stop();
        mediaStopRecording();
        mListIb.setActivated(true);
    }

    private void stopRecordToInit() {
        mediaStopRecording();
        animatorSet3.cancel();
        mRecordMidTv.setVisibility(View.GONE);
        mRecordOutTv.setVisibility(View.GONE);
        mTimeTv.stop();
        reInitRecordnoAnimation();
        mListIb.setActivated(true);
    }


    /**
     * 动画1：record大图从大变小，并转换位置
     */
    private void recordAnimation1() {

        ObjectAnimator animation1 = ObjectAnimator.ofFloat(mRecordIv, "scaleY", 0.65384615f);
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(mRecordIv, "scaleX", 0.65384615f);
        ObjectAnimator animation3 = ObjectAnimator.ofFloat(mRecordIv, "translationY", -57.0f);
        ObjectAnimator animation4 = ObjectAnimator.ofFloat(mTimeTv, "translationY", 1.0f);
        //此处的-57一直没搞清楚什么原因，原本应该是-35

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animation1, animation2, animation3, animation4);
        animatorSet.setDuration(Const.ANIMATION_LONG_1000).start();
    }


    /**
     * 动画2：time 和control的渐出，
     */
    private void recordAnimation2() {
        ObjectAnimator animation1;
        ObjectAnimator animation2;

        ObjectAnimator animation3 = ObjectAnimator.ofFloat(mControlRl, "scaleY", 0.0f, 1.0f);
        ObjectAnimator animation4 = ObjectAnimator.ofFloat(mControlRl, "scaleX", 0.0f, 1.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        if (mTimeTv.getVisibility() == View.GONE) {
            mTimeTv.setVisibility(View.VISIBLE);
            animation1 = ObjectAnimator.ofFloat(mTimeTv, "scaleX", 0.0f, 1.0f);
            animation2 = ObjectAnimator.ofFloat(mTimeTv, "scaleY", 0.0f, 1.0f);
            animatorSet.playTogether(animation1, animation2, animation3, animation4);
        } else {
            animatorSet.playTogether(animation3, animation4);
        }
        animatorSet.setDuration(Const.ANIMATION_LONG_1000).start();
    }


    /**
     * 动画4：点击保存，record大图从小变大，转换位置， time 转换位置
     */
    private void recordAnimation4(Animator.AnimatorListener listener) {
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(mTimeTv, "translationY", 56.0f);
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(mRecordIv, "scaleY", 1.0f);
        ObjectAnimator animation3 = ObjectAnimator.ofFloat(mRecordIv, "scaleX", 1.0f);
        ObjectAnimator animation4 = ObjectAnimator.ofFloat(mRecordIv, "translationY", -18.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animation1, animation2, animation3, animation4);
        animatorSet.setDuration(Const.ANIMATION_LONG_1000).start();
        animatorSet.addListener(listener);
    }


    /**
     * 动画3：record背景闪烁
     */
    private void recordAnimation3() {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mRecordMidTv, "alpha", 0f, 1f);
        animator1.setRepeatCount(20);
        animator1.setDuration(Const.ANIMATION_LONG_2000);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mRecordMidTv, "alpha", 0f, 1f);
        animator2.setRepeatCount(20);
        animator2.setDuration(Const.ANIMATION_LONG_2000);
        ObjectAnimator.ofFloat(mRecordOutTv, "alpha", 0f, 1f).setRepeatCount(10);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator1, animator2);
        set.start();
        animatorSet3 = set;
    }

    private void querySaveRecord() {
        Intent intent = new Intent(this, QuerySave.class);
        startActivityForResult(intent, Const.REQUESTCODE_QUERY_SAVE);
    }

    private void queryStopRecord() {
        Intent intent = new Intent(this, QueryStopRecord.class);
        startActivityForResult(intent, Const.REQUESTCODE_QUERY_STOP_RECORD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Const.REQUESTCODE_QUERY_SAVE) {
            if (resultCode == Const.RESULTCODE_OK) {
                startActivityForResult(new Intent(this, WearListActivity.class), Const
                        .REQUESTCODE_LIST);
                //state = STATE_RECORDSTOPED;
            } else if (resultCode == Const.RESULTCODE_CANCEL) {
                try {
                    deleteFile();
                    //state = STATE_RECORDSTOPED;
                    reInitRecord();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "cancel save Record failed " + e.getMessage());
                }
            }

        } else if (requestCode == Const.REQUESTCODE_QUERY_STOP_RECORD) {
            if (resultCode == Const.RESULTCODE_OK) {
                stopRecordToInit();
                //state = STATE_RECORDSTOPED;

            } else if (resultCode == Const.RESULTCODE_CANCEL) {

            }
        } else if (requestCode == Const.REQUESTCODE_LIST) {
            reInitRecord();
        }

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


    public class MyAnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
