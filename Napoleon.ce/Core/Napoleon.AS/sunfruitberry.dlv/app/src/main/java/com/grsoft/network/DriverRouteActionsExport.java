package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DriverRouteActions;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.network.exception.RuntimeException;

public class DriverRouteActionsExport implements ObjectExportListener {

	List<DriverRouteActions> items = new ArrayList<DriverRouteActions>();
	
	public DriverRouteActionsExport() {
		DataTraveler.travel(DriverRouteActions.class, new DataTraveler.Travel<DriverRouteActions>(true) {

			@Override
			public boolean travel(DataTraveler<DriverRouteActions> item) {
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
		for(DriverRouteActions ari : items) {
			ari.params = ParamState.ofExported;
			w.insertRecord(ari);
		}
		w.close();
	}

	@Override public String getObjectName() { return "DriverRouteActions"; }

	@Override
	public int size() {
		return items.size();
	}

	@Override
	public DataObject get(int i) {
		return i < items.size() ? items.get(i) : null;
	}

}
