package com.grsoft.napoleon.documents;

import java.util.Date;

import android.content.Context;

import com.grsoft.dataobjects.DocDataObject;
import com.grsoft.dataobjects.impl.DbObject;


/**
 * 
 * Все документы порождены от этого класса. У всех документов должно быть поле id
 * 
 * @author 1111
 *
 */

public abstract class Document<T extends DocDataObject> 
	extends DbObject<T>
{
	public Date getDate() { return data.date; }
	/**
	 * Возвращает id контрагента
	 * @return
	 */
	public String getId(){ return data.id; }
	
	/**
	 * Может содержать HTML коды
	 * @param context
	 * @return
	 */
	public String getDescription(Context context){ return "";}
	public long sum(){ return 0; }
	public int getSumType() { return 0; }
	public String getNumber() { return ""; }

	abstract public void open(Context context);
	public int qty() { return 0; }
}
