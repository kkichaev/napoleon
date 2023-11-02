package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Barcode;
import com.grsoft.dataobjects.BarcodeItem;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.BarcodeDetail;
import com.grsoft.napoleon.BarcodeEdit;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.GpsCoord;

import android.content.Context;
import android.widget.Toast;

public class BarcodeImpl extends CreatableDocument<Barcode> implements Itemsable, IMatrix{
	public OrgMatrixImpl matrix = null;
	
	@Override
	public void open(Context context) {
		BarcodeDetail.open(context, getRowid());
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		super.init(context, orgId, gpsCoord);
		Warehouse.open(context, this, false);
		
		return false;
	}
	
	public OrgMatrix getMatrix() {
		return MatrixInflator.inflate(this);
	}

	@Override
	public void editItem(long itemRowid, Context context) {
		PriceImpl price = new PriceImpl();
		price.read(itemRowid);
		BarcodeEdit.open(context, getRowid(), price.getData().id);
	}

	@Override
	public DataObject findItem(String itemId) {
		for(BarcodeItem i : data.items)
			if (i.id.equals(itemId))
				return i;
		
		return null;
	}

	@Override
	public int getItemColor() {
		return R.color.item_highlight;
	}

	@Override
	public int getItemValue(Price item) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemQty(Price item) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getItemSum(Price item) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, long cost, boolean inPack) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean delete() {
		boolean ret = super.delete();
		if(ret) {
			VisitImpl vi = new VisitImpl();
			vi.getData().created = data.visitDoc;
			if(vi.read()) {
				vi.delete();
			}
			vi.close();
		}
		return ret;
	}
	
	public boolean deleteItem(String id) {
		BarcodeItem i = (BarcodeItem) findItem(id);
		
		if (i != null) {
			VisitImplEx vi = new VisitImplEx();
			vi.getData().created = data.visitDoc;
			
			if(vi.read()) {
				vi.removePhoto(id);
			}
			
			data.items.remove(i);
			write();
			close();
		}
		
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		return data.items.size() == 0;
	}
}
