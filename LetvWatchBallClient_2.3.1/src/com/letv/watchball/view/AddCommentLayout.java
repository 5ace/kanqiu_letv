package com.letv.watchball.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.letv.http.bean.LetvDataHull;
import com.letv.http.parse.LetvGsonParser;
import com.letv.watchball.R;
import com.letv.watchball.activity.LoginMainActivity;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.Base;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.ui.PlayAlbumController.PlayAlbumControllerCallBack;
import com.letv.watchball.ui.PlayController;
import com.letv.watchball.ui.PlayLiveController;
import com.letv.watchball.ui.impl.BasePlayActivity;
import com.letv.watchball.utils.UIs;

public class AddCommentLayout extends LinearLayout {
	private View add_comment_send, add_comment_close;
	private ImageView add_comment_back;
	private EditText add_comment_edit;
	private RelativeLayout add_comment_title;
	private Activity activity;
	private PlayController playAlbumController;
	private PlayLiveController  PlayLiveController ;
	private InputHandler mHandler;
	/**
	 * 直播
	 */
	public static final int LAUNCH_MODE_LIVE = 4;
	/**
	 * 直播 全屏直播
	 */
	public static final int LAUNCH_MODE_LIVE_FULL = 5;
	public void init() {
		activity = (Activity) getContext();
		if(((BasePlayActivity) getContext()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE||((BasePlayActivity) getContext()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE_FULL){
			
			PlayLiveController = (PlayLiveController) ((BasePlayActivity) getContext()).mPlayController;
			System.out.println("PlayLiveController==");
		}else{
		
			playAlbumController = (PlayController) ((BasePlayActivity) getContext()).mPlayController;
			System.out.println("PlayLiveController==");
		}
		initCommentEditText();
	}

	public AddCommentLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void initCommentEditText() {
		mHandler = new InputHandler();
		add_comment_close = findViewById(R.id.add_comment_close);
		add_comment_send = findViewById(R.id.add_comment_send);
		add_comment_back = (ImageView) findViewById(R.id.add_comment_back);
		add_comment_edit = (EditText) findViewById(R.id.add_comment_edit);
		add_comment_title = (RelativeLayout) findViewById(R.id.add_comment_title);
		add_comment_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				add_comment_edit.getText().clear();
				// 强行隐藏输入法
				((InputMethodManager) ((Activity) getContext()).getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
						((Activity) getContext()).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				mHandler.sendEmptyMessage(KeybordClose);
			}
		});
		add_comment_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.finish();
			}
		});
		add_comment_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String content = add_comment_edit.getText().toString();
				if (!TextUtils.isEmpty(content)) {
					
					if(content.length()<5){
						UIs.showToast("评论小于5个字！");
						return;
					}
					add_comment_edit.getText().clear();
                    content.replace(" ","");
					// 强行隐藏输入法
					((InputMethodManager) ((Activity) getContext()).getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            ((Activity) getContext()).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					new RequestSendCommentTask(activity, content).start();
				} else {
					UIs.showToast("请输入评论！");
				}
			}
		});
		add_comment_edit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
		
				if (!PreferencesManager.getInstance().isLogin()) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						UIs.call(activity, R.string.dialog_message_not_login, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								LoginMainActivity.launch(activity);
							}
						}, null);
					}
					return true;
				}
				// 显示键盘
				mHandler.sendEmptyMessage(KeybordOpen);
				return false;
			}
		});
	}

	private void onKeyBordOpen() {
		LinearLayout.LayoutParams params = (LayoutParams) add_comment_edit.getLayoutParams();
		params.height = (int) (0.2f*UIs.getScreenHeight());
		add_comment_edit.requestLayout();
//		int padding = UIs.dipToPx(10);
//		add_comment_edit.setPadding(padding, padding, padding, padding);
		add_comment_edit.setGravity(Gravity.TOP);
//		add_comment_edit.setInputType(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
		
		add_comment_title.setVisibility(View.VISIBLE);
		add_comment_back.setVisibility(View.GONE);
	}

	private void onKeyBordClose() {
			LinearLayout.LayoutParams params = (LayoutParams) add_comment_edit.getLayoutParams();
			params.height = UIs.dipToPx(30);
			add_comment_edit.requestLayout();
//			add_comment_edit.setPadding(0, 0, 0, 0);
			add_comment_edit.setGravity(Gravity.CENTER_VERTICAL);
//		add_comment_edit.setInputType(EditorInfo.TYPE_NULL);
		
		add_comment_title.setVisibility(View.GONE);
		add_comment_back.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d("lhz", "w h oldw oldh:" + w + " " + h + " " + oldw + " " + oldh);
		if(null != mHandler){
			if (h > oldh) {
				// 隐藏键盘
				mHandler.sendEmptyMessage(KeybordClose);
			} else {
				// 显示键盘
//				mHandler.sendEmptyMessage(KeybordOpen);
				// onKeyBordOpen();
			}
		}
	}

	private static final int KeybordOpen = 0;
	private static final int KeybordClose = 1;

	private class InputHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case KeybordOpen:
				onKeyBordOpen();
				break;
			case KeybordClose:
				onKeyBordClose();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 请求数据
	 * 
	 */
	private class RequestSendCommentTask extends LetvHttpAsyncTask<Base> {

		private String content;

		public RequestSendCommentTask(Context context, String content) {
			super(context);
			this.content = content;
		}

		@Override
		public LetvDataHull<Base> doInBackground() {
			LetvDataHull<Base> requestAddComment = null;

		
			if(((BasePlayActivity) getContext()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE||((BasePlayActivity) getContext()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE_FULL){
				int id = (int) PlayLiveController.id;
				System.out.println("id=="+id);
				requestAddComment = LetvHttpApi.requestAddComment(0, new LetvGsonParser<Base>(0, Base.class), id + "", content);
			
			}else{
				int vid = (int) playAlbumController.vid;
				System.out.println("vid=="+vid);
				requestAddComment = LetvHttpApi.requestAddComment(0, new LetvGsonParser<Base>(0, Base.class), vid + "", content);
			
			}

			
		
			// requestDetailRecommendInfo = LetvHttpApi.requestGetComment(0, new
			// LetvGsonParser<Comments>(0, Comments.class),
			// PreferencesManager.getInstance()
			// .getSso_tk(), vid+"", pn+"", ps+"");
			return requestAddComment;
		}

		@Override
		public void onPostExecute(int updateId, Base result) {
			if (result.header.status.equals("1")) {
                UIs.showToast("评论成功！");
                // 刷新评论列表
            
				if(((BasePlayActivity) getContext()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE||((BasePlayActivity) getContext()).mPlayController.getLaunchMode()==LAUNCH_MODE_LIVE_FULL){
					PlayLiveController.getCommentsCallBack.notify(PlayAlbumControllerCallBack.STATE_RETRY);
					PlayLiveController.getViewPager().setCurrentItem(1);
					PlayLiveController.content= content;
				}else{
				
					playAlbumController.getViewPager().setCurrentItem(1);
					playAlbumController.getCommentsCallBack.notify(PlayAlbumControllerCallBack.STATE_RETRY);
					playAlbumController.content= content;
				}
				
			} else {
				UIs.showToast("评论失败！");
			}

		}

		@Override
		public void netNull() {
            UIs.showToast("评论失败！网络连接不给力");
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
		}
	}

}