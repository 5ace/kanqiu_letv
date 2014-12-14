package com.letv.watchball.view;

import com.letv.cache.LetvCacheMannager;
import com.letv.watchball.R;
import com.letv.watchball.ui.PlayLiveController;
import com.letv.watchball.view.PlayLoadLayout.PlayLoadLayoutCallBack;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayHalfPay extends FrameLayout implements OnClickListener {
	private ImageView homeImage;
	private ImageView guestImage;
	private TextView button;

	private View live_pay_login_container;
	private String homeImageUrl;
	private String guestImageUrl;
	private TextView ticketCount;
	private PlayHalfPayCallBack callBack;
	private static int status;
	private TextView button1;
	private TextView button2;
	private View live_nologin_container;

	public PlayHalfPay(Context context, String homeImageUrl,
			String guestImageUrl) {
		super(context);
		this.homeImageUrl = homeImageUrl;
		this.guestImageUrl = guestImageUrl;
		init(context);

	}

	private void init(Context context) {
		inflate(context, R.layout.live_pay_layout, this);
		findView();
		initGameView();
	}

	private void findView() {
		live_pay_login_container = findViewById(R.id.live_pay_login_container);
		button1 = (TextView) findViewById(R.id.button1);
		button2 = (TextView) findViewById(R.id.button2);
		live_nologin_container = findViewById(R.id.live_nologin_container);
		homeImage = (ImageView) findViewById(R.id.live_pay_home_icon);
		guestImage = (ImageView) findViewById(R.id.live_pay_guest_icon);
		button = (TextView) findViewById(R.id.live_pay_login);
		button.setOnClickListener(this);
		ticketCount = (TextView) findViewById(R.id.live_pay_login_text2);
	}

	public void noLogin() {
		live_pay_login_container.setVisibility(GONE);
		;
		live_nologin_container.setVisibility(VISIBLE);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getCallBack().onLogin();
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getCallBack().onBuyTicket();
			}
		});
	}

	public void setTicketCount(String count) {
		//Log.e("gongmeng", "init ticket count:" + count);
		live_pay_login_container.setVisibility(VISIBLE);
		ticketCount.setText("您有" + count + "张直播券");
		button.setText("使用1张直播券立即观看比赛");
		status = 1;
	}

	public void setZeroTicket() {
		live_pay_login_container.setVisibility(VISIBLE);
		button.setText("立即订票");
		status = 0;
	}

	public void initGameView() {
		homeImage.setImageResource(R.drawable.ic_default);
		guestImage.setImageResource(R.drawable.ic_default);
		//Log.e("gongmeng", homeImageUrl + "  " + guestImageUrl + "url");
		LetvCacheMannager.getInstance().loadImage(homeImageUrl, homeImage);
		LetvCacheMannager.getInstance().loadImage(guestImageUrl, guestImage);
	}

	public void buy_ticket(View v) {
		this.getCallBack().onBuyTicket();
	}

	public void login_click(View v) {
		this.getCallBack().onLogin();
	}

	@Override
	public void onClick(View v) {
		this.getCallBack().onUseTicket(status);

	}

	public PlayHalfPayCallBack getCallBack() {
		return callBack;
	}

	public void setCallBack(PlayHalfPayCallBack callBack) {
		this.callBack = callBack;
	}

	public interface PlayHalfPayCallBack {
		void onUseTicket(int status);

		void onLogin();

		void onBuyTicket();
	}
}