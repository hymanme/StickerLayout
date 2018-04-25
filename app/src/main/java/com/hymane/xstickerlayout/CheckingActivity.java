package com.hymane.xstickerlayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hymane.xstickerlayout.view.AdvAlertDialog;
import com.hymane.xstickerlayout.xtag.ImageViewTouch;
import com.hymane.xstickerlayout.xtag.ImageViewTouchBase;
import com.hymane.xstickerlayout.xtag.OnSickerLayoutListener;
import com.hymane.xstickerlayout.xtag.TagDetailPopup;
import com.hymane.xstickerlayout.xtag.sticker.Sticker;
import com.hymane.xstickerlayout.xtag.sticker.StickerType;
import com.hymane.xstickerlayout.xtag.utils.DialogUtils;
import com.hymane.xstickerlayout.xtag.utils.ScreenUtils;
import com.hymane.xstickerlayout.xtag.utils.UIUtils;

public class CheckingActivity extends AppCompatActivity implements OnSickerLayoutListener {
    public static final int REQUEST_CODE = 23333;
    private RelativeLayout correctLayout;
    private TextView tvTimeCount;
    private TextView tvWorkFrom;
    private RadioGroup rgHolder;
    private RadioButton rbRight;
    private RadioButton rbError;
    private RadioButton rbQuestion;
    private LinearLayout llDeleteFrame;
    private TextView tvHint;
    private ImageViewTouch mImageViewTouch;

    private AlertDialog mStickerDialog;
    private View llBottomTag;
    private Float sx;
    private Float sy;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    //退回原因
    private String returnReasonString;
    private PopupWindow luckyPopup;
    private TagDetailPopup mTagDetailPopup;
    private int bitmapWidth;
    private int bitmapHeight;
    private int screenWith;
    private int screenHeight;
    private boolean loadSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking);
        initView();
        initData();
        initListener();
    }

    @SuppressLint("WrongConstant")
    public void initView() {
        correctLayout = findViewById(R.id.correctLayout);
        rgHolder = findViewById(R.id.rg_holder);
        rbRight = findViewById(R.id.rb_right);
        rbError = findViewById(R.id.rb_error);
        rbQuestion = findViewById(R.id.rb_question);
        llDeleteFrame = findViewById(R.id.ll_delete_frame);
        tvHint = findViewById(R.id.tv_hint);
        mImageViewTouch = findViewById(R.id.imageViewTouch);
//        cvComplete = findViewById(R.id.cv_complete);

        mTagDetailPopup = new TagDetailPopup(this);
        mTagDetailPopup.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mTagDetailPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        screenWith = ScreenUtils.getScreenWidth(this);
        screenHeight = ScreenUtils.getScreenHeight(this);
//        doodleDialogFragment = new DoodleDialogFragment();
    }

    public void initData() {

        initHomework();
    }

    public void initListener() {
        mImageViewTouch.setDoubleTapEnabled(true);
        mImageViewTouch.setScaleEnabled(true);
        mImageViewTouch.setScrollEnabled(true);
//        mImageViewTouch.setReviewModel(review);
        mImageViewTouch.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        mImageViewTouch.setSickerLayoutListener(this);
        rgHolder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_right:
                        rgHolder.setTag(StickerType.CORRECT);
                        break;
                    case R.id.rb_error:
                        rgHolder.setTag(StickerType.WRONG);
                        break;
                    case R.id.rb_question:
                        rgHolder.setTag(StickerType.TIPS);
                        break;
                    case -1:
                        rgHolder.setTag(null);
                        break;
                }
            }
        });
    }

    private void initHomework() {

        Glide.with(this)
                .load("http://y1.ifengimg.com/cmpp/2014/04/27/02/1eed124a-6a0e-4df8-902d-a1df87781c9b.jpg")
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mImageViewTouch.setTouchEnabled(true);
                        loadSuccess = true;
                        bitmapWidth = resource.getWidth();
                        bitmapHeight = resource.getHeight();
                        mImageViewTouch.setPhotoBitmap(resource);
                        mImageViewTouch.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                mImageViewTouch.initTools();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    mImageViewTouch.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                } else {
                                    mImageViewTouch.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                }
                            }
                        });
                        hideLoadingDialog();
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        mImageViewTouch.setTouchEnabled(false);
                        mImageViewTouch.setImageDrawable(placeholder);
                        showLoadingDialog();
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        mImageViewTouch.setTouchEnabled(false);
                        mImageViewTouch.setImageDrawable(errorDrawable);
                        loadSuccess = false;
                        UIUtils.showShort(CheckingActivity.this, "图片加载失败，请重试..." + e.toString());
                        hideLoadingDialog();
                    }

                    @Override
                    public void onStop() {
                        super.onStop();
                    }
                });
    }

    protected void showLoadingDialog() {
//        if (!isFinishing()) {
//            DialogUtils.showLoadingDialog(this);
//        }
    }

    protected void hideLoadingDialog() {
//        DialogUtils.hideLoadingDialog();
    }

    /**********************************TagLayout Listener Start************************************/
    //点击图片一点，显示操作框
    @Override
    public void onIdleClick(float x, float y, float eventX, float eventY) {
//        Log.d("TAGS", "onIdleClick: " + String.format("x,y:(%f,%f)", x, y) + "|||" + String.format("eventX,eventY:(%f,%f)", eventX, eventY));
        this.sx = x;
        this.sy = y;
        final StickerType type = (StickerType) rgHolder.getTag();
        if (type == StickerType.TIPS || type == null) {
            initTagDialog();
            if (llBottomTag != null) {
                llBottomTag.setVisibility(View.GONE);
            }
            mImageViewTouch.drawCircle(x, y, true);
            mStickerDialog.show();
        } else {
            mImageViewTouch.addSticker(x, y, type);
        }
    }

    //点击了某一标签
    @Override
    public void onTagClick(float x, float y, Sticker sticker) {
        switch (sticker.getType()) {
            case TIPS:
                mTagDetailPopup.setContentText(sticker.getContent(), sticker);
                mTagDetailPopup.showPopup(mImageViewTouch, Gravity.NO_GRAVITY,
                        (int) x, (int) y - mTagDetailPopup.getHeight(),
                        sticker.getWidth(), sticker.getHeight());
                break;
            case IMAGE:
                break;

        }
    }

    @Override
    public void voicePlay(Sticker position) {

    }

    @Override
    public void onMoveOut(int position) {
        llDeleteFrame.setBackgroundColor(getResources().getColor(R.color.delete_sticker_color));
        tvHint.setText("松手即可删除");
    }

    @Override
    public void onMoving() {
        llDeleteFrame.setVisibility(View.VISIBLE);
        llDeleteFrame.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tvHint.setText("拖动到此处删除");
    }

    @Override
    public void onIdle() {
        llDeleteFrame.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onTouch() {
    }

    /**********************************TagLayout Listener End************************************/


    /****************************************Network*********************************************/
    /***
     * 放弃作业
     * @param force 是否显示提示框
     */

    private void giveUp(boolean force) {

    }

    /***
     * 批改超时
     * 自动放弃作业
     */
    private void outTime() {

    }

    /***
     * 退回作业
     */
    private void returnBack() {
    }


    //完成作业批改
    private void complete() {

    }

    //写评语
    private void addComment(String comment) {

    }

    private void getLastPic() {

    }

    /**************************************Network End*******************************************/
    private void initTagDialog() {
        if (mStickerDialog == null) {
            final AdvAlertDialog advAlertDialog = DialogUtils.buildStickerDialog(CheckingActivity.this, new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    mImageViewTouch.drawCircle(sx, sy, false);
                }
            }, new DialogUtils.OnStickerSelectedListener() {
                @Override
                public void correct() {
                    mImageViewTouch.addSticker(sx, sy, StickerType.CORRECT);
                }

                @Override
                public void wrong() {
                    mImageViewTouch.addSticker(sx, sy, StickerType.WRONG);
                }

                @Override
                public void tips(String content) {
                    mImageViewTouch.addTipsSticker(sx, sy, StickerType.TIPS, content);
                }

                @Override
                public void voice() {
                    mImageViewTouch.addVoiceSticker(sx, sy, StickerType.VOICE, "", 0);
                }

                @Override
                public void area() {

                }

                @Override
                public void doodle() {

                }
            });
            mStickerDialog = advAlertDialog.getAlertDialog();
        }
    }

    @Override
    protected void onDestroy() {
        mImageViewTouch.dispose();
        mImageViewTouch = null;
        super.onDestroy();
    }
}
