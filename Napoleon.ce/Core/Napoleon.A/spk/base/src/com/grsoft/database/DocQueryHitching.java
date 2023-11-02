package com.grsoft.database;

import java.util.Date;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.network.ObjectExportListener;

public class DocQueryHitching extends Hitching
implements ObjectExportListener{
	private DocQuery docQuery = new DocQuery();
	
	public DocQueryHitching(String objectName, Date date) {
		super(DocQuery.class, objectName);
		docQuery.date = date;
	}

	class DocQuery extends DataObject{
		public Date date;
	}

	@Override
	public int size() {
		return 1;
	}


	@Override
	public DataObject get(int i) {
		return docQuery;
	}
}
