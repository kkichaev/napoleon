package com.grsoft.napoleon.modules.print;

import java.util.List;

import android.app.Activity;

import com.grsoft.database.FoldersTree;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItemPrint;
import com.grsoft.dataobjects.DeliveryPrint;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderPrint;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgPrint;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.Documents;
import com.grsoft.napoleon.DocumentsPrint;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.Napoleon;
import com.grsoft.napoleon.UpdateDB;
import com.grsoft.napoleon.UpdateDBPrint;
import com.grsoft.napoleon.VanRestReport;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.ZeroPositionFilter;

public class Print {
	public static void init() {
		init(true);
	}
	
	public static void init(boolean changeDocMenu) {
		DebtDoc.init();
		
		ServerCommand.Category = "vanpda";

		DbObject.regNewDataType(Org.class, OrgPrint.class);
		DbObject.regNewDataType(Price.class, PricePrint.class);
		DbObject.regNewDataType(Order.class, OrderPrint.class);
		DbObject.regNewDataType(Delivery.class, DeliveryPrint.class);

		DataObjectInfo doi = DataObjectInfo.getInstance(); 
		doi.replaceListType(Sales.class, "items", SalesItem.class);
		doi.replaceListType(DeliveryPrint.class, "items", DeliveryItemPrint.class);

		
		// need check activiti assign in NapoleonAppBase
		UpdateDB.activity = UpdateDBPrint.class;
		Documents.activity = DocumentsPrint.class;
		
		Features.RECIEVE_REMNANTS_IN_MAIN_MENU = true;
		Features.PRINT_MODULE = true;
		
		FoldersTree.ZeroFilterStr = new ZeroFilter(FoldersTree.ZeroFilterStr);
		ZeroPositionFilter.SalesDoc = SalesDoc.instance();
		
		if( changeDocMenu )
			Napoleon.docMenuPrepared.add(new DocMenuHitching());
	}
}

class DocMenuHitching implements MenuPrepareHitching {

	@Override public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
		menu.add(new MenuHandler("Остатки на борту",
				new Runnable() { 
					@Override public void run() { VanRestReport.open(activity); }
				}));
	}	
}

class ZeroFilter implements FoldersTree.QtyZeroFilter {
	FoldersTree.QtyZeroFilter prevFilter;
	
	public ZeroFilter(FoldersTree.QtyZeroFilter prev) { prevFilter = prev; }
	
	@Override public String getFilter() {
		if( DocType.getCurDoc() == SalesDoc.instance() )
			return "vanQty>0";
		return prevFilter.getFilter();
	}
	
}

