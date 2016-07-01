package com.igeak.record;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Property;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public abstract class ConfirmationActivity extends Activity implements
        Handler.Callback, View.OnClickListener, View.OnLayoutChangeListener,
        ViewTreeObserver.OnScrollChangedListener {
    private float currentTranslation;
    protected View mButtonBar;
    private ObjectAnimator mButtonBarAnimator;
    private float mButtonBarFloatingHeight;
    protected Button mCancel;
    protected Button mConfirm;
    protected ViewGroup mConfirmation;
    protected ViewGroup mContent;
    private Handler mHandler = new Handler();
    private boolean mHiddenBefore;
    //	private Interpolator mInterpolator;
    protected ScrollView mScrollingContainer;
    protected TextView mSubtitle;
    protected TextView mTitle;

    private void generateButtonBarAnimator(float paramFloat1,
                                           float paramFloat2, float paramFloat3, float paramFloat4,
                                           long paramLong) {
        View localView = this.mButtonBar;
        PropertyValuesHolder[] arrayOfPropertyValuesHolder = new PropertyValuesHolder[2];
        Property localProperty1 = View.TRANSLATION_Y;
        float[] arrayOfFloat1 = new float[2];
        arrayOfFloat1[0] = paramFloat1;
        arrayOfFloat1[1] = paramFloat2;
        arrayOfPropertyValuesHolder[0] = PropertyValuesHolder.ofFloat(
                localProperty1, arrayOfFloat1);
        Property localProperty2 = View.TRANSLATION_Z;
        float[] arrayOfFloat2 = new float[2];
        arrayOfFloat2[0] = paramFloat3;
        arrayOfFloat2[1] = paramFloat4;
        arrayOfPropertyValuesHolder[1] = PropertyValuesHolder.ofFloat(
                localProperty2, arrayOfFloat2);
        this.mButtonBarAnimator = ObjectAnimator.ofPropertyValuesHolder(
                localView, arrayOfPropertyValuesHolder);
        this.currentTranslation = paramFloat2;
        this.mButtonBarAnimator.setDuration(paramLong);
//		this.mButtonBarAnimator.setInterpolator(this.mInterpolator);
        this.mButtonBarAnimator.start();
    }

    private void hideButtonBar() {
        int i = this.mScrollingContainer.getHeight()
                + this.mButtonBar.getHeight() - this.mContent.getHeight()
                + Math.max(this.mScrollingContainer.getScrollY(), 0);
        int j;
        if (i > 0) {
            j = mButtonBar.getHeight() - i;
            if (mHiddenBefore && mButtonBarAnimator != null) {
                if (this.mButtonBarAnimator.isRunning()) {
                    if (Math.abs(this.currentTranslation - j) > 0.01F) {
                        this.mButtonBarAnimator.cancel();
                        if (Math.abs(this.mButtonBar.getTranslationY() - j) > 0.01F) {
                            long l = Math.max((long) (500.0F * (j - this.mButtonBar
                                    .getTranslationY()) / this.mButtonBar
                                    .getHeight()), 0L);
                            generateButtonBarAnimator(
                                    this.mButtonBar.getTranslationY(), j,
                                    this.mButtonBarFloatingHeight, 0.0F, l);
                        } else {
                            this.mButtonBar.setTranslationY(j);
                            this.mButtonBar.setTranslationZ(0.0F);
                        }
                    }
                } else {
                    this.mButtonBar.setTranslationY(j);
                    this.mButtonBar.setTranslationZ(0.0F);
                }
            }
        } else {
            j = mButtonBar.getHeight();
        }
        mHiddenBefore = true;
    }

    protected String getConfirmationSubtitle() {
        return getString(R.string.are_you_sure);
    }

    protected int getConfirmationSubtitleSize() {
        return 20;
    }

    public abstract String getConfirmationTitle();

//	protected String getConfirmedSubtitle() {
//		return getString(R.string.are_you_sure);
//	}

//	protected String getConfirmedTitle() {
//		return null;
//	}

    public boolean handleMessage(Message paramMessage) {
        switch (paramMessage.what) {
            case 1001:
                hideButtonBar();
                return true;

            default:
                return false;
        }
    }

    public void onCancel(View paramView) {
        finish();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.confirmation_confirm_button) {// confirm
            onConfirm(view);
//			if (!isFinishing()) {
//				this.mConfirm.setVisibility(View.GONE);
//				this.mCancel.setVisibility(View.GONE);
//				String title = getConfirmedTitle();
//				if (!TextUtils.isEmpty(title))
//					this.mTitle.setText(title);
//				String subTitle = getConfirmedSubtitle();
//				if (!TextUtils.isEmpty(subTitle))
//					this.mSubtitle.setText(subTitle);
//			}
        }
        if (view.getId() == R.id.confirmation_cancel_button)// cancel
            onCancel(view);
    }

    public abstract void onConfirm(View paramView);

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.confirmation_activity_round);
//		this.mInterpolator = AnimationUtils.loadInterpolator(this,
//				R.anim.circular_image_button_anim);
        this.mButtonBarFloatingHeight = getResources().getDimension(R.dimen
                .conf_diag_floating_height);
//		 ((WatchViewStub)findViewById(2131361884)).setOnLayoutInflatedListener(new
//		 ConfirmationActivity.1(this));
        initView();
    }

    private void initView() {
        mTitle = (TextView) findViewById(R.id.confirmation_title);
        mSubtitle = (TextView) findViewById(R.id.confirmation_subtitle);
        mConfirm = (Button) findViewById(R.id.confirmation_confirm_button);
        mCancel = (Button) findViewById(R.id.confirmation_cancel_button);
        mButtonBar = findViewById(R.id.confirmation_button_bar);
        mConfirmation = (ViewGroup) findViewById(R.id.confirmation);
        mContent = (ViewGroup) findViewById(R.id.confirmation_content);
        mScrollingContainer = (ScrollView) findViewById(R.id.confirmation_scrolling_container);

        mTitle.setText(getConfirmationTitle());
        mSubtitle.setText(getConfirmationSubtitle());
        mSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, getConfirmationSubtitleSize());
        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mScrollingContainer.getViewTreeObserver().addOnScrollChangedListener(this);
    }

    public void onLayoutChange(View paramView, int paramInt1, int paramInt2,
                               int paramInt3, int paramInt4, int paramInt5, int paramInt6,
                               int paramInt7, int paramInt8) {
        if (this.mButtonBarAnimator != null)
            this.mButtonBarAnimator.cancel();
        this.mHandler.removeMessages(1001);
        this.mHiddenBefore = false;
        if (this.mContent.getHeight() > this.mScrollingContainer.getHeight()) {
            this.mButtonBar.setTranslationZ(this.mButtonBarFloatingHeight);
            this.mHandler.sendEmptyMessageDelayed(1001, 3000L);
            generateButtonBarAnimator(this.mButtonBar.getHeight(), 0.0F, 0.0F,
                    this.mButtonBarFloatingHeight, 1000L);
        } else {
            this.mButtonBar.setTranslationY(0.0F);
            this.mButtonBar.setTranslationZ(0.0F);
        }
    }

    public void onScrollChanged() {
        this.mHandler.removeMessages(1001);
        hideButtonBar();
    }

}
