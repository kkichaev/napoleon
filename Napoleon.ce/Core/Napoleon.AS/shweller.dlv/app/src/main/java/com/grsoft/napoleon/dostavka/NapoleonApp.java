package com.grsoft.napoleon.dostavka;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DShipment;
import com.grsoft.dataobjects.DWaybillDocumentItemEx;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RoutePoint;
import com.grsoft.dataobjects.RoutePointEx;
import com.grsoft.dataobjects.TareReq;
import com.grsoft.dataobjects.TareReturn;
import com.grsoft.dataobjects.TareReturnItem;
import com.grsoft.dataobjects.Waybill;
import com.grsoft.dataobjects.WaybillEx;
import com.grsoft.dataobjects.WaybillItem;
import com.grsoft.dataobjects.impl.DShipmentImplEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.VisitEditNew;
import com.grsoft.napoleon.documents.DShipmentDoc;
import com.grsoft.napoleon.documents.DTareDoc;
import com.grsoft.napoleon.documents.DocType;

public class NapoleonApp extends NapoleonAppBase {

	@Override
	protected void defineNewType() {
		super.defineNewType();
		DbObject.regNewDataType(Waybill.class, WaybillEx.class);
		DbObject.regNewDataType(RoutePoint.class, RoutePointEx.class);
		DbObject.regNewDataType(Price.class, PriceEx.class);
		DShipmentDoc.docClass = DShipmentImplEx.class;

		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Waybill.class, "items", WaybillItem.class);
		doi.replaceListType(TareReturn.class, "items", TareReturnItem.class);
		doi.replaceListType(DShipment.class, "items", DWaybillDocumentItemEx.class);

		MainService.AddRequest.add(new Hitching(TareReq.class));
	}

	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		DocType.addType(DTareDoc.instance());
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
