package com.igeak.record;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by xuqiang on 16-7-1.
 */
public class WearableListItemLayout extends FrameLayout
        implements WearableListView.OnCenterProximityListener, WearableListView.onItemTouch {


    private RelativeLayout mDetailRl;


    private RelativeLayout mInfoRl;
    private TextView mNameTv;
    private LinearLayout mDateTimeLl;
    private TextView mDateTv;
    private TextView mTimeTv;
    private LinearLayout mDuarionLl;
    private TextView mDuationTv;
    private ImageView mDuationImg;

    private boolean isScroll = false;

    public WearableListItemLayout(Context context) {
        this(context, null);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);

    }

    // Get references to the icon and text in the item layout definition
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // These are defined in the layout file for list items
        // (see next section)

        mDetailRl = (RelativeLayout) findViewById(R.id.item_detail_rl);

        mInfoRl = (RelativeLayout) findViewById(R.id.item_info_rl_up);
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
    public void onCenterPosition(boolean animate,int dy) {
        //Log.i("xerrard", "onCenterPosition animate = " + animate + "  dy = " + dy);
        //mDetailRl.setVisibility(VISIBLE);
        //mInfoRl.setVisibility(GONE);


        if (!isScroll) {
            mDateTimeLl.setVisibility(VISIBLE);
            mDuarionLl.setVisibility(VISIBLE);
        }
//        ObjectAnimator animation1 = ObjectAnimator.ofFloat(mNameTv, "scaleY", 1.0f);
//        ObjectAnimator animation2 = ObjectAnimator.ofFloat(mNameTv, "scaleX", 1.0f);
//        ObjectAnimator animation3 = ObjectAnimator.ofFloat(mNameTv, "translationY", -40.0f);
//        //此处的-57一直没搞清楚什么原因，原本应该是-35
//
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playTogether(animation1, animation2, animation3);
//        animatorSet.setDuration(300).start();

        mNameTv.setScaleX(1.0f);
        mNameTv.setScaleY(1.0f);
        mNameTv.setTranslationY(-40.0f);
    }

    @Override
    public void onNonCenterPosition(boolean animate,int dy) {
        //Log.i("xerrard", "onNonCenterPosition animate = " + animate + "  dy = " + dy);
        //mDetailRl.setVisibility(GONE);
        //mInfoRl.setVisibility(VISIBLE);
        //if (!isScroll) {
        mDateTimeLl.setVisibility(GONE);
        mDuarionLl.setVisibility(GONE);
        //}
//        ObjectAnimator animation1 = ObjectAnimator.ofFloat(mNameTv, "scaleY", 0.5f);
//        ObjectAnimator animation2 = ObjectAnimator.ofFloat(mNameTv, "scaleX", 0.5f);
//        ObjectAnimator animation3 = ObjectAnimator.ofFloat(mNameTv, "translationY", 40.0f);
//        //此处的-57一直没搞清楚什么原因，原本应该是-35
//
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playTogether(animation1, animation2, animation3);
//        animatorSet.setDuration(50).start();

        mNameTv.setScaleX(0.5f);
        mNameTv.setScaleY(0.5f);
        mNameTv.setTranslationY(0.0f);
    }

    @Override
    public void onTouchDown() {
        //Log.i("xerrard", "onTouchDown");
        isScroll = true;
        mDateTimeLl.setVisibility(GONE);
        mDuarionLl.setVisibility(GONE);
    }

    @Override
    public void onTouchUp() {
        //Log.i("xerrard", "onTouchUp");
        isScroll = false;
    }
}
