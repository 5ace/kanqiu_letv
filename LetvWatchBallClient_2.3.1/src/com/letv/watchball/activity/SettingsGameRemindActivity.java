package com.letv.watchball.activity;

import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.watchball.R;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.utils.LetvSubsribeGameUtil;

public class SettingsGameRemindActivity extends Activity implements OnItemClickListener, OnClickListener {

	// private RelativeLayout
	// gameRemind_5m,gameRemind_10m,gameRemind_30m,gameRemind_close;
	private TextView select_time_5,select_time_10,select_time_30,select_time_close;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_game_start_remind);
		// gameRemind_5m = (RelativeLayout)
		// findViewById(R.id.setting_game_start_5);
		// gameRemind_10m = (RelativeLayout)
		// findViewById(R.id.setting_game_start_10);
		// gameRemind_30m = (RelativeLayout)
		// findViewById(R.id.setting_game_start_30);
		// gameRemind_close = (RelativeLayout)
		// findViewById(R.id.setting_game_start_close);
		findViewById(R.id.head_back).setOnClickListener(this);
		findViewById(R.id.setting_game_start_5).setOnClickListener(this);
		findViewById(R.id.setting_game_start_10).setOnClickListener(this);
		findViewById(R.id.setting_game_start_30).setOnClickListener(this);
		findViewById(R.id.setting_game_start_close).setOnClickListener(this);
		onSelectTime();
	
	}
	public void onSelectTime(){
		
		select_time_5=(TextView) findViewById(R.id.select_time_5);
		select_time_10=(TextView)findViewById(R.id.select_time_10);
		select_time_30=(TextView)findViewById(R.id.select_time_30);
		select_time_close=(TextView)findViewById(R.id.select_time_close);
		
		int select_time =PreferencesManager.getInstance().getGameStartRemind();
		switch (select_time) {
		case 5:
			select_time_5.setVisibility(View.VISIBLE);
			break;
		case 10:
			select_time_10.setVisibility(View.VISIBLE);
			break;
		case 30:
			select_time_30.setVisibility(View.VISIBLE);
			break;
		case -1:
			select_time_close.setVisibility(View.VISIBLE);
			break;
	
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		PreferencesManager.getInstance().setGameStartRemind(getResources().getIntArray(R.array.game_remind_arr_int)[position]);
//		// 刷新闹钟
//		LetvSubsribeGameUtil.updateClock(this);
//		setResult(RESULT_OK);
//		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_back:
			finish();
			break;
		case R.id.setting_game_start_5:
			PreferencesManager.getInstance().setGameStartRemind(getResources().getIntArray(R.array.game_remind_arr_int)[0]);
			// 刷新闹钟
			LetvSubsribeGameUtil.updateClock(this);
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.setting_game_start_10:
			PreferencesManager.getInstance().setGameStartRemind(getResources().getIntArray(R.array.game_remind_arr_int)[1]);
			// 刷新闹钟
			LetvSubsribeGameUtil.updateClock(this);
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.setting_game_start_30:
			PreferencesManager.getInstance().setGameStartRemind(getResources().getIntArray(R.array.game_remind_arr_int)[2]);
			// 刷新闹钟
			LetvSubsribeGameUtil.updateClock(this);
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.setting_game_start_close:
			PreferencesManager.getInstance().setGameStartRemind(getResources().getIntArray(R.array.game_remind_arr_int)[3]);
			// 刷新闹钟
			LetvSubsribeGameUtil.updateClock(this);
			setResult(RESULT_OK);
			finish();
			break;
		default:
			break;
		}
	}
}
