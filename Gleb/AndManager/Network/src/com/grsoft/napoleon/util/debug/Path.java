package com.grsoft.napoleon.util.debug;

import java.io.File;

import android.content.Context;
import android.os.Environment;

import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;

public class Path
{
	public static String filesDirPath = "";
	public static  String BASE_NAME = "napoleon.db";
	private static String dataDirPath = ""; 
	public static final String SHARED_FOLDER = "Napoleon";

	/***
	 * Директория приложения
	 * @return
	 */
	public static String getFilesDir(){
		return filesDirPath;
	}
	
	/***
	 * Полный путь с именем файла базы данных
	 * @return
	 */
	public static String getDataBasePath(){
		return new File(filesDirPath, BASE_NAME).getAbsolutePath(); 
	}
	
	/***
	 * Путь к директории, которая хранит данные программы
	 * @return
	 */
	public static String getDataDir(){
		String result = dataDirPath;
		
		Config cfg = ConfigManager.getConfig();
		
		if (cfg.dataDirShare && Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED))
			result = Environment.getExternalStorageDirectory() + 
					"/" + SHARED_FOLDER + "/datadir";
		
		return result;
	}
	
	/***
	 * Инициализация директорий, создаются при необходимости
	 * @param context
	 */
	public static void init(Context context){
		File filesDir = context.getFilesDir();
		
		if(!filesDir.exists())
			filesDir.mkdirs();
		
		filesDirPath = filesDir.getAbsolutePath();
		
		File dataDir = new File(filesDir.getParent(), "src");
		
		if(!dataDir.exists())
			dataDir.mkdirs();
		
		dataDirPath = dataDir.getAbsolutePath();
		
		if (Environment.getExternalStorageState()
		.equals(Environment.MEDIA_MOUNTED)){
			File sharedFolder = new File(Environment.getExternalStorageDirectory(), SHARED_FOLDER);
			
			if (!sharedFolder.exists())
				sharedFolder.mkdirs();
		}
	}
	
	public static void clearDataDir(){
		String path = getDataDir();
		File f = new File(path);
		
		if (f.exists()) {
	        String deleteCmd = "rm -r " + path;
	        Runtime runtime = Runtime.getRuntime();
	        try {
	            runtime.exec(deleteCmd);
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	    }

	}
	
}
