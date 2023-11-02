/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgFolderItemEx;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.OrgFoldersEx;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ScriptDocEx;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.ViewInitializer;

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
		ServerCommand.Category = "btl";
		
		ScriptDocEx.init();
		
		DataObjectInfo.getInstance().replaceListType(Remnants.class, "items", RemnantItemEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(OrgFolders.class, OrgFoldersEx.class);

		DataObjectInfo.getInstance().replaceListType(OrgFoldersEx.class, "items", OrgFolderItemEx.class);
		
		DocType.addType(RemnantsDoc.instance(RemnantsImplEx.class));
		DocType.addType(VisitDoc.instance());
		DocType.addType(QuestionDoc.instance());
		
		DocType.setCurDoc(RemnantsDoc.instance());		

		Warehouse.activity = WarehouseEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		RemnantsDetail.activity = RemnantsDetailEx.class;
		Setting.activity = SettingEx.class;
		DocList.activity = DocListEx.class;
		PotenzialOrg.activity = PotenzialOrgEx.class;
		Documents.activity = DocumentsEx.class;
		
		Features.ASSORTMENT_MATRIX = true;
		Features.START_STOP = true;
		Features.SCRIPT_DOC = true;
		
		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 3;
		AssortmentMatrixAdapter.MATRIX_DOC = RemnantsDoc.instance();
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(activity.getString(R.string.route_edit), new Runnable() {
					@Override public void run() { RouteList.open(activity); }
				}));
			}
		});
		
		UpdateDB.initUI = new ViewInitializer(){
			@Override
			public void init(Activity activity) {
				activity.findViewById(R.id.cbPresent).setVisibility(View.GONE);
				activity.findViewById(R.id.cbDebt).setVisibility(View.GONE);
				View v = activity.findViewById(R.id.cbRecreateStory);
				((CheckBox)v).setText("Восстановить документы");
				((CheckBox)activity.findViewById(R.id.cbVisit)).setChecked(true);
				v = activity.findViewById(R.id.cbDocs);
				((CheckBox)v).setText("Документы (мерч)");
			}
		};
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
