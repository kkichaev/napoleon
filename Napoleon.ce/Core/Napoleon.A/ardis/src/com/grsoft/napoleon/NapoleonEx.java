package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DateDocType;
import com.grsoft.napoleon.documents.DocType;

import android.os.Bundle;
import android.view.View;

public class NapoleonEx extends Napoleon {
	@Override protected int getResourceID() { return R.layout.main_ex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnSync).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { UpdateDB.open(NapoleonEx.this); }
		});
	}

	void updateVisible(int gone, int visible) {
		View v;
		
		v = findViewById(gone);
		if( v != null )
			v.setVisibility(View.GONE);
		
		v = findViewById(visible);
		if( v != null )
			v.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);

		if(docType instanceof DateDocType)
			updateVisible(R.id.tvTotalSum, R.id.tvMainDocValColTitle);
		else
			updateVisible(R.id.tvMainDocValColTitle, R.id.tvTotalSum);			
	}
}
