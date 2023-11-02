/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Application;
import android.util.Log;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.Consts;
import com.grsoft.util.FirstRunInit;

public class NapoleonApp extends Application {
	
	public void initDocTypes() {
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		
		DocType.setCurDoc(VisitDoc.instance());
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(Consts.D_TAG, "NapoleonApp.onCreate");
		
		initDocTypes();
		FirstRunInit.init(this);
		
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
}
