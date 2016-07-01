package com.igeak.record;

import android.view.View;

/**
 * Created by xuqiang on 16-7-1.
 */
public class QuerySave extends ConfirmationActivity {


    @Override
    public String getConfirmationTitle() {
        return null;
    }

    @Override
    protected String getConfirmationSubtitle() {
        return getString(R.string.dialog_msg_record_end);
    }

    @Override
    public void onCancel(View paramView) {
        setResult(Const.RESULTCODE_CANCEL);
        finish();

    }

    @Override
    public void onConfirm(View paramView) {
        setResult(Const.RESULTCODE_OK);
        finish();
    }



}


