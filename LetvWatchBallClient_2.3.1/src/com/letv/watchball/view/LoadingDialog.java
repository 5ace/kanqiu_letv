package com.letv.watchball.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import com.letv.watchball.R;

public class LoadingDialog extends ProgressDialog{

	private int msgId ;
	
	public LoadingDialog(Context context , int msgId) {
		super(context,R.style.LoadingDialog_Fullscreen);
		this.msgId = msgId ;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadingdialog);
		TextView msg = (TextView) findViewById(R.id.msg);
		msg.setText(msgId);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}
}
