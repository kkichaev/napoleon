/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Слушатель для процесса обновления
 *
 * kki   05/11/2010   creating
 */
package com.grsoft.network;

import com.grsoft.network.UpdateProcessInfo.UpdateStatus;

public interface UpdateProcessListener
{
	void onUpdate(UpdateStatus status, int progress);
}
