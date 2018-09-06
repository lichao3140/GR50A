

package com.runvision.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tnno Wu on 2017/4/8.
 */

public class QRCodeUtil {

	/**
	 * 鐢熸垚浜岀淮鐮丅itmap
	 * 
	 * @param content
	 *            鍐呭
	 * @param widthPix
	 *            鍥剧墖瀹藉害
	 * @param heightPix
	 *            鍥剧墖楂樺害
	 * @param logoBm
	 *            浜岀淮鐮佷腑蹇冪殑Logo鍥炬爣锛堝彲浠ヤ负null锛�
	 * @param filePath
	 *            鐢ㄤ簬瀛樺偍浜岀淮鐮佸浘鐗囩殑鏂囦欢璺緞
	 * @return 鐢熸垚浜岀淮鐮佸強淇濆瓨鏂囦欢鏄惁鎴愬姛
	 */
	public static Bitmap createQRImage(String content, int widthPix, int heightPix) {
		try {
			if (content == null || "".equals(content)) {
				return null;
			}

			// 閰嶇疆鍙傛暟
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 瀹归敊绾у埆
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			// 璁剧疆绌虹櫧杈硅窛鐨勫搴�
			// hints.put(EncodeHintType.MARGIN, 2); //default is 4

			// 鍥惧儚鏁版嵁杞崲锛屼娇鐢ㄤ簡鐭╅樀杞崲
			BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
			int[] pixels = new int[widthPix * heightPix];
			// 涓嬮潰杩欓噷鎸夌収浜岀淮鐮佺殑绠楁硶锛岄�愪釜鐢熸垚浜岀淮鐮佺殑鍥剧墖锛�
			// 涓や釜for寰幆鏄浘鐗囨í鍒楁壂鎻忕殑缁撴灉
			for (int y = 0; y < heightPix; y++) {
				for (int x = 0; x < widthPix; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * widthPix + x] = 0xff000000;
					} else {
						pixels[y * widthPix + x] = 0xffffffff;
					}
				}
			}

			Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

			// 蹇呴』浣跨敤compress鏂规硶灏哹itmap淇濆瓨鍒版枃浠朵腑鍐嶈繘琛岃鍙栥�傜洿鎺ヨ繑鍥炵殑bitmap鏄病鏈変换浣曞帇缂╃殑锛屽唴瀛樻秷鑰楀法澶э紒
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 鍦ㄤ簩缁寸爜涓棿娣诲姞Logo鍥炬
	 */
	private static Bitmap addLogo(Bitmap src, Bitmap logo) {
		if (src == null) {
			return null;
		}

		if (logo == null) {
			return src;
		}

		// 鑾峰彇鍥剧墖鐨勫楂�
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		int logoWidth = logo.getWidth();
		int logoHeight = logo.getHeight();

		if (srcWidth == 0 || srcHeight == 0) {
			return null;
		}

		if (logoWidth == 0 || logoHeight == 0) {
			return src;
		}

		// logo澶у皬涓轰簩缁寸爜鏁翠綋澶у皬鐨�1/5
		float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
		Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
		try {
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(src, 0, 0, null);
			canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
			canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		}

		return bitmap;
	}

	/**
	 * 鏂囦欢瀛樺偍鏍圭洰褰�
	 */
	private static String getFileRoot(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File external = context.getExternalFilesDir(null);
			if (external != null) {
				return external.getAbsolutePath();
			}
		}

		return context.getFilesDir().getAbsolutePath();
	}

	/**
	 * @param text
	 *            鐢熸垚浜岀淮鐮佺殑瀛楃涓�
	 * @param imageView
	 *            鏄剧ず浜岀淮鐮佺殑ImageView
	 * @param centerPhoto
	 *            浜岀淮鐮佷腑闂寸殑鍥剧墖
	 */
	// public static void showThreadImage(final Activity mContext, final String
	// text, final ImageView imageView, final int centerPhoto) {
	// // String preContent = SPUtil.getString("share_code_content", "");
	// // if (text.equals(preContent)) {
	// // String preFilePath = SPUtil.getString("share_code_filePath", "");
	// // imageView.setImageBitmap(BitmapFactory.decodeFile(preFilePath));
	// // } else {
	// // SPUtil.putString("share_code_content", text);
	// final String filePath = getFileRoot(mContext) + File.separator + "qr_" +
	// System.currentTimeMillis() + ".jpg";
	// // SPUtil.putString("share_code_filePath", filePath);
	//
	// // 浜岀淮鐮佸浘鐗囪緝澶ф椂锛岀敓鎴愬浘鐗囥�佷繚瀛樻枃浠剁殑鏃堕棿鍙兘杈冮暱锛屽洜姝ゆ斁鍦ㄦ柊绾跨▼涓�
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// //boolean success = QRCodeUtil.createQRImage(text, 800, 800,
	// BitmapFactory.decodeResource(mContext.getResources(), centerPhoto),
	// filePath);
	//
	// if (success) {
	// mContext.runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));

	// }
	// });
	// }
	// }
	// }).start();
	// }
	// // }

}
