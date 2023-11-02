package com.grsoft.napoleon;

import android.view.View;
import android.widget.CheckBox;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImpl;

public class PriceCountEx extends PriceCount {
	static final int SERT_FLAG = 0x80;
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		if(document != null && document instanceof OrderImpl) {
			View v = findViewById(R.id.trSert);
			if(v != null) {
				v.setVisibility(View.VISIBLE);
				
				OrderItem oi = (OrderItem)((OrderImpl)document).findItem(price.getData().id);
				if( oi != null )
					((CheckBox)findViewById(R.id.cbSert)).setChecked((oi.flags & SERT_FLAG) != 0);
			}
		}
	}
	
	@Override
	protected boolean updateOrder() {
		boolean ret = super.updateOrder();
		if(document != null && document instanceof OrderImpl) {
			OrderItem oi = (OrderItem)((OrderImpl)document).findItem(price.getData().id);
			if( oi != null ) {
				CheckBox cb = (CheckBox)findViewById(R.id.cbSert);
				if( cb != null ) {
					if( cb.isChecked() ) oi.flags |= SERT_FLAG;
					else oi.flags &= (~SERT_FLAG);
					document.write();
				}
			}
		}
		
		return ret;
	}
}
