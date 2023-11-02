package com.grsoft.napoleon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.grsoft.napoleon.util.debug.Path;

public class PriceHelper {
	static boolean loaded = false;
	static final String FILE_NAME = "PriceIndex";
	static HashMap<String, Integer> values = new HashMap<String, Integer>();
	
	public static void clear() { values.clear(); }
	
	public static void put(String id) { values.put(id, values.size()); }
	
	public static int get(String id) {
		if( !loaded )
			load();
		
		Integer val = values.get(id);
		return (val == null) ? -1 : val;
	}
	
	public static void save() {
		File f = new File(Path.getDataDir(), FILE_NAME);
		try {
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(values);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static void load() {
		if( !loaded ) {
			File f = new File(Path.getDataDir(), FILE_NAME);
			if( f.exists() ) {
				try {
					FileInputStream fis = new FileInputStream(f);
					ObjectInputStream ois = new ObjectInputStream(fis);
					values = (HashMap<String, Integer>) ois.readObject();
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			loaded = true;
		}
	}
}
