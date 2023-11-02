/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * 
 *
 * kki   12/10/2010   creating
 */
package com.grsoft.napoleon.util;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.grsoft.util.RuntimeEnv;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.util.Xml;

public class ConfigManager
{
	public static String CFG_SHARED_PREFERENCE = "main_config";
	private static final String SETTING_IN_SHARED_PREF = "setting_in_shared_pref";
	public static final String DEFAULT_VALUE_INITED = "default_value_inited";
	private static Config config;
	private static final String FILE_NAME = "config.dat";
	private static Context owner;
	public static ConfigPhotoInitilizer photoInit = new ConfigPhotoInitilizer();
	
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
					editor.putLong(f.getName(), f.getLong(config));
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

	public static boolean isInited(Context context) {
		SharedPreferences pref = context.getSharedPreferences(CFG_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		return pref.getBoolean(SETTING_IN_SHARED_PREF, false);

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
	
	private static File checkOutDir(String path){
		File sdcard = Environment.getExternalStorageDirectory();
		File result = new File(sdcard, path);
		
		if(!result.exists())
			result.mkdirs();
		
		return result;
	}
	
	private static File getExportFile(String path){
		File targetDir = checkOutDir(path);
		File result = new File(targetDir, FILE_NAME);
		
		return result;
	}
	
	public static synchronized void save(Context context, String path) 
			throws IOException{
		FileOutputStream fos = new FileOutputStream(getExportFile(path));
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(config);
		oos.flush();
		oos.close();
	}
	
	public static synchronized void exportXml(String path){
		try{
			XmlSerializer ser = Xml.newSerializer();
			StringWriter wr = new StringWriter();
			ser.setOutput(wr);
			ser.startDocument("UTF-8", true);
			
			for(Field f : config.getClass().getFields()){
				if(!Modifier.isFinal(f.getModifiers())){
					try{
						ser.startTag("", f.getName());
						ser.text(f.get(config).toString());
						ser.endTag("", f.getName());
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			
			ser.endDocument();
			
			FileOutputStream fos = new FileOutputStream(getExportFile(path));
			OutputStreamWriter out = new OutputStreamWriter(fos);
			out.write(wr.toString());
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static String readXml(String path) throws IOException{
		FileInputStream fis = new FileInputStream(getExportFile(path));
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		
		StringBuilder sb = new StringBuilder();
		String line = null;
		while((line = br.readLine()) != null)
			sb.append(line);
		
		br.close();
		fis.close();
		
		return sb.toString();
	}
	
	public static synchronized void importXml(String path){
		try{
			String xml = readXml(path);
			
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(xml));
			
			Map<String, Field> data = new HashMap<String, Field>();
			config = getConfig();
			
			for(Field f : config.getClass().getFields()){
				if(!Modifier.isFinal(f.getModifiers())){
					data.put(f.getName(), f);
				}
			}
			
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (xpp.getEventType()) {
				case XmlPullParser.START_TAG:
					String name = xpp.getName();
					String val = xpp.nextText();
					
					if(data.containsKey(name)){
						Field f = data.get(name);
						setFieldVal(val, f);
					}

				default:
					break;
				}
				
				eventType = xpp.next();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static void setFieldVal(String val, Field f) throws IllegalAccessException {
		final Class<?> type = f.getType();
		if(type == int.class)
			f.setInt(config, Integer.parseInt(val));
		else if(type == String.class)
			f.set(config, val);
		else if(type == long.class)
			f.setLong(config, Long.parseLong(val));
		else if(type == boolean.class)
			f.setBoolean(config, Boolean.parseBoolean(val));
		else if(type == float.class)
			f.setFloat(config, Float.parseFloat(val));
	}
	
	private static int defIntVal(Field f){
		int result = 0;
		
		DefaultValue d = f.getAnnotation(DefaultValue.class);
		
		if(d != null)
			result = Integer.parseInt(d.value()); 
			
		return result;
	}
	
	private static String defStrVal(Field f){
		String result = "";
		
		DefaultValue d = f.getAnnotation(DefaultValue.class);
		if(d != null)
			result = d.value();
		
		return result;
	}
	
	private static long defLongVal(Field f){
		long result = 0;
		
		DefaultValue d = f.getAnnotation(DefaultValue.class);
		
		if(d != null)
			result = Long.parseLong(d.value()); 
			
		return result;
	}
	
	private static boolean defBoolVal(Field f){
		boolean result = false;
		
		DefaultValue d = f.getAnnotation(DefaultValue.class);
		
		if(d != null)
			result = Boolean.parseBoolean(d.value()); 
			
		return result;
	}
	
	private static float defFloatVal(Field f){
		float result = 0.0f;
				
		DefaultValue d = f.getAnnotation(DefaultValue.class);
		
		if(d != null)
			result = Float.parseFloat(d.value()); 
			
		return result;
	}
	
	public static synchronized Config load(Context context)
	{
		config = getConfig();
		owner = context;
		
		SharedPreferences pref = context.getSharedPreferences(CFG_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		
		if (pref.getBoolean(SETTING_IN_SHARED_PREF, false)){
			Map<String, ?> prefProps = pref.getAll();
			for(Field f : config.getClass().getFields()){
				if(!Modifier.isFinal(f.getModifiers())){
					try{
						if( prefProps.containsKey(f.getName()) == false)
							continue;
						
						final Class<?> type = f.getType();
						
						if(type == int.class)
							f.setInt(config, pref.getInt(f.getName(), defIntVal(f)));
						else if(type == String.class)
							f.set(config, pref.getString(f.getName(), defStrVal(f)));
						else if(type == long.class)
							f.setLong(config, pref.getLong(f.getName(), defLongVal(f)));
						else if(type == boolean.class)
							f.setBoolean(config, pref.getBoolean(f.getName(), defBoolVal(f)));
						else if(type == float.class)
							f.setFloat(config, pref.getFloat(f.getName(), defFloatVal(f)));
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
			photoInit.init(config);
			save();
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

