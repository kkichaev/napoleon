package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.VandSell;
import com.grsoft.dataobjects.VandSellItem;
import com.grsoft.napoleon.CellData;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.VandSellDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.VandSellDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;

public class VandSellImpl extends CreatableDocument<VandSell> implements Itemsable {

	@Override
	public long write() {
		long res = super.write();
		VandSellDoc.instance().refreshDocSum(data.id);
		return res;
	}
	
	@Override
	public DataObject findItem(String itemId) {
		if( data.items != null ) {
			for(VandSellItem i : data.items)
				if( i.id.equals(itemId) )
					return i;
		}
		return null;
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		List<CellData> items = CellData.getVandData(orgId, null);
		if( items.size() == 0 ) {
			Toast.makeText(context, "Нет документа \"Ревизия\" для автомата.", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		data.items = new ArrayList<VandSellItem>();
		for(CellData i : items) {
			VandSellItem vi = new VandSellItem();
			vi.id = i.id;
			vi.cell = i.cell;
			vi.cost = i.cost;
			data.items.add(vi);
		}
		OrgImpl oi = new OrgImpl();
		oi.read("id", orgId);
		data.costype = oi.getData().costype;
		
		return super.init(context, orgId, gpsCoord);
	}
	
	@Override
	public long sum() {
		long sum = 0;
		if( data.items != null ) {
			for(VandSellItem vi : data.items)
				sum += ((long)vi.chek * vi.cost / Consts.QTY_SCALE);
		}
		
		return sum;
	}
	
	@Override
	public int qty() {
		int result = 0;
		
		if( data.items != null ) {
			for(VandSellItem item : data.items)
				result += item.chek;
		}
		return result / Consts.QTY_SCALE;
	}

	@Override
	public Date getDate() { return data.date; }

	@Override
	public String getDescription(Context context) {
		return (isExported()) ? context.getString(R.string.sent) : ""; 
	}

	@Override
	public String getId() { return data.id; }
	
	@Override
	public int getSumType() { return data.costype; }

	@Override
	public void editItem(long itemRowid, Context context) {
	}

	@Override
	public int getItemColor() {
		return 0;
	}

	@Override public int getItemValue(Price item) { return item.qty; }

	@Override
	public int getItemQty(Price item) {
		return 0;
	}

	@Override
	public long getItemSum(Price item) {
		return 0;
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		return false;
	}

	@Override
	public void open(Context context) {
		VandSellDetail.open(context, this);
	}
}
