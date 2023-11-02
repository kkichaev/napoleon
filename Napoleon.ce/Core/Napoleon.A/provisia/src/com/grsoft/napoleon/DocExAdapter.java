package com.grsoft.napoleon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentsAdapter;

public class DocExAdapter extends DocumentsAdapter {

	public DocExAdapter(Context context, DocType docType, String orgId, String order) {
		super(context, docType, orgId, order, R.layout.docs_row_ex);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v= super.getView(position, convertView, parent);
		int vsbl = ( curDocType == DebtDoc.instance() ) ? View.VISIBLE : View.GONE;
		View tv = v.findViewById(R.id.tvType);
		tv.setVisibility(vsbl);
		return v;
	}
}
