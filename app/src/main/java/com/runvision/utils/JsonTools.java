package com.runvision.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.runvision.bean.AppData;
import com.runvision.bean.Date;
import com.runvision.core.Const;
import com.runvision.core.MD5;

/**
 * Created by Administrator on 2018/4/9.
 */

public class JsonTools {

    private static String MD5String;
    private  static Date timedate;

    public static void parseStringWithJSON(String jsonData,Handler handler) {

        Const.ReturnOrSend = true;
        LogToFile.i("json", jsonData);
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            AppData.getAppData().setMessageId(jsonObject.getString("MessageId"));
            AppData.getAppData().setMethodId(jsonObject.getString("MethodId"));
            AppData.getAppData().setDeviceNo(jsonObject.getString("DeviceNo"));
            AppData.getAppData().setSign(jsonObject.getString("Sign"));

            if (AppData.getAppData().getMethodId().equals("8002")) {
                JSONObject data = jsonObject.getJSONObject("Data");
                AppData.getAppData().setDeviceNo(data.getString("DeviceNo"));
                AppData.getAppData().setDeviceKey(data.getString("DeviceKey"));
                AppData.getAppData().setIP(data.getString("Ip"));
                AppData.getAppData().setPort(data.getInt("Port"));
                AppData.getAppData().setImgReceiveUrl(data.getString("ImgReceiveUrl"));
                AppData.getAppData().setParkingDeviceNo(data.getString("ParkingDeviceNo"));
                AppData.getAppData().setQualityScore(data.getInt("QualityScore"));
                AppData.getAppData().setPortalSysName(data.getString("PortalSysName"));
                AppData.getAppData().setPortalUyghurSysName(data.getString("PortalUyghurSysName"));

                SPUtil.putString("DeviceNo", data.getString("DeviceNo"));
                SPUtil.putString("DeviceKey", data.getString("DeviceKey"));
                Log.e("sulin1", data.getString("DeviceKey"));
                SPUtil.putString("Ip", data.getString("Ip"));
                SPUtil.putString("Port", data.getString("Port"));
                SPUtil.putString("ImgReceiveUrl", data.getString("ImgReceiveUrl"));
                SPUtil.putString("ParkingDeviceNo", data.getString("ParkingDeviceNo"));
                SPUtil.putInt("QualityScore", data.getInt("QualityScore"));
                SPUtil.putString("UpgradeApi", data.getString("UpgradeApi"));
                SPUtil.putString("PortalImgUrl", data.getString("PortalImgUrl"));

                SPUtil.putString("PortalSysName", data.getString("PortalSysName"));
                SPUtil.putString("PortalUyghurSysName", data.getString("PortalUyghurSysName"));
                handler.sendEmptyMessage(Const.UPDATE_SETTING);
//                // 更改标志位 通知Activity修改IP 并重启socketService
//                AppData.getAppData().setFlag(Const.UPDATE_SETTING);

            } else {

                if (!SPUtil.getString("DeviceKey", "").equals("")) {
                    MD5String = "messageid" + AppData.getAppData().getMessageId() + "methodid" + AppData.getAppData().getMethodId() + "deviceno" + AppData.getAppData().getDeviceNo() + "key"
                            + SPUtil.getString("DeviceKey", "");


                    if (!MD5.md5(MD5String).trim().toUpperCase().equals(AppData.getAppData().getSign())) {
                        Log.i("sulin", "MD5  验证不通过");
                        LogToFile.i("json", "MD5  验证不通过");
                        AppData.getAppData().setRetCode("1");
                        AppData.getAppData().setMessage("cannot_connect");
                        return;
                    }
                }

                if (AppData.getAppData().getMethodId().equals("8001")) {
                    Log.e("sulin", "8001");
                    handler.sendEmptyMessage(Const.HEARTBEAT);
                    //AppData.getAppData().setFlag(Const.HEARTBEAT);
                }

                if (AppData.getAppData().getMethodId().equals("8003")) {
                    JSONObject data = jsonObject.getJSONObject("Data");
                    AppData.getAppData().setTime(data.getString("Time"));
                    // 时间同步
                    timedate = new Date();
                    timedate.setYear(Integer.valueOf(AppData.getAppData().getTime().substring(0, 4)));
                    timedate.setMonth(Integer.valueOf(AppData.getAppData().getTime().substring(5, 7)));
                    timedate.setDate(Integer.valueOf(AppData.getAppData().getTime().substring(8, 10)));
                    timedate.setHours(Integer.valueOf(AppData.getAppData().getTime().substring(11, 13)));
                    timedate.setMinutes(Integer.valueOf(AppData.getAppData().getTime().substring(14, 16)));
                    timedate.setSecond(Integer.valueOf(AppData.getAppData().getTime().substring(17, 19)));
                    // setSystemTime(timedate);
                    setSystemTimer(timedate);

                }
                if (AppData.getAppData().getMethodId().equals("8004")) {
                    System.out.println("接受到车牌");
                    JSONObject data = jsonObject.getJSONObject("Data");
                    AppData.getAppData().setGUID(data.getString("GUID"));
                    AppData.getAppData().setPlate(data.getString("Plate"));
                    // AppData.getAppData().setQrcode(data.getString("Qrcode"));
                    AppData.getAppData().setDisplayContent(data.getString("DisplayContent"));
                    AppData.getAppData().setVoiceFileName(data.getString("VoiceFileName"));
                    AppData.getAppData().setVoiceContent(data.getString("VoiceContent"));

                    AppData.getAppData().setUyghurDisplayContent(data.getString("UyghurDisplayContent"));
//
//					// Log.i("code", data.getString("UyghurDisplayContent"));

                    AppData.getAppData().setUyghurVoiceFileName(data.getString("UyghurVoiceFileName"));
                    AppData.getAppData().setUyghurVoiceContent(data.getString("UyghurVoiceContent"));
                    AppData.getAppData().setMessageLevel(data.getInt("MessageLevel"));

                    handler.sendEmptyMessage(Const.CARNMBER_MSG_FLAG);


                }
                if (AppData.getAppData().getMethodId().equals("8005")) {

                    JSONObject data = jsonObject.getJSONObject("Data");
                    if (data.getString("RetCode").equals("0")) {
                        AppData.getAppData().setGUID(data.getString("GUID"));
                        AppData.getAppData().setQrcode(data.getString("Qrcode"));
                        AppData.getAppData().setDisplayContent(data.getString("DisplayContent"));
                        AppData.getAppData().setVoiceFileName(data.getString("VoiceFileName"));
                        AppData.getAppData().setVoiceContent(data.getString("VoiceContent"));
                        AppData.getAppData().setUyghurVoiceFileName(data.getString("UyghurVoiceFileName"));
                        AppData.getAppData().setUyghurVoiceContent(data.getString("UyghurVoiceContent"));
                        AppData.getAppData().setCompareResult_1(data.getInt("CompareResult"));
                        handler.sendEmptyMessage(Const.COMPER_RESULT);
                       // AppData.getAppData().setFlag(Const.COMPER_RESULT);

                    } else {
                        handler.sendEmptyMessage(Const.CLEAR_COMPER);
                       // AppData.getAppData().setFlag(Const.CLEAR_COMPER);
                    }
                }

                if (AppData.getAppData().getMethodId().equals("8006")) {
                    Log.i("code", AppData.getAppData().getMethodId() + ":jsonObject:" + jsonObject.toString());
                    JSONObject data = jsonObject.getJSONObject("Data");
                    Log.i("code", data.toString());
                    AppData.getAppData().setQrcode(data.getString("Qrcode"));
                    Log.i("code", data.getString("Qrcode"));
                    AppData.getAppData().setStatus(data.getInt("Status"));
                    Log.i("code", data.getString("Status"));
                    AppData.getAppData().setDisplayContent(data.getString("DisplayContent"));
                    Log.i("code", data.getString("DisplayContent"));
                    AppData.getAppData().setUyghurDisplayContent(data.getString("UyghurDisplayContent"));
                    Log.i("code", data.getString("UyghurDisplayContent"));
                    AppData.getAppData().setVoiceContent(data.getString("VoiceContent"));
                    Log.i("code", data.getString("VoiceContent"));
                    AppData.getAppData().setUyghurVoiceContent(data.getString("UyghurVoiceContent"));
                    Log.i("code", data.getString("UyghurVoiceContent"));
                    if (AppData.getAppData().getStatus() == 1) {
                        Log.i("code", "收到8006");
                        handler.sendEmptyMessage(Const.SHOW_QRCODE);
                        //AppData.getAppData().setFlag(Const.SHOW_QRCODE);
                    } else {
                        handler.sendEmptyMessage(Const.CLOSE_CODE);
                        //AppData.getAppData().setFlag(Const.CLOSE_CODE);
                    }

                }
            }

            AppData.getAppData().setRetCode("0");
            AppData.getAppData().setMessage("null");

            // 杩斿洖娌℃湁杩炴帴鍙傛暟鏁版嵁

        } catch (Exception e) {
            LogToFile.i("json", "json error");
            Log.i("sulin", "json error");
            e.printStackTrace();
        }
    }


    public static String parseJSONWithString() {
        JSONObject js = new JSONObject();
        JSONObject params = new JSONObject();
        // Log.d("Gavin_22222", "send scussed");
        try {
            params.put("MessageId", AppData.getAppData().getMessageId());
            params.put("MethodId", AppData.getAppData().getMethodId());
            params.put("DeviceNo", SPUtil.getString("DeviceNo", ""));
            params.put("Sign", AppData.getAppData().getSign());
            if (Const.ReturnOrSend == true) {
                js.put("RetCode", AppData.getAppData().getRetCode());
                js.put("Message", AppData.getAppData().getMessage());
            } else {
                js.put("GUID", AppData.getAppData().getGUID());
                js.put("Plate", AppData.getAppData().getPlate());
                js.put("DeviceNo", AppData.getAppData().getDeviceNo());
                js.put("ParkingDeviceNo", SPUtil.getString("ParkingDeviceNo", ""));
                js.put("CompareResult", AppData.getAppData().getCompareResult());
                js.put("CardType", AppData.getAppData().getCardType());
                js.put("CardNo", AppData.getAppData().getCardNo());
                js.put("Name", AppData.getAppData().getName());
                js.put("Gender", AppData.getAppData().getGender());
                js.put("Nation", AppData.getAppData().getNation());
                js.put("Nationality", AppData.getAppData().getNationality());
                js.put("Birthday", AppData.getAppData().getBirthday());
                js.put("Address", AppData.getAppData().getAddress());
                js.put("CardStartTime", AppData.getAppData().getCardStartTime());
                js.put("CardEndTime", AppData.getAppData().getCardEndTime());
                js.put("IssueDepartment", AppData.getAppData().getIssueDepartment());
                js.put("HasFinger", AppData.getAppData().getHasFinger());
                js.put("FingerFeature0", AppData.getAppData().getFingerFeature0());
                js.put("FingerFeature1", AppData.getAppData().getFingerFeature1());
                js.put("CollectFinger", AppData.getAppData().getCollectFinger());
                js.put("QualityScore", SPUtil.getInt("QualityScore", 0));
                js.put("CompareScore", 0);
            }
            params.put("Data", js);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String content = String.valueOf(params);


        return content;
    }

    private static void setSystemTimer(Date timedate) {
        String smonth = null, sdate = null, shours = null, sminutes = null, ssecond = null;
        try {
            Process process = Runtime.getRuntime().exec("su");
            if (timedate.getMonth() < 10) {
                smonth = "0" + String.valueOf(timedate.getMonth());
            } else {
                smonth = String.valueOf(timedate.getMonth());
            }
            // /////////////////////////////////////////////////////
            if (timedate.getDate() < 10) {
                sdate = "0" + String.valueOf(timedate.getDate());
            } else {
                sdate = String.valueOf(timedate.getDate());
            }
            // /////////////////////////////////////////////////////
            if (timedate.getHours() < 10) {
                shours = "0" + String.valueOf(timedate.getHours());
            } else {
                shours = String.valueOf(timedate.getHours());
            }
            // /////////////////////////////////////////////////////
            if (timedate.getMinutes() < 10) {
                sminutes = "0" + String.valueOf(timedate.getMinutes());
            } else {
                sminutes = String.valueOf(timedate.getMinutes());
            }
            // /////////////////////////////////////////////////////
            if (timedate.getSecond() < 10) {
                ssecond = "0" + String.valueOf(timedate.getSecond());
            } else {
                ssecond = String.valueOf(timedate.getSecond());
            }
            // /////////////////////////////////////////////////////

            String datetime = String.valueOf(timedate.getYear()) + smonth + sdate + "." + shours + sminutes + ssecond;
            // Log.d("Gavin", datetime);

            ArrayList<String> envlist = new ArrayList<String>();
            Map<String, String> env = System.getenv();
            for (String envName : env.keySet()) {
                envlist.add(envName + "=" + env.get(envName));
            }
            String[] envp = (String[]) envlist.toArray(new String[0]);
            String command;
            command = "date -s\"" + datetime + "\"";
            try {
                Runtime.getRuntime().exec(new String[]{"su", "-c", command}, envp);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // Log.i("Gavin", "error");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}