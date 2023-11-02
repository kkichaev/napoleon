/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.CategoryMatrix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Dover;
import com.grsoft.dataobjects.IncassDebDistr;
import com.grsoft.dataobjects.IncassDebDistrEx;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.StorcheckActions;
import com.grsoft.dataobjects.StorcheckGoods;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.StorcheckDoc;
import com.grsoft.napoleon.util.BuildSetThreadEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.BuildSetThread;

public class NapoleonApp extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		DebtDocEx.initialize();
		
		DataObjectInfo.getInstance().replaceListType(Return.class, "items", ReturnItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(IncassDebDistr.class, IncassDebDistrEx.class);
		
		BuildSetThread.type = BuildSetThreadEx.class;
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				Hitching[] ret = {
					new RcvNewHitching(MatrixOrder.class),
					new RcvNewHitching(CategoryMatrix.class),
					new RcvNewHitching(StorcheckActions.class),
					new RcvNewHitching(StorcheckGoods.class),
					new RcvNewHitching(Dover.class, "Dover"),
				};
				return Arrays.asList(ret);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
	
		UpdateDB.addHitchingCtor( new HitchingCtor() { 
			@Override public Hitching create() { return new DocumentRestore(StorcheckDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
}
	
	@Override
	protected void initChildFeature() {
		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 6;
		Features.SHOW_WEIGHT_IN_DOC_LIST = true;
		Features.USE_COST_IN_RETURNS = true;
		Features.COST_IN_PRESENTATION = true;
		Features.LOAD_FULL_PRICE = true;
		Features.INPUT_QTY_IN_PACK = true;
	
		Features.INCASS_DEBET_DISTRIB = true;
	}
	
	@Override
	protected void initChildActivity() {
		VisitEdit.activity = VisitEditorEx.class;
		Documents.activity = DocumentsEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		Warehouse.activity = WarehouseEx.class;
		PriceCount.activity = PriceCountEx.class;
		DeliveryDetail.activity = DeliveryDetailEx.class;
		PricePresentation.activity = PricePresentationFolderEx.class;
		IncassDebDistrEdit.editActivity = IncassDebDistrEditEx.class;
	}

	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		DocType.addType(StorcheckDoc.instance());
	}
	
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();

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
