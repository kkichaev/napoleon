/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.OrgFoldersEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.PlanogramDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.FirstRunInit;
import android.app.Application;
import android.content.Context;

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
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(OrgFolders.class, OrgFoldersEx.class);
		
//		DocType.addType(OrderDoc.instance());
//		DocType.addType(DebtDoc.instance());
		/*
		 * При добавлении новых типов документов
		 * необходимо переписать их DoceumnetSende, для проавильной отправки PotenzialOrg
		 * см. VisitEditEx
		 * */
		DocType.addType(RemnantsDoc.instance(RemnantsImplEx.class));
		DocType.addType(VisitDoc.instance());
		DocType.addType(PlanogramDoc.instance());
		
		DocType.setCurDoc(RemnantsDoc.instance());		

		Warehouse.activity = WarehouseEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		RemnantsDetail.activity = RemnantsDetailEx.class;
		Setting.activity = SettingEx.class;
		DocList.activity = DocListEx.class;
		Documents.activity = DocumentsEx.class;
		VisitEdit.activity = VisitEditEx.class;
		
		Features.ASSORTMENT_MATRIX = true;
		Features.MAX_FOTO_WIDTH = 5000;
		Features.MAX_FOTO_HEIGHT = 5000;
		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 3;
		AssortmentMatrixAdapter.MATRIX_DOC = RemnantsDoc.instance();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		FirstRunInit.init(this);

		initDocTypes();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
