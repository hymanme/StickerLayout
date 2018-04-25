package com.hymane.xstickerlayout.xtag;

import android.graphics.Bitmap;

import com.hymane.xstickerlayout.xtag.sticker.Sticker;
import com.hymane.xstickerlayout.xtag.sticker.StickerType;

import java.util.List;

/**
 * Author   :hymane
 * Email    :hymanme@163.com
 * Create at 2017-12-07
 * Description:
 */
public interface IStickerController {
    void setPhotoBitmap(Bitmap photo);

    List<Sticker> getStickers();

    /***
     * 简单标签（正确，错误）
     * @param x
     * @param y
     * @param type
     */
    void addSticker(float x, float y, StickerType type);

    /***
     * 小提示文本标签
     * @param x
     * @param y
     * @param type
     * @param content
     */
    void addTipsSticker(float x, float y, StickerType type, String content);

    /***
     * 语音标签
     * @param x
     * @param y
     * @param type
     * @param path
     * @param second
     */
    void addVoiceSticker(float x, float y, StickerType type, String path, long second);

    /***
     * 区域标签
     * @param x
     * @param y
     * @param type
     * @param width
     * @param height
     * @param content
     */
    void addAreaSticker(float x, float y, StickerType type, int width, int height, String content);

    /***
     * 文本标签
     * @param x
     * @param y
     * @param type
     * @param width
     * @param height
     * @param content
     */
    void addTextSticker(float x, float y, StickerType type, int width, int height, String content);

    /***
     * 标记触点
     * @param x
     * @param y
     * @param show
     */
    void drawCircle(float x, float y, boolean show);

    /***
     * 绘制指定标签
     * @param sticker
     */
    void drawSticker(Sticker sticker);

    void refreshView();

    void clearStickers();
}
