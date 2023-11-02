/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Документ - Посещения
 *
 * kki   04/03/2011   creating
 */
package com.grsoft.napoleon.documents;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.aceteam.R;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.DocExportListener;

public class VisitDoc extends DateDocType
{
	static public final String DOC_NAME = "Посещения";
	static public final String OBJ_NAME = "Visit";
		
	protected static VisitDoc instance = null;
	
	protected VisitDoc() {
		super(DOC_NAME, OBJ_NAME, VisitImpl.class);
	}

	protected VisitDoc(String docName, String objName, Class<? extends Document<?>> type) { 
		super(docName, objName, type);
	} 
	
	public static DocType instance() {
		if( instance == null )
			instance = new VisitDoc();
		return instance;
	}

	@Override
	public boolean photoDoc() {
		return true;
	}

	static public DocType instance(Class<? extends VisitImpl> type) {
		instance = new VisitDoc(DOC_NAME, OBJ_NAME, type);
		return instance;
	}
	
	@Override
	public List<CreateDocDataObject> getDirtyPhotos() {
		final List<CreateDocDataObject> ret = new ArrayList<CreateDocDataObject>();
		
		Class<? extends DataObject> visClass = DbObject.getDataType(Visit.class);
		DataTraveler.travel(visClass, new DataTraveler.Travel<Visit>(true) {

			@Override
			public boolean travel(DataTraveler<Visit> item) {
				ret.add(item.data);
				return true;
			}
		}, "(([params] & " + Integer.toString(ParamState.ofExported) + " ) == 0)");
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DocExportListener getDirtyDocuments() {
		CreatableDocument<?> d = (CreatableDocument<?>)create();
		return new VisitDocSendListner(objName, 
				(Class<? extends CreatableDocument<?>>) 
				d.getClass(), "params", ParamState.ofExported);
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.visit_doc;
	}
	
	@Override
	public int getResurce2Id() {
		return R.drawable.visit_doc_2;
	}
	
	@Override
	public boolean removeTill(Date tillDate) {
		VisitImpl visitImpl = (VisitImpl) VisitDoc.instance().create();
		Cursor<Visit> cursor = new Cursor<Visit>(visitImpl, "[date] < " + Long.toString(tillDate.getTime()));
		
		while(cursor.moveNext()){
			((VisitImpl)cursor.current()).deleteSrcItems();
		}
		
		return super.removeTill(tillDate);
	}
	
	class VisitDocSendListner extends DocSendListner{

		public VisitDocSendListner(String objName,
				Class<? extends CreatableDocument<?>> docType,
				String fieldName, int exportFlag) {
			super(objName, docType, fieldName, exportFlag);
			VisitDocList visitDocList = new VisitDocList(list);
			list = visitDocList;
		}
		
		class VisitDocList extends DocList{
			public VisitDocList(DocList list){
				document = create();
				ids = new Vector<Long>();
				
				long listSize = 0;
				
				long lim = ((CfgNplW)ConfigManager.getConfig()).max_packet_len;
				for(int i = 0; i < list.getCount() && listSize < lim; i++){
					PhotoDocument impl = (PhotoDocument) list.get(i);
					ids.add(impl.getRowid());
					listSize += impl.size();					
				}
			}
		}
		
	}
	
	@Override
	public int getDocTitle() {
		return R.string.visit_doc_title;
	}
}
