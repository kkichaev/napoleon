package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;

@TableInfo(name="FocusMatrix", keyFields="type")
@ServerInfo(name="FocusMatrix")
public class FocusMatrix extends DataObject {
	public static String MATRIX_NAME = "<Фокусная матрица>";
	
	public String type = "";
	
	public List<FocusMatrixItem> items = new ArrayList<FocusMatrixItem>();
	
	public static HashSet<String> get(String type) {
		HashSet<String> ret = new HashSet<String>();
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		FocusMatrix data = new FocusMatrix();
		DbReader r = new DbReader();
		if( r.select(data, data.getTableName(), "[type]='" + type +"'") )
			for(FocusMatrixItem i : data.items) {
				p.id = i.id;
				if(pi.read())
					ret.add(i.id);
			}
		r.close();
		pi.close();
		return ret;
	}
	
	public static HashSet<String> get(Document<?> o) {
		String type = OrgTypeBinding.getType(o.getId());
		return get(type);
	}
}
