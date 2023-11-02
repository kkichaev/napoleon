package com.grsoft.database;

import java.io.FileWriter;
import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.Crypto;

import android.util.Xml;

public class ManagerAgentHitching extends RcvNewHitching {
	
	public ManagerAgentHitching() {
		super(ManagerAgent.class, "ManagerAgent");
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		
		String table = (new ManagerAgent()).getTableName();
		String stmt = "delete from " + table + " where name = ''";
		DataBaseManager.getDataBase().execSQL(stmt);
		
		DbReader reader = new DbReader();
		ManagerAgent data = new ManagerAgent();
		boolean bdo = reader.select(data, table, null);
		
		if(bdo){
			XmlSerializer serializer = Xml.newSerializer();
		    try{
		    	StringWriter writer = new StringWriter(); 
		    	serializer.setOutput(writer);
		    	serializer.startDocument("UTF-8", true);
		    	serializer.startTag("", "agentinfo");
		    	
		    	while(bdo){
		    		serializer.startTag("", "item");
		    		serializer.attribute("", "id", data.id);
		    		serializer.attribute("", "login", data.login);
		    		serializer.attribute("", "password", data.password);
		    		serializer.attribute("", "name", data.name);
		    		serializer.endTag("", "item");
		    		bdo = reader.selectNext(data);
		    	}
		    	
		    	serializer.endTag("", "agentinfo");
		        serializer.endDocument();
		    	writer.close();
		    	
		    	String text = writer.toString();
		    	
		    	Config cfg = ConfigManager.getConfig();
		    	text = Crypto.encrypt(cfg.passw, text);
		    	
		    	FileWriter fw = new FileWriter(Path.getAgentInfo());
		    	fw.write(text);
		    	fw.close();
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
		}
		
	}

}
