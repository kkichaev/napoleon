/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Текущее значение прогресса в фоновой работе
 *
 * kki   22/03/2011   creating
 */
package com.grsoft.network;
import com.grsoft.aceteam.R;

import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.view.SimpleMessageBox;

public class ProgressValue
{
	public ProgressValue(UpdateStatus status, int value)
	{
		this(status, value, null);
	}
	
	public ProgressValue(UpdateStatus status, int value,
			SimpleMessageBox simpleMessageBox)
	{
		this.status = status;
		this.progress = value;
		this.simpleMessageBox = simpleMessageBox;
	}
	
	public UpdateStatus status;
	public int progress;
	public SimpleMessageBox simpleMessageBox;
}
