/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   12/04/2010   creating
 */
package com.grsoft.napoleon.documents;

import java.util.List;

/***
 * Документ, который содержит в себе 
 * items 
 * @author kki
 *
 */
public interface DocItemsStock {
	
	/***
	 * Получает id's из последнего документа
	 * @param id контрагент
	 * @param itemIds выходной параметр, id - items
	 */
	public void getItemsFromLastDoc(String id, List<String> itemIds, int period);
}
