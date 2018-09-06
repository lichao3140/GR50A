package com.runvision.bean;

import android.graphics.Rect;

/**
 * Created by Administrator on 2018/8/3.
 */

public class FaceStack {
    private boolean isReading = false;
    private FaceInfo imageOne = null;
    private FaceInfo imageTwo = null;

    public FaceStack(int width, int height) {
        imageOne = new FaceInfo(width, height);
        imageTwo = new FaceInfo(width, height);
    }

    /**
     * 出堆
     *
     * @return
     */
    public FaceInfo pullImageInfo() {
        //log("pullImageInfo() isReading:" + isReading);
        isReading = true;
        if (imageOne.isNew()) {
            imageTwo.setData(imageOne.getData());
            imageTwo.setTime(imageOne.getTime());
            imageTwo.setRect(imageOne.getRect());
            imageTwo.setmDegree(imageOne.getmDegree());
            imageTwo.setNew(true);
            imageOne.setNew(false);
        } else {
            imageTwo.setNew(false);
        }
        isReading = false;
        return imageTwo;
    }

    /**
     * 入堆
     *
     * @param
     */
    public void pushImageInfo(byte[] imgData, long time, Rect rect, int gree) {
        if (!isReading) {
            imageOne.setData(imgData);
            imageOne.setTime(time);
            imageOne.setNew(true);
            imageOne.setRect(rect);
            imageOne.setmDegree(gree);
        }
    }

    public void clearAll() {
        imageOne.setNew(false);
        imageTwo.setNew(false);
    }
}
