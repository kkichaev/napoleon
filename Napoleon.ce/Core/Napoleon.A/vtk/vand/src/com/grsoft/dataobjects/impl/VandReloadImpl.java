package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.grsoft.dataobjects.CellsAudit;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.VandReload;
import com.grsoft.dataobjects.VandReloadItem;
import com.grsoft.napoleon.CellData;
import com.grsoft.napoleon.VandReloadDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.VandReloadDoc;
import com.grsoft.util.GpsCoord;

public class VandReloadImpl extends CreatableDocument<VandReload> implements Itemsable {

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		List<CellData> items = CellData.getVandData(orgId);
		data.items = new ArrayList<VandReloadItem>();
		for( CellData cdi : items) {
			if( cdi.rest == 0 ) {
				VandReloadItem item = new VandReloadItem();
				item.cell = cdi.cell;
				item.id = cdi.id;
				item.limit = cdi.limit;
				item.cost = cdi.cost;
				data.items.add(item);
			}
		}
		
		if( data.items.size() == 0 ) {
			Toast.makeText(context, "В автомате нет пустых ячеек", Toast.LENGTH_SHORT).show();
			return false;
		}
		return super.init(context, orgId, gpsCoord);
	}
	
	@Override
	public long write() {
		long res = super.write();
		VandReloadDoc.instance().refreshDocSum(getId());
		return res;
	}
	
	public void updateLinkedAudit() {
		CellsAuditImpl cellsAudit = new CellsAuditImpl();
		CellsAudit audit = cellsAudit.getData();
		
		boolean readed = (data.linkedAudit != null);
		if( readed ) {
			audit.created = data.linkedAudit;
			readed = cellsAudit.read();
		}
		
		if( !readed ) {
			List<CellData> items = CellData.getVandData(getId());
			cellsAudit.createAudit(data, items);
			cellsAudit.write();
			
			data.linkedAudit = cellsAudit.getData().created;
		} else {
			cellsAudit.updateItems(data);
			cellsAudit.write();
		}
	}

	@Override
	public void editItem(long itemRowid, Context context) {

	}

	@Override
	public DataObject findItem(String itemId) {
		return null;
	}

	@Override
	public int getItemColor() {
		return 0;
	}

	@Override
	public int getItemValue(Price item) {
		return item.qty;
	}

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
		VandReloadDetail.open(context, this);
	}

}
