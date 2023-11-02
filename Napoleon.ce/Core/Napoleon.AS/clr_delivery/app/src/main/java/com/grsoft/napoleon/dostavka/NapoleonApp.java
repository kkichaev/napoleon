package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.DShipment;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RoutePoint;
import com.grsoft.dataobjects.RoutePointEx;
import com.grsoft.dataobjects.ShipmentItem;
import com.grsoft.dataobjects.Waybill;
import com.grsoft.dataobjects.WaybillEx;
import com.grsoft.dataobjects.WaybillItem;
import com.grsoft.dataobjects.impl.DShipmentImplEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.VisitEditNew;
import com.grsoft.napoleon.documents.DShipmentDoc;

public class NapoleonApp extends NapoleonAppBase {

	@Override
	protected void defineNewType() {
		super.defineNewType();

		DbObject.regNewDataType(Waybill.class, WaybillEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DbObject.regNewDataType(RoutePoint.class, RoutePointEx.class);

		DShipmentDoc.docClass = DShipmentImplEx.class;

		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Waybill.class, "items", WaybillItem.class);
		doi.replaceListType(DShipment.class, "items", ShipmentItem.class);
	}

	@Override
	protected void initChildActivities() {
		super.initChildActivities();
		VisitEditDelivery.activityVisit = VisitEditEx.class;
		DShipmentEdit.activity = DShipmentEditEx.class;
	}

	@Override
	protected void initChildFeatures() {
		super.initChildFeatures();
		Features.ORG_DISPOSITION = true;
	}
}
