package com.tencent.weibo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv1.OAuthV1;

public class TencentDialog extends Dialog{

	private static final String LOG_TAG = "TencentDialog";

	private static final int RENREN_BLUE = 0xFF005EAC;

//	private static final float[] DIMENSIONS_LANDSCAPE = { 460, 260 };
//
//	private static final float[] DIMENSIONS_PORTRAIT = { 280, 420 };
	private static final float[] DIMENSIONS_LANDSCAPE = { 580, 380 };
	private static final float[] DIMENSIONS_PORTRAIT = { 380, 520 };
	
	private int WIDTH;
	private int HEIGHT;

	/**
	 * 验证过程被取消的错误码
	 */
	private static final String CODE_AUTH_CANCEL = "login_denied";

	protected String mUrl;

	protected TencentAuthListener mListener;

	private ProgressDialog progress;

	protected WebView webView;

	private LinearLayout content;

	private TextView title;
	private int type;

	private boolean showTitle = false;
	
	private String mPostData;
	private boolean isPost = false;
	private RelativeLayout webViewContainer;
	private RelativeLayout top ;
	private Context context;
	private int width;
	private int height;

	public TencentDialog(Context context, String url,
			TencentAuthListener listener) {
		this(context, url, listener, false);
		this.context = context;
	}
	
	public TencentDialog(Context context, String url,
			TencentAuthListener listener, int width, int height, ProgressDialog progressDialog) {
		this(context, url, listener, false);
		Log.d("D", "TencentDialog");
		this.context = context;
		this.width = width;
		this.height = height;
		this.progress = progressDialog;
	}
	
	/**
	 * 用post方法发起请求。
	 * @param context
	 * @param url
	 * @param postDatas post的参数
	 * @param listener
	 */
	public TencentDialog(Context context, String url, String postDatas,
			TencentAuthListener listener) {
		this(context, url, listener);
		mPostData = postDatas;
		this.context = context;
//		isPost = true;
	}
	public TencentDialog(Context context, String url,
			TencentAuthListener listener, boolean showTitle) {
		super(context);
		mUrl = url;
		mListener = listener;
		this.showTitle = showTitle;
		this.context = context;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("D", "TencentDialog onCreate");
		Log.d("d", "TencentDialog onCreate 1111111");
//		progress = new ProgressDialog(getContext());
//		progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		progress.setMessage("Loading...");
//		progress.show();
		content = new LinearLayout(getContext());
		content.setOrientation(LinearLayout.VERTICAL);
		
		top = (RelativeLayout) getLayoutInflater().inflate(R.layout.top_back, null, false);
		TextView title = (TextView) top.findViewById(R.id.top_title);
		title.setText(this.getContext().getString(R.string.tencent_share_title));
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (this.showTitle) {
			setUpTitle();
		}
		setUpWebView();

//		DIMENSIONS_LANDSCAPE = {dipToPx(this.context, 700), dipToPx(this.context, 700)};
//		DIMENSIONS_LANDSCAPE = {700, 600};
		
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		float scale = getContext().getResources().getDisplayMetrics().density;
		
		if(width != 0 && height != 0){
			DIMENSIONS_LANDSCAPE[0] = dipToPx(context, width);
			DIMENSIONS_LANDSCAPE[1] = dipToPx(context, height);
			DIMENSIONS_PORTRAIT[0] = dipToPx(context, height);
			DIMENSIONS_PORTRAIT[1] = dipToPx(context, width);
		}
		
		float[] dimensions = display.getWidth() < display.getHeight() ? DIMENSIONS_PORTRAIT
				: DIMENSIONS_LANDSCAPE;
		addContentView(content, new FrameLayout.LayoutParams(
				(int) (dimensions[0] * scale + 0.5f), (int) (dimensions[1]
						* scale + 0.5f)));
	}
	
	public float dipToPx(Context context, int dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        float pxValue = (float) (dipValue * scale + 0.5f);
        return pxValue;
    }

	private void setUpTitle() {
		Drawable icon = getContext().getResources().getDrawable(
				R.drawable.content_bg);
		title = new TextView(getContext());
		title.setText("与腾讯微博连接");
		title.setTextColor(Color.WHITE);
		title.setGravity(Gravity.CENTER_VERTICAL);
		title.setTypeface(Typeface.DEFAULT_BOLD);
		title.setBackgroundColor(RENREN_BLUE);
		title.setBackgroundResource(R.drawable.content_bg);
		title.setCompoundDrawablePadding(6);
		title.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
		content.addView(title);
	}
	private void setUpWebView() {
		webView = new WebView(getContext());
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new TencentWebViewClient());
		if (isPost) {
//			webView.postUrl(mUrl, EncodingUtils.getBytes(mPostData, "BASE64"));
		} else {
			OAuthV1 oAuth = TWeibo.getInstance().getAuth();
			mUrl = OAuthConstants.OAUTH_V1_AUTHORIZE_URL+"?oauth_token="+oAuth.getOauthToken();
			webView.loadUrl(mUrl);
		}
		FrameLayout.LayoutParams fill = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		webView.setLayoutParams(fill);
		content.addView(top);
		content.addView(webView);
		
		
	}

	/**
	 * Reponse on the Back key click to close the authrization dialog and send
	 * back the Cancel message Added by Shaofeng Wang
	 * (shaofeng.wang@renren-inc.com)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView != null) {
				webView.stopLoading();
			}
			if (progress != null) {
				progress.dismiss();
			}
			TencentDialog.this.dismiss();
//			mListener.onPageBegin(Renren.DEFAULT_REDIRECT_URI + "?error="
//					+ CODE_AUTH_CANCEL);
			this.cancel();
		}
		return super.onKeyDown(keyCode, event);
	}

	private class TencentWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(LOG_TAG, "Redirect URL: " + url);
			view.loadUrl(url);
			int b = mListener.onPageBegin(url);
			switch (b) {
			case TencentAuthListener.ACTION_PROCCESSED:
				TencentDialog.this.dismiss();
				return true;
			case TencentAuthListener.ACTION_DIALOG_PROCCESS:
				return false;
			}
//			getContext().startActivity(
//					new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(LOG_TAG, "Webview loading URL: " + url);
			boolean b = mListener.onPageStart(url);
			if (url.indexOf("checkType=verifycode") != -1) {
                int start=url.indexOf("checkType=verifycode&v=")+23;
                String verifyCode=url.substring(start, start+6);
//                oAuth.setOauthVerifier(verifyCode);
//                TWeibo.getInstance().saveUserInfo(getApplicationContext(), oAuth);
                Bundle bundle = new Bundle();
                bundle.putString("verifyCode", verifyCode);
                mListener.onComplete(bundle);
                view.destroyDrawingCache();
                view.destroy();
                TencentDialog.this.dismiss();
            }
			if (b) {
				view.stopLoading();
				TencentDialog.this.dismiss();
				return;
			}
			super.onPageStarted(view, url, favicon);
			Log.e("D", "-------------mSpinner.show()");
			progress.show();
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onReceivedError(errorCode, description, failingUrl);
			progress.hide();
			TencentDialog.this.dismiss();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mListener.onPageFinished(url);
			if (showTitle) {
				String t = view.getTitle();
				if (t != null && t.length() > 0) {
					title.setText(t);
				}
			}
			progress.hide();
		}
	}
	
//	private WebViewClient client = new WebViewClient() {
//		/**
//		 * 回调方法，当页面开始加载时执行
//		 */
//		@Override
//		public void onPageStarted(WebView view, String url, Bitmap favicon) {
////			Log.i(TAG, "WebView onPageStarted...");
//			Log.i("D", "URL = " + url);
//			
//		 if (url.indexOf("checkType=verifycode") != -1) {
//                int start=url.indexOf("checkType=verifycode&v=")+23;
//                String verifyCode=url.substring(start, start+6);
////                oAuth.setOauthVerifier(verifyCode);
////                TWeibo.getInstance().saveUserInfo(getApplicationContext(), oAuth);
//                mListener.callBack(verifyCode);
//                view.destroyDrawingCache();
//                view.destroy();
//                finish();
//            }
//			super.onPageStarted(view, url, favicon);
//			try{
//				if(mSpinner != null && !mSpinner.isShowing()){
//					if(!LoginActivity.this.isFinishing()){
//						mSpinner.show();
//					}
//				}
//			}catch (Exception e) {
//			}
//		}
//
//		/*
//		 * TODO Android2.2及以上版本才能使用该方法
//		 * 目前https://open.t.qq.com中存在http资源会引起sslerror，待网站修正后可去掉该方法
//		 */
//		public void onReceivedSslError(WebView view,
//				SslErrorHandler handler, SslError error) {
//			if ((null != view.getUrl())
//					&& (view.getUrl().startsWith("https://open.t.qq.com"))) {
//				handler.proceed();// 接受证书
//			} else {
//				handler.cancel(); // 默认的处理方式，WebView变成空白页
//			}
//			// handleMessage(Message msg); 其他处理
//		}
//		
//		public void onPageFinished(WebView view, String url) {
//				Log.d(TAG, "onPageFinished URL: " + url);
//				super.onPageFinished(view, url);
//				try{
//					if (mSpinner != null && mSpinner.isShowing()) {
//						if(!LoginActivity.this.isFinishing()){
//							mSpinner.dismiss();
//						}
//					}
//				}catch (Exception e) {
//				}
//
//				mContent.setBackgroundColor(Color.TRANSPARENT);
//				//webViewContainer.setBackgroundResource(R.drawable.weibosdk_dialog_bg);
//				// mBtnClose.setVisibility(View.VISIBLE);
//				mWebView.setVisibility(View.VISIBLE);
//			
//		};
//	};

	@Override
	protected void onStop() {
		super.onStop();
		if (progress != null) {
			progress.dismiss();
		}
	}
	
	
}
