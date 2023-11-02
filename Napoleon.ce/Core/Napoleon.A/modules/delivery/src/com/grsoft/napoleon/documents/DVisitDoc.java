package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DVisit;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Dispatch;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.DVisitImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.dostavka.R;

public class DVisitDoc extends DocTypeBase
{
	static public final String DOC_NAME = "Посещения";
	static public final String OBJ_NAME = "Visit";
		
	protected static DVisitDoc instance = null;
	
	protected DVisitDoc() { super(DOC_NAME, OBJ_NAME, DVisitImpl.class);}

	protected DVisitDoc(String docName, String objName, Class<? extends Document<?>> type) { 
		super(docName, objName, type);
	} 
	
	public static DocTypeBase instance() {
		if( instance == null )
			instance = new DVisitDoc();
		return instance;
	}

	static public DocTypeBase instance(Class<? extends DVisitImpl> type) {
		instance = new DVisitDoc(DOC_NAME, OBJ_NAME, type);
		return instance;
	}
	
	@Override protected int getExportFlag() { return super.getExportFlag() | Dispatch.NOT_READY_TO_SEND;	}
	
	@Override public int getDocTitle() { return R.string.visit_doc_title; }
	
	@Override public boolean photoDoc() { return true; }
	
	
	@Override
	public List<CreateDocDataObject> getDirtyPhotos() {
		final List<CreateDocDataObject> ret = new ArrayList<CreateDocDataObject>();
		
		Class<? extends DataObject> visClass = DbObject.getDataType(DVisit.class);
		DataTraveler.travel(visClass, new DataTraveler.Travel<DVisit>(true) {

			@Override
			public boolean travel(DataTraveler<DVisit> item) {
				ret.add(item.data);
				return true;
			}
		}, "(([params] & " + Integer.toString(ParamState.ofExported) + " ) == 0)");
		
		return ret;
	}
}