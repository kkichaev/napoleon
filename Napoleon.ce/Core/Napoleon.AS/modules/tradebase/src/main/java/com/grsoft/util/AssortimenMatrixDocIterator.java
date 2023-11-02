package com.grsoft.util;

import java.lang.reflect.Field;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.AssortmentMatrixAdapter.IterFunc;

public class AssortimenMatrixDocIterator {
	
	@SuppressWarnings("unchecked")
	public void iterItems(Document<?> doc, PriceImpl pi, IterFunc func){
		try{
			Field f = doc.getData().getClass().getField("items");
			List<DataObject> items = (List<DataObject>) f.get(doc.getData());
			Field fid = null;
			for(DataObject i : items){
				if(fid == null)
					fid = i.getClass().getField("id");

				String id = fid.get(i).toString();

				Price p = pi.getData();
				p.id = id;
				if(pi.read()) {
					func.process(id);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
