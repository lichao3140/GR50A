package com.runvision.thread;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.runvision.core.MyApplication;
import com.runvision.utils.LogToFile;
import com.runvision.utils.UpdateUpgrade;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.util.Log;

public class DownFileTask extends AsyncTask<Void, Integer, Void> {
    private String path;
    private ProgressDialog pro;
    private int flag;
    private Handler mHandler;
    private File file;
    private Activity activity;

    public DownFileTask(String path, ProgressDialog pro, int flag, Handler mHandler, Activity activity) {
        this.path = path;
        this.pro = pro;
        this.flag = flag;
        this.mHandler = mHandler;
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.i("sulin", "down apk");
        // TODO Auto-generated method stub
        InputStream is = null;

        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoInput(true);
            int sum = conn.getContentLength();

            is = conn.getInputStream();
            if (flag == 1) {
                file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FaceAndroid", "GR50A" + MyApplication.getVersionName(activity) + ".apk");

            }
            if (flag == 0) {
                file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FaceAndroid", "GR50A.png");
            }
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);
            bis = new BufferedInputStream(is);

            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;

                double a = (double) total / (double) sum;

                int b = (int) (100 * a);

                publishProgress(b);//
            }
            mHandler.sendEmptyMessage(998);

        } catch (Exception e) {
            Log.i("sulin", "down apk error");
            LogToFile.i("update", "down apk error");
            publishProgress(-1);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
        if (values[0] >= 0) {
            pro.setProgress(values[0]);
        } else if (values[0] == -1) {
            mHandler.sendEmptyMessage(999);
        } else if (values[0] == -2) {
            mHandler.sendEmptyMessage(998);
        }

    }

    @Override
    protected void onPostExecute(Void result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        // UpdateUpgrade up=new uP
        if (file != null) {
            installApk(file);
        }
        pro.dismiss();

    }


    private void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        activity.startActivityForResult(intent, 0);
        MyApplication.finishActivity();
    }


}
