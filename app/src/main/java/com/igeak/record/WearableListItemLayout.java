package com.igeak.record;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Created by xuqiang on 16-7-1.
 */
public class WearableListItemLayout extends FrameLayout
        implements WearableListView.OnCenterProximityListener {


    private RelativeLayout mDetailRl;


    private RelativeLayout mInfoRl;

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

    }



    @Override
    public void onCenterPosition(boolean animate) {
        mDetailRl.setVisibility(VISIBLE);
        mInfoRl.setVisibility(GONE);
    }

    @Override
    public void onNonCenterPosition(boolean animate) {
        mDetailRl.setVisibility(GONE);
        mInfoRl.setVisibility(VISIBLE);
    }
}
