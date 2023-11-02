/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Базовый класс к операциям чтения - записи из сети
 *
 * kki   03/02/2011   creating
 */

package com.grsoft.network;
import com.grsoft.aceteam.R;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;

public abstract class NetService
{
	protected UpdateProcessListener updateProcessListener;
	protected IOStream ioStream;
	protected List<? extends Hitching> sendHitch;
	protected List<Hitching> recieveHitch;
	
	public void setUpdateProcessListenet(UpdateProcessListener listener)
	{
		updateProcessListener = listener;
	}
	
	protected void fireUpdate(UpdateStatus status, int progress)
	{
		if (updateProcessListener != null)
			updateProcessListener.onUpdate(status, progress);
	}
}
