package com.hymane.xstickerlayout.xtag.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.hymane.xstickerlayout.R;
import com.hymane.xstickerlayout.view.AdvAlertDialog;

/**
 * Author   :hymane
 * Email    :hymanme@163.com
 * Create at 2017-07-03
 * Description:
 */
public class DialogUtils {

    public static AdvAlertDialog buildStickerDialog(final Context context, DialogInterface.OnDismissListener onDismissListener
            , final OnStickerSelectedListener selectedListener) {
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_sticker_selector, null, false);
        final AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.Custom_Dialog_Sticker)
                .setView(view)
                .setCancelable(true)
                .setOnDismissListener(onDismissListener)
                .create();
        if (selectedListener != null) {
            final EditText etInput = view.findViewById(R.id.et_input);
            etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE) {
                        final String content = etInput.getText().toString();
                        if (!TextUtils.isEmpty(content)) {
                            etInput.setText("");
                            selectedListener.tips(content);
                            alertDialog.dismiss();
                        } else {
                            UIUtils.showShort(context, "提示不能为空");
                        }
                        return true;
                    }
                    return false;
                }
            });
            etInput.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Drawable drawable = etInput.getCompoundDrawables()[2];
                    //如果右边没有图片，不再处理
                    if (drawable == null)
                        return false;
                    //如果不是按下事件，不再处理
                    if (event.getAction() != MotionEvent.ACTION_UP)
                        return false;
                    if (event.getX() > etInput.getWidth()
                            - etInput.getPaddingRight()
                            - drawable.getIntrinsicWidth()) {
                        final String content = etInput.getText().toString();
                        if (!TextUtils.isEmpty(content)) {
                            selectedListener.tips(content);
                            etInput.setText("");
                            alertDialog.dismiss();
                        }
                    }
                    return false;
                }
            });
        }
        return new AdvAlertDialog(alertDialog);
    }

    public interface OnStickerSelectedListener {
        //正确标签
        void correct();

        //错误标签
        void wrong();

        //提示文字标签
        void tips(String content);

        //语音标签
        void voice();

        //区域标签
        void area();

        //涂鸦白板
        void doodle();
    }
}
