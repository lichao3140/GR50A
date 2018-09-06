package com.runvision.gr50a;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.runvision.bean.AppData;
import com.runvision.core.Const;
import com.runvision.core.FaceIDCardCompareLib;
import com.runvision.core.SocketService;
import com.runvision.utils.CameraHelp;
import com.runvision.utils.LogToFile;
import com.runvision.utils.SPUtil;
import com.zkteco.android.IDReader.IDPhotoHelper;
import com.zkteco.android.IDReader.WLTService;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.module.idcard.IDCardReader;
import com.zkteco.android.biometric.module.idcard.IDCardReaderFactory;
import com.zkteco.android.biometric.module.idcard.exception.IDCardReaderException;
import com.zkteco.android.biometric.module.idcard.meta.IDCardInfo;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MainService extends Service {
    private String TAG = "MainService";
    // ----------------------------------------����������-------------------------------------------------
    private static final int VID = 1024; // IDR VID
    private static final int PID = 50010; // IDR PID
    private final String ACTION_USB_PERMISSION = "com.example.scarx.idcardreader.USB_PERMISSION";
    private IDCardReader idCardReader = null;
    private UsbManager musbManager = null;
    private boolean bStop = false;
    public static boolean isReadCard = false;
    // -----------------------------------------end------------------------------------------------
    private static MainService myService = null;
    private Context mContext;
    private SocketService mSocketService = null;

    public static MainService getService() {
        return myService;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i(TAG, "onCreate");

        mContext = this;
        myService = this;

        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, usbDeviceStateFilter);
        startIDCardReader();

    }

    // -----------------------��������ģ��----------------------------------//

    /**
     * ������ ��ʼ��
     */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);

    }

    private void startIDCardReader() {
        LogHelper.setLevel(Log.ASSERT);
        Map idrparams = new HashMap();
        idrparams.put(ParameterHelper.PARAM_KEY_VID, VID);
        idrparams.put(ParameterHelper.PARAM_KEY_PID, PID);
        idCardReader = IDCardReaderFactory.createIDCardReader(this, TransportType.USB, idrparams);
        readCard();
    }

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

                UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    Log.e(TAG, "VID" + device.getProductId());
                    Log.e(TAG, "PID" + device.getVendorId());
                    if (device.getProductId() == PID && device.getVendorId() == VID) {
                        Toast.makeText(mContext, "读卡器拔出", Toast.LENGTH_SHORT).show();
                        LogToFile.i(TAG, "读卡器拔出");
                        bStop = true;
                        try {
                            idCardReader.close(0);
                        } catch (IDCardReaderException e) {
                            // TODO Auto-generated catch block

                        }
                        IDCardReaderFactory.destroy(idCardReader);
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {

                UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                if (device.getProductId() == PID && device.getVendorId() == VID) {

                    Toast.makeText(mContext, "插入读卡器", Toast.LENGTH_SHORT).show();
                    LogToFile.i(TAG, "插入读卡器");
                    startIDCardReader();
                }
            }
        }
    };

    private void readCard() {
        try {
            idCardReader.open(0);
            bStop = false;
            new Thread(new Runnable() {
                public void run() {
                    while (!bStop) {

                        if (!isReadCard) {
                            continue;
                        }
                        if (!Const.canRead) {
                            continue;
                        }
                        long begin = System.currentTimeMillis();
                        IDCardInfo idCardInfo = new IDCardInfo();
                        boolean ret = false;
                        try {
                            idCardReader.findCard(0);
                            idCardReader.selectCard(0);
                        } catch (IDCardReaderException e) {
                            continue;
                        }
                        try {
                            ret = idCardReader.readCard(0, 0, idCardInfo);
                        } catch (IDCardReaderException e) {
                            Log.i(TAG, "读卡失败" + e.getMessage());
                        }
                        if (ret) {
                            Const.canRead = false;
                            final long nTickUsed = (System.currentTimeMillis() - begin);
                            Log.i(TAG, "success>>>" + nTickUsed + ",name:" + idCardInfo.getName() + "," + idCardInfo.getValidityTime() + "," + idCardInfo.getDepart());
                            LogToFile.i(TAG, "读卡成功>>>" + nTickUsed + ",name:" + idCardInfo.getName() + "," + idCardInfo.getValidityTime() + "," + idCardInfo.getDepart());
                            Message msg = new Message();
                            msg.what = 2;
                            msg.obj = idCardInfo;
                            handler.sendMessage(msg);
                        }
                    }

                }
            }).start();

        } catch (IDCardReaderException e) {
            //Log.i(TAG, "读卡器异常");
            Log.i(TAG, "读卡失败，错误码：" + e.getErrorCode() + "\n错误信息：" + e.getMessage() + "\n内部代码=" + e.getInternalErrorCode());
            LogToFile.i(TAG, "读卡失败，错误码：" + e.getErrorCode() + "\n错误信息：" + e.getMessage() + "\n内部代码=" + e.getInternalErrorCode());
            Toast.makeText(mContext, "连接读卡器失败", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                final IDCardInfo idCardInfo = (IDCardInfo) msg.obj;
                if (idCardInfo.getPhotolength() > 0) {
                    byte[] buf = new byte[WLTService.imgLength];
                    if (1 == WLTService.wlt2Bmp(idCardInfo.getPhoto(), buf)) {
                        final Bitmap cardBmp = IDPhotoHelper.Bgr2Bitmap(buf);
                        if (cardBmp != null) {
                            AppData.getAppData().setCardBmp(cardBmp);
                            File file = FaceIDCardCompareLib.getInstance().saveFile(cardBmp, "card");
                            AppData.getAppData().setCardFile(file);
                            AppData.getAppData().setFlag(Const.READ_USER_CARD);
                            final byte[] nv21 = CameraHelp.getNV21(cardBmp.getWidth(), cardBmp.getHeight(), cardBmp);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    long start = System.currentTimeMillis();
                                    float comperFace = FaceIDCardCompareLib.getInstance().comperFace(nv21);
                                    setAlertConfig(comperFace,idCardInfo);

                                    Log.e("arc", "分数:" + comperFace + ",time=" + (System.currentTimeMillis() - start));

                                }
                            }).start();
                        }

                    } else {
                        Log.i(TAG, "cardBmp==null");
                        LogToFile.i(TAG, "cardBmp==null");
                    }
                } else {
                    LogToFile.i(TAG, "WLTService.wlt2Bmp(idCardInfo.getPhoto(), buf)!=1");
                }
            }
        }
    };


    public void setAlertConfig(float aVoid,IDCardInfo idCardInfo) {
        if (aVoid == 0) {
            AppData.getAppData().setHaveFace(false);
        } else {
            AppData.getAppData().setHaveFace(true);
        }
        if (aVoid >= SPUtil.getFloat("QualityScore", Const.FACE_SCORE)) {
            AppData.getAppData().setCompareResult(true);
        } else {
            AppData.getAppData().setCompareResult(false);
        }

        AppData.getAppData().setCompareScore((int) (aVoid*100));
        AppData.getAppData().setCardType(1);
        AppData.getAppData().setNation(idCardInfo.getNation());
        AppData.getAppData().setCardNo(idCardInfo.getId());
        AppData.getAppData().setName(idCardInfo.getName());
        if ("男".equals(idCardInfo.getSex())) {
            AppData.getAppData().setGender(1);
        }
        if ("女".equals(idCardInfo.getSex())) {
            AppData.getAppData().setGender(0);
        }
        AppData.getAppData().setBirthday(idCardInfo.getBirth());
        AppData.getAppData().setAddress(idCardInfo.getAddress());
        String[] strTime = idCardInfo.getValidityTime().split("-");
        AppData.getAppData().setCardStartTime(strTime[0].replace(".", "-"));
        AppData.getAppData().setCardEndTime(strTime[1].replace(".", "-"));
        AppData.getAppData().setIssueDepartment(idCardInfo.getDepart());
        if (idCardInfo.getFplength() > 0) {
            AppData.getAppData().setHasFinger(true);
        } else {
            AppData.getAppData().setHasFinger(false);
        }
        AppData.getAppData().setFlag(Const.COMPER_FINISH_FLAG);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bStop = true;

        try {
            idCardReader.close(0);
        } catch (IDCardReaderException e) {
            e.printStackTrace();
        }

        unregisterReceiver(mUsbReceiver);
        IDCardReaderFactory.destroy(idCardReader);
    }

}
