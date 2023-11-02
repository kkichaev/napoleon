package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;

@TableInfo(name="categMatrix", keyFields="name")
@ServerInfo(name="CategoryMatrix")
public class CategoryMatrix extends DataObject {
	public String name = "";
	public List<MatrixItem> items = new ArrayList<MatrixItem>();

	public static CategoryMatrix get(Document<?> doc) {
		CategoryMatrix ret = new CategoryMatrix();

		if(doc != null) {
			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx) oi.getData();
			oe.id = doc.getId();
			if(oi.read()) {
				DbReader r = new DbReader();
				r.select(ret, ret.getTableName(), "name = '" + oe.category + "'");
				r.close();
			}
			oi.close();
		}
		
		
		return ret;
	}

	public boolean inSet(String id) {
		for(MatrixItem mi : items)
			if(mi.id.equals(id))
				return true;
		
		return false;
	}
}
