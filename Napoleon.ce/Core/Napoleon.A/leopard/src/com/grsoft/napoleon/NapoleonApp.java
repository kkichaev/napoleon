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
import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.AgentTask;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ScriptEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.TaskBeginDoc;
import com.grsoft.napoleon.documents.TaskEndDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.Script;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.IncassImplEx;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.dataobjects.impl.ScriptImplEx;
import com.grsoft.script.dataobjects.impl.VisitImplEx;
import com.grsoft.script.documents.ScriptDoc;
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
		PriceCount.activity = PriceCountEx.class;
		ScriptEdit.activity = ScriptEditEx.class;
		Warehouse.activity = WarehouseEx.class;
		IncassEdit.activity = IncassEditEx.class;
		
		Features.SCRIPT_DOC = true;
		
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, 
				"items", OrderItemEx.class);
		DbObject.regNewDataType(Script.class, ScriptEx.class);

		DocType.addType(ScriptDoc.instance(ScriptImplEx.class));
		OrderDoc.instance(OrderImplEx.class);
		DocType.addType(OrderDoc.instance());
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance(VisitImplEx.class));
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(IncassDoc.instance(IncassImplEx.class));
		DocType.addType(TaskBeginDoc.instance());
		DocType.addType(TaskEndDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());
		
		UpdateDB.activity = UpdateDbEx.class;
		Features.COST_MANAGER = new CostManagerImpl();
		Documents.activity = DocumentsEx.class;
		Features.SCRIPT_DOC = true;
		Features.POTENZIAL_ORG = false;
		
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceTableName(AgentTask.class, "agenttask");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		FirstRunInit.init(this);
		initDocTypes();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		createScript();
	}

	public void createScript() {
		if(!ScriptDefImpl.canScripting()){
			CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
			cfg.scriptOff = false;
			ConfigManager.save();
			
			ConfigImpl c = new ConfigImpl();
			c.getData().key = "AllowScripting";
			boolean noScript = (c.read() == false || Integer.parseInt(c.getData().value) == 0);
			
			if( noScript ){
				c.getData().value = "1";
				c.write();
			}
			
			c.close();
			
			DbWriter.checkDBTable(ScriptDef.class);
			SQLiteDatabase db = DataBaseManager.getDataBase();
			db.execSQL(String.format("DELETE FROM %s", 
					DataObjectInfo.getInstance().getTableName(ScriptDef.class)));
			
			ScriptDefImpl scriptDefImpl = new ScriptDefImpl();
			ScriptDef scriptDef = scriptDefImpl.getData();
			scriptDef.name = "Сценарий";
			scriptDef.id = 0;
			
			ScriptDefItem taskBegin = new ScriptDefItem();
			taskBegin.curType = TaskBeginDoc.OBJ_NAME;
			taskBegin.nextDoc = 1;
			taskBegin.name = "Задачи (просмотр)";
			scriptDef.items.add(taskBegin);
			
			ScriptDefItem photoBegin = new ScriptDefItem();
			photoBegin.curType = "Visit";
			photoBegin.nextDoc = 2;
			photoBegin.name = "Фото начало";
			scriptDef.items.add(photoBegin);
			
			ScriptDefItem incass = new ScriptDefItem();
			incass.curType = "Incass";
			incass.nextDoc = 3;
			scriptDef.items.add(incass);
			
			ScriptDefItem orderItem = new ScriptDefItem();
			orderItem.curType = "Order";
			orderItem.nextDoc = 4;
			scriptDef.items.add(orderItem);
			
			ScriptDefItem taskEnd = new ScriptDefItem();
			taskEnd.curType = TaskEndDoc.OBJ_NAME;
			taskEnd.nextDoc = 5;
			taskEnd.name = "Задачи после";
			scriptDef.items.add(taskEnd);
			
			ScriptDefItem photoEnd = new ScriptDefItem();
			photoEnd.curType = "Visit";
			photoEnd.nextDoc = 6;
			photoEnd.name = "Фото после";
			scriptDef.items.add(photoEnd);
			
			scriptDefImpl.write();
			scriptDefImpl.close();
			
			ScriptDefImpl orderOneDefImpl = new ScriptDefImpl();
			scriptDef = orderOneDefImpl.getData();
			scriptDef.name = "Заявка";
			scriptDef.id = 1;
			
			ScriptDefItem order = new ScriptDefItem();
			order.curType = "Order";
			order.name = "Заявка";
			scriptDef.items.add(order);
			orderOneDefImpl.write();
			orderOneDefImpl.close();
			
			ScriptDefImpl incassOneDefImpl = new ScriptDefImpl();
			scriptDef = incassOneDefImpl.getData();
			scriptDef.name = "Инкассация";
			scriptDef.id = 2;
			
			ScriptDefItem incassOne = new ScriptDefItem();
			incassOne.curType = "Incass";
			incassOne.name = "Инкассация";
			scriptDef.items.add(incassOne);
			incassOneDefImpl.write();
			incassOneDefImpl.close();
		}
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
