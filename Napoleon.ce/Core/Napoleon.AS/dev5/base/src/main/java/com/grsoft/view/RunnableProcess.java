/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Интерфейс фонового процесса
 *
 * kki   24/03/2011   creating
 */
package com.grsoft.view;
import com.grsoft.aceteam.R;

public interface RunnableProcess extends Runnable
{
	void onPreExecute();
	void run();
	void onPostExecute();
}
