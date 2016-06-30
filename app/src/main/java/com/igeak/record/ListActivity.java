package com.igeak.record;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by xuqiang on 16-6-29.
 */
public class ListActivity extends Activity implements View.OnClickListener {
    File[] files;
    int currentIndex = 0;
    ImageView mRecordIv;
    TextView mPreFile;
    TextView mCurrentFileName;
    TextView mCurrentFileDate;
    TextView mCurrentFileTime;
    TextView mCurrentFileDuration;
    TextView mNextFile;
    FrameLayout mFl1;
    FrameLayout mFl2;
    FrameLayout mFl3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        initRes();
    }


    @Override
    protected void onResume() {
        super.onResume();
        files = getAllRecordFileNames();
        if (currentIndex >= files.length) {
            currentIndex = files.length - 1;
        }
        showPreviousFileInfo();
        showCurrentFileInfo();
        showNextFileInfo();
    }

    private void initRes() {
        mRecordIv = (ImageView) findViewById(R.id.record_img);
        mPreFile = (TextView) findViewById(R.id.last_record);
        mCurrentFileName = (TextView) findViewById(R.id.record_name);
        mCurrentFileDate = (TextView) findViewById(R.id.record_date);
        mCurrentFileTime = (TextView) findViewById(R.id.record_time);
        mCurrentFileDuration = (TextView) findViewById(R.id.record_duration);
        mNextFile = (TextView) findViewById(R.id.next_record);
        mFl1 = (FrameLayout) findViewById(R.id.list_fl1);
        mFl2 = (FrameLayout) findViewById(R.id.list_fl2);
        mFl3 = (FrameLayout) findViewById(R.id.list_fl3);
        mFl1.setOnClickListener(this);
        mFl2.setOnClickListener(this);
        mFl3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.list_fl1) {
            currentIndex = currentIndex - 1;
            showPreviousFileInfo();
            showCurrentFileInfo();
            showNextFileInfo();
        } else if (view.getId() == R.id.list_fl2) {
            playMusic(currentIndex);
        } else if (view.getId() == R.id.list_fl3) {
            currentIndex = currentIndex + 1;
            showPreviousFileInfo();
            showCurrentFileInfo();
            showNextFileInfo();
        }

    }


    private File[] getAllRecordFileNames() {
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


    private void showCurrentFileInfo() {
        if (files.length > 0) {
            showFileInfo(files[currentIndex]);
        } else {
            finish();
        }
    }

    private void showPreviousFileInfo() {

        int previousIndex = currentIndex - 1;
        if (previousIndex < 0) {
            mRecordIv.setVisibility(View.VISIBLE);
            mPreFile.setVisibility(View.GONE);
        } else {
            mRecordIv.setVisibility(View.GONE);
            String name = getString(R.string.record)
                    + " " + String.format("%02d", previousIndex + 1);
            mPreFile.setText(name);
            mPreFile.setVisibility(View.VISIBLE);
        }

    }

    private void showNextFileInfo() {

        int nextIndex = currentIndex + 1;
        if (nextIndex >= files.length) {
            mNextFile.setVisibility(View.GONE);
        } else {
            String name = getString(R.string.record)
                    + " " + String.format("%02d", nextIndex + 1);
            mNextFile.setText(name);
            mNextFile.setVisibility(View.VISIBLE);
        }
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

            MediaPlayer player = MediaPlayer.create(this, Uri.fromFile(file));
            int time = player.getDuration();
            if (player != null) {
                player.release();
                player = null;
            }

            String duration = tf.format(new Date(time));

            String name = getString(R.string.record)
                    + " " + String.format("%02d", currentIndex + 1);
            mCurrentFileName.setText(name);
            mCurrentFileDate.setText(dateText);
            mCurrentFileTime.setText(timeText);
            mCurrentFileDuration.setText(duration);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void playMusic(int currentIndex) {
        Intent intent = new Intent(ListActivity.this, PlayerActivity.class);
        intent.putExtra("index", currentIndex);
        startActivity(intent);
    }

}
