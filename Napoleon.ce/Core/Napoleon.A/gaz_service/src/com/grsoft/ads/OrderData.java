package com.grsoft.ads;


import android.os.Bundle;

import com.grsoft.ads.documents.OrderDataDoc;
import com.grsoft.ads.documents.OrderItemsDocument;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;

public class OrderData extends BaseActivity {
	public static final String TAB_NAME = "orderdara";
	public static final String TAB_CAPTION = "Данные";

	private long rowid;
	private OrderItemsDocument<? extends CreateDocDataObject> orderItemsDocument;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		
		orderItemsDocument = ((OrderItemsDocument<? extends CreateDocDataObject>) 
				DocType.getCurDoc().create());
		
		rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, 
				ExtrasConst.INVALID_ID);
	}

	private int getLayout(){
		int result = R.layout.userorderdata;
		
		DocType curDocType = DocType.getCurDoc();
		if (curDocType instanceof OrderDataDoc)
			result = ((OrderDataDoc)curDocType).getDataLayout();
		
		return result;
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		DocType curDocType = DocType.getCurDoc();
		if (orderItemsDocument != null &&
				curDocType instanceof OrderDataDoc &&
				orderItemsDocument.read(rowid, false))
			((OrderDataDoc)curDocType).updateView(this, orderItemsDocument);
		
		orderItemsDocument.close();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		DocType curDocType = DocType.getCurDoc();
		
		if (orderItemsDocument != null &&
				curDocType instanceof OrderDataDoc &&
				orderItemsDocument.read(rowid, false) &&
			((OrderDataDoc)curDocType).updateDoc(this, orderItemsDocument))
			orderItemsDocument.write();
		
		orderItemsDocument.close();
		
		closeAdapters();
	}


	public void closeAdapters() {
		DocType curDocType = DocType.getCurDoc();
		
		if (curDocType instanceof OrderDataDoc)
			((OrderDataDoc)curDocType).closeAdapters(this);
	}
}
