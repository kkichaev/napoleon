package com.grsoft.napoleon;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.grsoft.napoleon.documents.DiscountType;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindTextWatcher;

public class DocumentsEx extends Documents {
	
	DocumentsAdapterEx adapter;
	
	@Override
	protected int getContentViewID() { return R.layout.documentsex; }
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		View findBtn = findViewById(R.id.btnFind);
		int vis = ( docType != DiscountType.instance() ) ? View.GONE : View.VISIBLE;
		findBtn.setVisibility(vis);
		
		super.adjustViewForDocType(docType);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		View v = findViewById(R.id.btnFind);
		v.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { doFind(); }
		});
		
		final EditText edFind = (EditText) findViewById(R.id.edFind);
		
		v = findViewById(R.id.btnDelFind);
		v.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				edFind.setText("");
			}
		});
		
		edFind.addTextChangedListener(new FindTextWatcher(edFind, lvDocs));
	}
	
	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		adapter = new DocumentsAdapterEx(this, docType, id, "date"); 
		return adapter;
	}
	
	protected void doFind() {
		int newVis = View.GONE;
		View llFind = findViewById(R.id.llFind); 
		if( llFind.getVisibility() == View.GONE ) {
			newVis = View.VISIBLE;
		} else {
			EditText edFind = (EditText) findViewById(R.id.edFind);
			edFind.setText("");
			adapter.resetFilter();
		}
		llFind.setVisibility(newVis);
	}
}

class DocumentsAdapterEx extends DocumentsAdapter implements FilterAdapter {
	public DocumentsAdapterEx(Context context, DocType docType, String orgId, String order) {
		super(context, docType, orgId, order);
	}

	@Override
	public void applyFilter(String value) {
		if( documents instanceof FilterAdapter ) {
			((FilterAdapter)documents).applyFilter(value);
			notifyDataSetChanged();
		}
	}

	@Override
	public void resetFilter() {
		if( documents instanceof FilterAdapter ) {
			((FilterAdapter)documents).resetFilter();
			notifyDataSetChanged();
		}
	}
}