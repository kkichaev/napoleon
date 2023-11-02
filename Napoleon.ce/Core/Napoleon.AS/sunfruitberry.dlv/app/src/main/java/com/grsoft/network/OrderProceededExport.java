package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.network.exception.RuntimeException;

public class OrderProceededExport implements ObjectExportListener {
	List<OrderProceededEx> items = new ArrayList<OrderProceededEx>();
	
	public OrderProceededExport() {
		DataTraveler.travel(OrderProceededEx.class, new DataTraveler.Travel<OrderProceededEx>(true) {

			@Override
			public boolean travel(DataTraveler<OrderProceededEx> item) {
				items.add(item.data);
				return true;
			}
		}, "((params & " + Integer.toString(ParamState.ofExported) + ") = 0)");
	}
	
	@Override public void onStart() {}
	@Override public void onRead(RawObject rawObject) throws RuntimeException {}
	@Override public void onSave() {}

	@Override
	public void onEnd() {
		DbWriter w = new DbWriter();
		for(OrderProceededEx ari : items) {
			ari.params = ParamState.ofExported;
			w.insertRecord(ari);
		}
		w.close();
	}

	@Override public String getObjectName() { return "OrderProceeded"; }
	@Override public int size() { return items.size(); }
	@Override public DataObject get(int i) { return i < items.size() ? items.get(i) : null; }
}
