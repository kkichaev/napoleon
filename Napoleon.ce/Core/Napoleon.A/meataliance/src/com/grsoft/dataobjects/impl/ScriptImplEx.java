package com.grsoft.dataobjects.impl;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.script.dataobjects.Script;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;


public class ScriptImplEx extends ScriptImpl{
	@Override
	public String getDescription(Context context) {
		
		String result = super.getDescription(context);
		CreatableDocument<?>[] d = getDocuments();
		
		if(d != null)
			for(int i = 0; i < d.length; i++)
				if(d[i] != null && d[i].getClass().equals(OrderImplEx.class)){
					result = d[i].getDescription(context);
					break;
				}
		
		return result;
	}
	
	public CreatableDocument<Script> copy(){
		ScriptDefImpl def = new ScriptDefImpl();
		def.read("id", data.scriptId);
		List<ScriptDefItem> items = def.getData().items;
		
		ScriptImplEx copy=new ScriptImplEx();
		if( items.size()==1 && rowid != ExtrasConst.INVALID_ID 
			&&  DocType.getDocType(items.get(0).curType)==OrderDoc.instance()){
			copy.read(rowid);
			newFields(copy);
			OrderImplEx oi=new OrderImplEx();
			oi.read("created",data.items.get(0).date);
			newFields(oi);
			oi.write();
			oi.close();
			copy.data.items.get(0).date=oi.data.created;
			copy.write();
			copy.close();
		}
				
		return copy;
	}
	
	private void newFields(CreatableDocument<?> doc){
		doc.data.created = Util.getDateTime();
		doc.data.date = Util.getDate();
		doc.data.params = 0;
		doc.data.podRemark = "";
		doc.rowid =  ExtrasConst.INVALID_ID;
	}
	
}
