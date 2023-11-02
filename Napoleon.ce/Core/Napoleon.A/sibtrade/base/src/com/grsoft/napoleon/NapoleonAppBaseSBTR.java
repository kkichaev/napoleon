/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.BonusDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.ViewInitializer;
import android.app.Activity;
import android.content.Context;
import android.widget.CheckBox;

public class NapoleonAppBaseSBTR extends NapoleonAppBase {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
	}
	
	@Override
	protected void defineNewType() {
		DataObjectInfo.getInstance().replaceListType(Order.class, "items", OrderItemEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(Org.class, OrgEx.class);
	}
	
	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		
		DocType.addType(MonitoringDoc.instance());
		DocType.addType(BonusDoc.instance());
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		PriceCount.activity = PriceCountEx.class;
		IncassEdit.activity = IncassEditEx.class;
		Documents.activity = DocumentsEx.class;
		OrderDetail.activity = OrderDetailEx.class;
	}
	
	@Override
	protected void initAcivity() {
		super.initAcivity();
		
		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new AgentPlanRcv();}
		}, UpdateDB.GEN_DATA_HITCHING);
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler("Планы", new Runnable() {
					@Override public void run() { AgentPlanView.open(activity); }
				} ));
			}
		} );
		
		UpdateDB.initUI = new ViewInitializer() {
			public void init(Activity activity) { ((CheckBox)activity.findViewById(R.id.cbDebt)).setChecked(true); }
		};
	}
	
	protected void initFeatures() {
		initChildFeature();
		
		Features.FOCUSED_ITEMS = true;
		Features.SCRIPT_DOC = true;
		Features.DEL_VISIT_WITHOUT_PHOTO = true;
		Features.USE_COST_IN_RETURNS = true;
		Features.COUNT_DOCS_IN_DOCSLIST = true;
		Features.LOAD_FULL_PRICE = true;
		Features.UPDATE_DB_CHECK_VISITS = true;
		Features.SALES_FROM_ORDERS = false;
		Features.LAST_SALED_ITEMS_PERIOD = 3;
		Features.MAX_FOTO_HEIGHT = 3000;
		Features.MAX_FOTO_WIDTH = 3000;
	};
	
	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

class AgentPlanRcv extends RcvNewHitching {
	public AgentPlanRcv () {
		super(com.grsoft.dataobjects.AgentPlan.class, "AgentPlan");
		selectCMD = "SELECT";
	}
	
	@Override
	public String getParams() throws RuntimeException {
		SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd.MM.yyyy");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -6);
		c.set(Calendar.DAY_OF_MONTH, 1);
		String filter = String.format(" \"userid\" = '$CURRENT_USERID' and \"begin\" >= ToDate('%s')",
				simpleDateFormat.format(c.getTime()));
		return objectName + ":" + filter;
	}
}
