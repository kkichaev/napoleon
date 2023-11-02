package com.grsoft.dataobjects.impl;

public class SkladHelper{
	public static boolean useDiscount(String key) {
		SkladImpl s = new SkladImpl();
		s.read("key", key);
		
		return s.getData().useDiscount == 0 ? false : true;
	}
}
