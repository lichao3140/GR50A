package com.runvision.core;

public class Const {
    /**
     * 活体引擎激活状态
     */
    public static final boolean LIVE_ENABLE = false;
    /**
     * 活体引擎对应的SP 的KEY
     */
    public static final String KEY_LIVE_ENABLE = "activationLive";

    /**
     * FACE_SCORE 人脸比分阈值
     */
    public static final float FACE_SCORE = 0.5f;

    /**
     * 相机流的高
     */
    public static final int PRE_HEIGTH = 480;
    /**
     * 相机流的宽
     */
    public static final int PRE_WIDTH = 640;
    /**
     * 最大比对次数
     */
    public static final int MAX_COMPER_NUM = 50;

    public final static int CARD_WIDTH = 102;//身份证图片的宽度

    public final static int CARD_HEIGTH = 126;//身份证图片的高度

    public static boolean canRead = true;

    public static Boolean ReturnOrSend = false;


    public final static int UPDATE_FLAG = 100;// 更新
    public final static int JUMP_MAIN_FLAG = 101;// 进入主界面

    public final static int FLAG_CLEAN = 0;// 复原
    public final static int COMPER_FINISH_FLAG = 1;// 人证比对完成 做弹框 并向上位机发送信息
    public final static int CARNMBER_MSG_FLAG = 2;// 收到车牌信息
    public final static int SHOW_QRCODE = 3;// 没有车牌 显示二维码
    public final static int UPDATE_SETTING = 4;// 修改设备参数
    public final static int COMPER_RESULT = 5;// 比对结果返回
    public final static int HEARTBEAT = 6;// 心跳
    public final static int SEND_FACEBMP = 7;
    public final static int SEND_CARDBMP = 8;// 发送图片
    public final static int CLEAR_COMPER = 9;// 结束本次比对
    public final static int READ_USER_CARD = 10;

    public final static int CLOSE_CODE = 11;


}
