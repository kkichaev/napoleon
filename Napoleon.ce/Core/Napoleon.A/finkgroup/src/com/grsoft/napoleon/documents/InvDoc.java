package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Gather;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.GatherImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;

public class InvDoc extends DateDocType {
	static InvDoc instance = null;
	
	public static DocType instance() {
		if( instance == null )
			instance = new InvDoc();
		return instance;
	}
	
	InvDoc() { super("Инвентаризация", "Complete", GatherImpl.class); }
	
	@Override public int getResurceId() { return R.drawable.inv_doc; }
	
	@Override
	public DocExportListener getDirtyDocuments() {
		
		String where = "(([params] & " + Integer.toString(ParamState.ofExported | Gather.COMPLEETE) + " ) == " + 
				Integer.toString(Gather.COMPLEETE) + ")";
		
		return new DocSendListner(getObjectName(), GatherImpl.class, where); 
	}
}
