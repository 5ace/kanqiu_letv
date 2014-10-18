package com.letv.watchball.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.letv.watchball.R;


public class LetvSpinner extends LinearLayout implements OnClickListener,
		DialogInterface.OnClickListener {
	public interface OnLetvSpinnerSelectedListener {
		public void onSelected(int pos);
	}

	private TextView spinner_name;// 用于显示所选文字
	private LinearLayout spinner_selecter;
	private String selectedValue;
	private String itemValues[];
	private AlertDialog dialog;
	private OnLetvSpinnerSelectedListener mListener;
	private Context context ;
	private int whichSelect = 0;
	public LetvSpinner(Context context, AttributeSet set) {
		super(context, set);
		this.context = context;
		initView();
	}
	
	private void initView() {
		inflate(context, R.layout.letv_spinner_view, this);
        findView();
	}

	private void findView() {
		spinner_selecter = (LinearLayout) findViewById(R.id.spinner_selecter);
		spinner_name = (TextView) findViewById(R.id.spinner_name);
	}
	/**
	 * 使用此控件前,必须先调用此方法进行初始化
	 */
	public void init(String[] itemValues, String defaultItem) {
		this.itemValues = itemValues;
		spinner_selecter.setOnClickListener(this);
		dialog = new AlertDialog.Builder(getContext()).setSingleChoiceItems(this.itemValues,
				0, this).create();
		selectedValue = itemValues[0];
		spinner_name.setText(defaultItem);
	}

	/**
	 * 添加一个 "返回" 或者 "取消"
	 */
	public void setButton(String button) {
		dialog.setButton(button, this);
	}

	/**
	 * 添加一个 按钮
	 */
	public void SetButton(String button, DialogInterface.OnClickListener listener) {
		dialog.setButton(button, listener);
	}

	/**
	 * 取消显示alertDialog
	 */
	public void dismiss() {
		dialog.dismiss();
	}
	
	/**
	 * 设置选中的项,从0开始
	 */
	@Deprecated
	public void setSelection(int position) {
		if (position >= 0 && position < itemValues.length) {
			whichSelect = position;
			selectedValue = itemValues[position];
			spinner_name.setText(selectedValue);
			dialog.getListView().setSelection(position);// 此行不生效
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which >= 0 && which < itemValues.length) {
			whichSelect = which;
			selectedValue = itemValues[which];
			spinner_name.setText(selectedValue);
			if (mListener != null) {
				mListener.onSelected(which);
			}
		}
		dialog.dismiss();

	}

	@Override
	public void onClick(View v) {
		dialog.show();
		spinner_name.setText(selectedValue);
		mListener.onSelected(whichSelect);
	}

	/**
	 * 获取所选择的值
	 */
	public String getSelectedValue() {
		return selectedValue;
	}

	public void setListener(OnLetvSpinnerSelectedListener listener) {
		mListener = listener;
	}
}
