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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.PricePhotoHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Fridge;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ScriptItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.BonusDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.InvFrgDoc;
import com.grsoft.napoleon.documents.InvFrgSt1Doc;
import com.grsoft.napoleon.documents.InvFrgSt2Doc;
import com.grsoft.napoleon.documents.InvFrgSt3Doc;
import com.grsoft.napoleon.documents.QuestionDocEx;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.ReturnDocEx;
import com.grsoft.napoleon.documents.VisitDocEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.Script;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DocFilterOnClickListener;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}

	@Override
	protected Class<? extends ScriptImpl> scriptImplType() {
		return ScriptImplEx.class;
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
		DbObject.regNewDataType(Org.class, OrgEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);

		DataObjectInfo.getInstance().replaceListType(Script.class, "items", ScriptItemEx.class);

		UpdateDB.addHitchingCtor(new HitchingCtor(){ @Override public Hitching create() { return new RcvNewHitching(Fridge.class); }}, UpdateDB.GEN_DATA_HITCHING);
	}

	@Override
	protected void initFeatures() {
		super.initFeatures();

		Features.FOCUSED_GROUP = true;
		Features.FOCUSED_ITEMS = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.SCRIPT_SUM_ONLY_FOR_SALES = true;
		Features.WEIGHT_SCALE = Consts.WEIGHT_SCALE;
		Features.LOAD_FULL_PRICE = true;
		Features.USE_COST_IN_RETURNS = true;
	}

	@Override
	protected void initActivity() {
		super.initActivity();
		PriceCount.activity = PriceCountEx.class;
		CreateReturn.activity = CreateReturnEx.class;
		Documents.activity = DocumentsEx.class;
		ScriptEdit.activity = ScriptEditEx.class;
		OrderDetail.activity = OrderDetailEx.class;
	}

	@Override
	protected void initDocTypes() {
		DebtDocEx.initialize();
		VisitDocEx.initialize();
		QuestionDocEx.initialize();
		ReturnDocEx.initialize();
		ScriptDoc.instance(ScriptImplEx.class);
		super.initDocTypes();
	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DocType.addType(ReturnDoc.instance());
		DocType.addType(InvFrgDoc.instance());
		DocType.addType(BonusDoc.instance());

		DocType.addType(InvFrgSt1Doc.theInstance());
		DocType.addType(InvFrgSt2Doc.theInstance());
		DocType.addType(InvFrgSt3Doc.theInstance());

		DocFilterOnClickListener.HiddenTypes.add(InvFrgSt1Doc.theInstance());
		DocFilterOnClickListener.HiddenTypes.add(InvFrgSt2Doc.theInstance());
		DocFilterOnClickListener.HiddenTypes.add(InvFrgSt3Doc.theInstance());
	}

}
