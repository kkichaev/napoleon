package com.grsoft.script.dataobjects.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.script.ScriptContext;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.Script;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.script.documents.CreateByScriptDef;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.MessageBox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ScriptImpl extends CreatableDocument<Script> {

	ScriptDef def = null;
	
	static public ScriptContext containsDocument(String docType, Date created, String id) {
		ScriptContext res = null;

		DocList dl = ScriptDoc.instance().docList(id);
		for( int i=0; i<dl.getCount(); i++ ) {
			ScriptImpl si = (ScriptImpl) dl.get(i);
			int pos = si.getDocPos(docType, created);

			if( pos != -1 ) {
				res = new ScriptContext(si, pos);
				break;
			}
		}
		
		dl.close();

		return res;
	}
	
	boolean containsDoc(String docType, Date created) {
		boolean res = false;
		for(ScriptItem item : data.items) {
			if( item.type.equals(docType) && item.date.equals(created)) {
				res = true;
				break;
			}
		}
		return res;
	}

	int getDocPos(String docType, Date created) {
		int res = -1;

		for(int i =0; i < data.items.size(); i ++) {
			ScriptItem item = data.items.get(i);
			if( item.type.equals(docType) && item.date.equals(created)) {
				res = i;
				break;
			}
		}
		return res;
	}

	public CreatableDocument<?>[] getDocuments() {
		CreatableDocument<?>[] res = new CreatableDocument<?>[data.items.size()];
		int index = 0;
		boolean changed = false;
		for(ScriptItem item : data.items) {
			if( item.state == ScriptItem.DOC_INITED ) {
				CreatableDocument<?> doc = createDocument(item.type, item.date, null, null);
				res[index] = doc;
				if( doc == null ) {
					item.state = ScriptItem.DOC_NONE;
					changed = true;
				}
			}
			index++;
		}
		if( changed )
			write();
		return res;
	}
	
	protected CreatableDocument<?> createDocument(String docType, 
			Date docDate, ScriptDef def,  ScriptDefItem defItem) {
		CreatableDocument<?> doc = null;
		DocType dt = (DocType) DocType.getDocType(docType);
		
		if( dt != null ) {
			if (defItem != null && dt instanceof CreateByScriptDef)
				doc = (CreatableDocument<?>) ((CreateByScriptDef)dt).create(def, defItem);
			else
				doc = (CreatableDocument<?>) dt.create();
			
			if( docDate != null ) {
				doc.getData().created = docDate;
				boolean res = doc.read();
				doc.close();
				if( !res )
					doc = null;
			}
		}
		
		return doc;
	}
	
	@Override public void open(Context context) { 
		ScriptEdit.open(context, this, ExtrasConst.INVALID_ID);

		if( data.items.size() == 1 ) {
			ScriptItem si = data.items.get(0);
			if( si.state == ScriptItem.DOC_INITED ) {
				CreatableDocument<?> doc = createDocument(si.type, si.date, null, null);
				if( doc != null ) {
					DocType.setCurDocType(si.type);
					doc.open(context);
					return;
				}
			}
		}
	}
	
	@Override public long sum() { return data.sum; }
	
	protected List<ScriptDef> scripts;
	protected void scriptListProcess(Context context, String orgid){}
	
	@Override
	public boolean init(final Context context, final String orgId, final GpsCoord gpsCoord) {
		scripts = ScriptDefImpl.getAvailableScripts(orgId);
		scriptListProcess(context, orgId);
		if( scripts.size() == 0 ) {
			MessageBox.show(context, context.getString(R.string.error), 
					context.getString(R.string.scripts_isnot_found));
			return false;
		}
		
		// если сценариев несколько - выберем необходимый
		if( scripts.size() == 1) {
			initInternal(context, orgId, gpsCoord, scripts.get(0));
			return false;
		}
		
		CharSequence[] items = new CharSequence[scripts.size()];
		int index = 0;
		for(ScriptDef sd : scripts) {
			String name = "?";
			if( sd.name.length() > 0 )
				name = sd.name;
			else {
				DocType dt = (DocType) DocType.getDocType(sd.items.get(0).curType);
				if( dt != null )
					name = dt.getName();
			}
			items[index] = name;
			index++;
		}
		
		AlertDialog.Builder b = createSelectVariantDlg(context, orgId, gpsCoord, items);
		b.create().show();
		return false;
	}

	protected AlertDialog.Builder createSelectVariantDlg(final Context context, final String orgId, final GpsCoord gpsCoord, CharSequence[] items) {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle(R.string.select_variant);
		b.setItems(items, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				initInternal(context, orgId, gpsCoord, scripts.get(which));
			}
		});
		return b;
	}
	
	/**
	 * ќткрываем документ всегда сами, чтобы было одинаково дл€ скрипта из одного документа и из нескольких
	 * ѕри инициализации сразу создаем все элементы дл€ документов сценари€, но инициализируем только один документ
	 */
	protected boolean initInternal(Context c, String orgId, GpsCoord gpsCoord, ScriptDef srciptDef) {
		data.scriptId = srciptDef.id;
		
		// put self before
		super.init(c, orgId, gpsCoord);
		postInit(c);
		if( srciptDef.items.size() > 0 )
			ScriptEdit.open(c, this, srciptDef.id);
		
		return false;
	}
	
	protected void postInit(Context c){}
	
	public void openDoc(Context c, int index, ScriptDef defDoc) {
		ScriptDefItem defItem = defDoc.items.get(index);
		if( !IsEnabled(index, defDoc) )
			return;
		
		CreatableDocument<?> doc = null;
		ScriptItem item = data.items.get(index);
		if( item.state == ScriptItem.DOC_INITED || !isEditable())
			doc = createDocument(item.type, item.date, defDoc, defItem);

		DocType.setCurDocType(item.type);
		
		if( doc == null && isEditable()) {
			doc = createDocument(item.type, null, defDoc, defItem);
			GpsCoord gps = new GpsCoord(data.latitude, data.longitude, data.stltime);
			boolean res = doc.init(c, data.id, gps);
			
			item.date = doc.getData().created;
			item.state = ScriptItem.DOC_INITED;
			
			skipItemsTo(index);
			write();
			
			if( !res )
				return;
		}
		
		if(doc != null){
			doc.setExported(isExported());
			doc.open(c);
		}
	}

	/**
	 * ѕ–опускаем все документы до заданного
	 * @param index
	 */
	public void skipItemsTo(int index) {
		// если мы пропустили документы, надо их отметить как пропущенные
		for( int i=0; i<index; i++ ) {
			ScriptItem di = data.items.get(i);
			if( di.state == ScriptItem.DOC_NONE )
				di.state = ScriptItem.DOC_SKIPPED;
		}
	}
	
	public boolean isDone(int position) {
		return (position < data.items.size()) ? (data.items.get(position).state == ScriptItem.DOC_INITED) : false;
	}
	
	public String itemType(int position) {
		return (position < data.items.size()) ? (data.items.get(position).type) : null;
	}
	
	public boolean isComplete() {
		if(Features.DEL_VISIT_WITHOUT_PHOTO) {
			boolean removeVisits = false;
			VisitImpl vi = (VisitImpl) VisitDoc.instance().create();
			String objName = VisitDoc.instance().getObjectName();
			for(ScriptItem si : data.items) {
				if(si.type.equals(objName) && si.isCompleete()) {
					if(vi.read(si.date.getTime())) {
						if(vi.isEmpty()) {
							vi.delete();
							si.state = ScriptItem.DOC_NONE;
							removeVisits = true;
						}
					} else {
						si.state = ScriptItem.DOC_NONE;
						removeVisits = true;
					}
				}
			}

			vi.close();
			if(removeVisits) {
				return false;
			}
		}
		if(def == null) {
			List<ScriptDef> res = DbReader.fetch(ScriptDef.class, "id=" + Integer.toString(data.scriptId));
			if(res.size() == 0) {
				return false;
			}
			def = res.get(0);
		}
		int count = Math.min(data.items.size(), def.items.size());
		for(int i=0; i<count; i++) {
			ScriptItem si = data.items.get(i);
			if(def.canSkip(si.pos))
				continue;
			if(!si.isCompleete()) {
				return false;
			}
		}
		return true;
	}

	public boolean isContainsItem() {
		for(ScriptItem item : data.items)
			if( item.state == ScriptItem.DOC_INITED )
				return true;
		
		return false;
	}
	
	public boolean IsEnabled(int position, ScriptDef defDoc) {
		boolean result = position == 0;
		
		if( position > 0 && position < data.items.size() ){
			ScriptItem item = data.items.get(position);
			result = item.state == ScriptItem.DOC_INITED;
		
			if(!result){
				ScriptItem prevItem = data.items.get(position-1);
				result = prevItem.isCompleete();
				if( !result ) {
					// провер€ем можем ли мы пропустить пердыдыщие пункты
					boolean canSkip = true;
					for(int i=position-1; i>=0; i-- ) {
						ScriptDefItem prevDef = defDoc.items.get(i);
						if( !prevDef.canSkip() ) {
							prevItem = data.items.get(i);
							canSkip = prevItem.isCompleete();
							break;
						}
					}
					result = canSkip;
				}
			}
		}
		
		return result;
	}
	
	@Override
	public boolean delete() {
		CreatableDocument<?>[] docs = getDocuments();
		if( !super.delete() )
			return false;
		
		for(CreatableDocument<?> doc : docs)
			if( doc != null )
				doc.delete();

		return true;
	}

	/**
	 * провер€ем все ли документы на месте, если что пересохран€ем сценарий
	 */
	public void refreshDoc() {
		
		if(!isEditable())
			return;
		
		long curSum = calcDocSum();
		
		if( data.sum != curSum ) {
			data.sum = curSum;
			write();
			ScriptDoc.instance().refreshDocSum(data.id);
		}
	}

	protected boolean canAddDocSum(CreatableDocument<?> doc) {
		return OrderImplBase.class.isAssignableFrom(doc.getClass()) && ((OrderImplBase<?>)doc).useDocSumInscriptSum();
	}

	public long calcDocSum() {
		long curSum = 0;
		boolean haveOrder = false;
		
		CreatableDocument<?>[] docs = getDocuments();
		for(CreatableDocument<?> doc : docs) {
			if( doc == null )
				continue;
			
			long ds = doc.sum();
			if(canAddDocSum(doc)){
				curSum += ds;
				haveOrder = true;
			} else if( !Features.SCRIPT_SUM_ONLY_FOR_SALES  && haveOrder == false && curSum < ds )
				curSum = ds;
		}
		return curSum;
	}
	
	public List<DocExportListener> getSendedDocuments() {
		List<DocExportListener> docs = new ArrayList<DocExportListener>();
		
		docs.add(new DocSendListner(ScriptDoc.OBJ_NAME, this));

		int index = 0;
		CreatableDocument<?>[] cd = getDocuments();
		for( ScriptItem si : data.items ) {
			if( cd[index] != null)
				docs.add(new DocSendListner(si.type, cd[index]));
			index++;
		}
		
		return docs;
	}

	public void setSkipped(int position) {
		if( position >= 0 && position < data.items.size()  ){
			ScriptItem item = data.items.get(position);
			item.state = ScriptItem.DOC_SKIPPED;
			skipItemsTo(position); 
			write();
		}
	}

	public boolean isSkipped(int position) {
		boolean result = false;

		if( position >= 0 && position < data.items.size()  ){
			ScriptItem item = data.items.get(position);
			result = item.state == ScriptItem.DOC_SKIPPED;
		}
		
		return result;
	}
	
	@Override
	public Date getDate() {
		return data.created;
	}

	/***
	 * ¬озвращает true, если есть неоправленные "незаконченные" сценарии
	 * @return
	 */
	public static boolean hasUncomplete() {
		boolean result = false;
		
		DocExportListener e =  ((ScriptDoc)ScriptDoc.instance()).getNotSend();

		if( e != null ) {
			for(Document<?> d : e.getDocuments()){
				ScriptImpl s = (ScriptImpl)d;
				
				if(!s.isComplete()){
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	public ScriptItem createItem(){
		ScriptItem result = null;
		Class <? extends DataObject> itemClass = DataObjectInfo.getInstance().getListType(data.getClass(), "items");

		try {
			result = (ScriptItem)itemClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

//	public boolean tryCompleete(ScriptDef scrDef) {
//		for(ScriptItem si : data.items) {
//			if(!si.isCompleete() && !scrDef.canSkip(si.pos))
//				return false;
//		}
//
//		skipItemsTo(data.items.size() - 1);
//		return true;
//	}
}
