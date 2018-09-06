package com.runvision.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.runvision.bean.AppData;
import com.runvision.thread.DownFileTask;

public class UpdateUpgrade {
	private Activity mActivity;
	private final int IMG_FLAG = 0;
	private final int APK_FLAG = 1;
	private String url = "http://192.168.1.6:9090/ABC/GR50A_V02.apk";

	public UpdateUpgrade(Activity activity) {
		this.mActivity = activity;
	}

	public void StartUpdate(Handler handler) {
		
		boolean isUpdate = false;
		String sp_version = SPUtil.getString("version", "");
		String now_version = AppData.getAppData().getVersion();
		if (sp_version.equals("")) {			
				Log.i("sulin", "无版本  bicultural升级");
				LogToFile.i("update", "无版本,必须升级");
				downLoadApk(handler);
				
				//Toast.makeText(mActivity, "软件版本更新，下载最新版本软件", Toast.LENGTH_SHORT).show();
			
			
		} else {
			String[] sp_version_list = sp_version.split("\\.");
			String[] now_version_list = now_version.split("\\.");
			for (int i = 0; i < 3; i++) {
				if (Integer.parseInt(sp_version_list[i]) < Integer.parseInt(now_version_list[i])) {
					isUpdate = true;
					break;
				}
			}

			if (isUpdate) {
				Log.i("sulin", "升级");
				Toast.makeText(mActivity, "软件版本更新，下载最新版本软件", Toast.LENGTH_SHORT).show();
				LogToFile.i("update", "软件版本更新，下载最新版本软件");
				downLoadApk(handler);
			} else {
				Log.i("sulin", "不升级");
				Toast.makeText(mActivity, "软件版本没有更新", Toast.LENGTH_SHORT).show();
				LogToFile.i("update", "软件版本没有更新");
				handler.sendEmptyMessage(999);
			}

		}
		

		// if (getVersionName().equals(AppData.getAppData().getVersion()))
		// // if(getVersionName().equals("2.0"))
		// {
		// // 鏃犳洿鏂�
		// Toast.makeText(mActivity, "软件版本没有更新", Toast.LENGTH_SHORT).show();
		// update = 0;
		// } else {
		// // Log.i("Gavin","鐗堟湰鍙蜂笉鍚� ,鎻愮ず鐢ㄦ埛鍗囩骇 ");
		// // 鏍规嵁鍦板潃涓嬭浇apk
		// update = 1;
		// Toast.makeText(mActivity, "软件版本更新，下载最新版本软件", Toast.LENGTH_SHORT)
		// .show();
		// downLoadApk();
		// // 涓嬭浇瀹屽悗鏇存柊绋嬪簭
		// }
		
	}

	public void downLoadimg() {

		final ProgressDialog pd; // 杩涘害鏉″璇濇
		pd = new ProgressDialog(mActivity);
		// pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// pd.setMessage("正在下载首页背景图片");
		// pd.show();
		new Thread() {
			@Override
			public void run() {
				try {
					// File file =
					// getFileFromServer("http://192.168.1.6:9090/ABC/home_bg.png",
					// pd,IMG_FLAG);//测试用
					File file = getFileFromServer(SPUtil.getString("PortalImgUrl", ""), pd, IMG_FLAG);
					// pd.dismiss(); //缁撴潫鎺夎繘搴︽潯瀵硅瘽妗�
				} catch (Exception e) {

				}
			}
		}.start();
	}

	/*
	 * 浠庢湇鍔″櫒涓笅杞紸PK
	 */
	protected void downLoadApk(final Handler handler) {
		final ProgressDialog pd; 
		pd = new ProgressDialog(mActivity);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMax(100);
		pd.setMessage("正在下载更新软件");
		pd.show();
		DownFileTask task=new DownFileTask(AppData.getAppData().getDownloadLink(), pd, APK_FLAG, handler,mActivity);
		task.execute();

	}

	// 瀹夎apk
	public void installApk(File file) {
		Intent intent = new Intent();
		// 鎵ц鍔ㄤ綔
		intent.setAction(Intent.ACTION_VIEW);
		// 鎵ц鐨勬暟鎹被鍨�
		Log.d("Gavin", "installApk" + mActivity.toString());
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");// 缂栬�呮寜锛氭澶凙ndroid搴斾负android锛屽惁鍒欓�犳垚瀹夎涓嶄簡
		mActivity.startActivity(intent);
		uninstallSlient();
	}

	/*
	 * public static String getNameFromPath(String path){ int index =
	 * path.lastIndexOf("/"); return path.substring(index + 1); }
	 */

	// Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.apk"
	public static File getFileFromServer(String path, ProgressDialog pd, int flag) throws Exception {
		URL url = new URL(path);
		File file = null;
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		// 鑾峰彇鍒版枃浠剁殑澶у皬
		pd.setMax(conn.getContentLength());
		InputStream is = conn.getInputStream();
		if (flag == 1) {
			file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FaceAndroid", "GR50A.apk");
		}
		if (flag == 0) {
			file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FaceAndroid", "GR50A.png");
		}
		FileOutputStream fos = new FileOutputStream(file);
		BufferedInputStream bis = new BufferedInputStream(is);

		byte[] buffer = new byte[1024];
		int len;
		int total = 0;
		while ((len = bis.read(buffer)) != -1) {
			fos.write(buffer, 0, len);
			total += len;
			// 鑾峰彇褰撳墠涓嬭浇閲�
			pd.setProgress(total);
		}
		fos.close();
		bis.close();
		is.close();
		return file;

	}

	// 闈欓粯鍗歌浇

	private void uninstallSlient() {
//		Log.i("Gavin", "uninstallSlient:" + mActivity.getPackageName());
		String cmd = "pm uninstall " + mActivity.getPackageName();

		Process process = null;

		DataOutputStream os = null;

		BufferedReader successResult = null;

		BufferedReader errorResult = null;

		StringBuilder successMsg = null;

		StringBuilder errorMsg = null;

		try {

			// 鍗歌浇涔熼渶瑕乺oot鏉冮檺

			process = Runtime.getRuntime().exec("su");

			os = new DataOutputStream(process.getOutputStream());

			os.write(cmd.getBytes());

			os.writeBytes("\n");

			os.writeBytes("exit\n");

			os.flush();

			// 鎵ц鍛戒护

			process.waitFor();

			// 鑾峰彇杩斿洖缁撴灉

			successMsg = new StringBuilder();

			errorMsg = new StringBuilder();

			successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));

			errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			String s;

			while ((s = successResult.readLine()) != null) {

				successMsg.append(s);

			}

			while ((s = errorResult.readLine()) != null) {

				errorMsg.append(s);

			}
		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			try {

				if (os != null) {

					os.close();

				}

				if (process != null) {

					process.destroy();

				}

				if (successResult != null) {

					successResult.close();

				}

				if (errorResult != null) {

					errorResult.close();

				}

			} catch (Exception e) {

				e.printStackTrace();

			}

		}
	}

	/**
	 * 鑾峰彇鐗堟湰鍙�
	 * 
	 * @return
	 */
	public int getVersionCode() {
		PackageManager manager = mActivity.getPackageManager();// 鑾峰彇鍖呯鐞嗗櫒
		try {
			// 閫氳繃褰撳墠鐨勫寘鍚嶈幏鍙栧寘鐨勪俊鎭�
			PackageInfo info = manager.getPackageInfo(mActivity.getPackageName(), 0);// 鑾峰彇鍖呭璞′俊鎭�
			return info.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 鑾峰彇鍧傛湰鏄�
	 * 
	 * @return
	 */
	public String getVersionName() {
		PackageManager manager = mActivity.getPackageManager();
		try {
			// 绗簩涓弬鏁颁唬琛ㄩ澶栫殑淇℃伅锛屼緥濡傝幏鍙栧綋鍓嶅簲鐢ㄤ腑鐨勬墍鏈夌殑Activity
			PackageInfo packageInfo = manager.getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);
			ActivityInfo[] activities = packageInfo.activities;
			showActivities(activities);
			return packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void showActivities(ActivityInfo[] activities) {
		for (ActivityInfo activity : activities) {
			Log.i("activity=========", activity.name);
		}
	}
}
