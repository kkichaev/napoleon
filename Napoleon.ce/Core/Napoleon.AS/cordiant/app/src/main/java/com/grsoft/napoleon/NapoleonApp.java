/*
 * Copyright (C), 2011, ??????? ?????????????
 * 
 * ???? ????????? (? ????? ??????)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.MonitoringImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.CMonitoringDoc;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.ScanLocationDoc;
import com.grsoft.napoleon.documents.ScriptDocEx;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.ViewInitializer;

import java.util.List;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}

	@Override
	protected void defineNewType() {
		ScriptDocEx.init();

		super.defineNewType();
		DataObjectInfo.getInstance().replaceListType(Remnants.class, "items", RemnantItemEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);

		UpdateDB.initUI = new ViewInitializer(){
			@Override
			public void init(Activity activity) {
				int[] ids = new int[] {
						R.id.cbRemains,
						R.id.cbPresent,
						R.id.cbDebt,
				};
				for(int id : ids) {
					activity.findViewById(id).setVisibility(View.GONE);
				}
				((CheckBox)activity.findViewById(R.id.cbRecreateStory)).setText("¬осстановить мерч., мес");
			}
		};
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			@Override
			public void menuPrepared(List<MenuHandler> menu, Activity activity) {
				String dlvString = activity.getString(R.string.dlv_doc_list);
				for(MenuHandler mh : menu) {
					if(mh.name.equals(dlvString)) {
						menu.remove(mh);
						break;
					}
				}
			}
		});
	}

	protected void initDocTypes(){
		if(inited)
			throw new RuntimeException("Already inited");

		inited = true;
		// сложна€ инкассаци€ включаетс€ фичей, фичи надо инициализировать перед документами
		initFeatures();

		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance(RemnantsImplEx.class));
		DocType.addType(QuestionDoc.instance());
		DocType.addType(ScriptDoc.instance());
		DocType.addType(ScanLocationDoc.instance());
		DocType.addType(TaskDoneDoc.instance(OrgTaskExecImpl.class));
		DocType.addType(CMonitoringDoc.instance());

		if (Features._362) {
			DocType.addType(ReturnDoc.instance(returnsImplType()));
		}

		initChildDocTypes();

		setDefDocType();

		initActivity();
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		RemnantsDetail.activity = RemnantsDetailEx.class;
		Warehouse.activity = WarehouseEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.REPORT_REQUEST = false;
	}

	public void setDefDocType() {
		DocType.setCurDoc(VisitDoc.instance());
	}

	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNplEx());
		super.onCreate();

		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		//NapoleonChat.init(this);
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
