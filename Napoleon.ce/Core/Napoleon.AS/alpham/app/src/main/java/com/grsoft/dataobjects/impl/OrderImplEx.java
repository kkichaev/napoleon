package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.OrderTabakDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Pair;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		PriceEx pe = (PriceEx)item;
		int whIndex = ((OrderEx)data).whIndex;
		
		if( whIndex <= 0 || whIndex > pe.whQty.size())
			return super.getItemValue(item);
		
		return pe.whQty.get(whIndex-1).qty;
	}

	public long insert() {
		rowid = ExtrasConst.INVALID_ROWID;
		write();
		close();
		return rowid;
	}

	public boolean haveBc(String bc) {
		for(OrderItem oi : data.items) {
			if(((OrderItemEx)oi).haveBc(bc))
				return true;
		}
		return false;
	}

	@Override
	protected void postCopyProcess(CreatableDocument<Order> copy) {
		super.postCopyProcess(copy);
		for(OrderItem oi : copy.data.items) {
			((OrderItemEx)oi).barcodes.clear();
		}
	}

//	@Override
//	public void open(Context context) {
//		if(((OrderEx)data).tabak != 0) {
//			OrderTabakDetail.open(context, this);
//		} else {
//			super.open(context);
//		}
//	}

	public List<Pair<PriceEx, OrderItemEx>> makeIntersect(List<PriceEx> src) {
		List<Pair<PriceEx, OrderItemEx>> ret = new ArrayList<>();

		for(PriceEx p : src) {
			for(OrderItem oi : data.items) {
				if(oi.id.equals(p.id))
					ret.add(new Pair(p, oi));
			}
		}

		return ret;
	}

	public boolean isGood() {
		if(!isEditable()) return true;
		if(((OrderEx)data).tabak == 1) {
			for(OrderItem oid : data.items) {
				if(!((OrderItemEx)oid).isScanned())
					return false;
			}
		}
		return true;
	}
}
