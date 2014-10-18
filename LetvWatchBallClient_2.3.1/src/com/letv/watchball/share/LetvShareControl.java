package com.letv.watchball.share;

import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.bean.Share;
import com.letv.watchball.bean.ShareAlbum;
import com.letv.watchball.bean.Video;
import com.letv.watchball.utils.LetvUtil;

public class LetvShareControl {
	
	
	private Share share; // 分享地址，由客户端对服务器端吐出的数据进行规则替换
	
	private boolean isShare;//分享地址请求是否成功
	
	public static  final ShareAlbum mShareAlbum=new ShareAlbum();
	
	public  final static LetvShareControl mLetvShareControl=new LetvShareControl();
	
	public static LetvShareControl getInstance(){
		return mLetvShareControl;
		
	}
	
	/**
	 * 把要分享的需要的Album对象传进来
	 * @param array
	 */
	public void setAblum_att(Object...array){
		if(LetvUtil.isEmptyArray(array)){
			return;
		}
		if(array[0] instanceof Video && !LetvUtil.isEmptyArray(array[0]))
			mShareAlbum.setShare_vid((int)((Video) array[0]).getId());
			mShareAlbum.setIcon(((Video) array[0]).getPic());
			mShareAlbum.setShare_AlbumName(((Video) array[0]).getNameCn());
			
		if( array[1] instanceof AlbumNew  && !LetvUtil.isEmptyArray(array[1])){
			mShareAlbum.setCid(((AlbumNew) array[1]).getCid());
			mShareAlbum.setDirector(((AlbumNew) array[1]).getDirectory());
			mShareAlbum.setShare_id((int)((AlbumNew) array[1]).getId());
			mShareAlbum.setActor(((AlbumNew) array[1]).getStarring());
			mShareAlbum.setYear(((AlbumNew) array[1]).getReleaseDate());
		
		}
	}
	public ShareAlbum getAblum(){
		
		return mShareAlbum;
	}
	
	public Share getShare() {
		return this.share;
	}

	public void setShare(Share share) {
		this.share = share;
	}

	public boolean isShare() {
		return this.isShare;
	}

	public void setIsShare(boolean isShare) {
		this.isShare = isShare;
	}

	public interface LetvShareImp{
	
	}
}
