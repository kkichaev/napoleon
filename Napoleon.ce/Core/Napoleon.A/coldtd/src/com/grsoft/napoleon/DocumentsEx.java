package com.grsoft.napoleon;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.DocFilterOnClickListener;

public class DocumentsEx extends DocumentsPrint {
	
	@Override
	protected void init(Bundle b) {
		super.init(b);
		
		View llh = findViewById(R.id.llHeader);
		
		if( llh != null) {
			llh.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					String address = org.getData().address;
					String uri = String.format("geo:0,0?q=%s", address );
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					startActivity(intent);
					return false;
				}
			});
		}
	}
	
	@Override
	protected DocFilterOnClickListener createDocFilter() {
		return new DocFilterOnClickListener(this){
			@Override
			protected void initData(boolean creatableFilter) {
				super.initData(creatableFilter);
				data.remove(WSOrderDoc.instance());
			}
		};
	}
	
	@Override
	protected void onlyVisitInit() {}
}

