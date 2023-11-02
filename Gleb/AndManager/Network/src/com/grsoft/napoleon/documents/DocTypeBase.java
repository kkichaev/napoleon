package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.view.View;
import android.widget.Adapter;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.GPSPos;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.DatePeriod;

/**
 * Чтобы не заводить отдельный класс для DocTypeManager - статические методы класса - используются как Manager
 * Классы типов документа - singletion
 * Лучше всего их получать так DocType.getDocType("Заявки");
 * @author 1111
 *
 */
public abstract class DocTypeBase {
	
	public static ArrayList<DocTypeBase> docTypes = new ArrayList<DocTypeBase>();
	static private DocTypeBase curDoc;
	
	protected String name;
	protected String objName = "";
	protected Class<? extends Document<?>> docClass;
	
	protected DocTypeBase(String name, Class<? extends Document<?>> docClass) { this.name = name; this.docClass = docClass; }
	
	protected DocTypeBase(String name, String objName, Class<? extends Document<?>> docClass) { 
		this.name = name; 
		this.docClass = docClass;
		this.objName = objName;
	}
	
	public String getName() { return name; }
	
	public String getObjectName() { return objName; }
	
	
			
	public boolean isEqual(DocTypeBase dt) { return dt != null && name == dt.name; }
	
	public Document<?> create() {
		Document<?> ret = null;
		
		try{
			ret = docClass.newInstance();
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public Class<? extends DataObject> dataType() {
		Document<?> d = create();
		return d.getData().getClass();
	}
	
	public boolean isCreatable() { return CreatableDocument.class.isAssignableFrom(docClass); }
	
	public DocList docList(String orgId) { return docList(orgId, null); }
	
	public DocList docList(String orgId, String order) { return docList(orgId, order, ""); }
	
	public DocList docList(String orgId, String order, String where) {
		String whereStr = (orgId == null) ? "" : "id='" + orgId + "'";
		
		if( where != null && where.length() > 0 ) {
			if( whereStr.length() > 0 )
				whereStr += " AND ";
			whereStr += where;
		}
		
		DocList list = new DocList(docClass, whereStr, order);
		return list;
	}

	public DocList docList(String orgId, String order, DatePeriod selection){
		String where = "";
		
		if(selection != null){
			String selectionField = "date";
			
			if( selection.periodType == DatePeriod.CREATED 
					&& CreatableDocument.class.isAssignableFrom(docClass) )
				selectionField = "created";
			
			where = String.format(" %s >= %s and %s <= %s", 
					selectionField, Long.toString(selection.begin.getTime()), 
					selectionField, Long.toString(selection.end.getTime()));
		}
		
		return docList(orgId, order, where);
	}
	
	/**
	 * Отображение документа в Documents (layout docs_list_row)
	 * @param view
	 * @param doc
	 * @param position
	 */
	public void setView(Adapter adapter, View view, Document<?> doc) {}
	
	/**
	 * При закрытии необходимо привести вид к базовому
	 * @param documentsView
	 */
	public void viewClosed(Activity documentsView) {}
	
	/**
	 * Изменение заголовка столбцов для документов (layout documents)
	 * @param documentsView
	 */
	public void viewOpened(Activity documentsView) {}
	
	public static void removeType(DocTypeBase dt) {
		docTypes.remove(dt);
	}
	
	public static void addType(DocTypeBase dt) {
		docTypes.add(dt);
	}
	
	public static DocTypeBase getCurDoc() {
		return curDoc; 
	}
	
	public static void setCurDoc(DocTypeBase docType) { setCurDoc(docType, false); }
	
	public static void setCurDoc(DocTypeBase docType, boolean init) { 
		if( docTypes.contains(docType) == false )
			docTypes.add(docType);
		
		curDoc = docType;
		
		if(init)
			curDoc.updateTodayDocs();
	}
	
	public static void checkTables() {
		for(DocTypeBase dt : docTypes) {			
			if( CreatableDocument.class.isAssignableFrom(dt.docClass) ) {
				Document<?> d = dt.create();
				DbWriter.checkDBTable(d.getData().getClass());
			}
		}
		
		DbWriter.checkDBTable(GPSPos.class);
	}
	
	public static List<DocExportListener> getDocuments(boolean commonDocs, boolean visitDoc) {
		List<DocExportListener> ret = new ArrayList<DocExportListener>();
		
		for(DocTypeBase dt : docTypes) {			
			if( CreatableDocument.class.isAssignableFrom(dt.docClass) ) {
				boolean isVisit = false;
				
				try{
					isVisit = Class.forName("com.grsoft.napoleon.documents.VisitDoc")
							.isAssignableFrom(dt.getClass());
				}catch(Exception e){
					e.printStackTrace();
				}
				
				if( commonDocs && !isVisit || visitDoc && isVisit ) {
					DocExportListener docs = dt.getDirtyDocuments();
					if( docs != null && docs.getDocuments().getCount() > 0 )
						ret.add(docs);
				}
			}
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public DocExportListener getDirtyDocuments() {
		Document<?> d = create();
		
		if (d instanceof CreatableDocument){
			return new DocSendListner(getObjectName(), 
				(Class<? extends CreatableDocument<?>>) d.getClass(), 
				"params", ParamState.ofExported);
		} else
			return null;
	}

	public static DocTypeBase getDocType(String objectName) {
		DocTypeBase ret = null;
		for(DocTypeBase dt : docTypes)
		{
			if( dt.objName.compareTo(objectName) == 0 )
			{
				ret = dt;
				break;
			}
		}
		
		return ret;	
	}
	
	public static void setCurDocType(String objectName) {
		DocTypeBase dt = getDocType(objectName);
		if( dt != null )
			setCurDoc(dt);
	}
	
	public void updateTotalSum(Activity activity, int sum, int weight, int count){}
	
	private ArrayList<String> todays = new ArrayList<String>();
	
	private void updateTodayDocs(){
		synchronized (DocTypeBase.class) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date now = calendar.getTime();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			
			String selectionField = "date";
			if( CreatableDocument.class.isAssignableFrom(docClass) )
				selectionField = "created";
			
			String where = String.format(" %s >= %s and %s <= %s", 
					selectionField, Long.toString(now.getTime()), 
					selectionField, Long.toString(calendar.getTime().getTime()));
			
			todays.clear();

			Document<?> doc = create();			
			if (doc != null){
				String tableName = DataObjectInfo.getInstance().getTableName(doc.getData().getClass());
				
				if (tableName != null && DbWriter.isTableExists(tableName)){
					try {
						Cursor c = DataBaseManager.getDataBase()
								.query(tableName, new String[]{"id"}, where, null, null, null, null);
											
						while(c.moveToNext())
							todays.add(c.getString(c.getColumnIndexOrThrow("id")));
						
						c.close();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public boolean isHasCreatedToday(String id){
		return todays.contains(id);
	}
}
