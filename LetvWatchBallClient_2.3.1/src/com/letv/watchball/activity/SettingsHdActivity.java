package com.letv.watchball.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.letv.watchball.R;
import com.letv.watchball.db.PreferencesManager;

public class SettingsHdActivity extends Activity implements OnClickListener {

	private TextView select_item1,select_item2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_play_hd);
		findViewById(R.id.head_back).setOnClickListener(this);
		findViewById(R.id.setting_play_hd_gaoqing).setOnClickListener(this);
		findViewById(R.id.setting_play_hd_liuchang).setOnClickListener(this);
		
		select_item1=(TextView) findViewById(R.id.select_item1);
		select_item2=(TextView) findViewById(R.id.select_item2);
		
		if(PreferencesManager.getInstance().isPlayHd()!=0){
			
			select_item1.setVisibility(View.VISIBLE);
			
		}else{
			
			select_item2.setVisibility(View.VISIBLE);
		
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_back:
			finish();
			break;
		case R.id.setting_play_hd_gaoqing:
			PreferencesManager.getInstance().setIsPlayHd(1);
			setResult(RESULT_OK);
			select_item1.setVisibility(View.VISIBLE);
			select_item2.setVisibility(View.GONE);
			finish();
			break;
		case R.id.setting_play_hd_liuchang:
			PreferencesManager.getInstance().setIsPlayHd(0);
			setResult(RESULT_OK);
			select_item2.setVisibility(View.VISIBLE);
			select_item1.setVisibility(View.GONE);
			finish();
			break;
		default:
			break;
		}
	}
}
