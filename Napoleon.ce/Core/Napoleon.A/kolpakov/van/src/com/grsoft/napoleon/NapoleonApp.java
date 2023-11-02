/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.dataobjects.WSOrderItem;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import android.app.Activity;

public class NapoleonApp extends NapoleonAppBase {
	
	@Override
	protected void init() {
		super.initDocTypes(true);
		DocType.addType(SalesDoc.instance());
		DocType.addType(PkoDoc.instance());
		DocType.setCurDoc(SalesDoc.instance());
		
		DocFilterOnClickListener.HiddenTypes.add(WSOrderDoc.instance());

		DataObjectInfo.getInstance().replaceListType(WSOrder.class, "items", WSOrderItem.class);

		UpdateDB.addHitchingCtor(new HitchingCtor() {
			@Override public Hitching create() { return new DocumentRestore(WSOrderDoc.instance()); }
		}, UpdateDB.RESTORE_DATA_HITCHING);
		
		Napoleon.docMenuPrepared.add( new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.wsorder_title), new Runnable() {
					@Override public void run() { WSOrderList.open(activity); }
				}));
			}
		});
	}
}
