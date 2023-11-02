package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.ScriptEx;
import com.grsoft.dataobjects.Visit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.OrderBundleDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.GpsCoord;

import android.content.Context;

public class ScriptImplEx extends ScriptImpl {
	Set<String> disabledFirms;
	
	@Override
	public List<DocExportListener> getSendedDocuments() {
		List<DocExportListener> docs = new ArrayList<DocExportListener>();
		
		docs.add(new DocSendListner(ScriptDoc.OBJ_NAME, this));

		int index = 0;
		CreatableDocument<?>[] cd = getDocuments();
		for( ScriptItem si : data.items ) {
			CreatableDocument<?> doc = cd[index];
			if( doc != null) {
				docs.add(new DocSendListner(si.type, doc));
				if(doc instanceof OrderBundleImpl)
					docs.add(((OrderBundleImpl)doc).getSendedDocs(disabledFirms));
				else if(doc instanceof ReturnRequestImpl) {
					VisitImplEx vi = new VisitImplEx();
					Visit v = vi.getData();
					v.created = ((ReturnRequestImpl)doc).getData().visitDoc;
					if(vi.read())
						docs.add(new DocSendListner(VisitDoc.OBJ_NAME, vi));
					vi.close();
				}
			}
			index++;
		}
		
		return docs;
	}
	
	public String scriptName() {
		if(((ScriptEx)data).scriptName.length() == 0) {
			ScriptDefImpl sdi = new ScriptDefImpl();
			ScriptDef sd = sdi.getData();
			sd.id = data.scriptId;
			sdi.read();
			sdi.close();
			
			((ScriptEx)data).scriptName = sd.name;
			write();
		}
		return ((ScriptEx)data).scriptName;
	}
	
	@Override
	protected boolean initInternal(Context c, String orgId, GpsCoord gpsCoord, ScriptDef srciptDef) {
		((ScriptEx)data).isMain = ((ScriptDefEx)srciptDef).isMain;
		
		return super.initInternal(c, orgId, gpsCoord, srciptDef);
	}
	
	public void setDisabledFirms(Set<String> firms) { disabledFirms = firms; }

	public void addDocumentsToSend(Map<String, List<Long>> docs) {
		for(ScriptItem si : data.items) {
			if( si.state == ScriptItem.DOC_INITED ) {
				List<Long> dlist = docs.get(si.type);
				if(dlist == null) {
					dlist = new ArrayList<Long>();
					docs.put(si.type, dlist);
				}
				dlist.add(si.date.getTime());
				if(si.type.equals(OrderBundleDoc.instance().getObjectName())) {
					OrderBundleImpl obi = (OrderBundleImpl)createDocument(si.type, si.date, null, null);
					obi.addDocumentsToSend(docs, disabledFirms);
				}
			}
		}
	}

	@Override
	protected boolean canAddDocSum(CreatableDocument<?> doc) {
		return (doc instanceof OrderBundleImpl) || super.canAddDocSum(doc);
	}
}
