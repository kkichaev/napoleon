package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.ReturnImplEx;

public class ReturnDocEx extends ReturnDoc {

	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new ReturnDocEx(ReturnImplEx.class);
	}
	
	protected ReturnDocEx(
			Class<? extends OrderImplBase<? extends Order>> retClass) {
		super(retClass);
	}
	
	@Override
	public boolean outOfScript() {
		return true;
	}

}
