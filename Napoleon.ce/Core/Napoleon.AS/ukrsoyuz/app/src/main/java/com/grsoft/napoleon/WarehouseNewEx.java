package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceItemColor;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.AssortmentMatrixAdapterEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class WarehouseNewEx extends Warehouse {
	
	OrgImpl oi = new OrgImpl();
	
	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		return new AssortmentMatrixAdapterEx(this, document.getId());
	}
	
	@Override
	protected int getDefaultColor(Price p) {
		if(document != null) {
			Integer clr = PriceItemColor.getColor(p.id);
			if(clr != null)
				return Util.GrServerColorToSystem(clr);
		}
		return super.getDefaultColor(p);
	}

	@Override
	void packetInsert(OrderImplBase<?> o, PriceImpl p, int qty, boolean inPack, CostStrategy cs) {
		UnitItem sel = null;
		for(UnitItem ui : ((PriceEx)p.getData()).units) {
			if(sel == null || (inPack && ui.inpack > Consts.QTY_SCALE) || (!inPack && ui.inpack == Consts.QTY_SCALE)) {
				sel = ui;
			}
		}

		((OrderImplEx)o).updateQty(p, qty, cs.getItemCost(p.getData(), o), sel);
	}
}
