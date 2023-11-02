/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Действия после обновления базы данных
 *
 * kki   12/10/2010   creating
 */
package com.grsoft.database;

import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.network.exception.RuntimeException;

/**
 * @author kki
 *
 */
public class PostUpdateDB implements Runnable
{

	public static Runnable POST_UPDATE_RUN = null;
	
	@Override
	public void run()
	{
		
		try
		{
			DebtDoc.postUpdateProcess();
			if(POST_UPDATE_RUN != null)
				POST_UPDATE_RUN.run();
		} 
		catch (RuntimeException e)
		{
			e.printStackTrace();
		}
	}

}
