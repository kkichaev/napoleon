package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.OffTakeHistory;


public class PriceCountEx extends PriceCount {
	@Override
	protected OffTakeHistory getHistory(String docId, boolean fromOrders) {
		return new OffTakeHistoryEx(docId, price.getData().id, fromOrders);
	}
}
