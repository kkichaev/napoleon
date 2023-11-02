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
import android.os.Environment;
import android.util.Log;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.PricePhotoHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.RetResponseHitching;
import com.grsoft.dataobjects.AgentInfo;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.ReturnDocEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ReadService;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;

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
		ReturnDocEx.init();

		super.defineNewType();
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);

		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Delivery.class, "items", DeliveryItemEx.class);
		doi.replaceListType(Return.class, "items", ReturnItemEx.class);

		CostStrategy.defaultInstance = new CostStrategyEx();

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				CostStrategyEx.resetCache();
				Hitching[] h = new Hitching[] {
					new RcvNewHitching(AgentInfo.class),
					new RcvNewHitching(MatrixOrder.class),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				CfgNpl c = (CfgNpl) ConfigManager.getConfig();
				RetResponseHitching.RESTORE_PERIOD = c.daysToRecreate + c.monthsToRecreate * 30;
				return null;
			}
		}, UpdateDB.RESTORE_DATA_HITCHING);

		Hitching rr = new RetResponseHitching();
		ReadService.requestObjects.add(rr);
		WriteService.requestObjects.add(rr);
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		Warehouse.activity = WarehosueEx.class;
		PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
		OrderDetail.activity = OrderDetailEx.class;
	}

	@Override
	protected Class<? extends ReturnImpl> returnsImplType() {
		return ReturnImplEx.class;
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();

		Features.INPUT_QTY_IN_PACK = true;
		Features.HAVE_RETURN_DOC = true;
	}

}
