package com.letv.star;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class LetvStarDialog extends Dialog {

	private static final float[] DIMENSIONS_LANDSCAPE = { 530, 458 };
	private static final float[] DIMENSIONS_PORTRAIT = { 280, 420 };
	
	private TextView letvStarInfo;
	private TextView title;
	private EditText nameEditText;
	private EditText passwordEditText;
	private ImageView loginImageView;
	private ImageView cancelImageView;
	private ScrollView scroll;
	
	private LinearLayout mContent;

	Context mConetxt;
	LetvStarListener mListener;
	
	private ProgressDialog mSpinner;
	private Context context;
	private View view;
	private int width = 0;
	private int height = 0;

	public LetvStarDialog(Context context, LetvStarListener listener, int width, int height) {
		super(context,R.style.daka_dialog);
		mConetxt = context;
		mListener = listener;
		this.context = context;
		this.width = width;
		this.height = height;
	}
	
	public LetvStarDialog(Context context, LetvStarListener listener) {
		super(context,R.style.daka_dialog);
		mConetxt = context;
		mListener = listener;
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSpinner = new ProgressDialog(getContext());
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("Loading...");
		mSpinner.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				mSpinner.dismiss();
				dismiss();
				return false;
			}

		});
		mContent = new LinearLayout(getContext());
		mContent.setOrientation(LinearLayout.VERTICAL);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(width != 0 && height != 0){
			DIMENSIONS_LANDSCAPE[0] = dipToPx(context, width);
			DIMENSIONS_LANDSCAPE[1] = dipToPx(context, height);
			DIMENSIONS_PORTRAIT[0] = dipToPx(context, height);
			DIMENSIONS_PORTRAIT[1] = dipToPx(context, width);
		}
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		float scale = getContext().getResources().getDisplayMetrics().density;
		float[] dimensions = display.getWidth() < display.getHeight() ? DIMENSIONS_PORTRAIT
				: DIMENSIONS_LANDSCAPE;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				(int) (dimensions[0] * scale + 0.5f), (int) (dimensions[1]
						* scale + 0.5f));
		FrameLayout.LayoutParams paramsScroll = new FrameLayout.LayoutParams(
				width, height-50);
		
//		Log.d("D", "letv star");
//		setContentView(R.layout.letv_star_login_dialog);
		view = LayoutInflater.from(mConetxt).inflate(R.layout.letv_star_login_dialog, null);
		FrameLayout frameLayout = (FrameLayout)view.findViewById(R.id.detial_main_layout);
		scroll = (ScrollView)view.findViewById(R.id.scroll);
		scroll.setLayoutParams(paramsScroll);
		frameLayout.setLayoutParams(params);
		setContentView(view);
		initUI();

		bindListener();
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | 
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	
	public float dipToPx(Context context, int dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        float pxValue = (float) (dipValue * scale + 0.5f);
        return pxValue;
    }

	private void initUI() {
		title = (TextView)view.findViewById(R.id.top_title);
		letvStarInfo = (TextView)view.findViewById(R.id.info);
		nameEditText = (EditText)view.findViewById(R.id.name);
		passwordEditText = (EditText)view.findViewById(R.id.password);
		loginImageView = (ImageView)view.findViewById(R.id.login);
		cancelImageView = (ImageView)view.findViewById(R.id.cancel);

		String info = mConetxt.getString(R.string.letv_star_info);
		SpannableString ss = new SpannableString(info);
		ss.setSpan(new URLSpan("http://www.baidu.com"), 47, 53,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		letvStarInfo.setText(ss);
		letvStarInfo.setMovementMethod(LinkMovementMethod.getInstance());

		title.setText("大咔");
	}

	private void bindListener() {
		loginImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final String name = nameEditText.getText().toString().trim();
				final String password = passwordEditText.getText().toString()
						.trim();

				if (name == null || name.length() == 0) {
					Toast.makeText(mConetxt,
							R.string.name_null, Toast.LENGTH_SHORT).show();
					return;
				}

				if (password == null || password.length() == 0) {
					Toast.makeText(mConetxt,
							R.string.password_null, Toast.LENGTH_SHORT).show();
					return;
				}

				mSpinner.show();
				new Thread() {
					public void run() {
						LetvStar.getInstance().login(
								mConetxt, name, password,
								mListener);
					};
				}.start();
				mSpinner.dismiss();
				dismiss();
			}
		});

		cancelImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LetvStarDialog.this.dismiss();
			}
		});
	}
}
