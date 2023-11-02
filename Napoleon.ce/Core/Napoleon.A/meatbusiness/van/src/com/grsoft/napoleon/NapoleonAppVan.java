/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.modules.print.Print;

public class NapoleonAppVan extends NapoleonApp {
	
	@Override
	public void onCreate() {
		Print.init();
		DocType.addType(SalesDoc.instance());
		super.onCreate();
	}
	
	@Override
	protected void initChildActivity() {
		SalesDetail.activity = SalesDetailEx.class;
	}
	
	@Override
	public void setDefDocType() {
		DocType.setCurDoc(SalesDoc.instance());		
	}
}
