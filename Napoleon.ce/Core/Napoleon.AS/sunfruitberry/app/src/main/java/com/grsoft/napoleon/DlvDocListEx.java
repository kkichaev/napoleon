package com.grsoft.napoleon;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.documents.Document;

public class DlvDocListEx extends DlvDocList {
	@Override
	protected long getDocSum(Document<?> doc) {
		long sum = super.getDocSum(doc);
		if(doc instanceof DeliveryImpl) {
			for(DeliveryItem di : ((DeliveryImpl)doc).getData().items) {
				if(di.qty < 0)
					sum -= di.sum;
			}
		}
		return sum;
	}
}
