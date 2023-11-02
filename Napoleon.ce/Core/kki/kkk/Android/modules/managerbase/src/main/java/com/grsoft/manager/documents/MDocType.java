package com.grsoft.manager.documents;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DocDataObject;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

import android.content.Context;


public class MDocType extends DocTypeBase {
	private static Map<Class<? extends DataObject>, MDocType> docmap = null;
	
	/***
	 * 
	 * @param objName
	 * Имя объекта на сервере
	 * @param docClass
	 * Тип документа
	 */
	protected MDocType(String objName, Class<? extends Document<?>> docClass) {
		super(objName, objName, docClass);
	}
	
	public List<Document<?>> userDoc(String userid, Date date){
		date = Util.resetTime(date);
		DatePeriod dp = DatePeriod.createRange(date, DatePeriod.MIN_PER_DAY);
		
		String where = String.format("userid = '%s' and created >= %d and created < %d", userid, dp.begin.getTime(), dp.end.getTime());
		DocList list = new DocList(docClass, where, null);
		
		return list.instanceCollections();
	}
	
	/***
	 * 
	 * @return Тип вложенного объекта документа
	 */
	public Class<? extends DataObject> getDataType(){
		Class<? extends DataObject> result = DataObject.class;
		Document<?> doc = create();
		
		if(doc != null)
			result = doc.getData().getClass();
		
		return result;
	}
	
	/***
	 * Возвращает заголовок документа по типу
	 * @param context
	 * @param type
	 * @return
	 */
	public static String getTitle(Context context, Class<? extends DocDataObject> type){
		if (docmap == null)
			initTytpes();
		
		return context.getString(docmap.get(type).getDocTitle());
	}

	private static void initTytpes() {
		docmap = new HashMap<Class<? extends DataObject>, MDocType>();	
		
		for(DocTypeBase dt : docTypes){
			MDocType mdt = (MDocType) dt;
			docmap.put(mdt.getDataType(), mdt);
		}
	}
	
	/***
	 * @param type
	 * @return
	 */
	public static MDocType getInstance(Class<? extends CreateDocDataObject> type){
		if (docmap == null)
			initTytpes();
		
		return docmap.get(type);
	}

	public Hitching getRcvHitch() {	return new Hitching(getDataType(), objName);	}
}
