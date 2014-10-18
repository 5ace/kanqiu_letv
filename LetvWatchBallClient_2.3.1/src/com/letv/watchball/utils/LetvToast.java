package com.letv.watchball.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.watchball.R;

public class LetvToast extends Toast{

	private View view ;
	
	private TextView msgView ;
	
	private ImageView iconView ;
	
	public LetvToast(Context context) {
		super(context);

		view = UIs.inflate(context, R.layout.ok_toast_layout, null, false);
		msgView = (TextView) view.findViewById(R.id.tosat_msg);
		iconView = (ImageView) view.findViewById(R.id.tosat_icon);
		setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		setView(view);
	}
	
	public LetvToast(Context context , String msg) {
		super(context);

		view = UIs.inflate(context, R.layout.ok_toast_layout, null, false);
		msgView = (TextView) view.findViewById(R.id.tosat_msg);
		iconView = (ImageView) view.findViewById(R.id.tosat_icon);
		setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		setView(view);
		setMsg(msg);
	}
	
	public LetvToast(Context context , int msg) {
		super(context);

		view = UIs.inflate(context, R.layout.ok_toast_layout, null, false);
		msgView = (TextView) view.findViewById(R.id.tosat_msg);
		iconView = (ImageView) view.findViewById(R.id.tosat_icon);
		setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		setView(view);
		setMsg(msg);
	}
	
	public void setMsg(String msg){
		msgView.setText(msg);
	}
	
	public void setMsg(int msg){
		msgView.setText(msg);
	}
	
	public void setErr(boolean isErr){
		if(isErr){
			iconView.setImageResource(R.drawable.err_toast_icon);
		}else{
			iconView.setImageResource(R.drawable.tosat_icon);
		}
	}
	
	@Override
	public void show() {
		super.show();
	}
}
