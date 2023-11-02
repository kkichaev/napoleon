package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.grsoft.dataobjects.impl.DefectImpl;
import com.grsoft.napoleon.documents.DefectDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.ExtrasConst;

public class DefectDetail extends OrderDetail {

	OptionsMenuHelper optionsMenuHelper = new OptionsMenuHelper();
	DefectImpl doc = new DefectImpl();
	ListView items;
	
	static public void open(Context context, DefectImpl doc) {
		Intent i = new Intent(context, DefectDetail.class);		
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(DocType.getCurDoc() != DefectDoc.instance())
			DocType.setCurDoc(DefectDoc.instance());
		
		super.onCreate(savedInstanceState);
	}
	
//	@Override
//	protected void setAdapter() {
//		lvItems.setAdapter(new DetailAdapter());
//	}
//	
//	class DetailAdapter extends OrderItemsAdapter {
//		@Override
//		protected void drawInternal(View view, String name, int color, OrderItem item) {
//			super.drawInternal(view, name, color, item);
//		}
//	}
}
