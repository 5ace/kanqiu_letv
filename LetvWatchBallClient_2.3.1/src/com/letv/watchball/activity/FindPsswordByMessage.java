package com.letv.watchball.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.letv.watchball.R;
import com.letv.watchball.utils.LetvConstant;
import com.letv.watchball.utils.LetvUtil;

public class FindPsswordByMessage extends PimBaseActivity {
	private Button sendMsgBtn;
	public static void launch(Activity context){
		Intent intent = new Intent(context,FindPsswordByMessage.class);
		context.startActivity(intent);
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.find_psw_by_message;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		findView();
		setTitle(R.string.find_psw_message);
	}
	
	@Override
	public void findView() {
		// TODO Auto-generated method stub
		super.findView();
		sendMsgBtn = (Button) findViewById(R.id.send_btn);
		sendMsgBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LetvUtil.retrievePwdBySMS(FindPsswordByMessage.this, LetvConstant.retrievePwdPhoneNum);
			    FindPsswordByMessage.this.finish();
			}
		});
	}

}
