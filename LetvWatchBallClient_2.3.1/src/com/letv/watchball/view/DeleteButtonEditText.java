package com.letv.watchball.view;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.letv.watchball.R;

public class DeleteButtonEditText extends EditText {
	
	public DeleteButtonEditText(Context context) {
		super(context);
		init();
	}

	public DeleteButtonEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DeleteButtonEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		addTextChangedListener(mTextWatcher);
		setOnTouchListener(onTouchListener);
	}
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		
		boolean isnull = true;
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			if(TextUtils.isEmpty(s)){
				if(!isnull){
					setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
					isnull = true;
				}
			}else{
				if(isnull){
					setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.search_del_button_selecter), null);
					isnull = false;
				}
			}
		}
	};
	
	private OnTouchListener onTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()){
			case MotionEvent.ACTION_UP:
				int curX = (int)event.getX();
				if(curX > v.getWidth() - 60 && !TextUtils.isEmpty(getText())){
					setText("");
					int cacheInputType = getInputType();// backup  the input type
					setInputType(InputType.TYPE_NULL);// disable soft input
					onTouchEvent(event);// call native handler
					setInputType(cacheInputType);// restore input  type
                    return true;// consume touch even
				}
				break;
			}
			return false;
		}
	};
}
