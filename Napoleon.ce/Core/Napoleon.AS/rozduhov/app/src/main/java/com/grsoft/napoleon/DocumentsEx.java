package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class DocumentsEx extends Documents {

	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		String order = getOrder(docType); 
		return new Adapter(this, docType, id, order);
	}
	
	class Adapter extends DocumentsAdapter {

		public Adapter(Context context, DocType docType, String orgId, String order) {
			super(context, docType, orgId, order);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View res = super.getView(position, convertView, parent); 
			Document<?> doc = (Document<?>)getItem(position);
			if(doc instanceof OrderImplEx && ((OrderImplEx)doc).isLoadedFromKIS()) {
				res.setBackgroundResource(R.drawable.yellow_row_selector);
			}
			return res;
		}
		
	}
}
