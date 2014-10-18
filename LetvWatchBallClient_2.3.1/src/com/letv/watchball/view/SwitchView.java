package com.letv.watchball.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.letv.watchball.R;

public class SwitchView extends LinearLayout {
	
	Context context ;
	
	private Gallery gallery ;
	
	int[] onOff = {R.drawable.settings_switcher_off , R.drawable.settings_switcher_on};
	
	private OnItemSelectedListener listener ;
	
	public SwitchView(Context context) {
		super(context);
		this.context = context ;
		init(context);
	}

	public SwitchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context ;
		init(context);

	}
	
	private void init(Context context){
		inflate(context, R.layout.switch_view, this);

		findView();
	}

	private void findView() {
		gallery = (Gallery) findViewById(R.id.gallery_on_off);
		gallery.setAdapter(new SwitchAdapter(context));
		
		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position = gallery.getSelectedItemPosition();
				
				if(position == 0){
					gallery.setSelection(1);
				}else if(position == 1){
					gallery.setSelection(0);
				}
			}
		});
		
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
				if(listener != null){
					listener.onItemSelected(parent,view,position,id);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
	public int getSelection(){
		return gallery.getSelectedItemPosition();
	}
	
	
	public void setListener(OnItemSelectedListener listener){
		this.listener = listener ;
	}
	
	public void setSelection(int pos){
        if(gallery != null){
			gallery.setSelection(pos);
		}
	}
	
    class SwitchAdapter extends BaseAdapter {
        
        private Context mContext = null;
        
        public SwitchAdapter(Context cxt) {
            this.mContext = cxt;
        }

        @Override
        public int getCount() {
            return onOff.length;
        }

        @Override
        public Object getItem(int position) {
            return onOff[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageResource(onOff[position]);
            
            return imageView;
        }
    }
	@Override
	protected void dispatchDraw(Canvas canvas) {
//		try {
//			int vh = getHeight();
//			Path clipPath = new Path();
//			int w = this.getWidth();
//			int h = this.getHeight();
//			clipPath.addRoundRect(new RectF(0, 0, w , h), (float)vh*0.25f, (float)vh*0.25f,Path.Direction.CW);
//			canvas.clipPath(clipPath);
//		} catch (Exception e) {
//			Log.d("LHY", "RemoteImageViewRound-onDraw = " + e.toString());
//		}
//		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		super.dispatchDraw(canvas);
	}
}
