package com.igeak.record;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ListItemLayout extends FrameLayout
        implements WearableListView.OnCenterProximityListener, WearableListView.onItemTouch,
        WearableListView.onItemScroll {


    private RelativeLayout mDetailRl;
    private RelativeLayout mInfoRl;
    private TextView mNameTv;
    private LinearLayout mDateTimeLl;
    private TextView mDateTv;
    private TextView mTimeTv;
    private LinearLayout mDuarionLl;
    private TextView mDuationTv;
    private ImageView mDuationImg;


    public ListItemLayout(Context context) {
        this(context, null);
    }

    public ListItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListItemLayout(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDetailRl = (RelativeLayout) findViewById(R.id.item_detail_rl);

        //mInfoRl = (RelativeLayout) findViewById(R.id.item_info_rl);
        //mInfoRl = (RelativeLayout) findViewById(R.id.item_info_rl_up);

        mNameTv = (TextView) findViewById(R.id.record_name);

        mDateTimeLl = (LinearLayout) findViewById(R.id.datetime_ll);
        mDateTv = (TextView) findViewById(R.id.record_date);
        mTimeTv = (TextView) findViewById(R.id.record_time);

        mDuationImg = (ImageView) findViewById(R.id.record_duration_img);
        mDuationTv = (TextView) findViewById(R.id.record_duration);
        mDuarionLl = (LinearLayout) findViewById(R.id.record_duration_ll);


    }


    @Override
    public void onCenterPosition(boolean animate) {
        float scale = (Math.abs(getY() - 121)) / 60.0f;   //这个我们称之为偏离度

        mNameTv.setScaleX(1.55f - 0.55f * scale);
        mNameTv.setScaleY(1.55f - 0.55f * scale);
        mNameTv.setTranslationY(-40.0f + 40.0f * scale);
//        mDateTimeLl.setScaleX(1.0f - scale);
//        mDateTimeLl.setScaleY(1.0f - scale);
//        mDuarionLl.setScaleX(1.0f - scale);
//        mDuarionLl.setScaleY(1.0f - scale);
        if (scale < 0.3f) {
            mDateTimeLl.setAlpha(1.0f - scale);
            mDuarionLl.setAlpha(1.0f - scale);

        } else {
            mDateTimeLl.setAlpha(0.0f);
            mDuarionLl.setAlpha(0.0f);
        }
    }

    @Override
    public void onNonCenterPosition(boolean animate) {

        mNameTv.setScaleX(1.0f);
        mNameTv.setScaleY(1.0f);
        mNameTv.setTranslationY(0.0f);
        mDateTimeLl.setAlpha(0.0f);
        mDuarionLl.setAlpha(0.0f);
    }

    @Override
    public void onScrollStart() {

    }

    @Override
    public void onScrollStoped() {

    }

    @Override
    public void onTouchDown() {

    }

    @Override
    public void onTouchUp() {

    }
}

