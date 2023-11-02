package com.grsoft.napoleon;

import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.impl.PromoAuditImpl;
import com.grsoft.napoleon.documents.CreatableDocument;

public class PromoAuditActionList extends ActionListBase{
	
	public static void open(Context context, long rowid, String itemid, List<String> actions){
		ActionListBase.open(context, PromoAuditActionList.class, rowid, itemid, actions);
	}
	
	@Override
	protected CreatableDocument<? extends CreateDocDataObject> createDocument() {
		return new PromoAuditImpl();
	}

	@Override
	public int getLayoutId() { return R.layout.promoactionlist; }
	
	@Override
	protected void onResume() {
		super.onResume();
		document.read();
		
		BaseAdapter adapter = (BaseAdapter) list.getAdapter();
		
		if(adapter != null)
			adapter.notifyDataSetChanged();
	}
}
