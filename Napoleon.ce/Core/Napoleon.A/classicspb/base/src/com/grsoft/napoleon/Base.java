package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImplClassic;
import com.grsoft.napoleon.documents.DebtDocBase;
import com.grsoft.napoleon.documents.OrderDoc;

public class Base {
	public static void init() {
		DebtDocBase.initialize();
		OrderDoc.instance(OrderImplClassic.class);
		
		Warehouse.activity = WarehouseBase.class;
		CostStrategy.defaultInstance = new CostStrategyEx();
		OrderDetail.activity = OrderDetailClassic.class;
		FocusItemEditor.activity = FocusedItemEditorEx.class;
		
		Features.MAX_FOTO_HEIGHT = 4000;
		Features.MAX_FOTO_WIDTH = 4000;
	}
}
