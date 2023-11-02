/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.City;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Discs;
import com.grsoft.dataobjects.Gift;
import com.grsoft.dataobjects.Manufacturer;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PresentEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceFilterMask;
import com.grsoft.dataobjects.Regions;
import com.grsoft.dataobjects.TrdPromo;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.FirstRunInit;

import android.content.Context;
import android.content.SharedPreferences;

public class NapoleonApp extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	protected void initDocTypes() {
		DbObject.regNewDataType(Present.class, PresentEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
		
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(DebtDoc.instance());
		DocType.addType(VisitDoc.instance());
		DocType.addType(RemnantsDoc.instance());
		DocType.addType(QuestionDoc.instance());
		DocType.addType(ScriptDoc.instance());
		DocType.addType(IncassDoc.instance());
		DocType.addType(TaskDoneDoc.instance(OrgTaskExecImpl.class));
		
		DocType.setCurDoc(OrderDoc.instance());		

		Warehouse.activity = WarehouseEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		UpdateDB.activity = UpdateDBEx.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;

		Features.DOC_STATUS_IN_DOC_LIST = true;
		Features.UPDATE_PRICE_BACKGROUND = true;
		Features.MULTI_WORD_SEARCH = false;
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching [] {
					new RcvNewHitching(Manufacturer.class, "Manufacturers"),
					new RcvNewHitching(City.class, "Cites"),
					new RcvNewHitching(Action.class, "Actions"),
					new Hitching(Gift.class),
					new Hitching(Regions.class),
					new RcvNewHitching(Discs.class),
					new RcvNewHitching(PriceFilterMask.class, "PriceFilterMask"),
					new RcvNewHitching(TrdPromo.class, "TrdPromo"),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
				
		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 2;
		
		CostStrategy.defaultInstance = new CostStrategyEx();
}
	
	@Override
	public void onCreate() {
		super.onCreate();
		FirstRunInit.init(this);

		initDemo();

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
	
	private void initDemo() {
		final String GLOBAL_PREF_NAME = "main_pref";
		final String INITED_KEY = "demo_inited";
		boolean inited = false;
		
		SharedPreferences preferences = getSharedPreferences(
				GLOBAL_PREF_NAME, Context.MODE_PRIVATE);
		inited = preferences.getBoolean(INITED_KEY, false);

		if(!inited){
			unzipraw(R.raw.napoleon, Path.getDataBasePath());
			unzipraw(R.raw.costs, Path.getFilesDir() + "Costs.data");
			preferences.edit().putBoolean(INITED_KEY, true).commit();
		}
	}

	private void unzipraw(int input, String output) {
		try{
			File dataBaseFile = new File(output);
			InputStream dis = getResources().openRawResource(input);
			OutputStream dos = new BufferedOutputStream(new FileOutputStream(dataBaseFile));
			
			byte[] buffer = new byte[1024];
			int n = 0;
			
			while ((n = dis.read(buffer)) != -1)
			    dos.write(buffer, 0, n);

			dis.close();
			dos.close();
		}catch (Exception e){
			e.printStackTrace();
		}		
	}
}
