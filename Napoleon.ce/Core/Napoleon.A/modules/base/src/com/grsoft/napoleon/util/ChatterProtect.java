package com.grsoft.napoleon.util;

public class ChatterProtect {
	private long systemTime = 0;
	private final long CHATTER_TIME = 1000;
	
	public boolean check() {
		boolean result = false;
		long curTime = System.currentTimeMillis();
		
		if (systemTime == 0 || (curTime - systemTime) > CHATTER_TIME ) {
			systemTime = System.currentTimeMillis();
			result = true;
		}
		
		return result;
	}
}
