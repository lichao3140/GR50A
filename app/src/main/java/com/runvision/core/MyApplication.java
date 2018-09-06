/*
 * Copyright 2017, Tnno Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.runvision.core;


import java.lang.reflect.Method;
import java.util.ArrayList;

import com.runvision.bean.FaceStack;
import com.runvision.bean.MyMessage;
import com.runvision.bean.UnCeHandler;
import com.runvision.utils.LogToFile;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Tnno Wu on 2017/4/8.
 */

public class MyApplication extends Application {
    private static Context context;
    static ArrayList<Activity> list = new ArrayList<Activity>();

    public static MyFaceCore myFaceCore = new MyFaceCore();
    public static FaceStack imgStack = new FaceStack(Const.PRE_WIDTH, Const.PRE_HEIGTH);

    public static boolean init = false;
    /**
     * 用户最大数量
     */
    public final static int MAX_USER_NUMBER = 10000;

    //视频或图片人脸数据是否检测到
    public static boolean isCurrentReady = false;
    //身份证人脸数据是否检测到
    public static boolean isIdCardReady = false;

    public void init() {
        UnCeHandler catchExcep = new UnCeHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(catchExcep);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        String serlia = getSerialNumber();
        if (serlia.equals("") || serlia.length() < 4 || !serlia.substring(0, 4).equals("R50A")) {
            LogToFile.i("MyApplication", "没有SN序列号");
            finishActivity();
        }
        LogToFile.init(this);

        MyMessage msg = myFaceCore.initCore();
        if (msg.getCode() == 0) {
            init = true;
            Toast.makeText(this, msg.getMsg(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, msg.getMsg() + ",code=" + msg.getCode(), Toast.LENGTH_SHORT).show();
            init = false;
        }
    }

    public static Context getContext() {
        return context;
    }

    public void removeActivity(Activity a) {
        if (list.contains(a)) {
            list.remove(a);
            if (a != null) {
                a.finish();
            }
        }
    }

    public void addActivity(Activity a) {
        list.add(a);
    }

    public static void finishActivity() {
        for (Activity activity : list) {
            if (null != activity) {
                activity.finish();
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static String getVersionName(Context mContext) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        String version = packageInfo.versionName;
        return version;
    }

    public String getSerialNumber() {
        String serial = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");

        } catch (Exception e) {
            Log.i("error", e.getMessage());
        }
        return serial;
    }


}
