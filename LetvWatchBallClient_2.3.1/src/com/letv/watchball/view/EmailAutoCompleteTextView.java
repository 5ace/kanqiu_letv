package com.letv.watchball.view;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.letv.watchball.R;
import com.letv.watchball.adapter.MailListAdapter;

public class EmailAutoCompleteTextView extends AutoCompleteTextView {
	
	private String[] email_suffixs = null;

	public EmailAutoCompleteTextView(Context context) {
		super(context);
		init();
	}

	public EmailAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public EmailAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		email_suffixs = getResources().getStringArray(R.array.email_suffix);
		addTextChangedListener(mTextWatcher);
		setOnTouchListener(onTouchListener);
	}
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		
		boolean isnull = true;
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

			if(s.length() > 1){
				char c = s.charAt(s.length() - 1);
				if(c == '@'){
					String[] mails = new String[email_suffixs.length];
					String prefix = EmailAutoCompleteTextView.this.getText().toString();
					for(int i = 0; i < email_suffixs.length; i++){
						mails[i] = prefix + email_suffixs[i];
					}
					MailListAdapter mMailListAdapter = new MailListAdapter(getContext(), mails);
					setAdapter(mMailListAdapter);
				}else{
//					setAdapter(null);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line,new String[]{});
					EmailAutoCompleteTextView.this.setAdapter(adapter);
//					dismissDropDown();
				}
			}else{
//				setAdapter(null);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line,new String[]{});
				EmailAutoCompleteTextView.this.setAdapter(adapter);
//				dismissDropDown();
			}
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
