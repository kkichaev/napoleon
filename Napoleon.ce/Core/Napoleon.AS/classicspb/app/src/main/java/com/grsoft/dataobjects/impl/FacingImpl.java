package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.FaceMatrix;
import com.grsoft.dataobjects.Facing;
import com.grsoft.dataobjects.FacingItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgMatrixItem;
import com.grsoft.dataobjects.OrgMatrixItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.FacingDetail;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.FacingDoc;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.FPOperation;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;

import android.content.Context;
import android.graphics.Color;


public class FacingImpl  extends CreatableDocument<Facing> implements Itemsable, IMatrix{
	public OrgMatrixImpl matrix = null;
	
	@Override
	public void editItem(final long itemRowid, final Context context) {
		InputNumberDlg.open(context, new InputNumber() {
			
			@Override public boolean useComma() { return !Features.INTEGER_INPUTS_QTY; }
			@Override public boolean replaceCommaToPlus() { return Features.REPLACE_COMMA_TO_PLUS; }
			
			@Override
			public void applayInput(int value, Object... params) {
				
				if (isExported())
					return;
				
				PriceImpl priceImpl = new PriceImpl();
				priceImpl.read(itemRowid);
				
				updateQty(priceImpl, value, 0, false);
				
				if (context instanceof DataSetNotify)
					((DataSetNotify)context).notifyDataSetChanged();
				
				priceImpl.close();
				
				FacingDoc.instance().refreshDocSum(data.id);
			}

			@Override
			public long getValue() {
				PriceImpl priceImpl = new PriceImpl();
				priceImpl.read(itemRowid);
				priceImpl.close();
				FacingItem ri = (FacingItem)findItem(priceImpl.data.id);
				
				return ri == null ? 0 : ri.qty;
			}
		});
	}

	@Override
	public DataObject findItem(String itemId) {
		DataObject result = null;
		
		for(FacingItem i : data.items){
			if (i.id.equals(itemId)){
				result = i;
				break;
			}
		}
		
		return result;
	}

	@Override
	public int getItemColor() {	return com.grsoft.napoleon.R.color.magneta; }

	@Override
	public int getItemValue(Price item) { return item.qty; }

	@Override
	public int getItemQty(Price item) {
		FacingItem ri = (FacingItem) findItem(item.id);		
		return ri == null ? 0 : ri.qty;
	}

	@Override
	public long getItemSum(Price item) { return 0; }

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, long cost, boolean inPack) {
		Price price = priceImpl.getData();
		FacingItem item = (FacingItem) findItem(price.id);

		boolean needUpdate = true;
		if( item == null ) // new item
		{
			if( qty >= 0 )
			{
				item = new FacingItem();
				item.id = price.id;
				item.qty = qty;
				data.items.add(item);
			}
			else
				needUpdate = false;
		} else
		{
			if( item.qty != qty )
				item.qty = qty;
			else
				needUpdate = false;
		}
		
		item.modified = 1;
		
		if( needUpdate )
			write();
		
		return needUpdate;
	}

	@Override
	public void open(Context context) { FacingDetail.open(context, this); }
	
	@Override
	public void postInit() {
		OrgMatrix m = getMatrix();
		
		if(m != null && m.items != null)
			for(OrgMatrixItem i : m.items){
				FacingItem f = new FacingItem();
				f.id = i.id;
				f.qty = 0;
				
				data.items.add(f);
			}
		
		super.postInit();
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		boolean result =  super.init(context, orgId, gpsCoord);
		
		if (result) {
			 FacingDetail.open(context, this);
		}
		
		return false;
	}

	@Override
	public OrgMatrix getMatrix() {
		FaceMatrixImpl fi = new FaceMatrixImpl();
		FaceMatrix fm = fi.getData();
		
		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx) oi.getData();
		org.id = data.id;
		oi.read();
		oi.close();
		
		fm.name = org.faceMatrix;
		fi.read();
		fi.close();

		OrgMatrix om = new OrgMatrix();
		for(MatrixItem mi : fm.items) {
			OrgMatrixItemEx i = new OrgMatrixItemEx();
			i.id = mi.id;
			i.cost = 0;
			i.facing = 1;
			
			om.items.add(i);
		}
		return om;
	}

	public void deleteItem(Price p) {
		DataObject o = findItem(p.id);
		
		if(o != null)
			data.items.remove(o);
		
		write();
		close();
		
	}
	
	@Override
	public boolean isEmpty() {
		boolean result = true;
		
		for(FacingItem i : data.items)
			if (i.qty > 0){
				result = false;
				break;
			}
		
		return result;
	}
	
	 @Override
	public long sum() {
		long result = 0;
		if( data.items != null ) {
			DataObjectInfo info = DataObjectInfo.getInstance();
			int qty_scale = info.getScale(FacingItem.class, "qty");
			CostStrategy str = CostStrategy.getInstance(this.getClass());
			PriceImpl p = new PriceImpl();
			
			for (FacingItem i: data.items){
				p.read("id", i.id);
				long cost = str.getItemCost(p.getData(), this);
				result += FPOperation.itemMul(cost, i.qty, qty_scale);
			}
		}
		return result;
	}
	 
	 public int count() {
	    	int qty = 0;
	    	
	    	if( data.items != null )
		    	for(FacingItem item : data.items )
		    		qty += item.qty;
	    	
	    	return qty / Consts.QTY_SCALE;
	    }
}
