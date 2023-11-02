package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DymovTaskResult;
import com.grsoft.network.ObjectExportListener;

public class DymovTaskSender extends Hitching implements ObjectExportListener{
	List<DymovTaskResult> docs = new ArrayList<DymovTaskResult>();
	
	
	public DymovTaskSender() {
		super(DymovTaskResult.class, "DymovTaskResult");
		
		DataTraveler.travel(DymovTaskResult.class, new DataTraveler.Travel<DymovTaskResult>(){

			@Override
			public boolean travel(DataTraveler<DymovTaskResult> item) {
				docs.add(item.data);
				item.data = new DymovTaskResult();
				return true;
			}
			
		}, "flags=0");
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		DbWriter wr = new DbWriter();
		for(DymovTaskResult doc : docs) {
			doc.flags = DymovTaskResult.EXPORTED;
			wr.insertRecord(doc);
		}
		
		wr.close();
	}

	@Override public int size() { return docs.size(); }
	@Override public DataObject get(int i) { return i >= 0 && i < docs.size() ? docs.get(i) : null; }
}
