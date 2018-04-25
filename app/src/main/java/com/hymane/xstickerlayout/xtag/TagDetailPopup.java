package com.hymane.xstickerlayout.xtag;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hymane.xstickerlayout.R;
import com.hymane.xstickerlayout.xtag.sticker.Sticker;
import com.hymane.xstickerlayout.xtag.utils.UIUtils;


public class TagDetailPopup extends PopupWindow {

    private final InputMethodManager im;
    private Context mContext;
    private EditText tipsContent;
    private Sticker sticker;

    public TagDetailPopup(Context context) {
        setContentView(LayoutInflater.from(context).inflate(R.layout.check_tags_pop, null));
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setFocusable(true);
        setOutsideTouchable(true);
        this.mContext = context;
        im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        tipsContent = getContentView().findViewById(R.id.tips_content);
        tipsContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tipsContent.isFocusable()) {
                    tipsContent.setSelection(tipsContent.length());
                }
                tipsContent.setFocusableInTouchMode(true);
                tipsContent.setFocusable(true);
                tipsContent.requestFocus();

                if (im != null) {
                    im.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        });
        tipsContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE) {
                    final String content = tipsContent.getText().toString();
                    if (!TextUtils.isEmpty(content)) {
                        dismiss();
                    } else {
                        UIUtils.showShort(mContext, "提示语不能为空");
                    }
                    return true;
                }
                return false;
            }
        });
        // Disable default animation for circular reveal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimationStyle(0);
        }
    }

    @Override
    public void dismiss() {
        if (tipsContent != null && sticker != null) {
            final String content = tipsContent.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                sticker.setContent(content);
            } else {
                UIUtils.showShort(mContext, "提示语不能为空");
            }
            if (im != null) {
                im.hideSoftInputFromWindow(tipsContent.getWindowToken(), 0);
            }
        }
        super.dismiss();
        tipsContent.clearFocus();
        tipsContent.setFocusable(false);
        tipsContent.setFocusableInTouchMode(false);
    }

    public void showPopup(View parent, int gravity, int x, int y, int width, int height) {
        showAtLocation(parent, gravity, x, y);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circularReveal(x, y, width, height);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void circularReveal(final int x, final int y, final int width, final int height) {
        final View contentView = getContentView();
        contentView.post(new Runnable() {
            @Override
            public void run() {
                final int[] myLocation = new int[2];
                final int[] anchorLocation = new int[2];
                contentView.getLocationOnScreen(myLocation);
                final int cx = x - myLocation[0] + width / 2;
                final int cy = y - myLocation[1] + height / 2;

                contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                final int dx = Math.max(cx, contentView.getMeasuredWidth() - cx);
                final int dy = Math.max(cy, contentView.getMeasuredHeight() - cy);
                final float finalRadius = (float) Math.hypot(dx, dy);
                Animator animator = ViewAnimationUtils.createCircularReveal(contentView, cx, cy, 0f, finalRadius);
                animator.setDuration(200);
                animator.start();
            }
        });
    }

    public TextView getTextView() {
        return tipsContent;
    }

    public void setContentText(String content, Sticker sticker) {
        this.tipsContent.setText(content);
        this.sticker = sticker;
    }

    public String getContentText() {
        return this.tipsContent.getText().toString();
    }
}