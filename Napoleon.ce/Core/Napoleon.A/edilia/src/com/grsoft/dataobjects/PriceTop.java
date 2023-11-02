package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.OrgImpl;

@TableInfo(name="PriceTop", keyFields="id")
@ServerInfo(name="PriceTop")
public class PriceTop extends DataObject {
	public String id = "";
	
	public List<MatrixItem> items = new ArrayList<MatrixItem>();
	
	public static List<MatrixItem> get(String orgId) {
		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx)oi.getData();
		o.id = orgId;
		oi.read();
		oi.close();
		
		final List<MatrixItem> ret = new ArrayList<MatrixItem>();
		DataTraveler.travel(PriceTop.class, new DataTraveler.Travel<PriceTop>() {

			@Override
			public boolean travel(DataTraveler<PriceTop> item) {
				for(MatrixItem i : item.data.items)
					ret.add(i);
				return false;
			}
		}, "id='" + o.catCode + "'");
		return ret;
	}
}
