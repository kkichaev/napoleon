/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;

import com.grsoft.RestoreCheckResponse;
import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.CheckResponse;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgCost;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.BankDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.Print;
import com.grsoft.napoleon.modules.print.TextPrinter;
import com.grsoft.napoleon.modules.print.util.DateDocNumberStrategy;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import java.util.Arrays;
import java.util.List;

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
		DebtDocEx.init();
		Print.init(true);

		DbObject.regNewDataType(Org.class, OrgEx.class);
		CostStrategy.defaultInstance = new CostStrategyEx();
		DocHelper.makeDocNumberStrategy = new DateDocNumberStrategy(this);

		DocFilterOnClickListener.HiddenTypes.add(BankDoc.instance());
		DocFilterOnClickListener.HiddenTypes.add(WSOrderDoc.instance());

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(OrgCost.class),
						new Hitching(CheckResponse.class),
				};
				CostStrategyEx.clear();
				return Arrays.asList(h);
			}
		}, UpdateDBW.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new RestoreCheckResponse(),
				};
				return Arrays.asList(h);
			}
		}, UpdateDBW.RESTORE_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() {
				return new DocumentRestore(WSOrderDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);

		Main.docMenuPrepared.add(new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.wsorder_title), new Runnable() {
					@Override public void run() { WSOrderList.open(activity); }
				}));
				menu.add(new MenuHandler(getString(R.string.bank_inscass), new Runnable() {
					@Override public void run() { BankIncassList.open(activity); }
				}));
			}
		});
	}

	@Override
	public void setDefDocType() {
		DocType.setCurDoc(SalesDoc.instance());
	}

	@Override
	protected void initChildDocTypes() {
		DocType.addType(SalesDoc.instance());
		DocType.addType(PkoDoc.instance());

		DocType.removeType(IncassDoc.instance());
	}

	@Override
	protected void initChildFeature() {
		Features.ASSORTMENT_MATRIX = true;
		Features.INCASS_DEBET_DISTRIB = true;
		Features.UPD = true;
		Features.HIDE_SUM_IN_DEBET = true;

		AssortmentMatrixAdapter.PERIOD_IN_MONTH = 2;
	}

	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		PkoInfo.activity = PkoInfoEx.class;
	}
}
