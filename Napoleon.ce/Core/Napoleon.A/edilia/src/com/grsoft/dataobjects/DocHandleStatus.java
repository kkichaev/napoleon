package com.grsoft.dataobjects;

public class DocHandleStatus {
	public static final int SAVED = 1;
	public static final int HANDLED = 2;
	public static final int FAIL = 3;
	public static final int REPEATED = 4;

	public static int getStatus(String statusText) {
		if(statusText.equals("saved")) return DocHandleStatus.SAVED;
		if(statusText.equals("handled")) return DocHandleStatus.HANDLED;
		if(statusText.equals("fail")) return DocHandleStatus.FAIL;
		if(statusText.equals("repeat")) return DocHandleStatus.REPEATED;
		return DocHandleStatus.FAIL;
	}
}
