package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;

public class NapoleonEx extends Napoleon {
	@Override
	protected ArrayList<MenuHandler> createDocMenuList() {
		ArrayList<MenuHandler> ret = super.createDocMenuList(); 
		ret.add(new MenuHandler(getString(R.string.wsorder_title), new Runnable() {
			@Override public void run() { WSOrderList.open(NapoleonEx.this); }
		}));
		return ret;
	}
	
	@Override
	protected void onResume() {
		if(DocType.getCurDoc() == WSOrderDoc.instance())
			DocType.setCurDoc(SalesDoc.instance());
		
		super.onResume();
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
}
