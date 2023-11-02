package com.grsoft.ads.database;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.ads.dataobjects.PicStore;
import com.grsoft.ads.dataobjects.impl.PicStoreImpl;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.documents.DocumentUtils;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.SliceHitching;
import com.grsoft.network.exception.RuntimeException;

public class PicStoreHitching implements ObjectExportListener, SliceHitching{
	private final static String OBJ_NAME = "PicStore";
	private List<Long> ids = new ArrayList<Long>();
	
	public PicStoreHitching(){
		fetch();
	}
	
	@Override
	public void onStart() {}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {}

	@Override
	public void onSave() {}

	@Override
	public void onEnd() {
		PicStoreImpl pc = new PicStoreImpl();
		
		for( int i=0; i< ids.size(); i++ ) {
			if (pc.read(ids.get(i))) {
				DocumentUtils.setExported(pc, pc.getData().params, true);
				pc.close();
			}
			
			pc.close();
		}
	}

	@Override
	public String getObjectName() {	return OBJ_NAME; }

	@Override
	public int size() {	
		return ids.size(); 
	}

	@Override
	public DataObject get(int i) {
		PicStoreImpl pc = new PicStoreImpl();
		pc.read(ids.get(i));
		pc.close();
		return pc.getData();
	}

	@Override
	public void fetch() {
		ids = new ArrayList<Long>(); 
		StringBuilder sb = new StringBuilder();
		sb.append("(([params] & ").append(ParamState.ofExported).append(" ) == 0)");
		sb.append(" and [readytosend] = 1");
		
		DbWriter.checkDBTable(PicStore.class);
		String table = DataObjectInfo.getInstance().getTableName(PicStore.class);
		List<Long> arr = DbReader.readIds(table, sb.toString(), null);
		
		long listSize = 0;
		
		long lim = 5000000L;
		
		PicStoreImpl picStore = new PicStoreImpl();
		for(int i = 0; i < arr.size() && listSize < lim; i++){
			long rowid = arr.get(i);
			picStore.read(rowid);
			ids.add(rowid);
			listSize += picStore.size();					
		}
	}
}
