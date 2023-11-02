/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Application;
import android.content.Context;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.OrgFoldersEx;
import com.grsoft.dataobjects.Question;
import com.grsoft.dataobjects.QuestionItemEx;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.ConfigImplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FirstRunInit;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		DocType.addType(VisitDoc.instance(VisitImplEx.class));
		DocType.addType(QuestionDoc.instance());
		
		Documents.activity = DocumentsEx.class;
		UpdateDB.activity = UpdateDbEx.class;
		PotenzialOrg.activity = PotenzialOrgEx.class;
		VisitEdit.activity = VisitEditEx.class;
		Setting.PhotoSettingActivity = PhotoSettingEx.class;
		QuestionWebView.activity = QuestionWebViewEx.class;
		
		
		DbObject.regNewDataType(OrgFolders.class, OrgFoldersEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DataObjectInfo.getInstance().replaceListType(Visit.class, "items", VisitItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Question.class, "items", QuestionItemEx.class);
		DocType.setCurDoc(VisitDoc.instance());
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		ConfigManager.initConfig(new ConfigImplEx());
		FirstRunInit.init(this);
		initDocTypes();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		ServerCommand.Category = "btl";
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
