package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.InventoryDoc;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;

import android.content.Context;
import android.content.Intent;

public class InventoryDetail extends OrderDetail {
	
	static public void open(Context context, OrderImplBase<? extends Order> doc) {
		Intent i = new Intent(context, InventoryDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	};
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderItemsAdapter(){
			@Override
			int getResourceID() {
				return R.layout.inventorydetail_list_row;
			}
		});
	}
	
	@Override
	protected boolean haveFocusedGroup() {
		return false;
	}
	
	protected void setContentView(){
		setContentView(R.layout.inventorydetail);
	}
	
	protected void setDocType() {
		DocType.setCurDoc(InventoryDoc.instance());
		docType = InventoryDoc.instance();
	}
}

class Item {
	public String name;
	public int order;
	@Scale(value=Consts.QTY_SCALE, hideRest=true)
	public int qty;
	public String unit;
	
	public Item(OrderItem item, PriceImpl p, int order) {
		qty = item.qty;
		
		PricePrint pp = (PricePrint)p.getData();
		pp.id = item.id;
		p.read();
		
		if( item.inPack() ) {
			unit = pp.packName;
			qty = (int)((long)qty * Consts.QTY_SCALE / pp.qtyInPack);
		} else {
			unit = pp.unit;
		}
		
		name = pp.name;
		
		this.order = order;
	}
}
