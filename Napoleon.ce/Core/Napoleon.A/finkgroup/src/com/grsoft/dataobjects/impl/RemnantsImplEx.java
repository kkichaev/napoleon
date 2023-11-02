package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlgFace;
import com.grsoft.napoleon.InputNumberEx;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class RemnantsImplEx extends RemnantsImpl {
	
	public void editItem(final RemnantItemEx item, final Context context) {
		if( !isEditable() )
			return;
		
		InputNumberDlgFace.open(context, new InputNumberEx() {
			
			@Override public boolean useComma() { return true; }
			@Override public boolean replaceCommaToPlus() { return Features.REPLACE_COMMA_TO_PLUS; }
			
			@Override
			public void applayInput(int[] value, Object... params) {
				if(isExported())
					setExported(false);
				
				item.qty = value[0];
				item.weight = value[1];
				
				write();
				close();
				
				((DataSetNotify)context).notifyDataSetChanged();
				RemnantsDoc.instance().refreshDocSum(data.id);
			}

			@Override
			public int[] getValues() {
				int[] result = new int[4];
				result[0] = item.qty;
				result[1] = item.weight;
				
				return result;
			}
		});}

	@Override
	public void editItem(final long itemRowid, final Context context) { 
		if( !isEditable() )
			return;

		InputNumberDlgFace.open(context, new InputNumberEx() {
			
		@Override public boolean useComma() { return true; }
		@Override public boolean replaceCommaToPlus() { return Features.REPLACE_COMMA_TO_PLUS; }
		
		@Override
		public void applayInput(int[] value, Object... params) {
			if(isExported())
				setExported(false);
			
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.read(itemRowid);
			priceImpl.close();
			
			RemnantItemEx item = new RemnantItemEx();
			item.id = priceImpl.getData().id;
			data.items.add(item);
			
			item.qty = value[0];
			item.weight = value[1];
			
			write();
			close();
			
			((DataSetNotify)context).notifyDataSetChanged();
			RemnantsDoc.instance().refreshDocSum(data.id);
		}

		@Override
		public int[] getValues() {
			int[] result = new int[4];
			result[0] = 0;
			result[1] = 0;
			
			return result;
		}
	});}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		data.date = Util.getDateTime();
		data.created = Util.getDateTime();
		
		data.id = orgId;
		data.latitude = coord.latitude;
		data.longitude = coord.longitude;
		data.params = 0;
		write();
		
		long r = getRowid();
		
		if (r != ExtrasConst.INVALID_ID) {
			DocType.setCurDoc(RemnantsDoc.instance());
			Warehouse.open(context, this, false);
		}
		
		return false;
	}
}
