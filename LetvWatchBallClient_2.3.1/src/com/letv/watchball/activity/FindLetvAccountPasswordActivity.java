package com.letv.watchball.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.letv.watchball.R;
import com.letv.watchball.utils.LetvConstant;

public class FindLetvAccountPasswordActivity extends PimBaseActivity implements View.OnClickListener{
    private RelativeLayout byMsgText;
    private RelativeLayout byEmailText;
	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.find_letvaccount_password;
	}
	
	public static void launch(Activity context){
		Intent intent = new Intent(context,FindLetvAccountPasswordActivity.class);
		context.startActivity(intent);
//		context.finish();
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		findView();
		setTitle(R.string.find_password);
	}
	
	@Override
	public void findView() {
		// TODO Auto-generated method stub
		super.findView();
		byEmailText = (RelativeLayout) findViewById(R.id.find_psw_by_email);
		byMsgText = (RelativeLayout) findViewById(R.id.find_psw_by_msg);
		byEmailText.setOnClickListener(this);
		byMsgText.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.find_psw_by_email:
			LetvWebViewActivity.launch(FindLetvAccountPasswordActivity.this, LetvConstant.retrieve_pwd_byemail_url, 
					getResources().getString(R.string.find_pass_by_email));
			break;
		case R.id.find_psw_by_msg:
			FindPsswordByMessage.launch(FindLetvAccountPasswordActivity.this);
			break;
		default:
			break;  
		}
	}
	

}
