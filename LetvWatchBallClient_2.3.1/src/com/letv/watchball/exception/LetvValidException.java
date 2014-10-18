package com.letv.watchball.exception;

public class LetvValidException extends Exception {

	
	public LetvValidException(Exception e){
		super(e);
	}
	
	
	public LetvValidException(String e){
		super(e);
	}
}
