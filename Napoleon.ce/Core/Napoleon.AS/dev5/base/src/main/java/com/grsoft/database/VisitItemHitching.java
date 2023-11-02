package com.grsoft.database;

import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

@SuppressLint("SimpleDateFormat")
public class VisitItemHitching implements ObjectExportListener {

//	Visit doc; 
	VisitItem item;
	String objName;
	
	public VisitItemHitching(CreateDocDataObject doc, int index, List<VisitItem> items, String objName) {
//		this.doc = doc;
		this.objName = objName;
		this.item = items.get(index);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String __nameBase = "";
		if(doc.id.length() != 0)
			__nameBase += toFileName(doc.id) + "\\";
		__nameBase += sdf.format(doc.created) + "_" + Integer.toString(index+1);
		this.item.setNameBase(__nameBase);
		// create ì.á. != date 	
		this.item.setDateBase(doc.date);
	}
	
	@Override public void onStart() {	}
	@Override public void onRead(RawObject rawObject) throws RuntimeException { }
	@Override public void onSave() { }

	@Override
	public void onEnd() {
	}
	
	String toFileName(String str) {
		String ret = "";
		for(char sym: str.toCharArray()) {
			if(!Character.isLetter(sym) && !Character.isDigit(sym))
				sym = '_';
			ret += sym;
		}
		
		return ret;
	}

	@Override public String getObjectName() { return objName; }

	@Override public int size() { return 1; }

	@Override
	public DataObject get(int i) { return i != 0 ? null : item; }

}
