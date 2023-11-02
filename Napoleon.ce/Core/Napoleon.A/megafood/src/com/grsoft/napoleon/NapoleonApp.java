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

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.TaskRemarkExporter;
import com.grsoft.dataobjects.FocusMatrix;
import com.grsoft.dataobjects.FocusRejectReason;
import com.grsoft.dataobjects.MatrixOrder;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.OrgTypeBinding;
import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		OrderDocEx.init();
		
		DbObject.regNewDataType(ScriptDef.class, ScriptDefEx.class);
		DbObject.regNewDataType(Order.class, OrderEx.class);
		
		ScriptDefImpl.resolver = new ResolveScript();
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
					new RcvNewHitching(OrgTypeBinding.class, "OrgTypeBinding"),
					new RcvNewHitching(FocusRejectReason.class, "FocusRejectReason"),
					new RcvNewHitching(FocusMatrix.class, "FocusMatrix"),
					new RcvNewHitching(MatrixOrder.class, "MatrixOrder"),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override
			public Hitching create() {
				return new TaskRemarkExporter();
			}
		} ,UpdateDB.EXPORT_DATA_HITCHING );

	}
	
	@Override protected Class<? extends OrderImplBase<? extends Order>> orderImplType() { return OrderImplEx.class; }
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(ReturnDoc.instance());
	}
	
	@Override
	protected void initChildFeature() {
		Features.COST_MANAGER = new CostManagerImpl();
	}
	
	@Override
	protected void initChildActivity() {
		Warehouse.activity = WarehouseEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		TaskDocList.activity = TaskDocListEx.class;
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
}
