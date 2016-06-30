package com.igeak.record;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by gongchao on 16-6-22.
 */
public class CircleSrcView extends View {
    private static final String TAG = "CircleSrcView";
    private Paint mPaint;
    private float mProgress = 0f;
    private Context mContext;
    private int light_bg_color;
    private int dark_bg_color;
    private int light_progress_color;
    private int dark_progress_color;

    private int current_bg_color;
    private int current_progress_color;
    private boolean isLight = true;

    public CircleSrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mPaint = new Paint();
        light_bg_color = mContext.getResources().getColor(R.color.light_bg_color);
        dark_bg_color = mContext.getResources().getColor(R.color.dark_bg_color);
        light_progress_color = mContext.getResources().getColor(R.color.light_progress_color);
        dark_progress_color = mContext.getResources().getColor(R.color.dark_progress_color);
        current_bg_color = light_bg_color;
        current_progress_color = light_progress_color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
        mPaint.setAntiAlias(true); // 设置画笔为无锯齿
        canvas.drawColor(Color.TRANSPARENT);
        mPaint.setStrokeWidth((float) 2.5); // 线宽
        mPaint.setStyle(Paint.Style.STROKE);
        getSuqare();
        int progress = (int) mProgress * 36;
        for (int i = 0; i < 3600; i += 36) {
            if (progress == 0 && i == 0) {
                mPaint.setColor(current_bg_color); // 设置画笔颜色
                float startx = getWidth() / 2;
                float starty = 0;
                float endx = getWidth() / 2;
                float endy = getHeight() / 20;
                mPaint.setStrokeWidth((float) 5.0);
                canvas.drawLine(startx, starty, endx, endy, mPaint);
                // canvas.rotate(6, getWidth() / 2, getHeight() / 2);
                startx = getWidth() / 2 - 2f;
                starty = getHeight() / 20;
                endx = getWidth() / 2 - 2f;
                endy = getHeight() / 10;
                mPaint.setStrokeWidth((float) 1.0);
                canvas.drawLine(startx, starty, endx, endy, mPaint);
                canvas.rotate(3.6f, getWidth() / 2, getHeight() / 2);
                continue;
            }
            if (i <= progress) {
                if (i == 0) {
                    mPaint.setColor(current_progress_color);
                    float startx = getWidth() / 2;
                    float starty = 0;
                    float endx = getWidth() / 2;
                    float endy = getHeight() / 20;
                    mPaint.setStrokeWidth((float) 5.0);
                    canvas.drawLine(startx, starty, endx, endy, mPaint);
                    startx = getWidth() / 2 - 2f;
                    starty = getHeight() / 20;
                    endx = getWidth() / 2 - 2f;
                    endy = getHeight() / 10;
                    mPaint.setStrokeWidth((float) 1.0);
                    canvas.drawLine(startx, starty, endx, endy, mPaint);

//					canvas.rotate(3.6f, getWidth() / 2, getHeight() / 2);
                    continue;
                }


                mPaint.setColor(current_progress_color);
                float startx = getWidth() / 2;
                float starty = 0;
                float endx = getWidth() / 2;
                float endy = getHeight() / 20;
                mPaint.setStrokeWidth((float) 5.0);
                canvas.drawLine(startx, starty, endx, endy, mPaint);
                startx = getWidth() / 2 - 2f;
                starty = getHeight() / 20;
                endx = getWidth() / 2 - 2f;
                endy = getHeight() / 10;
                mPaint.setStrokeWidth((float) 1.0);
                canvas.drawLine(startx, starty, endx, endy, mPaint);

                canvas.rotate(3.6f, getWidth() / 2, getHeight() / 2);
                if (progress >= 3600 && (i == 3600 - 36)) {

                    mPaint.setColor(current_progress_color);
                    startx = getWidth() / 2;
                    starty = 0;
                    endx = getWidth() / 2;
                    endy = getHeight() / 20;
                    mPaint.setStrokeWidth((float) 5.0);
                    canvas.drawLine(startx, starty, endx, endy, mPaint);
                    startx = getWidth() / 2 - 2f;
                    starty = getHeight() / 20;
                    endx = getWidth() / 2 - 2f;
                    endy = getHeight() / 10;
                    mPaint.setStrokeWidth((float) 1.0);
                    canvas.drawLine(startx, starty, endx, endy, mPaint);
                    canvas.rotate(3.6f, getWidth() / 2, getHeight() / 2);
                }
                continue;
            }
            mPaint.setColor(current_bg_color); // 设置画笔颜色
            float startx = getWidth() / 2;
            float starty = 0;
            float endx = getWidth() / 2;
            float endy = getHeight() / 20;
            mPaint.setStrokeWidth((float) 5.0);
            canvas.drawLine(startx, starty, endx, endy, mPaint);
            // canvas.rotate(6, getWidth() / 2, getHeight() / 2);
            startx = getWidth() / 2 - 2f;
            starty = getHeight() / 20;
            endx = getWidth() / 2 - 2f;
            endy = getHeight() / 10;
            mPaint.setStrokeWidth((float) 1.0);
            canvas.drawLine(startx, starty, endx, endy, mPaint);
            canvas.rotate(3.6f, getWidth() / 2, getHeight() / 2);
            if (i == (3600 - 36)) {
                mPaint.setColor(current_bg_color); // 设置画笔颜色
                startx = getWidth() / 2;
                starty = 0;
                endx = getWidth() / 2;
                endy = getHeight() / 20;
                mPaint.setStrokeWidth((float) 5.0);
                canvas.drawLine(startx, starty, endx, endy, mPaint);
                // canvas.rotate(6, getWidth() / 2, getHeight() / 2);
                startx = getWidth() / 2 - 2f;
                starty = getHeight() / 20;
                endx = getWidth() / 2 - 2f;
                endy = getHeight() / 10;
                mPaint.setStrokeWidth((float) 1.0);
                canvas.drawLine(startx, starty, endx, endy, mPaint);
                canvas.rotate(-3.6f, getWidth() / 2, getHeight() / 2);
            }

        }
        canvas.rotate(-3.6f, getWidth() / 2, getHeight() / 2);
//		Log.e(TAG, "progress::"+progress);
    }

    public void setIsLight(boolean islight) {
        isLight = islight;
        if (islight) {
            current_bg_color = light_bg_color;
            current_progress_color = light_progress_color;
        } else {
            current_bg_color = dark_bg_color;
            current_progress_color = dark_progress_color;
        }
        invalidate();
    }

    public void setProgress(float progress) {
        Log.e(TAG, "sdsmProgress:" + progress);
        this.mProgress = progress * 100;
        Log.e(TAG, "mProgress:" + mProgress);
        invalidate();

    }

    public float getProgress() {
        Log.e(TAG, "mProgress:" + mProgress);
        return mProgress;
    }

    private int getSuqare() {
        Log.e(TAG, "width::" + getWidth() + "\t height::" + getHeight());
        return (getWidth() < getHeight()) ? getWidth() : getHeight();
    }


}
