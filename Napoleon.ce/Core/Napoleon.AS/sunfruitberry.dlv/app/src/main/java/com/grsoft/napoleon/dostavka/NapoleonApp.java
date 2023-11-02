package com.grsoft.napoleon.dostavka;

import com.grsoft.database.HitchOnSelect;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ReportAnswerHitching;
import com.grsoft.dataobjects.AutoInfo;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Fuel;
import com.grsoft.dataobjects.ReportList;
import com.grsoft.dataobjects.Route;
import com.grsoft.dataobjects.RouteDocEx;
import com.grsoft.dataobjects.RouteEx;
import com.grsoft.dataobjects.RouteItem;
import com.grsoft.dataobjects.RouteItemEx;
import com.grsoft.dataobjects.RoutePoint;
import com.grsoft.dataobjects.RoutePointEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.ReportParams;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.dostavka.documents.AutoWaybillDoc;
import com.grsoft.napoleon.dostavka.documents.DispatchReturnsInfoDoc;
import com.grsoft.network.ReportSync;

public class NapoleonApp extends NapoleonAppBase {
	@Override
	protected void defineNewType() {
		DbObject.regNewDataType(RouteItem.class, RouteItemEx.class);
		DbObject.regNewDataType(RoutePoint.class, RoutePointEx.class);
		DbObject.regNewDataType(Route.class, RouteEx.class);
		
		DataObjectInfo.getInstance().replaceListType(RouteItemEx.class, "docs", RouteDocEx.class);
		
		ReportSync.ReportListHitching = new HitchOnSelect(ReportList.class, "ReportList", "prgType='Dispatch'", true); 
		
		Setting.XML_PREFS = R.xml.prefs_ex;
		Main.ADAPTER_CLASS = MainAdapterEx.class;
		
		MainService.AddRequest.add(ReportSync.ReportListHitching);
		MainService.AddRequest.add(new ReportAnswerHitching());
		MainService.AddRequest.add(new RcvNewHitching(Fuel.class));
		MainService.AddRequest.add(new RcvNewHitching(AutoInfo.class));
	}
	
	@Override
	protected void initChildActivities() {
		DShipmentEdit.activity = DShipmentEditEx.class;
		RoutePointView.activity = RoutePointViewEx.class;
		ReportParams.activity = ReportParamsEx.class;
		Setting.activity = SettingEx.class;
	}
	
	@Override
	protected void initChildDocTypes() {
		DocType.addType(DispatchReturnsInfoDoc.instance());
		DocType.addType(AutoWaybillDoc.instance());
	}
	
	@Override
	protected void initChildFeatures() {
		Features.UNLIMIT_VISIT_ITEMS = true;
	}
}
