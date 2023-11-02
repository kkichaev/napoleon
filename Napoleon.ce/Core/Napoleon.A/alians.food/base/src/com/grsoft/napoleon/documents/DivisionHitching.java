package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DivisionInfo;
import com.grsoft.dataobjects.impl.DivisionInfoImpl;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class DivisionHitching extends Hitching {
	
	public static class DivisionAgents extends DataObject{
		public String id = "";
	}
	
	public static class Division extends DataObject{
		public int id = -1;
		public int parent = -1;
		public List<DivisionAgents> agents = new ArrayList<DivisionAgents>();
		public String delay = "";
	}
		
	public DivisionHitching() {
		super(Division.class, "Division");
	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Division d = (Division) rawObject.createDataObject(Division.class);
		
		if (d.agents.size() > 0)
			for (DivisionAgents a : d.agents) {
				DivisionInfoImpl info = new DivisionInfoImpl();
				info.getData().id = d.id;
				info.getData().userid = a.id;
				info.getData().delay = d.delay;
				info.getData().parent = d.parent;
				
				info.write();
			}
		else {
			DivisionInfoImpl info = new DivisionInfoImpl();
			info.getData().id = d.id;
			info.getData().userid = "";
			info.getData().delay = d.delay;
			info.getData().parent = d.parent;
			
			info.write();
		}
			
	}
	
	@Override
	public void prepareReading() {
		DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(DivisionInfo.class));
	}
}
