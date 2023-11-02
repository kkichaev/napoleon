package com.grsoft.prch_order.dataobjects;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.impl.ConfigImpl;

public class ConfigHelper {
	
	public static final String GATE_TYPE = "Тип ворот";
	public static final String GATE_COLOR = "Цвет ворот";
	public static final String DRIVE_UNIT = "Привод";
	public static final String DRIVE_TYPE = "Тип подъёма";
	public static final String TUBE_CUT_SIZE = "Сечение профтрубы";
	public static final String TUBE_COLOR = "Цвет профтрубы";

	public static final String USER_EMAIL = "user-email";
	
	public static void init() {
		DbWriter.checkDBTable(Config.class);
		
		if(get(GATE_TYPE).length() == 0) {
			ConfigImpl ci = new ConfigImpl();
			Config c = ci.getData();
			
			String[] keys = new String[] {
				GATE_TYPE, GATE_COLOR, 	DRIVE_UNIT, DRIVE_TYPE, TUBE_CUT_SIZE, TUBE_COLOR,
			};
			String[] values = new String[] {
				"RSD 01;RSD 02;ISD 01;ISD 02",
				"8017 коричневый;8014 коричневый;9003 белый;5005 синий;6005 зеленый;3005 бордовый;9006 серебро;1014 бежевый;7004 серый;3000 красный;7016 антрацит;Венге;Золотой дуб",
				"без привода;цепной редуктор;потолочный электропривод;вальный электропривод",
				"стандартный;низкий;высокий;вертикальный, вал сверху;вертикальный, вал снизу",
				"50х100;40х80;100х100;другая",
				"8017 коричневый;7004 серый;9003 белый;5005 синий;6005 зеленый;3005 бордовый;9005 черный",
			};
			
			int index = 0;
			for(String k : keys) {
				c.key = k;
				c.value = values[index++];
				ci.write();
			}
			
			ci.close();
		}

	}
	
	public static String get(String key) {
		StringBuilder sb = new StringBuilder();
		ConfigImpl ci = new ConfigImpl();
		if( ci.getValue(sb, key) )
			return sb.toString();
		return "";
	}
	
	public static void update(InputStream in) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "Windows-1251"));
			
			Set<String> usedKeys = new HashSet<String>(Arrays.asList(new String[] {
					GATE_TYPE, GATE_COLOR, 	DRIVE_UNIT, DRIVE_TYPE, TUBE_CUT_SIZE, TUBE_COLOR,
				}));
			
			ConfigImpl ci = new ConfigImpl();
			Config c = ci.getData();
			
			String line = reader.readLine(); 
			while(line != null) {
				String[] vals = line.split("=");
				if(vals.length == 2) {
					String key = vals[0].trim();
					if(usedKeys.contains(key)) {
						c.key = key;
						c.value = vals[1].trim();
						
						ci.write();
					}
				}
				
				line = reader.readLine();
			}
			
			ci.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			try {
//				if( is != null)
//					is.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		
	}
}
