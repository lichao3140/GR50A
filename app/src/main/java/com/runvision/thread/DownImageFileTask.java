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

import com.runvision.utils.LogToFile;
import com.runvision.utils.UpdateUpgrade;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class DownImageFileTask extends AsyncTask<Void, Integer, Void> {
	private String path;
	private ProgressDialog pro;

	private Handler mHandler;
	private File file;

	public DownImageFileTask(String path, ProgressDialog pro, Handler mHandler) {
		this.path = path;
		this.pro = pro;
		this.mHandler = mHandler;
	}

	@Override
	protected Void doInBackground(Void... params) {
		Log.i("sulin","在下载图片");
		// TODO Auto-generated method stub
		InputStream is = null;

		FileOutputStream fos = null;
		BufferedInputStream bis = null;
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);

			int sum = conn.getContentLength();

			is = conn.getInputStream();

			file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FaceAndroid", "GR50A.png");

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

			publishProgress(-2);

		} catch (Exception e) {
			LogToFile.i("update", "图片下载异常");
			Log.i("sulin","在下载图片error");
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
		} else {
			mHandler.sendEmptyMessage(997);
		} 

	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		// UpdateUpgrade up=new uP
		try {
			pro.dismiss();
		}catch (Exception e){
			System.out.println("error");
		}

	}

	

}
