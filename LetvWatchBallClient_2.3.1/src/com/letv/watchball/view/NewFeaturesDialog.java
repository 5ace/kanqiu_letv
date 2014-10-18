package com.letv.watchball.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.letv.watchball.R;
import com.letv.watchball.utils.UIs;

public class NewFeaturesDialog extends Dialog{
	
	public NewFeaturesDialogListener listener ;
	
	public View root ;
	
	public Bitmap[] bitmaps ;
	
	private LetvGallery gallery ;
	
	private NewFeaturesAdapter adapter ;
	
	{
		int[] images = {R.drawable.guide01,R.drawable.guide02,R.drawable.guide03 } ;
		
		bitmaps = new Bitmap[images.length];
		
		for(int i = 0 ; i < images.length ; i++){
			bitmaps[i] = UIs.loadResourcesBitmap(getContext(), images[i]);
		}
	}
	
	private OnDismissListener dismissListener = new OnDismissListener() {
		
		@Override
		public void onDismiss(DialogInterface dialog) {
			gallery.removeAllViewsInLayout();
			gallery.setAdapter(null);
			
			for(Bitmap b : bitmaps){
				UIs.recycleBitmap(b);
			}
			
			root = null ;
			gallery = null ;
			bitmaps = null ;
			
			if(listener != null)
				listener.onCancel();
		}
	};

	public NewFeaturesDialog(Context context, int theme,NewFeaturesDialogListener listener) {
		super(context, theme);
		this.context = context ;
		this.listener = listener ;
	}


	Context context ;
	private int[] images = {R.drawable.guide01,R.drawable.guide02,R.drawable.guide03  } ;
	
	public NewFeaturesDialog(Context context,NewFeaturesDialogListener listener) {
		this(context, R.style.Dialog_Fullscreen ,listener);
		this.setOnDismissListener(dismissListener);
	}
	
	public NewFeaturesDialog(Context context,NewFeaturesDialogListener listener,boolean cancelable) {
		this(context, R.style.Dialog_Fullscreen ,listener);
		this.setOnDismissListener(dismissListener);
		this.setCancelable(cancelable);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		root = UIs.inflate(context , R.layout.more_newfeatures , null , false);
		gallery = (LetvGallery) root.findViewById(R.id.newfeatures_gallery);
		gallery.setUnselectedAlpha(1);
		adapter = new NewFeaturesAdapter() ;
		
		gallery.setAdapter(adapter);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(position < images.length - 1){
					gallery.setBackgroundColor(0xff000000);
				}
				if(position >= images.length - 1){
					gallery.setBackgroundColor(0x00000000);
				}
				if(position == images.length){
					dismiss();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		setContentView(root);
		if(listener != null)
			listener.onStart();
	}
	
	public interface NewFeaturesDialogListener{
		public void onStart();
		public void onCancel();
	}
	
	class NewFeaturesAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return images.length + 1;
		}

		@Override
		public Object getItem(int position) {
			return 0;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHandler handler ;
			
			if(convertView == null){
				convertView = UIs.inflate(context, R.layout.newfeatures_item, parent, false);
				handler = new ViewHandler() ;
				
				handler.imageView = (ImageView)convertView.findViewById(R.id.newfeatures_pic);
				
				convertView.setTag(handler);
			}else{
				handler = (ViewHandler) convertView.getTag();
			}
			if(position < images.length){
				handler.imageView.setImageBitmap(bitmaps[position]);
			}else{
				handler.imageView.setImageBitmap(null);
			}
			
			return convertView;
		}
		
		class ViewHandler {
			ImageView imageView ;
		}
	}
}
