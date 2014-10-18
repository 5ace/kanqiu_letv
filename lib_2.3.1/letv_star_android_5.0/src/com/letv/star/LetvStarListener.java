package com.letv.star;


public interface LetvStarListener{

	public void onComplete();
	
	public void onErr(String errLog);
	
	public void onFail(String failLog);
}
