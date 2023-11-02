package com.grsoft.database;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.documents.DocType;

public class DocumentRestore extends DataObjectRestore {
	protected DocType docType;

	public DocumentRestore(DocType docType) {
		this(docType, docType.getObjectName());
	}
	
	public DocumentRestore(DocType docType, String objName) {
		super(docType.dataType(), objName, "created");
		this.docType = docType;		
	}
	
	public DocumentRestore(DocType docType, String objName, String field) {
		super(docType.dataType(), objName, field);
		this.docType = docType;		
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		try{
			docType.refreshDocSum();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void beforeWrite(DataObject dobj) {
		((CreateDocDataObject)dobj).params |= ParamState.ofExported;
	}
}
