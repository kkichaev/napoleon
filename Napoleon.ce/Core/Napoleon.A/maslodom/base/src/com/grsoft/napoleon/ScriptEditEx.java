package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;

public class ScriptEditEx extends ScriptEdit {
	@Override
	public void refreshDoc() {
		super.refreshDoc();
		
		if(def != null){
			OrgImpl org = new OrgImpl();
			org.getData().id = doc.getId();
			org.read();
			org.close();
			
			if(org.getData().isPotencial()){
				List<ScriptDefItem> list = new ArrayList<ScriptDefItem>();
				
				for(ScriptDefItem sdi : def.getData().items){
					if(!sdi.curType.equals(OrderDoc.instance().getObjectName()))
						list.add(sdi);
				}
				
				def.getData().items = list;
				
				if(doc instanceof ScriptImpl){
					ScriptImpl script = (ScriptImpl)doc;
					List<ScriptItem> scritems = new ArrayList<ScriptItem>();
					
					for(ScriptItem si : script.getData().items)
						if(!si.type.equals(OrderDoc.instance().getObjectName()))
							scritems.add(si);
					
					script.getData().items = scritems;
				}
			}
		}
	}
}
