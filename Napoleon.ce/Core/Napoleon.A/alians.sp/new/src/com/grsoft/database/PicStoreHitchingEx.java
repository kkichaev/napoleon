package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.PicStoreEx;
import com.grsoft.dataobjects.impl.PicStoreImplEx;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocSendListner;

public class PicStoreHitchingEx extends DocSendListner{
	private final static String OBJ_NAME = "PicStore";
	
	public PicStoreHitchingEx(){
		super(OBJ_NAME, PicStoreImplEx.class, "params", ParamState.ofExported);
		
		PicStoreList visitDocList = new PicStoreList(list);
		list = visitDocList;
	}
	
	class PicStoreList extends DocList{
		public PicStoreList(DocList list){
			document = new PicStoreImplEx();
			ids = new ArrayList<Long>();
			
			StringBuilder sb = new StringBuilder();
			sb.append("(([params] & ").append(ParamState.ofExported).append(" ) == 0)");
			
			DbWriter.checkDBTable(PicStoreEx.class);
			String table = DataObjectInfo.getInstance().getTableName(PicStoreEx.class);
			List<Long> arr = DbReader.readIds(table, sb.toString(), null);
			
			long listSize = 0;
			
			long lim = 5000000L;
			
			PicStoreImplEx picStore = new PicStoreImplEx();
			for(int i = 0; i < arr.size() && listSize < lim; i++){
				long rowid = arr.get(i);
				picStore.read(rowid);
				ids.add(rowid);
				listSize += picStore.size();					
			}
		}
	}
	
	@Override
	public String getObjectName() {	return OBJ_NAME; }
}
