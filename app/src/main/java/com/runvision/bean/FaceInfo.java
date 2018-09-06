package com.runvision.bean;

import android.graphics.Rect;

/**
 * Created by Administrator on 2018/8/3.
 */

public class FaceInfo {
    private int size = 0;
    private int width;
    private int height;
    private byte[] data;
    private long time;
    private boolean isNew;
    private Rect rect;
    private int mDegree;

    public FaceInfo(int width, int height) {
        this.width = width;
        this.height = height;
        size = width * height * 3 / 2;
        data = new byte[size];
        isNew = false;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public int getmDegree() {
        return mDegree;
    }

    public void setmDegree(int mDegree) {
        this.mDegree = mDegree;
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
