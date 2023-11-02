/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Компаратор для Filter
 *
 * kki   23/02/2011   creating
 */

package com.grsoft.dataobjects.impl;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.DataObject;

public interface FilterCmp
{
	boolean compareTo(DataObject dataObject, String filter);
}
