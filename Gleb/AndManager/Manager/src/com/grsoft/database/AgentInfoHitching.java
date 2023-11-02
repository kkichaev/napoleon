package com.grsoft.database;

import java.io.FileWriter;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.grsoft.dataobjects.AgentInfo;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.napoleon.util.debug.Path;

public class AgentInfoHitching extends RcvNewHitching {
	
	public AgentInfoHitching() {
		super(AgentInfo.class, "AgentInfo");
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		
		DbReader reader = new DbReader();
		AgentInfo data = new AgentInfo();
		boolean bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(AgentInfo.class), null);
		
		if(bdo){
			XmlSerializer serializer = Xml.newSerializer();
		    try{
		    	FileWriter writer = new FileWriter(Path.getAgentInfo());
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
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
		}
		
	}

}
