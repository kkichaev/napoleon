/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.PODHitching;
import com.grsoft.database.PODHitchingEx;
import com.grsoft.database.RestoreReturnNumbers;
import com.grsoft.database.ReturnResultHitching;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.ConfigHelper.DlvDateType;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.IncassImplEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import android.app.Activity;
import android.content.Context;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		
		ScriptDoc.instance(ScriptImplEx.class);
		IncassDoc.instance(IncassImplEx.class);
		
		DataObjectInfo doi = DataObjectInfo.getInstance();
		
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DbObject.regNewDataType(Visit.class, VisitEx.class);
		
		doi.replaceListType(ReturnEx.class, "items", ReturnItemEx.class);
		doi.replaceListType(OrderEx.class, "items", OrderItemEx.class);
		
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(OrderProceeded.class, OrderProceededEx.class);
		
		PODHitching.type = PODHitchingEx.class;
		ConfigHelper.DEFAULT_DATE_TYPE = DlvDateType.nextday;
		
		
		// убираем прайс-лист из списка документов
		Main.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, Activity activity) {
				String priceTitle = activity.getString(R.string.price_list);
				for(MenuHandler mh : menu) {
					if( mh.name.equals(priceTitle) ) {
						menu.remove(mh);
						break;
					}
				}
			}
		});
		
//		DocFilterOnClickListener.HiddenTypes.add(DebtDoc.instance());
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override public Hitching create() { return new RestoreReturnNumbers(); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
	}
	
	@Override
	protected void initChildActivity() {
		Warehouse.activity = WarehouseEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
		CreateReturn.activity = CreateReturnEx.class;
		PriceCount.activity = PriceCountEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		IncassEdit.activity = IncassEditEx.class;
		Documents.activity = DocumentsEx.class;
		DocList.activity = DocListEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		Features.USE_COST_IN_RETURNS = true;
		Features.OK_BTN_INCASS = true;
		Features.MAX_FOTO_WIDTH = 5000;
		Features.MAX_FOTO_HEIGHT = 5000;
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		Hitching rrh = new ReturnResultHitching();
		ReadService.recievers.add(rrh);
		WriteService.recievers.add(rrh);
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
