package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.InputNumber;
import android.content.Context;

public class SalesImplEx extends SalesImpl {
	@Override
	public boolean delete() {
		if( isExported() )
			return true;
		return super.delete();
	}
	
	@Override
	protected void updateQtyPrice(PriceImpl priceImpl, int priceUpdate) {
		PricePrint price = (PricePrint) priceImpl.getData();
		if( priceUpdate != 0 && checkPriceQty() ) {
			price.vanQty += priceUpdate;
			priceImpl.write();
		}
	}
	
	public void refreshDocSum() {
		getDocumentType().refreshDocSum(data.id);
		DebtDoc.instance().refreshDocSum(data.id);
	}

	public void inputIncass(final Context context) {
		if( !isEditable() )
			return;

		InputNumberDlg.open(context, new InputNumber() {
			
			@Override
			public void applayInput(int value, Object... params) {
				
				if (!isEditable())
					return;
				
				((SalesEx)data).incass = value;
				write();
				close();
				
				if (context instanceof DataSetNotify)
					((DataSetNotify)context).notifyDataSetChanged();
			}

			@Override public int getValue() { return ((SalesEx)data).incass; }
		}, Consts.SUM_SCALE, true, context.getString(R.string.input_value), false);
	}
}
