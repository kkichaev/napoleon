package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.Features;
import com.grsoft.network.DocExportListener;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.documents.CreateByScriptDef;

import java.util.ArrayList;

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

	@Override
	public DocExportListener getDirtyDocuments() {
		CreatableDocument<?> cd = (CreatableDocument<?>)create();
		DocExportListener dl =  new DocSendListner(getObjectName(),
				(Class<? extends CreatableDocument<?>>) cd.getClass(),
				"params", ParamState.ofExported);

		ArrayList<Long> needRemove = new ArrayList<Long>();
		DocList docs = dl.getDocuments();
		for(Document<?> d : docs) {
			OrderImplBase<? extends Order> doc = (OrderImplBase<? extends Order>) d;
			if( doc.isEmpty() || ((OrderEx)doc.getData()).needDecision() ) {
				needRemove.add(doc.getRowid());
			}
		}
		docs.removeDocuments(needRemove);
		docs.close();
		return dl;
	}
}
