package com.hymane.xstickerlayout.view;

import android.support.v7.app.AlertDialog;
import android.view.View;

/**
 * Author   :hymane
 * Email    :hymanme@163.com
 * Create at 2018-01-04
 * Description:
 */
public class AdvAlertDialog {
    private AlertDialog mAlertDialog;

    public AdvAlertDialog(AlertDialog mAlertDialog) {
        this.mAlertDialog = mAlertDialog;
    }

    public AdvAlertDialog(AlertDialog mAlertDialog, View mTagView) {
        this.mAlertDialog = mAlertDialog;
    }

    public AlertDialog getAlertDialog() {
        return mAlertDialog;
    }

    public void setAlertDialog(AlertDialog mAlertDialog) {
        this.mAlertDialog = mAlertDialog;
    }
}
