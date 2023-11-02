package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.GoodsRest;
import com.grsoft.dataobjects.GoodsRestItem;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceParty;
import com.grsoft.napoleon.GoodRestForm;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class GoodsRestImpl extends DbObject<GoodsRest> implements CreatableDocument, Itemsable {

	@Override
	public CreatableDocument copy() { return null; }

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		data.date = Util.getDate();
		data.created = Util.getDateTime();
		data.id = orgId;
		data.latitude = gpsCoord.latitude;
		data.longitude = gpsCoord.longitude;
		data.params = 0;
		
		if( write() != ExtrasConst.INVALID_ID )
			this.open(context);
		return false;
	}

	@Override
	public boolean isExported() { return (data.params & ParamState.ofExported) == ParamState.ofExported; }

	@Override
	public boolean isProceeded() { return false; }

	@Override
	public void setExported(boolean value) {
		if (value)
			data.params |= ParamState.ofExported;
		else
			data.params &= ~ParamState.ofExported;
		
		write();
	}

	@Override
	public void setProceeded() { }

	@Override
	public Date getDate() { return data.date; }

	@Override
	public String getDescription() { return ((isExported()) ? "отправлен" :  ""); }

	@Override
	public String getId() { return data.id; }

	@Override
	public int getSumType() { return 0; }

	@Override
	public void open(Context context) { GoodRestForm.open(context, this); }

	@Override
	public int sum() { return 0; }

	@Override
	public void editItem(long itemRowid, Context context) {	}

	@Override
	public DataObject findItem(String itemId) {
		if( data.items != null )
			for( GoodsRestItem item : data.items) {
				if( item.id.compareTo(itemId) == 0 )
					return item;
			}

		return null;
	}

	@Override
	public int getItemColor() { return Color.GREEN; }

	@Override
	public int getItemValue(String itemid) { return 0; }
	
	public ArrayList<GoodsRestItem> getItems(PriceEx p) {
		ArrayList<GoodsRestItem> ret = new ArrayList<GoodsRestItem>();
		
		Date current = new Date();
		
		if( p.party != null ) {
			for(PriceParty pp : p.party) {
				Date check = new Date(pp.date.getTime() + (p.realiz + p.unload) * 3600 * 24 * 1000);
				if( current.before(check) ) {
					GoodsRestItem item = new GoodsRestItem();
					item.date = pp.date;
					item.id = p.id;
					ret.add(item);
				}
			}
	
			Collections.sort(ret, new Comparator<GoodsRestItem>(){
				@Override public int compare(GoodsRestItem object1, GoodsRestItem object2) { 
					return object1.date.compareTo(object2.date); 
			}});
		}
		
		if( data.items != null ) {
			for(GoodsRestItem gri : data.items) {
				if( gri.id.compareTo(p.id) == 0 )
					updateQty(ret, gri);
			}
		}
		return ret;
	}

	private void updateQty(ArrayList<GoodsRestItem> ret, GoodsRestItem item) {
		boolean found = false;
		
		for(GoodsRestItem gri : ret) {
			if( gri.date.compareTo(item.date) == 0 ) {
				gri.qty = item.qty;
				gri.vqty = item.vqty;
				found = true;
				break;
			}
		}
		
		if( !found )
			ret.add(item);
	}

	public boolean updateItem(GoodsRestItem item) {
		if( data.items == null )
			data.items = new ArrayList<GoodsRestItem>();
		
		boolean updated = false, finded = false;
		//for(int i=0; i<data.items.size(); i++ ) {
		//	GoodsRestItem gri = data.items.get(i);
		for(GoodsRestItem gri : data.items) {
			if( gri.id.compareTo(item.id) == 0 && gri.date.compareTo(item.date) == 0 ) {
				finded = true;
				
				if( item.qty != gri.qty || item.vqty != gri.vqty ) {
					updated = true;
					if( item.qty != 0 || item.vqty != 0 ) {
						gri.qty = item.qty;
						gri.vqty = item.vqty;
					} else {
						data.items.remove(gri);
					}
				}					
				break;
			}
		}
		
		if( !finded && (item.qty != 0 || item.vqty != 0) ) {
			GoodsRestItem gri = new GoodsRestItem();
			gri.date = item.date;
			gri.id = item.id;
			gri.qty = item.qty;
			gri.vqty = item.vqty;
			
			data.items.add(gri);
			
			updated = true;
		}
		
		if( updated )
			write();
		
		return updated;		
	}

}
