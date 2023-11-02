package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;

public class OrderDeliveryDetailEx extends OrderDeliveryDetail {
	@Override
	public void onBackPressed() {
		if( doc.getData().items != null && doc.getData().items.size() > 0 ) {
			boolean summertime = false;
			StringBuilder sb = new StringBuilder();
			ConfigImpl ci = new ConfigImpl();
			if( ci.getValue(sb, "ЛетнийВариант")) {
				int val = 0;
				try {
					val = Integer.parseInt(sb.toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				if( val == 1 )
					summertime = true;
			}
	
			if( summertime && ((OrderImplEx)doc).haveUnsettedItems()) {
				WarehouseEx.openSummerTime(this, doc);
				return;
			}
		}
		super.onBackPressed();
	}
}
