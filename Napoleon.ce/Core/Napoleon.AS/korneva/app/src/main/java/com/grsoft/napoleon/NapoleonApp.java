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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.PricePhotoHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
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
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.RWServiceFactoryEx;
import com.grsoft.network.ServerCommand;

public class NapoleonApp extends NapoleonAppBase implements com.grsoft.napoleon.util.PresentSdcard.InitStrategy{
	public static final String UPDTATE_PRESENT_TIME = "updtate_present_time";
	private static final String SETING_NAME = "prezent_settings";

	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		DebtDocEx.initialize();
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
	protected void initChildActivity() {
		super.initChildActivity();

		Warehouse.activity = WarehouseEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolderEx.class;
		PricePresentationFolder.activity = PricePresentationFolderEx.class;
		PriceCount.activity = PriceCountEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		Documents.activity = DocumentsEx.class;
//		IncassEdit.activity = IncassEditEx.class;
		DocList.activity = DocListEx.class;

		RWServiceFactory.instance = new RWServiceFactoryEx();

	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();

		DbObject.regNewDataType(Delivery.class, DeliveryEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
//		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(OrderProceeded.class, OrderProceededEx.class);
	}

	@Override
	protected void initChildFeature() {
		super.initChildFeature();

		Features.SHOW_PRESENT_IMG = true;
		Features.PRESENTATION_ON_SDCARD = true;
	}

	public void markUpdatePresentTime(long time) {
		SharedPreferences pref = getSharedPreferences(SETING_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = pref.edit();
		edit.putLong(UPDTATE_PRESENT_TIME, time);
		edit.commit();
	}

	public void initPresentation() {
		final String fileName = "prezent";
		SharedPreferences pref = getSharedPreferences(SETING_NAME, Context.MODE_PRIVATE);
		String path = ((CfgNpl)ConfigManager.getConfig()).presentpath;
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

					SharedPreferences.Editor edit = pref.edit();
					edit.putLong(UPDTATE_PRESENT_TIME, txt.lastModified());
					edit.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	@Override
	public void init(Context context) {
		initPresentation();
	}
}
