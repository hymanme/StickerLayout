package com.hymane.xstickerlayout.xtag;

import com.hymane.xstickerlayout.xtag.sticker.Sticker;

/**
 * Author   :hymane
 * Email    :hymanme@163.com
 * Create at 2017-03-08
 * Description:标签事件触发
 */
public interface OnSickerLayoutListener {
    void onIdleClick(float x, float y, float eventX, float eventY);

    void onTagClick(float x, float y, Sticker sticker);

    void voicePlay(Sticker position);

    void onMoveOut(int position);

    void onMoving();

    void onIdle();

    void onTouch();
}
