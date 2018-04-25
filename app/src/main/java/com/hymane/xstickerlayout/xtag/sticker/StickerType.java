package com.hymane.xstickerlayout.xtag.sticker;

/**
 * Author   :hymane
 * Email    :hymanme@163.com
 * Create at 2017-03-06
 * Description: 标签类型
 */
public enum StickerType {
    //标签类型:0、错误，1、正确，2、语音 3、疑问，4、区域大标签,5、板书涂鸦

    UNKNOWN(99)//未知
    , WRONG(0)//错误
    , CORRECT(1)//正确
    , TIPS(3)//文字提示
    , VOICE(2)//语音
    , AREA(4)//区域选区
    , IMAGE(5)//板书涂鸦
    ;

    private Integer value;

    StickerType() {
    }

    StickerType(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public StickerType setValue(Integer value) {
        this.value = value;
        return this;
    }
}