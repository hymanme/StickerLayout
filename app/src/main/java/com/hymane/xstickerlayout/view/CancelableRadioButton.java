package com.hymane.xstickerlayout.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;

/**
 * Author   :hymane
 * Email    :hymanme@163.com
 * Create at 2017-11-14
 * Description:
 */
public class CancelableRadioButton extends android.support.v7.widget.AppCompatRadioButton {
    public CancelableRadioButton(Context context) {
        super(context);
    }

    public CancelableRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CancelableRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
        if (!isChecked()) {
            ((RadioGroup) getParent()).clearCheck();
        }
    }
}
