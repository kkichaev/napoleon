/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderProceeded;
import com.grsoft.dataobjects.OrderProceededEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.RWServiceFactoryEx;
import com.grsoft.network.ServerCommand;

public class NapoleonApp extends NapoleonAppBase {
	public static final String SDCARD_PREZENT_PATH_DEFAULT = "/sdcard/Napoleon/prezent";
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	public static final String UPDTATE_PRESENT_TIME = "updtate_present_time"; 

	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}

	@Override
	protected void defineNewType() {
		DebtDocEx.initialize();
	
		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(OrderProceeded.class, OrderProceededEx.class);
	
		RWServiceFactory.instance = new RWServiceFactoryEx();
	}
	
	@Override
	protected Class<? extends OrderImplBase<? extends Order>> orderImplType() {
		return OrderImplEx.class;
	}
	
	@Override
	protected void initChildActivity() {
		Documents.activity = DocumentsEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDeliveryDetail.activity = OrderDeliveryDetailEx.class;
		Warehouse.activity = WarehouseEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		Setting.activity = SettingEx.class;
		Setting.BehaviorSettingActivity = BehaviorSettingEx.class;
		PricePresentation.activity = PricePresentationEx.class;
		DocList.activity = DocListEx.class;
		Setting.BehaviorSettingActivity = BehaviorSettingEx.class;
		Setting.GPSSettingActivity = GpsSettingEx.class;
		Setting.WarehouseSettingActivity = WarehouseSettingEx.class;
	}
	
	@Override
	protected void initChildFeature() {
		Features.DELIVERY_REPLACE_ORDER_SUM = true;
		Features.SHOW_PRESENT_IMG = true;
	}
	
//	private void initDocTypes() {
//		DebtDocEx.initialize();
//
//		DocType.addType(OrderDoc.instance(OrderImplEx.class));
//		DocType.addType(DebtDoc.instance());
//		DocType.addType(VisitDoc.instance());
//
//		SharedPreferences pref = getApplicationContext().getSharedPreferences(
//				BehaviorSettingEx.SETING_NAME, Context.MODE_PRIVATE);
//
//		if (pref.getBoolean(BehaviorSettingEx.REMNANTS_SHOW, false))
//			DocType.addType(RemnantsDoc.instance());
//
//		DocType.setCurDoc(OrderDoc.instance());
//
//		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
//		DbObject.regNewDataType(Org.class, OrgEx.class);
//		DbObject.regNewDataType(Order.class, OrderEx.class);
//		DbObject.regNewDataType(Price.class, PriceEx.class);
//		DbObject.regNewDataType(OrderProceeded.class, OrderProceededEx.class);
//
//		Warehouse.activity = WarehouseNew.class;
//		Documents.activity = DocumentsEx.class;
//		OrderDetail.activity = OrderDetailEx.class;
//		PriceCount.activity = PriceCountEx.class;
//		OrderDeliveryDetail.activity = OrderDeliveryDetailEx.class;
//		Warehouse.activity = WarehouseEx.class;
//		UpdateDB.activity = UpdateDBEx.class;
//		Setting.activity = SettingEx.class;
//		Setting.BehaviorSettingActivity = BehaviorSettingEx.class;
//		PricePresentation.activity = PricePresentationEx.class;
//		DocList.activity = DocListEx.class;
//		Setting.BehaviorSettingActivity = BehaviorSettingEx.class;
//		Setting.GPSSettingActivity = GpsSettingEx.class;
//		Setting.WarehouseSettingActivity = WarehouseSettingEx.class;
//
//		RWServiceFactory.instance = new RWServiceFactoryEx();
//
//		Features.DELIVERY_REPLACE_ORDER_SUM = true;
//		Features.SHOW_PRESENT_IMG = true;
//	}

	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
	}

	private void setProgrammVersion() {
		try {
			ServerCommand.ProgramVersion = getResources().getString(
					R.string.version);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initPresentation() {
		final String fileName = "prezent";
		SharedPreferences pref = getSharedPreferences(
				BehaviorSettingEx.SETING_NAME, Context.MODE_PRIVATE);
		String path = pref.getString(BehaviorSettingEx.PREZENT_PATH, SDCARD_PREZENT_PATH_DEFAULT);
		long time = pref.getLong(UPDTATE_PRESENT_TIME, -1);

		if (path.length() > 0) {
			File txt = new File(path, fileName);

			if (txt.isFile() && (time == -1 || 
					txt.lastModified() != time)) {
				try {
					SQLiteDatabase database = DataBaseManager.getDataBase();
					SQLiteStatement statement = database
							.compileStatement("SELECT folderid from price WHERE id=?");
					DbWriter.checkDBTable(Present.class);
					database.execSQL("delete from " + DataObjectInfo.getInstance().getTableName(Present.class));
					InputStream is = new FileInputStream(txt);
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);

					String s = br.readLine();
					final String DELIMITER = ";";
					DbWriter writer = new DbWriter();
					
					while (s != null) {
						String[] data = s.split(DELIMITER);
						Present p = new Present();
						
						statement.bindString(1, data[0]);
						p.folderId = statement.simpleQueryForString();
						p.id = data[0];
						p.photoPath = path + "//" + data[1];
						writer.insertRecord(p);
						s = br.readLine();
					}

					br.close();
					writer.close();
					
					Editor edit = pref.edit();
					edit.putLong(UPDTATE_PRESENT_TIME, txt.lastModified());
					edit.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}
}
