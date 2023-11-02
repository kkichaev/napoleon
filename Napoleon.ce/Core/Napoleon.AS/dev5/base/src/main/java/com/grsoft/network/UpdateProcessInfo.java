/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Информация о текущем процессе обновления базы данных
 *
 * kki   05/11/2010   creating
 */
package com.grsoft.network;
import com.grsoft.aceteam.R;

public class UpdateProcessInfo
{
	public enum UpdateStatus 
	{
		BEGIN_UPDATE,
		BEGIN_SEND,
		ENDREQUEST_UPDATE,
		ENDREQUEST_SEND,
		END, 
		STEP,
		STEP_SEND,
		END_OF_PROCESS,
		SHOW_MESSAGE,
		START_OF_PROCESS,
		GPS_UPDATE,
		BEGIN_SEND_VISITS,
	};
	
	private String info;
	private UpdateStatus status;
	
	public UpdateProcessInfo(UpdateStatus status, String info)
	{
		this.info = info;
		this.status = status; 
	}
	
	public String getInfo()
	{
		return info;
	}
	
	public UpdateStatus getStatus()
	{
		return status;
	}
}
