package com.grsoft.napoleon.documents;

import java.util.Date;

import com.grsoft.dataobjects.impl.ReturnRequestImpl;
import com.grsoft.napoleon.R;

public class ReturnRequestDoc extends OrderDoc {
	static ReturnRequestDoc instance = null;
	
	static public ReturnRequestDoc instance() { 
		if(instance == null)
			instance = new ReturnRequestDoc();
		return instance;
	}
	
	ReturnRequestDoc() {
		super("Заявка на возврат", "ReturnRequest", ReturnRequestImpl.class);
	}

	@Override public int getResurceId() { return R.drawable.return_doc; }
	@Override public int getResurce2Id() { return R.drawable.return_doc_2; }
	
	@Override public boolean outOfScript() { return true; }
	
	@Override public int getDocTitle() { return R.string.return_request_doc; }
	
	@Override
	public boolean removeTill(Date tillDate) {	
		return true;		
//		DocList dl = docList(null, "", "created < " + Long.toString(tillDate.getTime()));
//		for(Document<?> d : dl) {
//			d.delete();
//		}
//		dl.close();
//		return true;
	}
}
