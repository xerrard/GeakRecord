package com.igeak.record;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by xuqiang on 16-7-1.
 */
public class WearableListItemLayout extends LinearLayout
        implements WearableListView.OnCenterProximityListener {

    private RelativeLayout mImgRl;
    private ImageView mRecordIv;

    private RelativeLayout mDetailRl;
    private TextView mNameTv;
    private TextView mDateTv;
    private TextView mTimeTv;
    private TextView mDuationTv;

    private RelativeLayout mInfoRl;
    private TextView mSmallNameTv;

    private final float mFadedTextAlpha;
    private final int mFadedCircleColor;
    private final int mChosenCircleColor;

    public WearableListItemLayout(Context context) {
        this(context, null);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);

        mFadedTextAlpha = 0.5f;
        mFadedCircleColor = getResources().getColor(R.color.grey);
        mChosenCircleColor = getResources().getColor(R.color.blue);
    }

    // Get references to the icon and text in the item layout definition
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // These are defined in the layout file for list items
        // (see next section)
        mImgRl = (RelativeLayout) findViewById(R.id.item_img_rl);
        //mRecordIv = itemView.findViewById(R.id.)

        mDetailRl = (RelativeLayout) findViewById(R.id.item_detail_rl);
        mNameTv = (TextView) findViewById(R.id.record_name);
        mDateTv = (TextView) findViewById(R.id.record_date);
        mTimeTv = (TextView) findViewById(R.id.record_time);
        mDuationTv = (TextView) findViewById(R.id.record_duration);

        mInfoRl = (RelativeLayout) findViewById(R.id.item_info_rl);
        mSmallNameTv = (TextView) findViewById(R.id.record_name_small);

    }

    @Override
    public void onCenterPosition(boolean animate) {
        //mImgRl.setVisibility(GONE);
        mDetailRl.setVisibility(VISIBLE);
        mInfoRl.setVisibility(GONE);
    }

    @Override
    public void onNonCenterPosition(boolean animate) {
        //((GradientDrawable) mCircle.getDrawable()).setColor(mFadedCircleColor);
        //mImgRl.setVisibility(GONE);
        mDetailRl.setVisibility(GONE);
        mInfoRl.setVisibility(VISIBLE);
    }
}
