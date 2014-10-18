package com.letv.watchball.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.letv.watchball.R;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.PublicLoadLayout;

/**
 * 个人信息各功能activity的base activity，复用了标题栏各控件，并初始化
 * 
 * @author zhanglibin
 * 
 */
public abstract class PimBaseActivity extends LetvBaseActivity {
	public PublicLoadLayout mRootView;
	private ImageView mBack;// 返回键
	private TextView mTitle;// 标题
	private ImageView mVipIcon;// vip图标
	private Button mSendto;// 发送按钮

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		mRootView = UIs.createPage(this, getContentView());
		setContentView(mRootView);

	}

	/**
	 * 设置无网或无数据等信息
	 */
	public void showErrorLayoutMessage(String msg) {
		if (mRootView != null) {
			mRootView.showErrorMessage(msg);
		}
	}
	
	/**
	 * 设置无网信息
	 */
	public void showNetNullMessage() {
		if (mRootView != null) {
			mRootView.error(true,false);
		}
	}

	/**
	 * 显示加载对话框
	 */
	public void showLoading() {
		if (mRootView != null) {
			mRootView.loading(true);
		}
	}

	/**
	 * 设置无网或无数据等提示信息
	 */
	public void showErrorLayoutMessage(int msg) {
		if (mRootView != null) {
			mRootView.showErrorMessage(getResources().getString(msg));
		}
	}

	/**
	 * 隐藏提示信息和loading对话框
	 */
	public void hideErrorLayoutMessage() {
		if (mRootView != null) {
			mRootView.finish();
		}
	}

	public void findView() {
		mBack = (ImageView) findViewById(R.id.btn_back);
		mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				finish();
			}
		});

		mTitle = (TextView) findViewById(R.id.title);
//		mVipIcon = (ImageView) findViewById(R.id.vip_icon);
//		mSendto = (Button) findViewById(R.id.btn_send);
	}

	public abstract int getContentView();

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		mTitle.setText(title);
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(int title) {
		mTitle.setText(title);
	}

	protected void setOnBackClickListener(OnClickListener l) {
		mBack.setOnClickListener(l);
	}

	protected void setVipIconVisiable(boolean b) {
		mVipIcon.setVisibility(b ? View.VISIBLE : View.GONE);
	}

	protected void setSendBtnVisiable(boolean b) {
		mSendto.setVisibility(b ? View.VISIBLE : View.GONE);
	}

	/**
	 * 发送按钮监听器
	 * 
	 * @param l
	 */
	protected void setOnSendClickListener(OnClickListener l) {
		mSendto.setOnClickListener(l);
	}
}
