package com.grsoft.napoleon.util.debug;

import java.io.File;

import android.content.Context;
import android.os.Environment;

import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;

public class Path
{
	public static String filesDirPath = "";
	public static  String BASE_NAME = "napoleon.db";
//	private static String dataDirPath = "";
	static File dataDir = null;
	public static String SHARED_FOLDER = "Napoleon";
	private static final String AGENTINFOXML = "exchange.dat";
	public static String GRSOFT_EXCHANGE = "grsoft_exchange";
	static String packageName = "";

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
	static File PictDir = null;
	public static String getDataDir(){
		return dataDir.getAbsolutePath();

//		String result = "";
//		result = Environment.getExternalStorageDirectory() +"/" + SHARED_FOLDER + "/datadir";
//
//		File dataDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + packageName + "/files");
//		result = dataDir.getAbsolutePath();
//
//		File ft = new File(result);
//		if(!ft.exists())
//			ft.mkdirs();
//		return result;
	}
	
	/***
	 * Инициализация директорий, создаются при необходимости
	 * @param context
	 */
	public static void init(Context context){
		packageName = context.getPackageName();
//		PictDir = new File(Environment.getExternalStorageDirectory() +"/NapoleonPic");
		File extDir = context.getExternalFilesDir(null);
		PictDir = new File(extDir, "NapoleonPic");

		File filesDir = context.getFilesDir();
		if(!filesDir.exists())
			filesDir.mkdirs();
		filesDirPath = filesDir.getAbsolutePath();
		
//		File dataDir = new File(filesDir.getParent(), "src");
//		if(!dataDir.exists())
//			dataDir.mkdirs();
//		dataDirPath = dataDir.getAbsolutePath();

		dataDir = new File(extDir, "src");
		if(!dataDir.exists())
			dataDir.mkdirs();


//		File sharedFolder = new File(Environment.getExternalStorageDirectory(), SHARED_FOLDER);
		File sharedFolder = new File(extDir, SHARED_FOLDER);
		if (!sharedFolder.exists())
			sharedFolder.mkdirs();
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
	
	public static File getCacheDir(Context context){
		File cacheDir = context.getExternalCacheDir();

//		File cacheDir = new File(Environment.getExternalStorageDirectory(),
//				"Android/data/" + context.getPackageName() +"/files/");
		if(!cacheDir.exists())
			cacheDir.mkdirs();

		return cacheDir;
	}
	
	public static File getAgentInfo(){
		File path = new File("");
		
		//if (Environment.getExternalStorageState()
		//		.equals(Environment.MEDIA_MOUNTED)) {
			path = new File (Environment.getExternalStorageDirectory(), GRSOFT_EXCHANGE);
			
			if(!path.exists())
				path.mkdirs();
		//}
		
		return new File(path, AGENTINFOXML);
	}
}
