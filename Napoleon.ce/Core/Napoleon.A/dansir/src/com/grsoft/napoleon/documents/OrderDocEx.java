package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.OrderDetailEx;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class OrderDocEx extends OrderDoc {
	public static void init() {
		instance = new OrderDocEx();
	}
	
	@Override
	public DocExportListener getDirtyDocuments() {
		return new DirtyOrders(objName);
	}
}

class DirtyOrders implements DocExportListener {
	
	DirtyOrderList list;
	String objName;
	
	public DirtyOrders(String objName) {
		this.objName = objName;
		list = new DirtyOrderList();
	}

	@Override public void onStart() {}

	@Override public void onRead(RawObject rawObject) throws RuntimeException { }

	@Override public void onSave() { }

	@Override public String getObjectName() { return objName; }

	@Override
	public void onEnd() {
		for(Document<?> doc : list) {
			if( doc != null )
				((CreatableDocument<?>)doc).setExported(true);
		}
	}

	@Override public DocList getDocuments() { return list; }
}

class DirtyOrderList extends DocList {
	public DirtyOrderList() {
		document = new OrderImpl();
		ids = new ArrayList<Long>();
		
		String table = DataObjectInfo.getInstance().getTableName(Order.class);
		String where = "(([params] & " + Integer.toString(ParamState.ofExported) + " ) == 0)";
		
		List<Long> idDoc = DbReader.readIds(table, where, null);
		for(Long rid : idDoc) {
			document.read(rid);
			if( document.sum() >= OrderDetailEx.MIN_ORDER_SUM )
				ids.add(rid);
		}
	}
}
