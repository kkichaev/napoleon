package com.grsoft.napoleon.documents;

import java.util.Vector;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.DefectReportImpl;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.DocExportListener;


public class DefectReportDoc extends VisitDoc {
	static private DefectReportDoc instance = null;
	static public final String DOC_NAME = "DefectReport";
	static public final String OBJ_NAME = "DefectReport";
	
	public static DocType instance() {
		if( instance == null )
			instance = new DefectReportDoc();
		return instance;
	}
	
	protected DefectReportDoc() {
		super(DOC_NAME, OBJ_NAME, DefectReportImpl.class);
	}
	
	@Override
	public int getDocTitle() {	return R.string.defect_doc_title; }
	
	@SuppressWarnings("unchecked")
	@Override
	public DocExportListener getDirtyDocuments() {
		CreatableDocument<?> d = (CreatableDocument<?>)create();
		return new DocSendListnerEx(OBJ_NAME, 
				(Class<? extends CreatableDocument<?>>) 
				d.getClass(), "params", ParamState.ofExported){
		};
	}
	
	
	class DocSendListnerEx extends DocSendListner{

		public DocSendListnerEx(String objName,
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
				long lim = ((CfgNpl)ConfigManager.getConfig()).max_packet_len;
				
				for(int i = 0; i < list.getCount() && listSize < lim; i++){
					PhotoDocument impl = (PhotoDocument) list.get(i);
					listSize += impl.size();
					
					if (listSize < lim)
						ids.add(impl.getRowid());
				}
			}
		}
	}
	
	@Override
	public boolean outOfScript() { return true; }
}
