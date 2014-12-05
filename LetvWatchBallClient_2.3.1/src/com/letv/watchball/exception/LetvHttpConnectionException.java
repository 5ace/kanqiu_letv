package com.letv.watchball.exception;

public class LetvHttpConnectionException extends Exception {

	public LetvHttpConnectionException(Exception e) {
		super(e);
	}

	public LetvHttpConnectionException(String e) {
		super(e);
	}
}
