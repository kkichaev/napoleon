package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.Defect;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.napoleon.DefectDetail;
import com.grsoft.napoleon.DefectEditor;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DefectDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.InputNumber;

public class DefectImpl extends OrderImplBase<Defect> implements Itemsable {

	@Override
	public void open(Context context) {
		DefectDetail.open(context, this);
	}

	@Override
	public void editItem(final long itemRowid, final Context context) { 
		InputNumberDlg.open(context, new InputNumber() {
		
		@Override
		public void applayInput(int value, Object... params) {
			
			if (isExported())
				return;
			
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.read(itemRowid);
			
			if (updateQty(priceImpl, value, 0, false) && context instanceof DataSetNotify)
				((DataSetNotify)context).notifyDataSetChanged();
			
			priceImpl.close();
		}

		@Override
		public int getValue() {
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.read(itemRowid);
			priceImpl.close();
			OrderItem ri = (OrderItem)findItem(priceImpl.data.id);
			
			return ri == null ? 0 : ri.qty;
		}
	});}
	
	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		DefectEditor.open(ctx, this, isOldOrder);
	}

	@Override
	public CreatableDocument<Defect> createInstance() {
		return new DefectImpl();
	}
	
	@Override
	public DocType getDocumentType() {
		return DefectDoc.instance();
	}
}
