package com.grsoft.dataobjects;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.DbWriter;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.napoleon.util.Config;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.Util;

@TableInfo(name="ProgramSettings", keyFields="created")
@ServerInfo(name="PDASettings")
public class ProgramSettings extends DataObject {
	public int params = 0;
	public Date created = new Date();
	
	public List<ProgramSettingsItem> items = new ArrayList<ProgramSettingsItem>();
	
	public static void saveSettings(Config config) {
		ProgramSettings prs = new ProgramSettings();
		prs.created = Util.getDateTime();
		
		Field[] fields = config.getClass().getFields();
		for(Field f : fields) {
			if( (f.getModifiers() & (Modifier.FINAL|Modifier.STATIC|Modifier.PUBLIC)) == Modifier.PUBLIC ) {
				try {
					String value = "null";
					Object val = f.get(config);
					if(val != null) {
						value = val.toString();
					}
					ProgramSettingsItem i = new ProgramSettingsItem();
					i.id = f.getName();
					i.value = value;
					prs.items.add(i);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		ProgramSettingsItem i = new ProgramSettingsItem();
		i.id = "_PDA_Model";
		i.value = android.os.Build.MODEL;
		prs.items.add(i);
		i = new ProgramSettingsItem();
		i.id = "_PDA_VERSION";
		i.value = android.os.Build.VERSION.RELEASE;
		prs.items.add(i);
		i = new ProgramSettingsItem();
		i.id = "_PDA_ID";
		i.value = ServerCommand.DeviceID;
		prs.items.add(i);
		
		
		DbWriter w = new DbWriter();
		w.insertRecord(prs);
		w.close();
	}
}
