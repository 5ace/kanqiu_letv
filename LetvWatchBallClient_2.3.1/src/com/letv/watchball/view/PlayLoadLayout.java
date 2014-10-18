package com.letv.watchball.view;

import android.content.Context;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.letv.watchball.R;
import com.letv.watchball.utils.NetWorkTypeUtils;

public class PlayLoadLayout extends FrameLayout implements OnClickListener {

	private int errState = 0;

	private View loading;

	/**
	 * 没有视频
	 * */
	private View not_play;

	/**
	 * 请求错误提示
	 * */
	private View request_error;

	/**
	 * 请求错误提示点击
	 * */
	private View request_error_btn;
	

	/**
	 * 播放失败
	 * */
	private View cannot_play;

	/**
	 * 播放失败提示点击
	 * */
	private View cannot_play_btn;

	/**
	 * vip付费提示
	 * */
	private View vip_not_login_error;

	/**
	 * vip付费提示
	 * */
	private View vip_login_error;

	/**
	 * 无版权外跳提示
	 * */
	private View jump_error;

	/**
	 * ip海外版权提示
	 * */
	private View ip_error;

	/**
	 * 点播片提示
	 * */
	private View demand_error;
	
	/**
	 * 本地播放异常
	 * */
	private View local_error ;

	/**
	 * vip付费提示按钮
	 * */
	private View vip_not_login_error_btn;

	/**
	 * vip付费提示按钮
	 * */
	private View vip_login_error_btn;

	/**
	 * 无版权外跳提示按钮
	 * */
	private View jump_error_btn;

	/**
	 * 点播片提示按钮
	 * */
	private View demand_error_btn;

	private PlayLoadLayoutCallBack callBack;

	public PlayLoadLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PlayLoadLayout(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		inflate(context, R.layout.play_loading_layout, this);
		findView();
	}

	public int getErrState() {
		return errState;
	}

	public void setErrState(int errState) {
		this.errState = errState;
	}

	private void findView() {
		loading = findViewById(R.id.loading);
		not_play = findViewById(R.id.no_play_error);
		request_error = findViewById(R.id.request_error);
		request_error_btn = findViewById(R.id.request_error_btn);
		
		cannot_play = findViewById(R.id.cannot_play);
		cannot_play_btn = findViewById(R.id.cannot_play_btn);
		
		vip_not_login_error = findViewById(R.id.vip_not_login_error);
		vip_login_error = findViewById(R.id.vip_login_error);
		jump_error = findViewById(R.id.jump_error);
		ip_error = findViewById(R.id.ip_error);
		vip_not_login_error_btn = findViewById(R.id.vip_not_login_error_button);
		vip_login_error_btn = findViewById(R.id.vip_login_error_button);
		jump_error_btn = findViewById(R.id.jump_error_button);
		demand_error = findViewById(R.id.demand_error);
		demand_error_btn = findViewById(R.id.demand_error_button);
		local_error = findViewById(R.id.local_error);

		request_error_btn.setOnClickListener(this);
		cannot_play_btn.setOnClickListener(this);
		vip_not_login_error_btn.setOnClickListener(this);
		vip_login_error_btn.setOnClickListener(this);
		jump_error_btn.setOnClickListener(this);
		demand_error_btn.setOnClickListener(this);
	}

	public void loading() {
		loading.setVisibility(VISIBLE);
		not_play.setVisibility(GONE);
		request_error.setVisibility(GONE);
		cannot_play.setVisibility(GONE);
		vip_not_login_error.setVisibility(GONE);
		vip_login_error.setVisibility(GONE);
		jump_error.setVisibility(GONE);
		ip_error.setVisibility(GONE);
		demand_error.setVisibility(GONE);
		local_error.setVisibility(GONE);
		Log.e("xue", "loading--------------");
	}

	public void finish() {
		errState = 0;
		loading.setVisibility(View.GONE);
		not_play.setVisibility(GONE);
		request_error.setVisibility(GONE);
		cannot_play.setVisibility(GONE);
		vip_not_login_error.setVisibility(GONE);
		vip_login_error.setVisibility(GONE);
		jump_error.setVisibility(GONE);
		ip_error.setVisibility(GONE);
		demand_error.setVisibility(GONE);
		local_error.setVisibility(GONE);
	}

	public void notPlay() {
		errState = 1;
		loading.setVisibility(View.GONE);
		not_play.setVisibility(VISIBLE);
		request_error.setVisibility(GONE);
		cannot_play.setVisibility(GONE);
		vip_not_login_error.setVisibility(GONE);
		vip_login_error.setVisibility(GONE);
		jump_error.setVisibility(GONE);
		ip_error.setVisibility(GONE);
		demand_error.setVisibility(GONE);
		local_error.setVisibility(GONE);
	}

	public void requestError() {
		errState = 2;
		loading.setVisibility(View.GONE);
		not_play.setVisibility(GONE);
		request_error.setVisibility(VISIBLE);
		cannot_play.setVisibility(GONE);
		vip_not_login_error.setVisibility(GONE);
		vip_login_error.setVisibility(GONE);
		jump_error.setVisibility(GONE);
		ip_error.setVisibility(GONE);
		demand_error.setVisibility(GONE);
		local_error.setVisibility(GONE);
	}
	
	public void cannotPlayError() {
		errState = 2;
		loading.setVisibility(View.GONE);
		not_play.setVisibility(GONE);
		cannot_play.setVisibility(VISIBLE);
		request_error.setVisibility(GONE);
		cannot_play.setVisibility(GONE);
		vip_not_login_error.setVisibility(GONE);
		vip_login_error.setVisibility(GONE);
		jump_error.setVisibility(GONE);
		ip_error.setVisibility(GONE);
		demand_error.setVisibility(GONE);
		local_error.setVisibility(GONE);
	}

	public void vipNotLoginError() {
		errState = 3;
		loading.setVisibility(View.GONE);
		not_play.setVisibility(GONE);
		request_error.setVisibility(GONE);
		cannot_play.setVisibility(GONE);
		vip_not_login_error.setVisibility(VISIBLE);
		vip_login_error.setVisibility(GONE);
		jump_error.setVisibility(GONE);
		ip_error.setVisibility(GONE);
		demand_error.setVisibility(GONE);
		local_error.setVisibility(GONE);
	}

	public void vipLoginError() {
		errState = 4;
		loading.setVisibility(View.GONE);
		not_play.setVisibility(GONE);
		request_error.setVisibility(GONE);
		cannot_play.setVisibility(GONE);
		vip_not_login_error.setVisibility(GONE);
		vip_login_error.setVisibility(VISIBLE);
		jump_error.setVisibility(GONE);
		ip_error.setVisibility(GONE);
		demand_error.setVisibility(GONE);
		local_error.setVisibility(GONE);
	}

	public void demandError() {
		errState = 5;
		loading.setVisibility(View.GONE);
		not_play.setVisibility(GONE);
		request_error.setVisibility(GONE);
		cannot_play.setVisibility(GONE);
		vip_not_login_error.setVisibility(GONE);
		vip_login_error.setVisibility(GONE);
		jump_error.setVisibility(GONE);
		ip_error.setVisibility(GONE);
		demand_error.setVisibility(VISIBLE);
		local_error.setVisibility(GONE);
	}

	public void jumpError() {
		errState = 6;
		loading.setVisibility(View.GONE);
		not_play.setVisibility(GONE);
		request_error.setVisibility(GONE);
		cannot_play.setVisibility(GONE);
		vip_not_login_error.setVisibility(GONE);
		vip_login_error.setVisibility(GONE);
		jump_error.setVisibility(VISIBLE);
		ip_error.setVisibility(GONE);
		demand_error.setVisibility(GONE);
		local_error.setVisibility(GONE);
	}

	public void ipError() {
		errState = 7;
		loading.setVisibility(View.GONE);
		not_play.setVisibility(GONE);
		request_error.setVisibility(GONE);
		cannot_play.setVisibility(GONE);
		vip_not_login_error.setVisibility(GONE);
		vip_login_error.setVisibility(GONE);
		jump_error.setVisibility(GONE);
		ip_error.setVisibility(VISIBLE);
		demand_error.setVisibility(GONE);
		local_error.setVisibility(GONE);
	}
	
	public void localError() {
		errState = 8;
		loading.setVisibility(View.GONE);
		not_play.setVisibility(GONE);
		request_error.setVisibility(GONE);
		cannot_play.setVisibility(GONE);
		vip_not_login_error.setVisibility(GONE);
		vip_login_error.setVisibility(GONE);
		jump_error.setVisibility(GONE);
		ip_error.setVisibility(GONE);
		demand_error.setVisibility(GONE);
		local_error.setVisibility(VISIBLE);
	}

	@Override
	public void onClick(View v) {
		NetworkInfo networkInfo = NetWorkTypeUtils.getAvailableNetWorkInfo();
		if(networkInfo== null) {
			Toast.makeText(getContext(), "没有网络 ", Toast.LENGTH_SHORT).show();
			return;
		}
		if (request_error_btn == v) {
			if (callBack != null)
				callBack.onRequestErr();
			errState = 0;
		} else if (vip_not_login_error_btn == v) {
			if (callBack != null)
				callBack.onVipErr(false);
			errState = 0;
		} else if (vip_login_error_btn == v) {
			if (callBack != null)
				callBack.onVipErr(true);
			errState = 0;
		} else if (jump_error_btn == v) {
			if (callBack != null)
				callBack.onJumpErr();
			errState = 0;
		} else if (demand_error_btn == v) {
			if (callBack != null)
				callBack.onDemandErr();
			errState = 0;
		} else if(cannot_play_btn == v) {
			
				callBack.onPlayFailed();
				errState = 0;
			
			
		}
	}

	public PlayLoadLayoutCallBack getCallBack() {
		return callBack;
	}

	public void setCallBack(PlayLoadLayoutCallBack callBack) {
		this.callBack = callBack;
	}

	/**
	 * 点击回调
	 * */
	public interface PlayLoadLayoutCallBack {
		void onRequestErr();

		void onVipErr(boolean isLogin);

		void onJumpErr();

		void onDemandErr();
		
		void onPlayFailed();
	}
}
