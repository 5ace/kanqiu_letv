package com.letv.watchball.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.letv.watchball.R;
import com.letv.watchball.utils.LetvUtil;

public class SettingsAboutActivity extends Activity implements OnClickListener {

	String mail, tel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_about);
		findViewById(R.id.head_back).setOnClickListener(this);
		findViewById(R.id.settings_mail).setOnClickListener(this);
		findViewById(R.id.setting_tel).setOnClickListener(this);
		String versionName = " V" + LetvUtil.getClientVersionName();
		mail = getResources().getString(R.string.settings_about_mail);
		tel = getResources().getString(R.string.settings_about_tel);
		((TextView) findViewById(R.id.settings_about_version_name)).append(versionName);
		((TextView) findViewById(R.id.settings_mail_tv)).append(mail);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_back:
			finish();
			break;
		case R.id.settings_mail:
			// 邮箱
			if (null != mail) {
				Intent data = new Intent(Intent.ACTION_SENDTO);
				data.setData(Uri.parse("mailto:" + mail));
				// data.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
				// data.putExtra(Intent.EXTRA_TEXT, "这是内容");
				startActivity(data);
			}
			break;
		case R.id.setting_tel:
			// 电话
			if (null != tel) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
				startActivity(intent);
			}
			break;
		default:
			break;
		}
	}
}
