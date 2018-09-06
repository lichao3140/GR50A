package com.runvision.thread;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


import com.arcsoft.facetracking.AFT_FSDKFace;

import com.arcsoft.liveness.FaceInfo;
import com.arcsoft.liveness.LivenessEngine;
import com.arcsoft.liveness.LivenessInfo;
import com.runvision.bean.FaceStack;
import com.runvision.bean.MyMessage;
import com.runvision.core.Const;
import com.runvision.core.FaceIDCardCompareLib;
import com.runvision.core.MyApplication;

import com.runvision.gr50a.MainActivity;
import com.runvision.gr50a.MainService;
import com.runvision.myview.CameraSurfaceView;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class PreviewTask extends AsyncTask<Void, Rect, Void> {


    @Override
    protected Void doInBackground(Void... arg0) {

        byte[] faceData = CameraSurfaceView.cameraData;

        MyMessage myMessage = MyApplication.myFaceCore.FaceDetection(faceData, Const.PRE_WIDTH, Const.PRE_HEIGTH, true);
        if (myMessage.getCode() == 0) {
            List<AFT_FSDKFace> result = (List<AFT_FSDKFace>) myMessage.getData();
            if (result.size() > 0) {
                // publishProgress(result.get(0).getRect());
                //活体检测
                List<FaceInfo> faceInfos = new ArrayList<>();
                faceInfos.add(new FaceInfo(result.get(0).getRect(), result.get(0).getDegree()));
                MyMessage message = MyApplication.myFaceCore.startLivenessDetect(faceData, Const.PRE_WIDTH, Const.PRE_HEIGTH, LivenessEngine.CP_PAF_NV21, faceInfos);
                if (message.getCode() != 0) {
                    publishProgress(new Rect(0, 0, 0, 0));
                    return null;
                }
                List<LivenessInfo> livenessInfos = (List<LivenessInfo>) message.getData();
                if (livenessInfos.size() == 0) {
                    publishProgress(new Rect(0, 0, 0, 0));
                    return null;
                }
                int liveness = livenessInfos.get(0).getLiveness();
                if (liveness != LivenessInfo.LIVE) {
                    publishProgress(new Rect(0, 0, 0, 0));
                    return null;
                }
                MyApplication.imgStack.pushImageInfo(faceData, System.currentTimeMillis(), result.get(0).getRect(), result.get(0).getDegree());
                publishProgress(result.get(0).getRect());
            } else {
                publishProgress(new Rect(0, 0, 0, 0));
            }
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(Rect... values) {
        super.onProgressUpdate(values);
        if (MainActivity.myFaceFrameView == null) {
            return;
        }
        Rect rect = values[0];
        if (rect.top == 0 && rect.left == 0 && rect.right == 0 && rect.bottom == 0) {
            MainActivity.myFaceFrameView.setVisibility(View.GONE);
        } else {
            MainActivity.myFaceFrameView.setVisibility(View.VISIBLE);
            MainActivity.myFaceFrameView.setFacePamaer(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
    }

}
