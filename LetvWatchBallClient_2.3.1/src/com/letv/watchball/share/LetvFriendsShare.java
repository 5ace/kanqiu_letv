package com.letv.watchball.share;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.widget.Toast;

import com.letv.watchball.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXVideoObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class LetvFriendsShare {

	public static void share(Activity context, String caption, String imaUrl,
			String playUrl) {
		try {
			IWXAPI api = WXAPIFactory.createWXAPI(context,
					ShareConstant.Weixin.APP_ID, true);
			api.registerApp(ShareConstant.Weixin.APP_ID);
			Bitmap bmp = null;
			if (!imaUrl.equalsIgnoreCase(""))
				bmp = returnBitMap(imaUrl);

			if (bmp != null) {

				WXVideoObject videoObject = new WXVideoObject();
				videoObject.videoUrl = playUrl;

				WXMediaMessage msg = new WXMediaMessage(videoObject);
				msg.title = caption;
				// msg.description = caption;

				Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 80, 80, true);
				bmp.recycle();
				msg.thumbData = bmpToByteArray(thumbBmp, true);

				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.transaction = buildTransaction("video");
				req.message = msg;
				req.scene = SendMessageToWX.Req.WXSceneTimeline;
				api.sendReq(req);
			} else {
				WXVideoObject videoObject = new WXVideoObject();
				videoObject.videoUrl = playUrl;

				InputStream is = context.getResources().openRawResource(
						R.drawable.icon);
				bmp = BitmapFactory.decodeStream(is);
				Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 80, 80, true);
				bmp.recycle();
				WXMediaMessage msg = new WXMediaMessage(videoObject);
				msg.thumbData = bmpToByteArray(thumbBmp, true);
				msg.title = caption;
				// msg.description = caption;

				// 构造一个Req
				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.transaction = buildTransaction("video"); // transaction字段用于唯一标识一个请求
				req.message = msg;
				req.scene = SendMessageToWX.Req.WXSceneTimeline;
				// 调用api接口发送数据到微信
				api.sendReq(req);
			}

		} catch (Exception e) {
			Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	public static Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

}
