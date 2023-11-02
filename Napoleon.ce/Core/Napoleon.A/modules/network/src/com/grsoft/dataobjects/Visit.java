/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Объект данных Visit
 *
 * kki   26/10/2010   creating
 */
package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;

@TableInfo(name="Visit", keyFields = "created")
public class Visit extends VisitInfo implements PhotoListDoc
{
	/**
	 * Служебный флаг описывающий
	 * текущее состояния
	 * 
	 * устаревшее - теперь используется <b>params</b>
	 */
	@Scale(value=1)
	@Deprecated
	public int flags;
	
	/**
	 * Фотографии
	 */
	public List<VisitItem> items = new ArrayList<VisitItem>();

	/**
	 * причины визита
	 */
	public String cause = "";
	
	public int sendedPhotos = 0;

	@Override public List<VisitItem> getItems() { return items; }
	@Override public String getDocName() { return "Visit"; }
	@Override public String getItemName() { return "VisitItemDoc"; }
	@Override public void setItems(List<VisitItem> newItems) { items = newItems; }
}
