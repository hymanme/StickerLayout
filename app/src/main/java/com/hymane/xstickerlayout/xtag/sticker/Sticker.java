package com.hymane.xstickerlayout.xtag.sticker;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author   :hymane
 * Email    :hymanme@163.com
 * Create at 2017-02-22
 * Description: 标签实体
 */
public class Sticker implements Parcelable {
    private static Rect rect = new Rect();
    //坐标，比例
    private float x;
    private float y;
    //标签的宽和高
    private int width;
    private int height;

    //标签文本内容
    private String content;
    //标签类型:0、错误，1、正确，2、语音 3、疑问，4、区域大标签
    private StickerType type;
    //是否是选中状态
    private boolean isSelected;
    //语音的url
    private String voicePath;
    //语音时长，单位秒
    private long voiceLength;
    //语音是否正在播放
    private boolean isPlay;

    public Sticker() {
    }

    //正确，错误，疑问标签
    public Sticker(float x, float y, StickerType type, int size) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.voiceLength = 0;
        this.width = size;
        this.height = size;
    }

    //带内容的普通标签（保留）
    public Sticker(float x, float y, StickerType type, String content, int size) {
        this(x, y, type, size);
        this.content = content;
    }

    //语音标签
    public Sticker(float x, float y, StickerType type, String path, long second, int size) {
        this(x, y, type, size);
        this.voicePath = path;
        this.voiceLength = second;
    }

    //区域大标签,文本标签，划区域，且区域可改变大小
    public Sticker(float x, float y, StickerType type, int width, int height, String content) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.voiceLength = 0;
        this.width = width;
        this.height = height;
        this.content = content;
    }

    /***
     * 相对移动
     * @param x x移动量
     * @param y y移动量
     */
    public void scrollBy(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public int getCenterSizeX() {
        return width / 2;
    }

    public int getCenterSizeY() {
        return height / 2;
    }

    public static Rect getRect(Sticker sticker) {
        rect.set((int) (sticker.getX() - sticker.getCenterSizeX())
                , (int) (sticker.getY() - sticker.getCenterSizeY())
                , (int) (sticker.getX() + sticker.getCenterSizeX())
                , (int) (sticker.getY() + sticker.getCenterSizeY()));
        return rect;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public StickerType getType() {
        return type;
    }

    public void setType(StickerType type) {
        this.type = type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }

    public long getVoiceLength() {
        return voiceLength;
    }

    public void setVoiceLength(long voiceLength) {
        this.voiceLength = voiceLength;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    protected Sticker(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
        width = in.readInt();
        height = in.readInt();
        content = in.readString();
        isSelected = in.readByte() != 0;
        voicePath = in.readString();
        voiceLength = in.readLong();
        isPlay = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(content);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeString(voicePath);
        dest.writeLong(voiceLength);
        dest.writeByte((byte) (isPlay ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Sticker> CREATOR = new Creator<Sticker>() {
        @Override
        public Sticker createFromParcel(Parcel in) {
            return new Sticker(in);
        }

        @Override
        public Sticker[] newArray(int size) {
            return new Sticker[size];
        }
    };
}
