/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;

import com.grsoft.database.DbReader;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Dogovor;
import com.grsoft.dataobjects.NapoleonTask;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.NapoleonTaskImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ResponceDoc;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.FirstRunInit;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.ViewInitializer;

public class NapoleonApp extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "NapoleonApp";
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	private void initDocTypes() {
		DocType.addType(OrderDoc.instance(OrderImplEx.class));
		DocType.addType(ResponceDoc.instance());
		
		DocType.setCurDoc(OrderDoc.instance());		

		DbObject.regNewDataType(Order.class, OrderEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DataObjectInfo.getInstance().replaceListType(OrderEx.class, "items", OrderItemEx.class);

		Warehouse.activity = WarehouseEx.class;
		PriceCount.activity = PriceCountEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		Presentation.activity = PresentationFolder.class;
		PricePresentation.activity = PricePresentationFolder.class;
		Documents.activity = DocumentsEx.class;
		Setting.activity = SettingEx.class;
		Setting.BehaviorSettingActivity = BehaviorSettingEx.class;
		Setting.WarehouseSettingActivity = WarehouseSettingEx.class;
		UpdateDB.activity = UpdateDBEx.class;
		DocList.activity = DocListEx.class;
		
		CostStrategy.defaultInstance = new CostStrategyEx();
		
		Features.LOAD_FULL_PRICE = true;
		Features.POTENZIAL_ORG = false;
		
		UpdateDB.initUI = new ViewInitializer(){
			@Override
			public void init(Activity activity) {
				activity.findViewById(R.id.cbRemains).setVisibility(View.GONE);
				activity.findViewById(R.id.cbVisit).setVisibility(View.GONE);
				activity.findViewById(R.id.cbPresent).setVisibility(View.GONE);
				activity.findViewById(R.id.cbDebt).setVisibility(View.GONE);
			}
		};
		
		DocFilterOnClickListener.HiddenTypes.add(ResponceDoc.instance());
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { 
			@Override public Hitching create() { 
				CostStrategyEx.clearCache();
				return new RcvNewHitching(Dogovor.class);
				}
			}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() { 
			@Override public Hitching create() { return new Hitching(NapoleonTask.class, "NapoleonTask"); }
		}, UpdateDB.GEN_DATA_HITCHING);
		
		UpdateDB.addHitchingCtor(new HitchingCtor() { 
			@Override public Hitching create() { return new RcvNewHitching(AgentPrefix.class, "AgentPrefix"); }
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor() { 
			@Override public Hitching create() { return new NewTaskExport(); }
		}, UpdateDB.EXPORT_DATA_HITCHING);

		
		Napoleon.mainMenuPrepared.add( new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(0, new MenuHandler("Добавить задачу", new Runnable() {
					@Override public void run() { TaskAdd.open(activity); }
				}));
			}
		});
		
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, Activity activity) {
				for(MenuHandler h : menu)
					if(h.name.equals(activity.getString(R.string.price_list))){
						menu.remove(h);
						break;
					}
			}
		});
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		FirstRunInit.init(this);

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
}

class NewTaskExport extends Hitching implements ObjectExportListener{
	NapoleonTaskImpl impl = new NapoleonTaskImpl();
	List<Long> list;
	public NewTaskExport() {
		super(NapoleonTask.class, "NapoleonTask");
		
		String where = "(([params] & " + Integer.toString(NapoleonTask.CREATED | NapoleonTask.SENDED) + " ) == " + Integer.toString(NapoleonTask.CREATED) + ")";
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(NapoleonTask.class), where, "");
	}

	@Override public int size() { return list.size(); }
	
	@Override
	public void onEnd() {
		for( int i=0; i<list.size(); i++ ) {
			impl.read(list.get(i));
			impl.getData().params |=  NapoleonTask.SENDED;
			impl.write();
		}
		impl.close();
	}

	@Override
	public DataObject get(int i) {
		impl.read(list.get(i));
		return impl.getData();
	}
}
