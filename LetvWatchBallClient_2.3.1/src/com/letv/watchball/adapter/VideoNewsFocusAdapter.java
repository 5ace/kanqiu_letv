/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.letv.watchball.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.cache.LetvCacheMannager;
import com.letv.cache.view.LetvImageView;
import com.letv.watchball.R;
import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.bean.FocusPicInfo.FocusPic;
import com.letv.watchball.ui.impl.BasePlayActivity;
import com.letv.watchball.utils.UIs;

public class VideoNewsFocusAdapter extends LetvBaseAdapter {
	private Context mContext;
	private ChannelFocusAdapterCallBack mChannelFocusAdapterCallBack;
	private static final int ID_TITLE1 = 1;
	private static final int ID_TITLE2 = 2;
	public interface ChannelFocusAdapterCallBack {
		public void updateLiveHall(boolean isFromLiveHall);
	}

	public VideoNewsFocusAdapter(Context context, ChannelFocusAdapterCallBack mChannelFocusAdapterCallBack) {
		super(context);
		mContext = context;
		this.mChannelFocusAdapterCallBack = mChannelFocusAdapterCallBack;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;// list == null ? 0 :
									// list.size();//Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		RelativeLayout mRelativeLayout = null;
		LetvImageView imageView = null ;
		TextView title = null ;
		TextView subTitle = null ;
			/**
			 * 适配低端手机，用xml会报找移除，采用代码形式,恶心的不得了
			 */
			RelativeLayout.LayoutParams layoutParam_defaultImageView = new RelativeLayout.LayoutParams( LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT);
			layoutParam_defaultImageView.addRule(RelativeLayout.CENTER_IN_PARENT);
			
			mRelativeLayout=new RelativeLayout(mContext);
			mRelativeLayout.setBackgroundResource(R.color.letv_color_ffdadada);

			ImageView defaultImageView=new ImageView(mContext);
			defaultImageView.setBackgroundResource(R.drawable.poster_defualt_pic);
			defaultImageView.setLayoutParams(layoutParam_defaultImageView);
			
			
			RelativeLayout.LayoutParams LayoutParams_LetvImageView = new RelativeLayout.LayoutParams( LayoutParams.FILL_PARENT, UIs.dipToPx(145));
			imageView = new LetvImageView(mContext);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setLayoutParams(LayoutParams_LetvImageView);
			
			title = new TextView(mContext);
			subTitle =  new TextView(mContext);
			
			
			RelativeLayout.LayoutParams TextViewlayoutParam = new RelativeLayout.LayoutParams( LayoutParams.FILL_PARENT, UIs.dipToPx(50)); 
			TextViewlayoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			TextView mTextView=new TextView(mContext);
			mTextView.setGravity(Gravity.CENTER_VERTICAL|Gravity.BOTTOM);
			mTextView.setBackgroundResource(R.drawable.gradual_rectangle);
			mTextView.setLayoutParams(TextViewlayoutParam);
			
			
			RelativeLayout.LayoutParams TitlelayoutParam = new RelativeLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
			TitlelayoutParam.setMargins(UIs.dipToPx(10), 0, 0, 0);
			
			TitlelayoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);		
			title.setId(ID_TITLE1);
			title.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
			title.setSingleLine(false);
			title.setLayoutParams(TitlelayoutParam);
			title.setTextAppearance(mContext, R.style.letv_text_15_ffffffff_shadow);
			title.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
			
			
			RelativeLayout.LayoutParams subTitlelayoutParam = new RelativeLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
			
			subTitlelayoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);	
			subTitle.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
			subTitle.setSingleLine(false);
			subTitle.setPadding(UIs.dipToPx(20), 0, 0, UIs.dipToPx(20));
			subTitle.setLayoutParams(subTitlelayoutParam);
			subTitle.setTextAppearance(mContext, R.style.letv_text_15_ffffffff_shadow);
			subTitle.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
			
			mRelativeLayout.addView(defaultImageView);
			mRelativeLayout.addView(imageView);
			mRelativeLayout.addView(mTextView);
			mRelativeLayout.addView(title);
			mRelativeLayout.addView(subTitle);
		
			
			
			
			UIs.zoomView(400, 145, imageView);
			UIs.zoomView(400, 145, mRelativeLayout);
			
		int lsize = getListSize();
		if(lsize<=0){
			return null;
		}
		final int pos = position % lsize;
		final FocusPic block = (FocusPic) list.get(pos);
		imageView.setVisibility(View.VISIBLE);
		if (!TextUtils.isEmpty(block.getPic())) {
			LetvCacheMannager.getInstance().loadImage(block.getPic(), imageView);
		} else {
			LetvCacheMannager.getInstance().loadImage(block.getPic_200_150(), imageView);
		}
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (block.getAt() == 3 && mChannelFocusAdapterCallBack != null) {
					mChannelFocusAdapterCallBack.updateLiveHall(true);
				}
				BasePlayActivity.launch(mContext, block.getPid(), block.getVid());
//				UIControllerUtils.gotoActivity(context, block, false);
//				LetvUtil.staticticsInfoPost(context,
//						DataConstant.StaticticsVersion2Constatnt.CategoryCode.CHANNEL_CONTENT_HOME_FOCUS, null, pos,
//						channelId, channelId + "", block.getPid() + "", block.getVid() + "", block.getLiveUrl());
			}
		});
		title.setVisibility(View.VISIBLE);
		title.setMaxLines(1);
		title.setText(block.getNameCn());
		if (((block.getCid() == AlbumNew.Channel.TYPE_TV) || (block.getCid() == AlbumNew.Channel.TYPE_CARTOON))
				&& block.getType() == AlbumNew.Type.VRS_MANG) {
//			if (block.getNowEpisodes() <= 0) {
//				vh.subTitle.setVisibility(View.GONE);
//			} else {
//				vh.subTitle.setVisibility(View.VISIBLE);
//				vh.subTitle.setText(block.getIsEnd() == 1 ? mContext.getString(R.string.channel_list_totalcount,
//						block.getNowEpisodes()) : mContext.getString(R.string.channel_list_currcount,
//						block.getNowEpisodes()));
//			}
		} else {
			if (!TextUtils.isEmpty(block.getSubTitle())) {
				subTitle.setVisibility(View.VISIBLE);
				subTitle.setText(block.getSubTitle());
			} else {
				subTitle.setVisibility(View.GONE);
				title.setMaxLines(2);
			}
		}

		return mRelativeLayout;
	}


	private int getListSize() {
		return list == null ? 0 : list.size();
	}

}
