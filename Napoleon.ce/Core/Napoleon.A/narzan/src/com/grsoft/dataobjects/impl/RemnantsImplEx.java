package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.MatrixItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlgFace;
import com.grsoft.napoleon.InputNumberEx;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.DataSetNotify;

public class RemnantsImplEx extends RemnantsImpl {
	@Override
	public void editItem(final long itemRowid, final Context context) { 
		InputNumberDlgFace.open(context, new InputNumberEx() {
			
		@Override public boolean useComma() { return Features.INTEGER_INPUTS_QTY; }
		@Override public boolean replaceCommaToPlus() { return Features.REPLACE_COMMA_TO_PLUS; }
		
		@Override
		public void applayInput(int[] value, Object... params) {
			
			if (isExported())
				return;
			
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.read(itemRowid);
			priceImpl.close();
			
			RemnantItemEx item = (RemnantItemEx) findItem(priceImpl.getData().id);
			
			if(item == null){
				item = new RemnantItemEx();
				item.id = priceImpl.getData().id;
				data.items.add(item);
			}
			
			item.qty = value[0];
			item.face = value[1];
			
			write();
			close();
			
			((DataSetNotify)context).notifyDataSetChanged();
			RemnantsDoc.instance().refreshDocSum(data.id);
		}

		@Override
		public int[] getValues() {
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.read(itemRowid);
			priceImpl.close();
			RemnantItemEx ri = (RemnantItemEx)findItem(priceImpl.data.id);
			
			
			int[] result = new int[4];
			
			if(ri != null){
				result[0] = ri.qty;
				result[1] = ri.face;
			}
			
			OrgImpl org = new OrgImpl();
			org.getData().id = getId();
			
			if(org.read()){
				OrgEx oe = (OrgEx) org.getData();
				if (oe.matrix != null && oe.matrix.size() > 0)
					for(MatrixItem item: oe.matrix){
						if(item.id.equals(priceImpl.data.id)){
							MatrixItemEx ie = (MatrixItemEx)item;
							result[2] = ie.qty;
							result[3] = ie.face;
						}
					}
			}
			
			org.close();
			
			return result;
		}
	});}
}
