/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * 
 *
 * kki   12/10/2010   creating
 */
package com.grsoft.napoleon.util;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

import com.grsoft.util.CameraHelper;
import com.grsoft.util.RuntimeEnv;
import com.grsoft.util.Size;

public class ConfigManager
{
	public static final String CFG_SHARED_PREFERENCE = "main_config";
	private static final String SETTING_IN_SHARED_PREF = "setting_in_shared_pref";
	public static final String DEFAULT_VALUE_INITED = "default_value_inited";
	private static Config config;
	private static final String FILE_NAME = "config.dat";
	private static Context owner;
	
	public static synchronized 
		void save(Context context) throws FileNotFoundException, IOException
	{
		SharedPreferences pref = context.getSharedPreferences(
				CFG_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		
		try{
			for(Field f : config.getClass().getFields()){
				final Class<?> type = f.getType();
				if(type == int.class)
					editor.putInt(f.getName(), f.getInt(config));
				else if(type == String.class)
					editor.putString(f.getName(), (String)f.get(config));
				else if(type == long.class)
					editor.putLong(f.getName(), f.getLong(context));
				else if(type == boolean.class)
					editor.putBoolean(f.getName(), f.getBoolean(config));
				else if(type == float.class)
					editor.putFloat(f.getName(), f.getFloat(config));
			}
			
			if (!pref.getBoolean(SETTING_IN_SHARED_PREF, false))
				editor.putBoolean(SETTING_IN_SHARED_PREF, true);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			editor.commit();
		}
			
	}
	
	public static synchronized 
	void save() throws RuntimeException
	{
		try
		{
			save(owner);
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}
	
	public static synchronized void save(Context context, String path) 
			throws IOException{
		
		
		File sdcard = Environment.getExternalStorageDirectory();
		File targetDir = new File(sdcard, path);
		
		if(!targetDir.exists())
			targetDir.mkdirs();
		
		File configDat = new File(targetDir, FILE_NAME);
		FileOutputStream fos = new FileOutputStream(configDat);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(config);
		oos.flush();
		oos.close();
	}
	
	public static synchronized Config load(Context context)
	{
		config = getConfig();
		owner = context;
		
		SharedPreferences pref = context.getSharedPreferences(CFG_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		
		if (pref.getBoolean(SETTING_IN_SHARED_PREF, false)){
			for(Field f : config.getClass().getFields()){
				if(!Modifier.isFinal(f.getModifiers())){
					try{
						final Class<?> type = f.getType();
							if(type == int.class)
								f.setInt(config, pref.getInt(f.getName(), 0));
							else if(type == String.class)
								f.set(config, pref.getString(f.getName(), ""));
							else if(type == long.class)
								f.setLong(config, pref.getLong(f.getName(), 0));
							else if(type == boolean.class)
								f.setBoolean(config, pref.getBoolean(f.getName(), false));
							else if(type == float.class)
								f.setFloat(config, pref.getFloat(f.getName(), 0.0f));
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		} else {
			StringBuilder configPath = new StringBuilder(context.getFilesDir().getAbsolutePath());
			configPath.append('/');
			configPath.append(FILE_NAME);
			
			File configFile = new File(configPath.toString());
			if (configFile.exists())
			{
				try
				{
					checkAndSetSerialID(context.openFileInput(FILE_NAME));
					
					FileInputStream fis = context.openFileInput(FILE_NAME);
					ObjectInputStream ois = new ObjectInputStream(fis);
					Config stored = (Config)ois.readObject();
					
					if(stored.getClass() != config.getClass())
						copy(stored);
					else
						config = stored;
					
					ois.close();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		if (RuntimeEnv.isPhotoSupported())
		{
			Size size = new Size(config.cameraWidth, config.cameraHeight);
			
			if (size == null || size.width == 0 || size.hight == 0) {
				try {
					Size newSize = CameraHelper.getMinCamSize();
					config.cameraWidth = newSize.width;
					config.cameraHeight = newSize.hight;
					save();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		if( config.monthsToRecreate == 0 )
			config.monthsToRecreate = 1;
		
		if (pref.getBoolean(DEFAULT_VALUE_INITED, false) == false){
			config.resetToDefault();
			save();
			pref.edit().putBoolean(DEFAULT_VALUE_INITED, true).commit();
		}
		
		return config;
	}

	/**
	 * Исправим serialVersionUID из потока, но только для ConfigImpl (это почти все проекты)
	 * @param fis
	 */
	private static void checkAndSetSerialID(FileInputStream fis) {
		DataInputStream dis = new DataInputStream(fis);
		try {
			int pos = 7;
			while(pos-- > 0)
				dis.read();
			
			int read = fis.read();
			if(read == 35) {
				// ConfigImpl
				while(read-- > 0)
					dis.read();
				
				long value = dis.readLong();
							
				Field serField = Config.class.getDeclaredField("serialVersionUID");
				serField.setAccessible(true);
				serField.set(null, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 * Копирует поля из stored в config
	 * @param stored
	 */
	private static synchronized void copy(Config stored) {
		Field[] fs = stored.getClass().getFields();
		Class<? extends Config> cfgType = config.getClass();
		
		for(Field f : fs){
			try{
				cfgType.getField(f.getName()).set(config, f.get(stored));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Без условно инициализирует конфиг
	 * @param c
	 */
	public static void initConfig(Config c) { config = c; }

	/**
	 * Инициализирует конфиг, если он не был с инициализирован ранее
	 * @param c
	 */
	public static void tryInitConfig(Config c) {
		if( config == null )
			config = c;
	}

	public static synchronized Config getConfig()
	{
		if (config == null)
			config = new Config();
		
		return config;
	}

	public synchronized static void load(String path) 
			throws StreamCorruptedException, IOException, 
			ClassNotFoundException {
		File sdcard = Environment.getExternalStorageDirectory();
		File targetDir = new File(sdcard, path);
		File configDat = new File(targetDir, FILE_NAME);
		FileInputStream fis = new FileInputStream(configDat);
		ObjectInputStream ois = new ObjectInputStream(fis);
		config = (Config)ois.readObject();
		ois.close();
	}
}

