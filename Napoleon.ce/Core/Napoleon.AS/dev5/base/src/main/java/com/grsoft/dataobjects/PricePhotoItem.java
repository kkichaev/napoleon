/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * kki   15/07/2011   creating
 */
package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.types.FieldOrder;

/***
 * Объект списка PricePhoto.items
 * @author kki
 *
 */
public class PricePhotoItem extends DataObject {
	
	/***
	 * Код связи с прайсом
	 */
	@FieldOrder(order=0)
	public String id = "";
}
