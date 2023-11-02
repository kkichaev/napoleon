package com.grsoft.dataobjects;

import java.util.Date;
import java.util.Hashtable;

import com.grsoft.database.DbReader;
import com.grsoft.database.TableInfo;
import com.grsoft.napoleon.documents.SalesDoc;

@TableInfo(name="HandledDocs", keyFields="created")
public class HandledDocuments extends DataObject {
	
	public static String getLastNum(String docType) {
		String lastNum = "";
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(HandledDocuments.class);
		HandledDocuments doc = new HandledDocuments();
		if( r.select(doc, table, "type='"+docType+"'" , "pdaNumber desc") )
			lastNum = doc.pdaNumber;
		r.close();
		return lastNum;
	}
	
	public Date created;
	public String pdaNumber;
	public String baseNumber;
	public String type;
	
	static Hashtable<String, String> docNumbers = null;
	
	public static void clearCache() {
		docNumbers = null;
	}
	
	public static void loadCache() {
		if(docNumbers == null) {
			docNumbers = new Hashtable<String, String>();
			HandledDocuments hd = new HandledDocuments();
			String table = DataObjectInfo.getInstance().getTableName(hd.getClass());
			DbReader r = new DbReader();
			boolean bdo = r.select(hd, table, null);
			while( bdo ) {
				docNumbers.put(hd.type.trim().length() == 0 ?
						SalesDoc.OBJ_NAME + hd.created : hd.type + hd.created, 
						hd.baseNumber);
				hd = new HandledDocuments();
				bdo = r.selectNext(hd);
			}
			r.close();
		}
	}
	
	public static String getNumber(String docType, Date created){
		String result = "";
		String key = docType + created;
		
		if(docNumbers.containsKey(key))
			result = docNumbers.get(key);
			
		return result;
	}
}
