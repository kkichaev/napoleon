/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 *
 * kki   11/04/2011   creating
 */
package com.grsoft.napoleon.documents;

import android.content.Context;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;

/**
 * Документ с подпунктами
 * @author kki
 *
 */
public interface Itemsable {

	/**
	 * Для диалога по созданию/редактированию item
	 * @param itemRowid rowid пункта для редактирования
	 * @param context Activity на которой будет показан диалог
	 */
	void editItem(long itemRowid, Context context);
	
	/***
	 * Возвращает items по ID
	 * @param itemId item id
	 * @return найденный объект, может быть null, если не найден
	 */
	DataObject findItem(String itemId);
	
	int getItemColor(); 
	int getItemValue(Price item);
	int getItemQty(Price item);
	long getItemSum(Price item);
	boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack);
}
