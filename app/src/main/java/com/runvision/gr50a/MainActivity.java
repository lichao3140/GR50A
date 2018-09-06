package com.runvision.gr50a;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.runvision.bean.AppData;
import com.runvision.core.Const;
import com.runvision.core.MyApplication;
import com.runvision.core.SocketService;

import com.runvision.myview.CameraSurfaceView;
import com.runvision.myview.FaceFrameView;
import com.runvision.utils.LogToFile;
import com.runvision.utils.QRCodeUtil;
import com.runvision.utils.SPUtil;
import com.runvision.utils.UploadUtil;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android_serialport_api.SerialPort;

public class MainActivity extends Activity {
    // ------------Home页面（待机页面UI�?????------------
    private View home_xml;
    private ImageView home_bg;
    private TextView home_msg, home_msg_w;
    // -------------Index页面（首页UI�?????---------------
    private View index_xml;
    private TextView showCardNumber, hintMsg, hintMsgW;
    private RelativeLayout loading_layout, showCardNumber_layout,
            showMsg_layout;
    // ---------------comper页面（比对信息页面UI�?????---------
    private View comper_xml;
    private ImageView card_bmp, face_bmp, comper_icon;
    private TextView card_name, card_sex, card_birthday, card_addr,
            face_text_result, face_text_result_w;
    private LinearLayout face_bgColor;
    // ----------------code页面（二维码页面UI�?????---------------------------
    private View code_xml;
    private ImageView code_bmp;
    private TextView code_showMsg, code_showMsg_w;
    // -------------------------------------------
    private Context mContext;
    public static boolean isInitSdk = false;
    public static FaceFrameView myFaceFrameView;
    public static CameraSurfaceView myCameraView;
    private SocketService mSocketService = null;
    private boolean flag = true;
    private String TAG = "MainActivity";
    private Thread trd1, trd;
    private MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        // 横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 全屏代码
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 隐藏下面的虚拟按�?????
        hideBottomUIMenu();
        initView();
        startService(new Intent(MainActivity.this, MainService.class));
        // �?????启socketService
        openSocketServie();
        // �?????启监听flag的线�?????
        updateView();

        application = (MyApplication) getApplication();
        application.init();
        application.addActivity(this);


    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Const.COMPER_FINISH_FLAG:
                    Log.i(TAG, "--比对完成-");
                    LogToFile.i("MainActivity", "收到传来比对完成的通知");
                    try {
                        Const.ReturnOrSend = false;
                        AppData.getAppData().setMethodId("8005");
                        mSocketService.mOutputStream();
                        mSocketService.mOutputStream();
                        handler.sendEmptyMessage(Const.SEND_FACEBMP);
                    } catch (Exception e) {
                        // TODO: handle exception
                        Log.i(TAG, "out error");
                    }
                    // 比对完成
                    break;
                case Const.SHOW_QRCODE:
                    // 显示二维�?????
                    LogToFile.i("MainActivity", "收到需要显示二维通知");
                    showQRcode();
                    break;
                case Const.CARNMBER_MSG_FLAG:
                    // 接收到传来的车牌号码
                    LogToFile.i("MainActivity", "接收到传来的车牌号码");
                    Log.i("MainActivity", "接收到传来的车牌号码");
                    myCameraView.openCamera();
                    handler.removeCallbacks(myTime);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    goToComper();
                    break;
                case Const.UPDATE_SETTING:
                    // 修改参数（IP�?????
                    LogToFile.i("MainActivity", "初始化参数");
                    updateSetting();
                    break;
                case Const.COMPER_RESULT:
                    // 收到比对结果
                    Log.i(TAG, "收到比对结果");
                    LogToFile.i("MainActivity", "收到比对结果");
                    MainService.isReadCard = false;
                    Const.canRead = true;
                    if (AppData.getAppData().getCompareResult_1() == 1) {
                        showComperResultDialog(true);
                    } else if (AppData.getAppData().getCompareResult_1() == 0) {

                        showComperResultDialog(false);
                    }

                    break;
                case Const.CLEAR_COMPER:
                    // 关闭本次比对
                    LogToFile.i("MainActivity", "关闭本次比对");
                    index_xml.setVisibility(View.GONE);
                    comper_xml.setVisibility(View.GONE);
                    code_xml.setVisibility(View.GONE);
                    home_xml.setVisibility(View.VISIBLE);
                    MainService.isReadCard = false;
                    Const.canRead = true;
                    myCameraView.releaseCamera();

                    showCardNumber_layout.setVisibility(View.VISIBLE);
                    showMsg_layout.setVisibility(View.VISIBLE);

                    loading_layout.setVisibility(View.GONE);
                    break;
                case Const.HEARTBEAT:
                    // 心跳
                    mSocketService.mOutputStream();
                    break;
                case Const.SEND_FACEBMP:
                    // 发�?�face照片
                    LogToFile.i("MainActivity", "发face照片");
                    sendFaceBmp();
                    break;
                case Const.SEND_CARDBMP:
                    // 发�?�身份证照片
                    LogToFile.i("MainActivity", "发card照片");
                    sendCardBmp();
                    break;
                case Const.READ_USER_CARD:
                    // 用户刷了身份�?????
                    LogToFile.i("MainActivity", "检测到用户刷了身份");
                    showCardNumber_layout.setVisibility(View.GONE);
                    showMsg_layout.setVisibility(View.GONE);

                    loading_layout.setVisibility(View.VISIBLE);
                    // 发�?�face照片
                    sendCardBmp();
                    break;
                case Const.CLOSE_CODE:
                    Log.i(TAG, "返回待机页面");
                    //LogToFile.i("MainActivity", "返回待机页面");
                    index_xml.setVisibility(View.GONE);
                    comper_xml.setVisibility(View.GONE);
                    code_xml.setVisibility(View.GONE);
                    home_xml.setVisibility(View.VISIBLE);

                    showCardNumber_layout.setVisibility(View.VISIBLE);
                    showMsg_layout.setVisibility(View.VISIBLE);

                    loading_layout.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    private void initView() {
        // ----------------------------------
        home_xml = findViewById(R.id.home_xml);
        home_bg = (ImageView) home_xml.findViewById(R.id.img_home_bg);
        home_msg = (TextView) home_xml.findViewById(R.id.home_msg1);
        home_msg_w = (TextView) home_xml.findViewById(R.id.home_msg1_w);

        if (!SPUtil.getString("PortalSysName", "").equals("")) {
            home_msg.setText(SPUtil.getString("PortalSysName", ""));
        }
        if (!SPUtil.getString("PortalUyghurSysName", "").equals("")) {
            if (SPUtil.getString("PortalUyghurSysName", "").length() == 10) {
                home_msg_w.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20);
            }
            home_msg_w.setText(SPUtil.getString("PortalUyghurSysName", ""));
        }
        try {
            File file = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/FaceAndroid", "GR50A.png");
            if (file.exists()) {
                Bitmap bmp = BitmapFactory.decodeFile(Environment
                        .getExternalStorageDirectory().getAbsolutePath()
                        + "/FaceAndroid/GR50A.png");
                home_bg.setImageBitmap(bmp);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.i(TAG, "set bg error");
            LogToFile.i("MainActivity", "load home bg error");
            home_bg.setImageResource(R.mipmap.home_bg);
        } finally {
            // -----------------------------------
            index_xml = findViewById(R.id.index_xml);
            index_xml.setVisibility(View.GONE);
            showCardNumber = (TextView) index_xml.findViewById(R.id.cardNumber);
            hintMsg = (TextView) index_xml.findViewById(R.id.showMsg);
            hintMsgW = (TextView) index_xml.findViewById(R.id.hintMsgW);
            loading_layout = (RelativeLayout) index_xml
                    .findViewById(R.id.loading_layout);
            showCardNumber_layout = (RelativeLayout) index_xml
                    .findViewById(R.id.showCardNumber_layout);
            showMsg_layout = (RelativeLayout) index_xml
                    .findViewById(R.id.showMsg_layout);
            myFaceFrameView = (FaceFrameView) index_xml
                    .findViewById(R.id.myFaceFrameView);
            myCameraView = (CameraSurfaceView) index_xml
                    .findViewById(R.id.myCameraSurfaceView);
            // myCameraView.setVisibility(View.VISIBLE);
            // index_xml.setVisibility(View.VISIBLE);
            // index_xml.setVisibility(View.GONE);
            // -------------------------------------
            comper_xml = findViewById(R.id.comper_xml);
            comper_xml.setVisibility(View.GONE);
            card_bmp = (ImageView) comper_xml.findViewById(R.id.card_Bmp);
            face_bmp = (ImageView) comper_xml.findViewById(R.id.face_bmp);
            comper_icon = (ImageView) comper_xml.findViewById(R.id.comper_icon);
            card_name = (TextView) comper_xml.findViewById(R.id.card_name);
            card_sex = (TextView) comper_xml.findViewById(R.id.card_sex);
            card_birthday = (TextView) comper_xml.findViewById(R.id.card_birthday);
            card_addr = (TextView) comper_xml.findViewById(R.id.card_addr);
            face_text_result = (TextView) comper_xml
                    .findViewById(R.id.face_text_result);
            face_text_result_w = (TextView) comper_xml
                    .findViewById(R.id.face_text_result_w);
            face_bgColor = (LinearLayout) comper_xml
                    .findViewById(R.id.face_bgColor);
            // --------------------------------------
            code_xml = findViewById(R.id.code_xml);
            code_xml.setVisibility(View.GONE);
            code_bmp = (ImageView) code_xml.findViewById(R.id.code_bmp);
            code_showMsg = (TextView) code_xml.findViewById(R.id.code_showMsg);
            code_showMsg_w = (TextView) code_xml.findViewById(R.id.code_showMsg_w);
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        hideBottomUIMenu();
    }

    /**
     * 隐藏虚拟按键，并且全�?????
     */
    @SuppressLint("NewApi")
    protected void hideBottomUIMenu() {
        // 隐藏虚拟按键，并且全�?????
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            // for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void openSocketServie() {
        if (mSocketService != null) {
            try {
                mSocketService.stopServerAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocketService = null;
        }
        mSocketService = new SocketService(9001, handler);
        mSocketService.startServerAsync();
    }

    public void updateView() {
        new Thread(new MyRunnable()).start();

    }

    // 修改设置（IP�?????
    private void updateSetting() {

        if (mSocketService != null) {
            try {
                mSocketService.stopServerAsync();
                mSocketService = null;
            } catch (IOException e) {
                mSocketService = null;
            }
        }

        Log.i("aa", AppData.getAppData().getIP());
        String[] Sip = AppData.getAppData().getIP().split("\\.");

        String str = Sip[0] + "." + Sip[1] + "." + Sip[2] + ".1";
        Log.i("aa", str + "," + AppData.getAppData().getIP());

        String[] staticIP = new String[]{AppData.getAppData().getIP(),
                "255.255.255.0", str, "8.8.8.8"};
        Intent closeIntent = new Intent("com.snstar.networkparameters.ETH_CLOSE");
        sendBroadcast(closeIntent);

        Intent i = new Intent("com.snstar.networkparameters.ETHSETINGS");
        Bundle bundle = new Bundle();
        bundle.putSerializable("STATIC_IP", staticIP);
        i.putExtras(bundle);
        sendBroadcast(i);
        Intent iopen = new Intent("com.snstar.networkparameters.ETH_OPEN");
        sendBroadcast(iopen);

        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        mSocketService = new SocketService(AppData.getAppData().getPort());
//        mSocketService.startServerAsync();
//        ((Activity) mContext).finish();
        finish();

        Intent intent = new Intent(application.getApplicationContext(),
                UpdateActivity.class);
        @SuppressLint("WrongConstant")
        PendingIntent restartIntent = PendingIntent.getActivity(application.getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

        AlarmManager mgr = (AlarmManager) application
                .getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                restartIntent);


    }

    // 显示二维�?????
    private void showQRcode() {
        Bitmap code = QRCodeUtil.createQRImage(
                AppData.getAppData().getQrcode(), 500, 500);
        code_bmp.setImageBitmap(code);

        code_showMsg.setText(AppData.getAppData().getDisplayContent());
        code_showMsg_w.setText(AppData.getAppData().getUyghurDisplayContent());

        index_xml.setVisibility(View.GONE);
        comper_xml.setVisibility(View.GONE);
        code_xml.setVisibility(View.VISIBLE);
        home_xml.setVisibility(View.GONE);
    }

    private void goToComper() {
        Log.i(TAG, "进入比对 已经收到车牌");
        MainService.isReadCard = true;//

        Const.ReturnOrSend = true;


        showCardNumber.setText(AppData.getAppData().getPlate());
        hintMsg.setText(AppData.getAppData().getDisplayContent());
        hintMsgW.setText(AppData.getAppData().getUyghurDisplayContent());

        index_xml.setVisibility(View.VISIBLE);
        comper_xml.setVisibility(View.GONE);
        code_xml.setVisibility(View.GONE);
        home_xml.setVisibility(View.GONE);

        SerialPort.openLED();

        mSocketService.mOutputStream();
    }

    public void sendFaceBmp() {
        Log.i(TAG, "SEND_FACEBMP");
        if (AppData.getAppData().getFaceFile() == null) {
            Log.i(TAG, "SEND_FACEBMP is null");
            return;
        }

        final Map<String, String> params = new HashMap<String, String>();
        params.put("Guid", AppData.getAppData().getGUID());
        params.put("ImageType", "1");
        params.put("DeviceNo", SPUtil.getString("DeviceNo", ""));

        final Map<String, File> files = new HashMap<String, File>();
        files.put("File", AppData.getAppData().getFaceFile());
        if (trd != null) {
            trd.interrupt();
            trd = null;
        }
        // ////////////////////////////////////////////////////////////////////////
        trd = new Thread(new Runnable() {

            public void run() {
                try {
                    UploadUtil.post(SPUtil.getString("ImgReceiveUrl", ""),
                            params, files);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });
        trd.start();
    }

    public void sendCardBmp() {
        Log.i(TAG, "SEND_CARDBMP");
        if (AppData.getAppData().getCardFile() == null) {
            Log.i(TAG, "SEND_CARDBMP is null");
            return;
        }

        final Map<String, String> params1 = new HashMap<String, String>();
        params1.put("Guid", AppData.getAppData().getGUID());
        params1.put("ImageType", "2");
        params1.put("DeviceNo", SPUtil.getString("DeviceNo", ""));

        final Map<String, File> files1 = new HashMap<String, File>();
        files1.put("File", AppData.getAppData().getCardFile());
        if (trd1 != null) {
            trd1.interrupt();
            trd1 = null;
        }
        trd1 = new Thread(new Runnable() {
            private int time = 0;

            @Override
            public void run() {
                try {
                    Log.i(TAG, SPUtil.getString("ImgReceiveUrl", ""));
                    UploadUtil.post(SPUtil.getString("ImgReceiveUrl", ""),
                            params1, files1);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        trd1.start();
    }

    private void showComperResultDialog(final boolean isComper) {
        if (isComper) {
            comper_icon.setImageResource(R.mipmap.success);
            // 后期去掉
            if (!AppData.getAppData().isHaveFace()) {
                face_bmp.setImageResource(R.mipmap.tx);
            } else {
                face_bmp.setImageBitmap(AppData.getAppData().getFaceBmp());
            }
            face_text_result.setText("验证成功");
            face_text_result_w.setText("تەكشۈرۈش مۇۋەپپەقىيەتلىك بولدى");
            face_bgColor.setBackgroundColor(Color.parseColor("#0080ff"));

        } else {

            Log.i(TAG, "" + AppData.getAppData().isHaveFace());

            if (AppData.getAppData().isHaveFace()) {
                face_bmp.setImageBitmap(AppData.getAppData().getFaceBmp());
            } else {
                face_bmp.setImageResource(R.mipmap.tx);
            }
            comper_icon.setImageResource(R.mipmap.error);
            face_text_result.setText("验证失败");
            face_text_result_w.setText("دەلىللەش مەغلۇپ بولدى");
            face_bgColor.setBackgroundColor(Color.parseColor("#ff2d55"));
        }
        card_bmp.setImageBitmap(AppData.getAppData().getCardBmp());

        card_name.setText(AppData.getAppData().getName());
        if (AppData.getAppData().getGender() == 0) {
            card_sex.setText("女");
        } else if (AppData.getAppData().getGender() == 1) {
            card_sex.setText("男");
        } else {
            card_sex.setText("未知");
        }

        card_birthday.setText(AppData.getAppData().getBirthday()
                .substring(0, 4)
                + "-"
                + AppData.getAppData().getBirthday().substring(5, 7)
                + "-" + AppData.getAppData().getBirthday().substring(8, 10));

        int index = AppData.getAppData().getAddress().indexOf("县");
        if (index == -1) {
            index = AppData.getAppData().getAddress().indexOf("市");
        }
        card_addr.setText(AppData.getAppData().getAddress()
                .substring(0, index + 1));
        loading_layout.setVisibility(View.GONE);
        showMsg_layout.setVisibility(View.VISIBLE);
        showCardNumber_layout.setVisibility(View.VISIBLE);
        index_xml.setVisibility(View.GONE);
        comper_xml.setVisibility(View.VISIBLE);

        if (isComper) {
            handler.postDelayed(myTime, 5000);
        }

    }

    private Runnable myTime = new Runnable() {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    comper_xml.setVisibility(View.GONE);
                    home_xml.setVisibility(View.VISIBLE);
                    myCameraView.releaseCamera();

                }
            });
        }
    };

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            while (flag) {
                if (MainService.getService() == null) {
                    continue;
                }
                if (AppData.getAppData().getFlag() == Const.FLAG_CLEAN) {
                    continue;
                }
                handler.sendEmptyMessage(AppData.getAppData().getFlag());
                AppData.getAppData().setFlag(Const.FLAG_CLEAN);
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        Log.i(TAG, "ondestory");

        // 关闭服务
        stopService(new Intent(MainActivity.this, MainService.class));

        // 取消循环监听标志位的线程
        flag = false;

        // mHandler.removeCallbacks(handler);
    }
}
