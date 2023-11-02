package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Return;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.CreateReturn;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.ReturnDetail;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.InputNumber;

import android.content.Context;

public abstract class ReturnImplBase<T extends Return> extends OrderImplBase<T> {

	@Override
	public void editProperties(Context context, boolean isOldOrder) {
		CreateReturn.open(context, this, isOldOrder);
	}

	@Override public DocType getDocumentType() { return ReturnDoc.instance(); }
	
	@Override
	public void open(Context context) {
		ReturnDetail.open(context, this);
	}

	@Override
	public void editItem(final long itemRowid, final Context context) {
		if( !isEditable() )
			return;

		final PriceImpl priceImpl = new PriceImpl();
		priceImpl.read(itemRowid);
		priceImpl.close();
		
		InputNumberDlg.open(context, new InputNumber() {
			
			@Override
			public void applayInput(int value, Object... params) {
				
				if (!isEditable())
					return;
								
				long cost = 0;
				if( Features.USE_COST_IN_RETURNS ) {
					Class<? extends Document<?>> type = (Class<? extends Document<?>>) createInstance().getClass();
					CostStrategy cs = CostStrategy.getInstance(type);
					cost = cs.getItemCost(priceImpl.getData(), ReturnImplBase.this);
				}
				if (updateQty(priceImpl, value, cost, false) && context instanceof DataSetNotify)
					((DataSetNotify)context).notifyDataSetChanged();
								
				ReturnDoc.instance().refreshDocSum(data.id);
			}

			@Override
			public long getValue() {
				OrderItem ri = (OrderItem)findItem(priceImpl.data.id);				
				return ri == null ? 0 : ri.qty;
			}
		});
	}
	
	@Override protected boolean checkPriceQty() { return false; }
	
	@Override
	public boolean useDocSumInscriptSum() {
		return !Features.EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT;
	}
}
