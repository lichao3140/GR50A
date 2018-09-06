package com.runvision.myview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.runvision.bean.FaceStack;
import com.runvision.core.Const;

import com.runvision.core.MyApplication;
import com.runvision.thread.PreviewTask;
import com.runvision.utils.LogToFile;

/**
 * Created by dyk on 2016/4/6.
 */
public class CameraSurfaceView extends SurfaceView implements
        SurfaceHolder.Callback, PreviewCallback {

    private static final String TAG = "CameraSurfaceView";
    private PreviewTask task = null;
    public static ExecutorService exec = Executors.newFixedThreadPool(10);
    private Context mContext;
    private SurfaceHolder holder;
    private Camera mCamera;
    private Camera.Parameters parameters;
    private int mScreenWidth;
    private int mScreenHeight;
    private boolean cameraStatus = false;



    public static byte[] cameraData = null;

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs,
                             int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        holder = getHolder();
        holder.addCallback(this);
        this.setLayoutParams(new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        getScreenMetrix(context);
    }


    @SuppressLint("NewApi")
    public void openCamera() {
        if (cameraStatus) {
            return;
        }
        releaseCamera();
        if (mCamera == null) {
            if (Camera.getNumberOfCameras() >= 1) {
                try {
                    mCamera = Camera.open(1);
                } catch (Exception e) {
                    mCamera = null;
                    Toast.makeText(mContext, "打开相机失败", Toast.LENGTH_SHORT).show();
                    LogToFile.i(TAG, "打开相机失败");
                }

                initCamera();

            } else {
                Log.d(TAG, "dsasad");
                Toast.makeText(mContext, "相机不是双目的", Toast.LENGTH_SHORT).show();
                LogToFile.i(TAG, "相机不是双目的");
            }
        }
    }

    public void initCamera() {

        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(this);

                if (parameters == null) {
                    parameters = mCamera.getParameters();
                }
                parameters.setPreviewFormat(ImageFormat.NV21);
                parameters.setPictureSize(640, 480);
                mCamera.setParameters(parameters);
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
                cameraStatus = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public synchronized void releaseCamera() {
        if (!cameraStatus) {
            return;
        }
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCamera = null;
        }


        cameraStatus = false;
    }

    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
        Log.i(TAG, "屏幕分辨率：" + mScreenWidth + "*" + mScreenHeight);

    }

    @SuppressLint("NewApi")
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        //openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.i(TAG, "surfaceChanged");
        initCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        releaseCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // TODO Auto-generated method stub
        cameraData = data;
        if (cameraData == null) {
            return;
        }
//        if (!MyApplication.init) {
//            return;
//        }
        if (task != null) {
            switch (task.getStatus()) {
                case RUNNING:
                    return;
                case PENDING:
                    task.cancel(false);
                    break;
                default:
                    break;
            }
        }

        task = new PreviewTask();
        task.executeOnExecutor(exec);
    }

}
