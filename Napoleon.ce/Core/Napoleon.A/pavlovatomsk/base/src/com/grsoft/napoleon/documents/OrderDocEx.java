package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.documents.CreateByScriptDef;

public class OrderDocEx extends OrderDoc implements CreateByScriptDef{

	public OrderDocEx() {
		super("«‡ˇ‚ÍË", "Order", OrderImplEx.class);
	}
	
	public static void initialize() { instance = new OrderDocEx(); }

	@Override
	public Document<?> create(ScriptDef def, ScriptDefItem item) {
		Document<?> result = super.create();
		final String BONUS = "¡ŒÕ”—";
		final String UCENKA = "”÷≈Õ ¿";
		
		String itemName = item.name.toUpperCase(); 
		if(itemName.contains(BONUS.toUpperCase()) ||
				itemName.contains(UCENKA.toUpperCase())) {
			OrderEx oe = (OrderEx) result.getData();
			oe.bonus = 1;
		}
		
		return result;
	}

}
