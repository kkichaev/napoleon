package com.grsoft.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

import com.grsoft.napoleon.FeaturesBase;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.network.util.UnicodUtils;

public class Debug
{
	private static final String DBG_LOG_FILE_NAME = Path.getFilesDir() + "dbg_log.log";
	private static final String NAPOLEON_TAG = "Napoleon";
	
	public static void dbgPrint(String msg)
	{
		dbgPrint(msg,(Object[])null);
	}
	
	public static void dbgPrint(String format, Object... args )
	{
		Log.d(NAPOLEON_TAG, String.format(insertSpaceTail(format), args).toString());
	}
	
	private static String insertSpaceTail(String string)
	{
		return string + "\t";
	}
	
	public static void dbgWriteFileLn(String message) throws RuntimeException
	{
		try
		{
			dbgWriteFileLn(UnicodUtils.toBytes(message), false);
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}
	
	public static void dbgWriteFileLn(byte[] buffer, boolean append)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(DBG_LOG_FILE_NAME, append);
			fos.write(buffer);
			fos.close();
		} catch (IOException e)
		{
			dbgPrint("DbgWriteFileLn error %s", e.getMessage());
		}
	}
	
	public static void DbgWriteFile(String message)
	{
		
	}
	
	public static void putLog(String str) {
		if(FeaturesBase.DO_LOGING) {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

				try {
					File logFile = new File(Environment.getExternalStorageDirectory(),"Napoleon/debug.log");
					
					if (!logFile.exists()) 
						logFile.createNewFile();
					
					FileOutputStream fos = new FileOutputStream(logFile, true);
					fos.write(str.getBytes());
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
