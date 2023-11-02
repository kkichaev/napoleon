package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.impl.OrgDistrictImpl;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;


public class ScriptEditEx extends ScriptEdit {
	@Override
	public void refreshDoc() {
		super.refreshDoc();
		
		if(def != null){
			OrgDistrictImpl org = new OrgDistrictImpl();
			org.read("id", doc.getId());
			
			if( org.getData().rejret != 0){
				List<ScriptDefItem> list = new ArrayList<ScriptDefItem>();
				
				String retName = ReturnDoc.instance().getObjectName();
				
				for(ScriptDefItem sdi : def.getData().items){
					if(!sdi.curType.equals(retName))
						list.add(sdi);
				}
				
				def.getData().items = list;
				
				if(doc instanceof ScriptImpl){
					ScriptImpl script = (ScriptImpl)doc;
					List<ScriptItem> scritems = new ArrayList<ScriptItem>();
					
					for(ScriptItem si : script.getData().items)
						if(!si.type.equals(retName))
							scritems.add(si);
					
					script.getData().items = scritems;
				}
			}
		}
	}
}
