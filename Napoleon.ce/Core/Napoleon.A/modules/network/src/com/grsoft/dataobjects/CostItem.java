/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Составное поле CostItem объекта Price
 *
 * kki   23/10/2010   creating
 */

package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;


public class CostItem extends DataObject
{
	@FieldOrder(order=0)
	@Scale(value=100)
    public int cost; 
}
