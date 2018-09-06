package com.runvision.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.runvision.bean.AppData;
import com.runvision.core.Const;

/**
 * 娑撳﹣绱堕弬鍥︽閸掔増婀囬崝鈥虫珤缁拷
 *
 * @author tom
 */
public class UploadUtil {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000;
    private static final String CHARSET = "utf-8";

    /**
     * Android娑撳﹣绱堕弬鍥︽閸掔増婀囬崝锛勵伂
     *
     * @param file       闂囷拷鐟曚椒绗傛导鐘垫畱閺傚洣娆�
     * @param RequestURL 鐠囬攱鐪伴惃鍓卽l
     * @return 鏉╂柨娲栭崫宥呯安閻ㄥ嫬鍞寸�癸拷
     */
    public static String uploadFile(File file, String RequestURL) {
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString();
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", CHARSET);
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (file != null) {

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);

                sb.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();

                int res = conn.getResponseCode();
                Log.e(TAG, "response code:" + res);

                Log.e(TAG, "request success");
                InputStream input = conn.getInputStream();
                StringBuffer sb1 = new StringBuffer();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                result = sb1.toString();
                Log.e(TAG, "result : " + result);
                // }
                // else{
                // Log.e(TAG, "request error");
                // }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param url    Service net address
     * @param params text content
     * @param files  pictures
     * @return String result of Service response
     * @throws IOException
     */

    public static void post(String url, Map<String, String> params, Map<String, File> files)
            throws IOException {
        String BOUNDARY = UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(10 * 1000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
            sb.append(entry.getValue());
            sb.append(LINEND);
        }
        conn.connect();
        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(sb.toString().getBytes());

        if (files != null) {
            for (Map.Entry<String, File> file : files.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                        + file.getValue().getName() + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());
                InputStream is = new FileInputStream(file.getValue());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                is.close();
                outStream.write(LINEND.getBytes());
            }
        }
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();

        Log.d("Gavin", "outStream_write_over");
        // 瀵版鍩岄崫宥呯安閻拷
        //  new Thread() {//鍒涘缓瀛愮嚎绋嬭繘琛岀綉缁滆闂殑鎿嶄綔
        //     public void run() {
        try {
            int res = conn.getResponseCode();
            InputStream in = conn.getInputStream();
            StringBuilder sb2 = new StringBuilder();
            if (res == 200) {
                int ch;
                while ((ch = in.read()) != -1) {
                    sb2.append((char) ch);
                }
            }
            //璋冪敤json澶勭悊鍑芥暟
            JSONAnalysis(sb2.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //      }
        //  }.start();

        outStream.close();
        conn.disconnect();


        //return sb2.toString();
    }


    public static void get(String path) throws IOException {
        String data = null;
        // 新建一个URL对象
        URL url = new URL(path);
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        // 设置连接超时时间
        urlConn.setConnectTimeout(5 * 1000);
        // 开始连接
        urlConn.connect();
        // 判断请求是否成功
        if (urlConn.getResponseCode() == 200) {
            // 获取返回的数据
            InputStream is = urlConn.getInputStream();//得到网络返回的输入流
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));//转化为字符缓冲流
            data = buf.readLine();
            buf.close();
            is.close();
            //处理接收的数据
            System.out.println(data);
            JSONGetAnalysis(data);
        } else {
            Log.i("sulin", "Get方式请求失败");
        }


//        URL http_url = new URL(url);
//        String data = null;
//        if (http_url != null) {
//            //打开一个HttpURLConnection连接
//            HttpURLConnection conn = (HttpURLConnection) http_url.openConnection();
//            conn.setConnectTimeout(5 * 1000);//设置连接超时
//            conn.setRequestMethod("GET");//以get方式发起请求
//            //允许输入流
//            conn.setDoInput(true);
//            //接收服务器响应
//            if (conn.getResponseCode() == 200) {
//                InputStream is = conn.getInputStream();//得到网络返回的输入流
//                BufferedReader buf = new BufferedReader(new InputStreamReader(is));//转化为字符缓冲流
//                data = buf.readLine();
//                buf.close();
//                is.close();
//                //处理接收的数据
//                JSONGetAnalysis(data);
//            } else {
//                Log.i("sulin", "code:" + conn.getResponseCode());
//            }
//        }
    }

    /**
     * JSON解析方法
     */
    public static void JSONGetAnalysis(String string) {
        JSONObject object = null;
//        String mRetCode = null, mMessage;
        Log.d("Gavin", "JSONdata:" + string);
        try {
            object = new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            // Log.d("Gavin","Version:"+object.getString("Version"));
            //  Log.d("Gavin","DownloadLink:"+object.getString("DownloadLink"));
            AppData.getAppData().setVersion(object.getString("Version"));

            AppData.getAppData().setDownloadLink(object.getString("DownloadLink"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * JSON瑙ｆ瀽鏂规硶
     */
    public static void JSONAnalysis(String string) {
        JSONObject object = null;
        String mRetCode = null, mMessage;
        // Log.d("Gavin",string);
        try {
            object = new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /**
         * 鍦ㄤ綘鑾峰彇鐨剆tring杩欎釜JSON瀵硅薄涓紝鎻愬彇浣犳墍闇�瑕佺殑淇℃伅銆�
         */
        // JSONObject ObjectInfo = object.optJSONObject("RetCode");
        try {
            mRetCode = object.getString("RetCode");
            mMessage = object.getString("Message");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i(TAG, "mRetCode" + mRetCode);
        if (mRetCode.equals("1")) {
            Log.i(TAG, "chong  fu");
            AppData.getAppData().setFlag(Const.SEND_FACEBMP);
        }

        //  weatherResult = "鍩庡競锛�" + city + "\n鏃ユ湡锛�" + date + "\n鏄熸湡锛�" + week
        //         + "\n娓╁害锛�" + temp + "\n澶╂皵鎯呭喌锛�" + weather + "\n绌胯。鎸囨暟锛�" + index;
        // textView.setText(weatherResult);
    }
}