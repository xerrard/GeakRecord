package com.igeak.record;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
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

    private boolean inTouchIng = false;



    AnimatorSet animatorSetCenter = null;
    AnimatorSet animatorSetCenterInfo = null;
    AnimatorSet animatorSetNoCenter = null;

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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDetailRl = (RelativeLayout) findViewById(R.id.item_detail_rl);

        mInfoRl = (RelativeLayout) findViewById(R.id.item_info_rl);
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
    public void onCenterPosition(boolean animate, int dy) {


        if (animate) {
            if (!inTouchIng) {
                ObjectAnimator animation1 = ObjectAnimator.ofFloat(mNameTv, "scaleY", 1.55f);
                ObjectAnimator animation2 = ObjectAnimator.ofFloat(mNameTv, "scaleX", 1.55f);
                ObjectAnimator animation3 = ObjectAnimator.ofFloat(mNameTv, "translationY", -40.0f);
                //此处的-57一直没搞清楚什么原因，原本应该是-35

                animatorSetCenter = new AnimatorSet();
                animatorSetCenter.playTogether(animation1, animation2, animation3);
                animatorSetCenter.setDuration(300).start();

                animatorSetCenterInfo = new AnimatorSet();
                ObjectAnimator animation11 = ObjectAnimator.ofFloat(mDateTimeLl, "scaleY", 1.0f);
                ObjectAnimator animation21 = ObjectAnimator.ofFloat(mDateTimeLl, "scaleX", 1.0f);
                ObjectAnimator animation31 = ObjectAnimator.ofFloat(mDuarionLl, "scaleY", 1.0f);
                ObjectAnimator animation41 = ObjectAnimator.ofFloat(mDuarionLl, "scaleX", 1.0f);
                animatorSetCenterInfo.playTogether(animation11, animation21, animation31,
                        animation41);
                animatorSetCenterInfo.setDuration(300).start();
            }
        } else {
            mNameTv.setScaleX(1.55f);
            mNameTv.setScaleY(1.55f);
            mNameTv.setTranslationY(-40.0f);
            mDateTimeLl.setScaleX(1.0f);
            mDateTimeLl.setScaleY(1.0f);
            mDuarionLl.setScaleX(1.0f);
            mDuarionLl.setScaleY(1.0f);
        }

    }

    @Override
    public void onNonCenterPosition(boolean animate, int dy) {


        if (animate) {
            if (!inTouchIng) {
                ObjectAnimator animation1 = ObjectAnimator.ofFloat(mNameTv, "scaleY", 1.0f);
                ObjectAnimator animation2 = ObjectAnimator.ofFloat(mNameTv, "scaleX", 1.0f);
                ObjectAnimator animation3 = ObjectAnimator.ofFloat(mNameTv, "translationY", 0.0f);

                //此处的-57一直没搞清楚什么原因，原本应该是-35

                animatorSetNoCenter = new AnimatorSet();
                //animatorSetNoCenter.playTogether(animation1, animation2, animation3);
                animatorSetNoCenter.playTogether(animation1, animation2, animation3);
                animatorSetNoCenter.setDuration(300).start();


                animatorSetCenterInfo = new AnimatorSet();
                ObjectAnimator animation11 = ObjectAnimator.ofFloat(mDateTimeLl, "scaleY", 0.0f);
                ObjectAnimator animation21 = ObjectAnimator.ofFloat(mDateTimeLl, "scaleX", 0.0f);
                ObjectAnimator animation31 = ObjectAnimator.ofFloat(mDuarionLl, "scaleY", 0.0f);
                ObjectAnimator animation41 = ObjectAnimator.ofFloat(mDuarionLl, "scaleX", 0.0f);
                animatorSetCenterInfo.playTogether(animation11, animation21, animation31,
                        animation41);
                animatorSetCenterInfo.setDuration(300).start();

            }
        } else {
            mNameTv.setScaleX(1.0f);
            mNameTv.setScaleY(1.0f);
            mNameTv.setTranslationY(0.0f);

            mDateTimeLl.setScaleX(0.0f);
            mDateTimeLl.setScaleY(0.0f);
            mDuarionLl.setScaleX(0.0f);
            mDuarionLl.setScaleY(0.0f);

        }



    }

    @Override
    public void onTouchDown() {
        mDateTimeLl.setScaleX(0.0f);
        mDateTimeLl.setScaleY(0.0f);
        mDuarionLl.setScaleX(0.0f);
        mDuarionLl.setScaleY(0.0f);
        inTouchIng = true;



    }

    @Override
    public void onTouchUp() {
        inTouchIng = false;
    }
}
