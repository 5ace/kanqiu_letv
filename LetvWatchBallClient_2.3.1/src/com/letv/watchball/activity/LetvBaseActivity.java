package com.letv.watchball.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.letv.datastatistics.DataStatistics;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.bean.IP;
import com.letv.watchball.utils.LetvUtil;
import com.umeng.analytics.MobclickAgent;

public class LetvBaseActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 注册友盟统计的crash事件统计
		MobclickAgent.onError(this);
		MobclickAgent.setDebugMode(true);
	
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
	}
	
}
