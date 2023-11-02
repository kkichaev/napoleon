/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.PricePhotoHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Bonus;
import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.BonusItem;
import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceWhData;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnCause;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.BonusDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDocEx;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.ReqOrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.DocFilterOnClickListener;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();

		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				PriceCash.load();
				return null;
			}}.execute((Void[])null);

		//NapoleonChat.init(this);
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void defineNewType() {
		super.defineNewType();
		DebtDocEx.initialize();
		IncassDocEx.initialize();
		OrderDocEx.initialize();

		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);

		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Return.class, "items", ReturnItem.class);
		doi.replaceListType(PriceEx.class, "whQty", PriceWhData.class);
		doi.replaceListType(Bonus.class, "items", BonusItem.class);
		doi.replaceListType(OrderEx.class, "items", OrderItemEx.class);

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[]{
						new RcvNewHitching(BonusDef.class, "ActionDef"),
						new RcvNewHitching(Sklad.class),
						new Hitching( ReturnCause.class),
						new DecisionHitching(),
				};
				return Arrays.asList(h);
			}
		}, UpdateDBW.GEN_DATA_HITCHING);

		DocFilterOnClickListener.HiddenTypes.add(ReqOrderDoc.instance());
	}

	@Override
	protected Class<? extends ReturnImpl> returnsImplType() {
		return ReturnImplEx.class;
	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DocType.addType(BonusDoc.instance());
		DocType.addType(ReqOrderDoc.instance());
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		Warehouse.activity = WarehouseEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
		IncassEdit.activity = IncassEditEx.class;
		ScriptEdit.activity = ScriptEditEx.class;
		DocList.activity = DocListEx.class;
		UpdateDB.activity = UpdateDBEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.CHECK_UNCOMPLETE_SCRIPTS = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		ConfigHelper.DEFAULT_DATE_TYPE = ConfigHelper.DlvDateType.nextday;
		//Features.START_STOP = true;
		Features.FOCUSED_GROUP = true;
		Features.FOCUSED_ITEMS = true;
		Features.START_VISIT_OPEN_CAMERA = true;
		Features.REMOVE_EMPTY_ORDERS = true;
		Features.EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT = true;
		Features.INCASS_DEBET_DISTRIB = false;
		Features.LOAD_FULL_PRICE = true;
		Features.HAVE_RETURN_DOC = true;
	}

	@Override protected Class<? extends ScriptImpl> scriptImplType() { return ScriptImplEx.class;	}
}
