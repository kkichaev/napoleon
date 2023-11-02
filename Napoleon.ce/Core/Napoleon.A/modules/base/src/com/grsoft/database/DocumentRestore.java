package com.grsoft.database;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;

public class DocumentRestore extends DataObjectRestore {
	protected DocTypeBase docType;

	public DocumentRestore(DocTypeBase docType) {
		this(docType, docType.getObjectName());
	}
	
	public DocumentRestore(DocTypeBase docType, String objName) {
		super(docType.dataType(), objName, "created");
		this.docType = docType;		
	}

	public DocumentRestore(DocTypeBase docType, String objName, int months) {
		super(docType.dataType(), objName, "created");
		this.docType = docType;		
		makeDocReceiveCondition("created", months, 0);
	}
	
	public DocumentRestore(DocTypeBase docType, String objName, String field) {
		super(docType.dataType(), objName, field);
		this.docType = docType;		
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		try{
			if(docType instanceof DocType)
				((DocType)docType).refreshDocSum();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void beforeWrite(DataObject dobj) {
		((CreateDocDataObject)dobj).params |= ParamState.ofExported;
	}
}
