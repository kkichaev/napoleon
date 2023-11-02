package com.grsoft.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.util.debug.Path;

import android.os.Environment;

public class FLog {
	private static void appendLog(String text)
	{       
		File logFile = new File(Environment.getExternalStorageDirectory(), Path.SHARED_FOLDER + "/log.txt");
		
		if (!logFile.exists())
		{
			try{
				logFile.createNewFile();
			} 
			catch (IOException e){
				e.printStackTrace();
			}
	   }
	   
	   BufferedWriter buf = null;
	   
	   try{
	      buf = new BufferedWriter(new FileWriter(logFile, true)); 
	      buf.append(text);
	      buf.newLine();
	   }
	   catch (IOException e){
	      e.printStackTrace();
	   }
	   finally {
		   try {
			   buf.close();
		   }catch(Exception e) {}
	   }
	}
	
	public static void d(String text) {
		if(Features.FILE_LOG_DEBUG) {
			StringBuilder sb = new StringBuilder();
			sb.append(Calendar.getInstance().getTime().toString());
			sb.append('\t');
			sb.append(text);
			appendLog(sb.toString());
		}
	}
}
