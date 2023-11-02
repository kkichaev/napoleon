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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.PricePhotoHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.FolderEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgProp;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceSklads;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.Sklads;
import com.grsoft.dataobjects.StringCause;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.OffTakeHistory;
import com.grsoft.napoleon.documents.OffTakeInflatorEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.PriceComparer;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}

	@Override
	protected Class<? extends RemnantsImpl> remnantsImplType() {
		return RemnantsImplEx.class;
	}

	@Override
	protected void defineNewType() {
		super.defineNewType();
		ScriptDoc.instance(ScriptImplEx.class);
		DebtDocEx.initialize();

		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Folder.class, FolderEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);
		DataObjectInfo.getInstance().replaceListType(Remnants.class, "items", RemnantItemEx.class);

		CostStrategy.defaultInstance = new CostStrategyEx();
		OffTakeHistory.inflator = new OffTakeInflatorEx();

		FoldersAdapter.TreeNodeComparator = new PriceComparer();

		Main.docMenuPrepared.add(new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler("Планы", new Runnable() {
					@Override public void run() { AgentPlan.open(activity); }
				} ));
			}
		} );

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] data = new Hitching[] {
						new RcvNewHitching(Sklads.class),
						new RcvNewHitching(PriceSklads.class),
						new RcvNewHitching(OrgProp.class),
						new RcvNewHitching(StringCause.class),
				};
				return Arrays.asList(data);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();

		Warehouse.activity = WarehouseEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
		UpdateDB.activity = UpdateDBEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();
		Features.ORG_STOP_TABLE = true;
		Features.LOAD_FULL_PRICE = true;
		Features.MAX_FOTO_HEIGHT = 5000;
		Features.MAX_FOTO_WIDTH = 5000;
	}

	@Override
	public void onCreate() {
		Features.VER_4_1 = true;

		ConfigManager.initConfig(new CfgNpl());
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
