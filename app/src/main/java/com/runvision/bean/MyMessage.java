package com.runvision.bean;

public class MyMessage<T> {
    private int code;
    private String msg;
    private T data;

    public MyMessage() {
    }

    public MyMessage(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public MyMessage(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
