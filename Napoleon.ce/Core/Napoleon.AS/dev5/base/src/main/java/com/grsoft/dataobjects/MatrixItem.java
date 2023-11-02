/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   24/06/2011   creating
 */
package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.types.FieldOrder;

/***
 * Содержание матрицы
 * @author kki
 *
 */
public class MatrixItem extends DataObject{
	/***
	 * Код из price
	 */
	@FieldOrder(order=0)
	public String id = "";
	
	@FieldOrder(order=1)
	public int order;
	
	public MatrixItem(String id) { this.id = id; }
	public MatrixItem() {}
}
