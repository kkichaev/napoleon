package com.grsoft.dataobjects.impl;

import java.util.List;

import android.content.Context;
import android.graphics.Color;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.MovementItem;
import com.grsoft.dataobjects.MovementWh;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.MovementDetail;
import com.grsoft.napoleon.MovementProps;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.MovementDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;

public class MovementWhImpl extends CreatableDocument<MovementWh> implements Itemsable {

	int whIndex = -1; 
	int whDest = -1;
	
	public int getWhIndex() {
		if( whIndex == -1 ) 
			whIndex = OrderImplEx.getWhIndex(data.whSrc);
		return whIndex;
	}
	
	public int getWhDest() {
		if( whDest == -1 )
			whDest = OrderImplEx.getWhIndex(data.whDest);
		return whDest;
	}
		
	@Override
	public void open(Context context) {
		MovementDetail.open(context, this);
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		super.init(context, orgId, gpsCoord);
		MovementProps.open(context, this, true);
		return false;
	}

	@Override
	public void editItem(long itemRowid, final Context context) {
		final PriceImpl pi = new PriceImpl();
		pi.read(itemRowid);
		Price p = pi.getData();
		final MovementItem mi = (MovementItem)findItem(p.id);
		final int qip = (p.qtyInPack == 0) ? Consts.QTY_SCALE : p.qtyInPack; 
		
		InputNumberDlg.open(context, new InputNumber() {
			
			@Override
			public boolean isInpack() {
				return mi != null && mi.isInPack();
			}
			
			@Override
			public int getValue() {
				if( mi == null )
					return 0;
				if( !mi.isInPack() )
					return mi.qty;
				return (int)((long)mi.qty * Consts.QTY_SCALE / qip);
			}
			
			@Override
			public void applayInput(int value, Object... params) {
				boolean useInPack = (Boolean)params[0];
				if( useInPack )
					value = (int)((long)value * qip / Consts.QTY_SCALE);
				
				if( updateQty(pi, value, 0, useInPack) && context instanceof DataSetNotify)
					((DataSetNotify)context).notifyDataSetChanged();
			}
		}, Consts.QTY_SCALE, true, "¬ведите количество", true);
		
		pi.close();
	}

	@Override
	public DataObject findItem(String itemId) {
		for(MovementItem i : data.items)
			if( i.id.equals(itemId))
				return i;
		return null;
	}

	@Override
	public int getItemColor() { return Color.MAGENTA; }

	@Override
	public int getItemValue(Price item) {
		if( whIndex == -1 ) 
			getWhIndex();

		List<PriceQtyItem> whQty = ((PriceEx)item).whQty;
		return ( whIndex == 0 || whIndex > whQty.size() ) ?  item.qty : whQty.get(whIndex-1).qty;
	}
	
	public int getItemDestValue(Price item) {
		if( whDest == - 1 )
			getWhDest();
		List<PriceQtyItem> whQty = ((PriceEx)item).whQty;
		return ( whDest == 0 || whDest > whQty.size() ) ?  item.qty : whQty.get(whDest-1).qty;
	}

	@Override
	public int getItemQty(Price item) {
		MovementItem mi = (MovementItem)findItem(item.id);
		return mi == null ? 0 : mi.qty;
	}

	@Override
	public long getItemSum(Price item) {
		return 0;
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		Price price = priceImpl.getData();
		MovementItem item = (MovementItem) findItem(price.id);

		boolean needUpdate = true;
		if( item == null ) {
			if( qty > 0 ) {
				item = new MovementItem();
				item.id = price.id;
				item.qty = qty;
				data.items.add(item);
			}
			else
				needUpdate = false;
		} else {
			if( qty == 0 )
				data.items.remove(item);
			else {
				if( item.qty != qty )
					item.qty = qty;
				else
					needUpdate = false;
			}
		}

		if( item.isInPack() != inPack && qty != 0 ) {
			needUpdate = true;
			if( inPack ) item.flags |= MovementItem.IN_PACK;
			else item.flags &= (~MovementItem.IN_PACK);
		}
			
		if( needUpdate ) {
			write();
		}
		
		return needUpdate;
	}

	@Override
	public long write() {
		long ret = super.write();
		MovementDoc.instance().refreshDocSum(getId());
		return ret;
	}
}
