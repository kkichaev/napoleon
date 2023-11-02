package com.grsoft.ads.documents;

import android.app.Activity;

import com.grsoft.dataobjects.CreateDocDataObject;

public interface OrderDataDoc {
	void updateView(Activity activity, OrderItemsDocument<? extends CreateDocDataObject> doc);
	boolean updateDoc(Activity activity, OrderItemsDocument<? extends CreateDocDataObject> doc);
	int getDataLayout();
	void closeAdapters(Activity activity);
}
