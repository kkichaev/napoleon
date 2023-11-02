package com.grsoft.util;
import com.grsoft.aceteam.R;

import java.lang.reflect.Field;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.AssortmentMatrixAdapter.IterFunc;

public class AssortimenMatrixDocIterator {
	
	@SuppressWarnings("unchecked")
	public void iterItems(Document<?> doc, IterFunc func){
		try{
			Field f = doc.getData().getClass().getField("items");
			List<DataObject> items = (List<DataObject>) f.get(doc.getData());
			Field fid = null;
			for(DataObject i : items){
				if(fid == null)
					fid = i.getClass().getField("id");
				func.process(fid.get(i).toString());	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
