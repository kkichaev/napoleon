package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.DatePeriod;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class DocListEx extends DocList {
	@Override
	protected DocListAdapter createListAdapter(DocType docType) {
		return new Adapter(this, docType, saveDatePeriod);
	}
	
	class Adapter extends DocListAdapter {

		public Adapter(Context context, DocType docType, DatePeriod filter) {
			super(context, docType, filter);
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
