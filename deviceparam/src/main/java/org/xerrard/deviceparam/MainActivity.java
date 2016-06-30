package org.xerrard.deviceparam;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView mTextView;
    TextView paramBundle;
    TextView localTextView3;
    TextView localTextView1;
    TextView localTextView2;

    private static final int LCD_DENSITY_HDPI = 240;
    private static final int LCD_DENSITY_LDPI = 120;
    private static final int LCD_DENSITY_MDPI = 160;
    private static final int LCD_DENSITY_TVDPI = 213;
    private static final int LCD_DENSITY_XHDPI = 320;

    private String getResoure(int paramInt1, int paramInt2) {
        paramInt1 = Math.min(paramInt1, paramInt2);
        return "sw" + paramInt1 + "dp-";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                //mTextView = (TextView) stub.findViewById(R.id.text);
                paramBundle = (TextView) findViewById(R.id.tvew_resolution);
                localTextView3 = (TextView) findViewById(R.id.tvew_deviceparams);
                localTextView1 = (TextView) findViewById(R.id.tvew_resource);
                localTextView2 = (TextView) findViewById(R.id.tvew_more);
                DisplayMetrics localDisplayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
                paramBundle.setText("分辨率：" + localDisplayMetrics.widthPixels + "x" +
                        localDisplayMetrics
                        .heightPixels);
                localTextView3.setText("密度:" + localDisplayMetrics.density + "\n屏幕像素密度:" +
                        localDisplayMetrics.densityDpi);
            }
        });


    }
}


