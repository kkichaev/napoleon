package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.ReqNewOrg;
import com.grsoft.dataobjects.impl.ReqNewOrgImpl;

public class ReqNewOrgDoc extends DateDocType {
	static ReqNewOrgDoc instance = null;
	
	public static ReqNewOrgDoc instance() {
		if(instance == null)
			instance = new ReqNewOrgDoc();
		return instance;
	}
	
	ReqNewOrgDoc() {
		super("Новая органиация", "ReqNewOrg", ReqNewOrgImpl.class);
	}

	@Override
	public List<CreateDocDataObject> getDirtyPhotos() {
		final List<CreateDocDataObject> ret = new ArrayList<CreateDocDataObject>();
		
		DataTraveler.travel(ReqNewOrg.class, new DataTraveler.Travel<ReqNewOrg>(true) {

			@Override
			public boolean travel(DataTraveler<ReqNewOrg> item) {
				ret.add(item.data);
				return true;
			}
		}, "(([params] & " + Integer.toString(ParamState.ofExported) + " ) == 0)");
		
		return ret;
	}
}
