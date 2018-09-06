package com.runvision.gr50a;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.runvision.bean.AppData;
import com.runvision.core.Const;


import com.runvision.thread.DownImageFileTask;
import com.runvision.utils.LogToFile;
import com.runvision.utils.SPUtil;
import com.runvision.utils.UpdateUpgrade;
import com.runvision.utils.UploadUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android_serialport_api.SerialPort;

public class UpdateActivity extends Activity {

	private Context mContext;

	private int updateflag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		hideBottomUIMenu();
		mContext = this;
		UpdateUpgrade();

	}

	@Override
	public void onDestroy() {
		super.onDestroy(); // Always call the superclass

		// Stop method tracing that the activity started during onCreate()
		android.os.Debug.stopMethodTracing();

	}


	@SuppressLint("NewApi")
	protected void hideBottomUIMenu() {
		if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
			View v = this.getWindow().getDecorView();
			v.setSystemUiVisibility(View.GONE);
		} else if (Build.VERSION.SDK_INT >= 19) {
			// for new api versions.
			View decorView = getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}

	private void UpdateUpgrade() {
		Log.i("sulin", "UpgradeApi:" + SPUtil.getString("UpgradeApi", ""));

		System.out.println("UpgradeApi:" + SPUtil.getString("UpgradeApi", ""));

		if (!SPUtil.getString("UpgradeApi", "").equals("")) {
			new Thread() {
				@Override
				public void run() {
					try {
						UploadUtil.get(SPUtil.getString("UpgradeApi", ""));
					} catch (Exception e) {
						e.printStackTrace();
						Log.i("sulin", "dizhi buxing");
						LogToFile.i("update", "更新地址是错误的");
					} finally {

						mhandler.sendEmptyMessage(2222);
					}
				}
			}.start();

			// SPUtil.putString("UpgradeApi", data.getString("UpgradeApi"));
			// SPUtil.putString("PortalImgUrl", data.getString("PortalImgUrl"));

		} else {
			System.out.println("地址为空，不更新");
			Toast.makeText(this, "地址为空，不更新", Toast.LENGTH_SHORT).show();
			LogToFile.i("update", "地址为空，不更新");
			SendJumpMainMsg();
		}

	}

	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Const.UPDATE_FLAG:
				Log.i("sulin", "UPDATE_FLAG");
				LogToFile.i("update", "UPDATE_FLAG");
				UpdateUpgrade mUpdateUpgrade = new UpdateUpgrade((Activity) mContext);
				mUpdateUpgrade.StartUpdate(mhandler);
				break;
			case 998:
				Log.i("sulin", "998");
				LogToFile.i("update", "998");
				SPUtil.putString("version", AppData.getAppData().getVersion());

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				SendJumpMainMsg();
				break;
			case 999:
				Log.i("sulin", "999");
				LogToFile.i("update", "999");
				SendJumpMainMsg();
				break;
			case 997:
				// ����ͼƬ���ظ��Ҹ�����
				Log.i("sulin", "997");
				LogToFile.i("update", "997");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = new Intent((Activity) mContext, MainActivity.class);
				startActivity(intent);
				finish();
				break;

			case 2222:
				Log.i("sulin", "222");
				LogToFile.i("update", "222");
				Log.i("sulin", "AppData.getAppData().getVersion():" + AppData.getAppData().getVersion());
				LogToFile.i("update", "AppData.getAppData().getVersion():" + AppData.getAppData().getVersion());
				Log.i("sulin", "DownloadLink:" + AppData.getAppData().getDownloadLink());
				LogToFile.i("update", "DownloadLink:" + AppData.getAppData().getDownloadLink());
				Log.i("sulin", SPUtil.getString("version", "not has version"));
				LogToFile.i("update", SPUtil.getString("version", "not has version"));
				if ((AppData.getAppData().getVersion() != null) && (!AppData.getAppData().getVersion().equals("")) && (!AppData.getAppData().getDownloadLink().equals(""))) {
					mhandler.sendEmptyMessage(Const.UPDATE_FLAG);
				} else {
					Toast.makeText((Activity) mContext, "不更新", Toast.LENGTH_SHORT).show();
					SendJumpMainMsg();
				}

				break;
			default:
				break;
			}
		};
	};

	private void SendJumpMainMsg() {
		//Log.i("sulin", "����SendJumpMainMsg,��ʱ��Url=" + SPUtil.getString("PortalImgUrl", ""));
		LogToFile.i("update", "Image url="+SPUtil.getString("PortalImgUrl", ""));
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FaceAndroid", "GR50A.png");
		if (file.exists()) {
			file.delete();
		}
		if (SPUtil.getString("PortalImgUrl", "").equals("")) {
			Log.i("sulin", "图片下载地址为空");
			LogToFile.i("update", "图片下载地址为空");
			mhandler.sendEmptyMessage(997);
			return;
		}
		Log.i("sulin", "URI cunzai");
		ProgressDialog pd = new ProgressDialog(mContext);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("下载图片");
		pd.setMax(100);
		pd.show();
		DownImageFileTask task = new DownImageFileTask(SPUtil.getString("PortalImgUrl", ""), pd, mhandler);
		task.execute();

		// // DownloadImg();
		// Log.i("Gavin", "DOWNLOAD_IMG");
		// Message msg1 = new Message();
		// msg1.what = Const.JUMP_MAIN_FLAG;
		// msg1.obj = Const.COMPARE_NUMBER;
		// mhandler.sendMessage(msg1);
	}

	private void DownloadImg() {
		// final String path = "http://192.168.1.6:9090/ABC/home_bg.png";
		final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FaceAndroid", "GR50A.png");
		if (file.exists()) {
			
			// Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
		} else {
			if (!SPUtil.getString("PortalImgUrl", "").equals("")) {
				UpdateUpgrade mUpdateUpgrade = new UpdateUpgrade((Activity) mContext);
				mUpdateUpgrade.downLoadimg();
			}
		}
	}

	// public String getNameFromPath(String path){
	// int index = path.lastIndexOf("/");
	// return path.substring(index + 1);
	// }
}
