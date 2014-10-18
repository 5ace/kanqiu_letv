package com.letv.watchball.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letv.watchball.R;

public class PublicLoadLayout
		extends FrameLayout {

	private LinearLayout content;

	private View loading;

	private View error;
	private TextView refreshBtn;
	private Context context;
	private TextView errorTxt;
	private ImageView errorImage;

	private RefreshData mRefreshData;

	public RefreshData getmRefreshData() {
		return mRefreshData;
	}

	public void setmRefreshData(RefreshData refreshData) {
		this.mRefreshData = refreshData;
	}

	public PublicLoadLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PublicLoadLayout(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		inflate(context, R.layout.public_loading_layout, this);
		findView();
	}

	public void addContent(int viewId) {
		inflate(getContext(), viewId, content);
	}

	private void findView() {
		errorImage = (ImageView) findViewById(R.id.net_error_flag);
		content = (LinearLayout) findViewById(R.id.content);
		loading = findViewById(R.id.loading);
		error = findViewById(R.id.error);
		refreshBtn = (TextView) findViewById(R.id.try_agin);
		refreshBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mRefreshData != null) {
					mRefreshData.refreshData();
				}
			}
		});
		errorTxt = (TextView) findViewById(R.id.errorTxt);
	}

	public void loading(boolean isShowContent) {
		loading.setVisibility(View.VISIBLE);
		error.setVisibility(View.GONE);
		if (isShowContent) {
			content.setVisibility(View.VISIBLE);
		} else {
			content.setVisibility(View.GONE);
		}
	}

	public void loading(boolean isShowContent, boolean shouLoading) {
		loading.setVisibility(View.GONE);
		error.setVisibility(View.GONE);
		if (isShowContent) {
			content.setVisibility(View.VISIBLE);
		} else {
			content.setVisibility(View.GONE);
		}
	}

	public void finish() {
		loading.setVisibility(View.GONE);
		error.setVisibility(View.GONE);
		content.setVisibility(View.VISIBLE);
	}
	public void finishLoad() {
		loading.setVisibility(View.GONE);
	}

	// ,boolean isHome
	public void error(boolean isShowContent, boolean isHome) {
		loading.setVisibility(View.GONE);
		error.setVisibility(View.VISIBLE);
		// errorTxt.setText("数据获取失败");
		if (isShowContent) {
			content.setVisibility(View.VISIBLE);
		} else {
			content.setVisibility(View.GONE);
		}
	}

	public void error(boolean isShowContent) {
		loading.setVisibility(View.GONE);
		error.setVisibility(View.GONE);
		refreshBtn.setVisibility(View.VISIBLE);
		if (isShowContent) {
			content.setVisibility(View.VISIBLE);
		} else {
			content.setVisibility(View.GONE);
		}
	}

	public void error(String errMsg) {
		loading.setVisibility(View.GONE);
		errorImage.setVisibility(View.GONE);
		error.setVisibility(View.VISIBLE);
		errorTxt.setText(errMsg);
		content.setVisibility(View.GONE);
		refreshBtn.setVisibility(View.GONE);
	}

	public void error(String errMsg, boolean isDisp) {
		loading.setVisibility(View.GONE);
		errorImage.setVisibility(View.GONE);
		error.setVisibility(View.VISIBLE);
		errorTxt.setText(errMsg);
		refreshBtn.setVisibility(View.GONE);
		content.setVisibility(isDisp ? View.VISIBLE : View.GONE);
	}

	public void showErrorMessage(String errMsg) {
		loading.setVisibility(View.GONE);
		errorImage.setVisibility(View.GONE);
		error.setVisibility(View.VISIBLE);
		errorTxt.setText(errMsg);
		content.setVisibility(View.VISIBLE);
		refreshBtn.setVisibility(View.GONE);
	}

	public void error(int errmsg) {
		loading.setVisibility(View.GONE);
		errorImage.setVisibility(View.GONE);
		error.setVisibility(View.VISIBLE);
		errorTxt.setText(errmsg);
		content.setVisibility(View.GONE);
		refreshBtn.setVisibility(View.GONE);
	}

	public interface RefreshData {
		public void refreshData();
	}

}
