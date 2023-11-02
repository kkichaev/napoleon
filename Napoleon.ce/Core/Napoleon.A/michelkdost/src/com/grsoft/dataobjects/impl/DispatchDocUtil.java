package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DispatchItem;
import com.grsoft.napoleon.documents.CreatableDocument;

public class DispatchDocUtil {
	public static boolean delete(DispatchDocImpl<?> doc){
		boolean result = false;
		
		DispatchImpl disp = new DispatchImpl();
		disp.getData().created = doc.data.dispatch;
		
		if(disp.read()){
			for(DispatchItem i: disp.getData().items){
				if(doc instanceof CreatableDocument<?> &&  i.date.getTime() == ((CreatableDocument<?>)doc).getData().created.getTime()){
					i.state = 0;
					result = true;
					break;
				}
			}
		}
		
		disp.write();
		disp.close();
		
		return result;
	}
}
