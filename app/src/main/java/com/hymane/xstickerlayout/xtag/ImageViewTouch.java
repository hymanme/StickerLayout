package com.hymane.xstickerlayout.xtag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ViewConfiguration;

import com.hymane.xstickerlayout.R;
import com.hymane.xstickerlayout.xtag.sticker.Sticker;
import com.hymane.xstickerlayout.xtag.sticker.StickerType;
import com.hymane.xstickerlayout.xtag.utils.DensityUtils;
import com.hymane.xstickerlayout.xtag.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class ImageViewTouch extends ImageViewTouchBase implements IStickerController {
    private static final String TAG = ImageViewTouch.class.getSimpleName();

    private static final int STATUS_DEFAULT = 0;//默认未触及屏幕状态
    private static final int STATUS_IDLE_CLICK = 1;//点击了非标签状态
    private static final int STATUS_TAG_CLICK = 2;//点击了标签
    private static final int STATUS_TAG_MOVING = 3;//移动标签位置
    private static final int STATUS_TAG_MOVED_OUT = 6;//正在移动标签，且标签已经移出控件上边界，松手删除该标签

    static final float SCROLL_DELTA_THRESHOLD = 1.0f;
    protected ScaleGestureDetector mScaleDetector;
    protected GestureDetector mGestureDetector;
    protected int mTouchSlop;
    protected float mScaleFactor;
    protected int mDoubleTapDirection;
    protected OnGestureListener mGestureListener;
    protected OnScaleGestureListener mScaleListener;
    protected boolean mDoubleTapEnabled = true;
    protected boolean mScaleEnabled = true;
    protected boolean mScrollEnabled = true;
    private OnImageViewTouchDoubleTapListener mDoubleTapListener;
    private OnImageViewTouchSingleTapListener mSingleTapListener;
    private OnZoomAnimationListener mZoomAnimationListener;
    private OnSickerLayoutListener mSickerLayoutListener;
    private int STATUS = STATUS_DEFAULT;

    /**************sticker*******************/
    private int TAG_CIRCLE_RADIUS = DensityUtils.dp2px(getContext(), 3);
    private Bitmap mStickerBitmap;
    private Bitmap mCorrectBitmap;
    private Bitmap mWrongBitmap;
    private Bitmap mTipsBitmap;
    private Bitmap mVoiceBitmap;

    private Paint mStickerPaint;
    private int viewWidth;
    private int viewHeight;
    private Canvas mStickerCanvas;//绘制标签的画布
    private ArrayList<Sticker> mStickers;//标签集合
    private Sticker mSelectedSticker;//当前选中的标签
    private float offsetY;//手指点击标签之后离标签中心点的Y轴偏移量
    private Paint mClearPaint;//清理画布画笔
    private boolean enable;//可用
    private boolean review;//预览模式

    /****************************************/

    public ImageViewTouch(Context context) {
        super(context);
    }

    public ImageViewTouch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageViewTouch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mGestureListener = getGestureListener();
        mScaleListener = getScaleListener();

        mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);
        mGestureDetector = new GestureDetector(getContext(), mGestureListener, null, true);

        mDoubleTapDirection = 1;
        setMaxScale(4);

        mStickerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStickerPaint.setDither(true);
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mStickers = new ArrayList<>();
    }

    public void setReviewModel(boolean review) {
        this.review = review;
    }

    public boolean getReviewModel() {
        return review;
    }


    public void setDoubleTapListener(OnImageViewTouchDoubleTapListener listener) {
        mDoubleTapListener = listener;
    }

    public void setSingleTapListener(OnImageViewTouchSingleTapListener listener) {
        mSingleTapListener = listener;
    }

    public void setDoubleTapEnabled(boolean value) {
        mDoubleTapEnabled = value;
    }

    public void setScaleEnabled(boolean value) {
        mScaleEnabled = value;
    }

    public void setScrollEnabled(boolean value) {
        mScrollEnabled = value;
    }

    public boolean getDoubleTapEnabled() {
        return mDoubleTapEnabled;
    }

    protected OnGestureListener getGestureListener() {
        return new GestureListener();
    }

    protected OnScaleGestureListener getScaleListener() {
        return new ScaleListener();
    }

    @Override
    protected void _setImageDrawable(final Drawable drawable, final Matrix initial_matrix,
                                     float min_zoom, float max_zoom) {
        super._setImageDrawable(drawable, initial_matrix, min_zoom, max_zoom);
        mScaleFactor = getMaxScale() / 3;
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        if (bitmap == null) {
            return;
        }
        initTools();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            //获取view的宽和高
            viewWidth = getWidth();
            viewHeight = getHeight();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mStickerCanvas == null && !mStickers.isEmpty()) {
            viewWidth = getWidth();
            viewHeight = getHeight();
        }
    }

    public void initTools() {
        viewWidth = getWidth();
        viewHeight = getHeight();
        if (viewWidth > 0 && viewHeight > 0 && mStickerBitmap == null) {
            mStickerBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
            mCorrectBitmap = UIUtils.getBitmap(getContext(), R.drawable.ic_check_correct);
            mWrongBitmap = UIUtils.getBitmap(getContext(), R.drawable.ic_check_wrong);
            mTipsBitmap = UIUtils.getBitmap(getContext(), R.drawable.ic_check_tips);
            mVoiceBitmap = UIUtils.getBitmap(getContext(), R.drawable.ic_check_voice3);
            mStickerCanvas = new Canvas(mStickerBitmap);
        }
    }

    public void setTouchEnabled(boolean enable) {
        this.enable = enable;
    }

    public boolean getTouchEnabled() {
        return this.enable;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mStickerBitmap != null) {
            //绘制标签层
            canvas.drawBitmap(mStickerBitmap, getDisplayMatrix(), mStickerPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (enable == false) {
            return false;
        }
        mScaleDetector.onTouchEvent(event);

        if (!mScaleDetector.isInProgress()) {
            mGestureDetector.onTouchEvent(event);
        }

        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                return onUp(event);
        }
        return true;
    }


    @Override
    protected void onZoomAnimationCompleted(float scale) {

        if (LOG_ENABLED) {
            Log.d(LOG_TAG, "onZoomAnimationCompleted. scale: " + scale + ", minZoom: "
                    + getMinScale());
        }

        if (scale < getMinScale()) {
            zoomTo(getMinScale(), 50);
        }
        if (mZoomAnimationListener != null) {
            mZoomAnimationListener.onZoomAnimEnd(scale);
        }
    }
    /**********************************************/
    /*******************手势事件********************/
    /**********************************************/
    public boolean onDown(MotionEvent e) {
        if (!review) {
            float x = (e.getX() - getTranslateX()) / getScale();
            float y = (e.getY() - getTranslateY()) / getScale();
            mSelectedSticker = checkTouchSticker(x, y);
        }
        if (mSelectedSticker == null) {
            STATUS = STATUS_IDLE_CLICK;
        } else {
            STATUS = STATUS_TAG_CLICK;
        }
        if (mSickerLayoutListener != null) {
            mSickerLayoutListener.onTouch();
        }
        return true;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (STATUS == STATUS_IDLE_CLICK) {
            //缩放背景图
            if (getScale() == 1f)
                return false;
            mUserScaled = true;
            scrollBy(-distanceX, -distanceY);
            invalidate();
        } else if (!review) {
            //移动单个标签
            if (mSelectedSticker == null) {
                return true;
            }
            mSelectedSticker.scrollBy(-distanceX / getScale(), -distanceY / getScale());
            if (mSickerLayoutListener != null) {
                if (e2.getY() - offsetY < 0) {
                    mSickerLayoutListener.onMoveOut(mStickers.indexOf(mSelectedSticker));
                    STATUS = STATUS_TAG_MOVED_OUT;
                } else {
                    mSickerLayoutListener.onMoving();
                    STATUS = STATUS_TAG_MOVING;
                }
            }
            refreshView();
        }
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();
        if (Math.abs(velocityX) > 800 || Math.abs(velocityY) > 800) {
            mUserScaled = true;
            scrollBy(diffX / 2, diffY / 2, 300);
            refreshView();
            return true;
        }
        return false;
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (STATUS == STATUS_IDLE_CLICK) {
            if (!review) {
//                final float rawY = e.getRawY();
//                final float y1 = e.getY();
//                final int top = getTop();
//                final float dis = rawY - y1;
//                final float translateY = getTranslateY();
//                final float scale = getScale();
//                final float ydes = y1 - translateY;

                final float x = (e.getX() - getTranslateX()) / getScale();
                final float y = (e.getY() - getTranslateY()) / getScale();
                if (mSickerLayoutListener != null) {
                    mSickerLayoutListener.onIdleClick(x, y, e.getX(), e.getY());
                }
            }
        } else {
            if (mSelectedSticker != null) {
                switch (mSelectedSticker.getType()) {
                    case TIPS:

                        break;
                    case VOICE:
                        break;
                }
                final float x = (e.getX() - getTranslateX()) / getScale();
                final float y = (e.getY() - getTranslateY()) / getScale();
                if (mSickerLayoutListener != null) {
                    mSickerLayoutListener.onTagClick(e.getRawX(), e.getRawY(), mSelectedSticker);
                }
            }
        }
        return true;
    }

    protected float onDoubleTapPost(float scale, float maxZoom) {
        if (mDoubleTapDirection == 1) {
            if ((scale + (mScaleFactor * 2)) <= maxZoom) {
                return scale + mScaleFactor;
            } else {
                mDoubleTapDirection = -1;
                return maxZoom;
            }
        } else {
            mDoubleTapDirection = 1;
            return 1f;
        }
    }

    public boolean onUp(MotionEvent e) {
        if (getScale() < getMinScale()) {
            zoomTo(getMinScale(), 50);
        }
        if (STATUS == STATUS_TAG_MOVED_OUT) {
            mStickers.remove(mSelectedSticker);
            refreshView();
        }
        if (mSickerLayoutListener != null) {
            mSickerLayoutListener.onIdle();
        }
//        STATUS = STATUS_DEFAULT;
        return true;
    }
    /************************End***************************/

    /**
     * Determines whether this ImageViewTouch can be scrolled.
     *
     * @param direction - positive direction value means scroll from right to left,
     *                  negative value means scroll from left to right
     * @return true if there is some more place to scroll, false - otherwise.
     */
    public boolean canScroll(int direction) {
        RectF bitmapRect = getBitmapRect();
        updateRect(bitmapRect, mScrollRect);
        Rect imageViewRect = new Rect();
        getGlobalVisibleRect(imageViewRect);

        if (null == bitmapRect) {
            return false;
        }

        if (bitmapRect.right >= imageViewRect.right) {
            if (direction < 0) {
                return Math.abs(bitmapRect.right - imageViewRect.right) > SCROLL_DELTA_THRESHOLD;
            }
        }

        double bitmapScrollRectDelta = Math.abs(bitmapRect.left - mScrollRect.left);
        return bitmapScrollRectDelta > SCROLL_DELTA_THRESHOLD;
    }

    /**
     * 设置缩放动画监听
     *
     * @param mZoomAnimationListener
     */
    public void setmZoomAnimationListener(OnZoomAnimationListener mZoomAnimationListener) {
        this.mZoomAnimationListener = mZoomAnimationListener;
    }

    /***
     *  设置标签状态监听
     * @param mSickerLayoutListener
     */
    public void setSickerLayoutListener(OnSickerLayoutListener mSickerLayoutListener) {
        this.mSickerLayoutListener = mSickerLayoutListener;
    }

    /**********************Sticker Controller****************************/
    @Override
    public void setPhotoBitmap(Bitmap photo) {
        mStickers.clear();
        refreshView();
        setImageBitmap(photo);
    }

    //***获取全局信息***//
    @Override
    public List<Sticker> getStickers() {
        return mStickers;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    @Override
    public void addSticker(float x, float y, StickerType type) {
        if (mStickerBitmap != null) {
            mStickers.add(new Sticker(x, y, type, mCorrectBitmap.getWidth()));
            refreshView();
        }
    }

    @Override
    public void addTipsSticker(float x, float y, StickerType type, String content) {
        if (mStickerBitmap != null) {
            mStickers.add(new Sticker(x, y, type, content, mCorrectBitmap.getWidth()));
            refreshView();
        }
    }

    @Override
    public void addVoiceSticker(float x, float y, StickerType type, String path, long second) {
        if (mStickerBitmap != null) {
        }
    }

    @Override
    public void addAreaSticker(float x, float y, StickerType type, int width, int height, String content) {
        if (mStickerBitmap != null) {
        }
    }


    @Override
    public void addTextSticker(float x, float y, StickerType type, int width, int height, String content) {
        if (mStickerBitmap != null) {
            mStickers.add(new Sticker(x, y, type, content, mCorrectBitmap.getWidth()));
            refreshView();
        }
    }

    @Override
    public void drawCircle(float x, float y, boolean show) {
        if (mStickerBitmap != null) {
            if (show) {
                mStickerPaint.setColor(Color.GRAY);
                mStickerCanvas.drawCircle(x, y, TAG_CIRCLE_RADIUS, mStickerPaint);
                invalidate();
            } else {
                refreshView();
            }
        }
    }

    @Override
    public void drawSticker(Sticker sticker) {
        switch (sticker.getType()) {
            case CORRECT:
                mStickerCanvas.drawBitmap(mCorrectBitmap, new Rect(0, 0, mCorrectBitmap.getWidth()
                        , mCorrectBitmap.getHeight()), Sticker.getRect(sticker), mStickerPaint);
                break;
            case WRONG:
                mStickerCanvas.drawBitmap(mWrongBitmap, new Rect(0, 0, mCorrectBitmap.getWidth()
                        , mCorrectBitmap.getHeight()), Sticker.getRect(sticker), mStickerPaint);
                break;
            case TIPS:
                mStickerCanvas.drawBitmap(mTipsBitmap, new Rect(0, 0, mCorrectBitmap.getWidth()
                        , mCorrectBitmap.getHeight()), Sticker.getRect(sticker), mStickerPaint);
                break;
            case VOICE:
                mStickerCanvas.drawBitmap(mVoiceBitmap, new Rect(0, 0, mCorrectBitmap.getWidth()
                        , mCorrectBitmap.getHeight()), Sticker.getRect(sticker), mStickerPaint);
            case AREA:
            case IMAGE:
//                mStickerPaint.setColor(0xff000000);
//                mStickerCanvas.drawText(sticker.getContent(), sticker.getX(), sticker.getY(), mStickerPaint);
                break;
            default:
                break;
        }
    }

    @Override
    public void refreshView() {
        if (mStickerCanvas != null) {
            mStickerCanvas.drawPaint(mClearPaint);
            for (Sticker sticker : mStickers) {
                drawSticker(sticker);
            }
        }
        invalidate();
    }

    @Override
    public void clearStickers() {
        if (mStickers != null) {
            mStickers.clear();
            refreshView();
        }
    }

    /***
     * GestureListener
     * 委托的手势监听
     */
    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (null != mSingleTapListener) {
                mSingleTapListener.onSingleTapConfirmed(e.getX(), e.getY());
            }

            return ImageViewTouch.this.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(LOG_TAG, "onDoubleTap. double tap enabled? " + mDoubleTapEnabled);
            if (mDoubleTapEnabled) {
                mUserScaled = true;
                float scale = getScale();
                float targetScale = scale;
                targetScale = onDoubleTapPost(scale, getMaxScale());
                targetScale = Math.min(getMaxScale(), Math.max(targetScale, getMinScale()));
                zoomTo(targetScale, e.getX(), e.getY(), DEFAULT_ANIMATION_DURATION);
                refreshView();
            }

            if (null != mDoubleTapListener) {
                mDoubleTapListener.onDoubleTap();
            }

            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (isLongClickable()) {
                if (!mScaleDetector.isInProgress()) {
                    setPressed(true);
                    performLongClick();
                }
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!mScrollEnabled)
                return false;
            if (e1 == null || e2 == null)
                return false;
            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
                return false;
            if (mScaleDetector.isInProgress())
                return false;
            return ImageViewTouch.this.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!mScrollEnabled)
                return false;
            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
                return false;
            if (mScaleDetector.isInProgress())
                return false;
            if (getScale() == 1f)
                return false;
            return ImageViewTouch.this.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return ImageViewTouch.this.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return ImageViewTouch.this.onDown(e);
        }
    }

    /***
     * 检查触摸的点是否含有标签
     * @param x 点x
     * @param y 点y
     * @return 选中的sticker，null：当前点不含标签
     */
    private Sticker checkTouchSticker(float x, float y) {
        for (Sticker sticker : mStickers) {
            final Rect rect = Sticker.getRect(sticker);
            if (rect.contains((int) x, (int) y)) {
                offsetY = (y - sticker.getY()) * getScale();
                return sticker;
            }
        }
        return null;
    }

    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        protected boolean mScaled = false;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float span = detector.getCurrentSpan() - detector.getPreviousSpan();
            float targetScale = getScale() * detector.getScaleFactor();

            if (mScaleEnabled) {
                if (mScaled && span != 0) {
                    mUserScaled = true;
                    targetScale = Math.min(getMaxScale(),
                            Math.max(targetScale, getMinScale() - 0.1f));
                    zoomTo(targetScale, detector.getFocusX(), detector.getFocusY());
                    mDoubleTapDirection = 1;
                    invalidate();
                    return true;
                }

                // This is to prevent a glitch the first time 
                // image is scaled.
                if (!mScaled)
                    mScaled = true;
            }
            return true;
        }

    }

    public interface OnImageViewTouchDoubleTapListener {

        void onDoubleTap();
    }

    public interface OnImageViewTouchSingleTapListener {

        void onSingleTapConfirmed(float x, float y);
    }

    /**
     * 缩放动画监听
     *
     * @author linjinfa 331710168@qq.com
     * @date 2014年6月11日
     */
    public interface OnZoomAnimationListener {
        void onZoomAnimEnd(float scale);
    }

    @Override
    public void dispose() {
        mStickers.clear();
        super.dispose();
    }
}