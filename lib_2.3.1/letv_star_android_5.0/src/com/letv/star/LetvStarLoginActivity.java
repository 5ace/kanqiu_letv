package com.letv.star;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class LetvStarLoginActivity extends Activity{
	
	private TextView letvStarInfo ;
	
	private TextView title ; 
	
	private EditText nameEditText ;
	
	private EditText passwordEditText ;
	
	private ImageView loginImageView ;
	
	private ImageView cancelImageView ;
	
	private static LetvStarListener listener ;
	
	private static ProgressDialog dialog ;
	
	private boolean state = false ;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if(dialog != null && !dialog.isShowing() && !isFinishing()){
					try {
						dialog.show();
					} catch (Exception e) {
					}
				}
				break;
			case 1:
				if(state){
					finish();
				}else{
					nameEditText.setText("");
					passwordEditText.setText("");
				}
				if(dialog != null && dialog.isShowing() && !isFinishing()){
					try {
						dialog.dismiss();
					} catch (Exception e) {
					}
					
				}
				break;
			}
		};
	};
	
	public static void lanuch(Context context , LetvStarListener listener){
		LetvStarLoginActivity.listener = listener ;
		Intent intent = new Intent(context , LetvStarLoginActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.letv_star_login);

		title = (TextView) findViewById(R.id.top_title);
		letvStarInfo = (TextView) findViewById(R.id.info);
		nameEditText = (EditText) findViewById(R.id.name);
		passwordEditText = (EditText) findViewById(R.id.password);
		loginImageView = (ImageView) findViewById(R.id.login);
		cancelImageView = (ImageView) findViewById(R.id.cancel);
		
		dialog = new ProgressDialog(this);
		dialog.setMessage("正在加载...");
		
		String info = getString(R.string.letv_star_info);
        SpannableString ss = new SpannableString(info);
        ss.setSpan(new URLSpan("http://starcast.letv.com/"), 47, 53,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        letvStarInfo.setText(ss);
		letvStarInfo.setMovementMethod(LinkMovementMethod.getInstance());
		
		loginImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String name = nameEditText.getText().toString().trim() ;
				final String password = passwordEditText.getText().toString().trim() ;
				
				if(name == null || name.length() == 0){
					Toast.makeText(LetvStarLoginActivity.this, R.string.name_null, Toast.LENGTH_SHORT).show();
					return ;
				}
				
				if(password == null || password.length() == 0){
					Toast.makeText(LetvStarLoginActivity.this, R.string.password_null, Toast.LENGTH_SHORT).show();
					return ;
				}
				
				new Thread(){
					public void run() {
						showLoading();
						state = LetvStar.getInstance().login(LetvStarLoginActivity.this, name, password, listener);
						cancelLoading();
					};
				}.start();
			}
		});		
		
		cancelImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		title.setText(R.string.maintitle);
	}
	
	private void showLoading(){
		handler.sendEmptyMessage(0);
	}
	
	private void cancelLoading(){
		handler.sendEmptyMessage(1);
	}
}
