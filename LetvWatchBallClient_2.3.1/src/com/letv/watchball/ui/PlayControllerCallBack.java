package com.letv.watchball.ui;

public interface PlayControllerCallBack {

	public void seekFinish(int progress);

	public void star();

	public void pause();

	public void full();

	public void half();

	public void favorite();

	public void cancelFavorite();

	public void download();

	public void back();
	
	public void changeDownLoad(boolean isHd);

	public void share();
	
	public void forward();
	
	public void rewind();
	
	public void openDownload();

	public void closeDownload();
	
	void toPip();
}
