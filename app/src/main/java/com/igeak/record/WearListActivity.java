package com.igeak.record;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by xuqiang on 16-7-1.
 */

public class WearListActivity extends Activity
        implements WearableListView.ClickListener, View.OnLayoutChangeListener {

    //String[] elements = { "List Item 1", "List Item 2" };
    File[] files;
    private RelativeLayout mImgRecordRl;
    WearableListView listView;
    MyListAdapter myListAdapter;
    private int mInitialHeaderHeight;
    FileObserver fileObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        files = getAllRecordFileNames();
        if (files == null || files.length == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_file_not_exist),
                    Toast.LENGTH_LONG).show();
            finish();
        }
        setContentView(R.layout.my_list_activity);

        mImgRecordRl = (RelativeLayout) findViewById(R.id.item_img_rl);
        // Get the list component from the layout of the activity
        listView =
                (WearableListView) findViewById(R.id.wearable_list);
        myListAdapter = new MyListAdapter(this, files);
        // Assign an adapter to the list
        listView.setAdapter(myListAdapter);

        // Set a click listener
        listView.setClickListener(this);

        //listView.animateToCenter();
        listView.addOnCentralPositionChangedListener(new WearableListView
                .OnCentralPositionChangedListener() {
            @Override
            public void onCentralPositionChanged(int centerPosition) {
                /**
                 * 当选中第一个的时候，显示record图标
                 */
//                if (centerPosition > 0) {
//                    mImgRecordRl.setVisibility(View.GONE);
//                } else {
//                    mImgRecordRl.setVisibility(View.VISIBLE);
//                }

            }
        });
        listView.addOnScrollListener(new WearableListView.OnScrollListener() {
            @Override
            public void onScroll(int var1) {
                adjustHeaderTranslation();
            }

            @Override
            public void onAbsoluteScrollChange(int var1) {

            }

            @Override
            public void onScrollStateChanged(int var1) {
            }

            @Override
            public void onCentralPositionChanged(int var1) {

            }
        });
        mImgRecordRl.addOnLayoutChangeListener(this);
        listView.addOnLayoutChangeListener(this);
        fileObserver = new FileObserver(MainActivity.FILE_PATH, FileObserver.DELETE) {
            @Override
            public void onEvent(int event, String path) {
                if (event == FileObserver.DELETE) {
                    myListAdapter.setFiles(getAllRecordFileNames());
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            myListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        };
        fileObserver.startWatching();

    }

    @Override
    protected void onDestroy() {
        fileObserver.stopWatching();
        super.onDestroy();
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int
            oldTop, int oldRight, int oldBottom) {
        if (v == mImgRecordRl) {
            mInitialHeaderHeight = bottom - top;
            mInitialHeaderHeight += ((ViewGroup.MarginLayoutParams) v.getLayoutParams()).topMargin;

        } else if (v == listView) {
            adjustHeaderTranslation();
        }
    }


    private void adjustHeaderTranslation() {
        int translation = 0;
        if (listView.getChildCount() > 0) {
            translation = listView.getCentralViewTop() - listView.getChildAt(0).getTop();
        }
        float newTranslation = Math.min(Math.max(-mInitialHeaderHeight, -translation), 0);
        int position = listView.getChildPosition(this.listView.getChildAt(0));
        if (position != 0 && newTranslation >= 0) {
            return;
        }
        mImgRecordRl.setTranslationY(newTranslation);
    }

    private File[] getAllRecordFileNames() {
        File mfile = new File(MainActivity.FILE_PATH);
        if (!mfile.exists()) {
            mfile.mkdir();
        }
        File[] filelist = mfile.listFiles();
        if (filelist != null && filelist.length > 0) {
            Arrays.sort(filelist, new Comparator<File>() {
                @Override
                public int compare(File file, File t1) {
                    return file.lastModified() < t1.lastModified() ? 1 : -1;
                }
            });
        }
        return filelist;
    }

    // WearableListView click listener
    @Override
    public void onClick(WearableListView.ViewHolder v) {
        Integer tag = (Integer) v.itemView.getTag();
        v.itemView.setPressed(true); //增加按压效果
        // use this data to complete some action ...
        playMusic(tag);
    }

    @Override
    public void onTopEmptyRegionClick() {
        files = getAllRecordFileNames();
    }

    private void playMusic(int currentIndex) {
        Intent intent = new Intent(this, PlayerNewActivity.class);
        intent.putExtra("index", currentIndex);
        startActivityForResult(intent, Const.REQUESTCODE_PLAYER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((Const.REQUESTCODE_PLAYER == requestCode) && (Const.RESULTCODE_UPDATE == resultCode)) {
            files = getAllRecordFileNames();
            if (files == null || files.length == 0) {
//                Toast.makeText(getApplicationContext(), getString(R.string.toast_file_not_exist),
//                        Toast.LENGTH_LONG).show();
                finish();
            }
            myListAdapter.setFiles(files);
            myListAdapter.notifyDataSetChanged();
        }
    }


    private static final class MyListAdapter extends WearableListView.Adapter {
        private File[] files;
        private final Context mContext;
        private final LayoutInflater mInflater;


        // Provide a suitable constructor (depends on the kind of dataset)
        public MyListAdapter(Context context, File[] files) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            this.files = files;
        }

        public void setFiles(File[] files) {
            this.files = files;
        }


        // Provide a reference to the type of views you're using
        public static final class ItemViewHolder extends WearableListView.ViewHolder {

            private ImageView mRecordIv;

            private RelativeLayout mDetailRl;
            private TextView mNameTv;
            private TextView mDateTv;
            private TextView mTimeTv;
            private TextView mDuationTv;
            private ImageView mDuationImg;

            private RelativeLayout mInfoupRl;
            private TextView mSmallNameupTv;


            public ItemViewHolder(View itemView) {
                super(itemView);

                mDetailRl = (RelativeLayout) itemView.findViewById(R.id.item_detail_rl);
                mNameTv = (TextView) itemView.findViewById(R.id.record_name);
                mDateTv = (TextView) itemView.findViewById(R.id.record_date);
                mTimeTv = (TextView) itemView.findViewById(R.id.record_time);
                mDuationImg = (ImageView) itemView.findViewById(R.id.record_duration_img);
                mDuationTv = (TextView) itemView.findViewById(R.id.record_duration);
                //mInfoupRl = (RelativeLayout) itemView.findViewById(R.id.item_info_rl);
                //mSmallNameupTv = (TextView) itemView.findViewById(R.id.record_name_small);

            }
        }


        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
            return new ItemViewHolder(mInflater.inflate(R.layout.list_item, null));
        }


        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder,
                                     int position) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;

            int currentIndex = position;

            File file = files[currentIndex];
            String dateString = file.getName().substring(3,17);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");


            try {
                Date date = sdf.parse(dateString);
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");
                String dateText = sdfDate.format(date);

                SimpleDateFormat sdfName = new SimpleDateFormat("a HH:mm");
                String timeText = sdfName.format(date);

                MediaPlayer player = MediaPlayer.create(mContext, Uri.fromFile(file));
                if(player==null){
                    return;
                }
                int time = player.getDuration();
                player.release();

                SimpleDateFormat tf = new SimpleDateFormat("mm:ss");
                tf.setTimeZone(TimeZone.getTimeZone("UTC"));

                if (time > 3600000) {
                    tf.applyPattern("HH:mm:ss");
                }
                String duration = tf.format(time);

//                String name = mContext.getString(R.string.record)
//                        + " " + String.format("%02d", currentIndex + 1);
                if(file.getName().length()>21){
                    String str = file.getName().substring(24);
                    String[] strings = str.split("\\.");
                    String nameIndexString = strings[0];
                    long nameIndex = Long.parseLong(nameIndexString);
                    String name = mContext.getString(R.string.record)
                            + " " + String.format("%02d", nameIndex);
                    itemHolder.mNameTv.setText(name);
                }
                else{
                    String name = mContext.getString(R.string.record);
                    itemHolder.mNameTv.setText(name);
                }

                //itemHolder.mSmallNameupTv.setText(name);
                itemHolder.mDateTv.setText(dateText);
                itemHolder.mTimeTv.setText(timeText);
                itemHolder.mDuationTv.setText(duration);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                holder.itemView.setTag(position);
            }


        }

        // Return the size of your dataset
        // (invoked by the WearableListView's layout manager)
        @Override
        public int getItemCount() {
            return files.length;
        }
    }


}