/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Составное поле CostItem объекта Price
 *
 * kki   23/10/2010   creating
 */

package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class CostItem extends DataObject
{
	@FieldOrder(order=0)
	@Scale(value= Consts.SUM_SCALE)
    public int cost; 
}
