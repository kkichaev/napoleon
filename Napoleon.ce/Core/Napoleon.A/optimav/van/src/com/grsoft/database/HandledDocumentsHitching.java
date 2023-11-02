package com.grsoft.database;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.HandledDocuments;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class HandledDocumentsHitching extends Hitching {
		boolean inited = false;
		
		public HandledDocumentsHitching() {
			super(HandledDocuments.class, "HandledDocuments");
		}
		
		@Override
		public void onRead(RawObject rawObject) throws RuntimeException {
			if(!inited){
				DbWriter.dropTable(DataObjectInfo.getInstance()
						.getTableName(DbObject.getDataType(HandledDocuments.class)));
				DbWriter.checkDBTable(DbObject.getDataType(HandledDocuments.class));
				inited = true;
			}
				
			HandledDocuments dobj = (HandledDocuments) rawObject.createDataObject(dataObject);
			
			if(dobj.type.trim().length() == 0)
				dobj.type = SalesDoc.OBJ_NAME;
			
			dbProxy.insertRecord(dobj);
		}
}
