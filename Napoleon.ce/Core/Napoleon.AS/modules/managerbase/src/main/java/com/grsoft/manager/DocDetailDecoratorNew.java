package com.grsoft.manager;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DocDataObject;

import android.app.ActionBar;
import android.view.View;
import android.widget.TextView;

public class DocDetailDecoratorNew extends DocDetailDecorator {
	@Override public int getLayoutID() { return R.layout.docitems_new; }
	
	@Override
	public void init(DocDetail dd) {
		super.init(dd);
		
		View v = dd.getLayoutInflater().inflate(R.layout.doc_detail_action_bar, null);
		TextView tv = (TextView) v.findViewById(R.id.tvTitle);
		
		DocDataObject data = getDocument().getData();
		if(data instanceof CreateDocDataObject){
			CreateDocDataObject exdata = (CreateDocDataObject)data;
			tv.setText(dd.getTitle(exdata));
		}
		
		ActionBar a = dd.getActionBar();
        a.setCustomView(v);
        a.setDisplayShowTitleEnabled(false);
        a.setDisplayShowCustomEnabled(true);
	}
}
