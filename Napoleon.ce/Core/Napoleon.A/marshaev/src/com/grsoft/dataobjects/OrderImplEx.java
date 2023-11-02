package com.grsoft.dataobjects;

import android.graphics.Color;

import com.grsoft.dataobjects.impl.OrderImpl;

public class OrderImplEx extends OrderImpl {
	@Override
	protected boolean checkPriceQty() {
		return false;
	}
	
	@Override
	public int getItemColor() {
		return Color.RED;
	}
}
