/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 *
 *
 * kki   14/10/2010   creating
 */

package com.grsoft.dataobjects;

import com.grsoft.network.exception.RuntimeException;

public interface CommandArgs
{
	String getCommand();
	String getParams() throws RuntimeException;
}
