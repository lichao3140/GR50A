package com.runvision.core;

import android.graphics.Rect;
//
//import com.arcsoft.facedetection.AFD_FSDKEngine;
//import com.arcsoft.facedetection.AFD_FSDKError;
//import com.arcsoft.facedetection.AFD_FSDKFace;
//import com.arcsoft.facerecognition.AFR_FSDKEngine;
//import com.arcsoft.facerecognition.AFR_FSDKError;
//import com.arcsoft.facerecognition.AFR_FSDKFace;
//import com.arcsoft.facerecognition.AFR_FSDKMatching;
//import com.arcsoft.facetracking.AFT_FSDKEngine;
//import com.arcsoft.facetracking.AFT_FSDKError;
//import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.liveness.ErrorInfo;
import com.arcsoft.liveness.FaceInfo;
import com.arcsoft.liveness.LivenessEngine;
import com.arcsoft.liveness.LivenessInfo;
import com.runvision.bean.MyMessage;
import com.runvision.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;

public class MyFaceCore {
    private byte[] a = new byte[1];
    private byte[] b = new byte[1];
    private byte[] c = new byte[1];
    private byte[] d = new byte[1];
    private AFD_FSDKEngine engine_fd = null;
    private AFR_FSDKEngine engine_fr = null;
    private AFT_FSDKEngine engine_ft = null;
    private LivenessEngine engine_live = null;
    /**
     * 活体部分 密钥
     */
    private String Live_ID = "J3Yscp63XC1M1ut6Fk6DguUGGqdGZocc2c8QK2GfnzTt";
    private String Live_KEY = "9LQgVXRmKTXtph3pbzibo9mCErzEC5Gg8yjX3JyU79FP";
    /**
     * 其他密钥
     */
    private String APP_ID = "J3Yscp63XC1M1ut6Fk6DguTmdEaYzya4tb3teTZZZbAp";
    private String FD_KEY = "5VkbWAVASfvjVKGnnkdzu5n6xQgpjRKT2Acr8dzgsqzE";
    private String FR_KEY = "5VkbWAVASfvjVKGnnkdzu5nE7ox2Ve87ZxV3sKToKpFR";
    private String FT_KEY = "5VkbWAVASfvjVKGnnkdzu5myo1RheXt6ToyELTBqvvgu";


    /**
     * 初始化算法
     */
    public MyMessage initCore() {
        //先初始化FD引擎
        if (engine_fd == null) {
            engine_fd = new AFD_FSDKEngine();
        }
        AFD_FSDKError fd_error = engine_fd.AFD_FSDK_InitialFaceEngine(APP_ID, FD_KEY, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 1);
        if (fd_error.getCode() != AFD_FSDKError.MOK) {
            return new MyMessage(fd_error.getCode(), "FD引擎初始化失败");
        }
        if (engine_fr == null) {
            engine_fr = new AFR_FSDKEngine();
        }
        AFR_FSDKError fr_error = engine_fr.AFR_FSDK_InitialEngine(APP_ID, FR_KEY);
        if (fr_error.getCode() != AFR_FSDKError.MOK) {
            return new MyMessage(fr_error.getCode(), "FR引擎初始化失败");
        }
        if (engine_ft == null) {
            engine_ft = new AFT_FSDKEngine();
        }
        AFT_FSDKError ft_error = engine_ft.AFT_FSDK_InitialFaceEngine(APP_ID, FT_KEY, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 1);
        if (ft_error.getCode() != AFT_FSDKError.MOK) {
            return new MyMessage(ft_error.getCode(), "FT引擎初始化失败");
        }
        if (engine_live == null) {
            engine_live = new LivenessEngine();
        }
        if (SPUtil.getBoolean(Const.KEY_LIVE_ENABLE, Const.LIVE_ENABLE) == false) {
            ErrorInfo info = engine_live.activeEngine(Live_ID, Live_KEY);
            if (info.getCode() == ErrorInfo.MOK) {
                SPUtil.putBoolean(Const.KEY_LIVE_ENABLE, true);
            }
        }
        ErrorInfo errorInfo = engine_live.initEngine(LivenessEngine.AL_DETECT_MODE_VIDEO);
        if (errorInfo.getCode() != ErrorInfo.MOK) {
            return new MyMessage((int) errorInfo.getCode(), "活体引擎初始化失败");
        }
        return new MyMessage(0, "算法初始化成功");
    }

    /**
     * 人脸定位接口
     *
     * @param des  NV21的视频流
     * @param w    流的宽度
     * @param h    流的高度
     * @param type 流的类型  true代表视频流,false代表身份证图片
     * @return MyMessage
     */
    public MyMessage FaceDetection(byte[] des, int w, int h, boolean type) {
        if (type) {
            // 用来存放检测到的人脸信息列表
            List<AFT_FSDKFace> result = new ArrayList<>();
            if (engine_ft == null) {
                return new MyMessage(-1, "引擎未初始化");
            }
            AFT_FSDKError err = null;
            synchronized (b) {
                err = engine_ft.AFT_FSDK_FaceFeatureDetect(des, w, h, AFT_FSDKEngine.CP_PAF_NV21, result);
            }
            return err.getCode() == 0 ? new MyMessage(err.getCode(), "success", result) : new MyMessage(err.getCode(), "检测人脸失败", result);
        } else {

            List<AFD_FSDKFace> result = new ArrayList<>();
            if (engine_fd == null) {
                return new MyMessage(-1, "引擎未初始化");
            }
            AFD_FSDKError err = null;
            synchronized (a) {
                err = engine_fd.AFD_FSDK_StillImageFaceDetection(des, w, h, AFD_FSDKEngine.CP_PAF_NV21, result);
            }
            return err.getCode() == 0 ? new MyMessage(err.getCode(), "success", result) : new MyMessage(err.getCode(), "检测人脸失败", result);
        }
    }


    /**
     * 人脸特征提取
     *
     * @param des    NV21的流
     * @param w
     * @param h
     * @param rect   坐标
     * @param degree 角度
     * @return
     */
    public MyMessage FaceFeature(byte[] des, int w, int h, Rect rect, int degree) {
        if (engine_fr == null) {
            return new MyMessage(-1, "引擎未初始化");
        }
        AFR_FSDKError err = null;
        AFR_FSDKFace face = new AFR_FSDKFace();
        synchronized (c) {
            err = engine_fr.AFR_FSDK_ExtractFRFeature(des, w, h, AFR_FSDKEngine.CP_PAF_NV21, rect, degree, face);
        }
        return err.getCode() == 0 ? new MyMessage(err.getCode(), "success", face) : new MyMessage(err.getCode(), "人脸特征提取失败", face);
    }

    /**
     * 人脸比对
     *
     * @param face1 人脸特征一
     * @param face2 人脸特征二
     * @return
     */
    public MyMessage FacePairMatching(AFR_FSDKFace face1, AFR_FSDKFace face2) {
        if (engine_fr == null) {
            return new MyMessage(-1, "引擎未初始化");
        }
        AFR_FSDKMatching score = new AFR_FSDKMatching();
        AFR_FSDKError err = null;
        synchronized (c) {
            err = engine_fr.AFR_FSDK_FacePairMatching(face1, face2, score);
        }
        return err.getCode() == 0 ? new MyMessage(err.getCode(), "success", score) : new MyMessage(err.getCode(), "人脸特征提取失败", score);
    }


    /**
     * 活体检测
     *
     * @param data
     * @param width
     * @param height
     * @param format    流的格式 LivenessEngine.CP_PAF_NV21
     * @param faceInfos 人脸信息
     * @return
     */
    public MyMessage startLivenessDetect(byte[] data, int width, int height, int format, List<FaceInfo> faceInfos) {
        if (engine_live == null) {
            return new MyMessage(-1, "引擎未初始化");
        }
        List<LivenessInfo> livenessInfos = new ArrayList<>();
        ErrorInfo info = null;
        synchronized (d) {
            info = engine_live.startLivenessDetect(data, width, height, format, faceInfos, livenessInfos);
        }
        return info.getCode() == 0 ? new MyMessage((int) info.getCode(), "success", livenessInfos) : new MyMessage((int) info.getCode(), "活体检测失败", livenessInfos);
    }


    /**
     * 销毁引擎
     */
    public void AFR_FSDK_UninitialEngine() {

        if (engine_fd != null) {
            engine_fd.AFD_FSDK_UninitialFaceEngine();
            engine_fd = null;
        }
        if (engine_fr != null) {
            engine_fr.AFR_FSDK_UninitialEngine();
            engine_fr = null;
        }
        if (engine_ft != null) {
            engine_ft.AFT_FSDK_UninitialFaceEngine();
            engine_ft = null;
        }
        if (engine_live != null) {
            engine_live.unInitEngine();
            engine_live = null;
        }

    }

}
