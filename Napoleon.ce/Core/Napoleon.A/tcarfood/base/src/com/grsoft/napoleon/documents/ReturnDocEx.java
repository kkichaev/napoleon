package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.ReturnImpl;

public class ReturnDocEx extends ReturnDoc {
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("QuestionDoc уже создан!");
		instance = new ReturnDocEx(ReturnImpl.class);
	}
	
	public ReturnDocEx(Class<? extends OrderImplBase<? extends Order>> retClass){
		super(retClass);
	}
	
	@Override
	public boolean outOfScript() {
		return false;
	}
}
