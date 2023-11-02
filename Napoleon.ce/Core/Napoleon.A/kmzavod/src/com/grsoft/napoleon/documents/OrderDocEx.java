package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.documents.CreateByScriptDef;

public class OrderDocEx extends OrderDoc implements CreateByScriptDef{

	public OrderDocEx(String name, String objName, Class<? extends Document<?>> docClass) {
		super(name, objName, (Class<? extends OrderImplBase<? extends Order>>) docClass);
	}

	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("OrderDoc уже создан!");
		instance = new OrderDocEx("Заявки", "Order", OrderImplEx.class);
	}
	
	@Override
	public Document<?> create(ScriptDef def, ScriptDefItem item) {
		OrderImpl result = (OrderImpl) create();
		((OrderEx)result.getData()).phone = ((ScriptDefEx)def).phone;
		
		return result;
	}
}
