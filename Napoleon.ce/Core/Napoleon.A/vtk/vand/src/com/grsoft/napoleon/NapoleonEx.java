package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.RestockDoc;
import com.grsoft.napoleon.documents.VandSellDoc;
import com.grsoft.util.MenuHandler;

public class NapoleonEx extends Napoleon {
	@Override
	protected ArrayList<MenuHandler> createDocMenuList() {
		ArrayList<MenuHandler> ret = super.createDocMenuList(); 
		ret.add(new MenuHandler("Заказ на борт", new Runnable() {
			@Override public void run() { RestockList.open(NapoleonEx.this); }
		}));
		return ret;
	}
	
	@Override
	protected void onResume() {
		if(DocType.getCurDoc() == RestockDoc.instance())
			DocType.setCurDoc(VandSellDoc.instance());
		
		super.onResume();
	}
}
