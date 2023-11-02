package com.grsoft.napoleon;

import java.util.List;

import android.app.Activity;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx2;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.printsource.SalesPrintEx;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;


public class NapoleonAppVan extends NapoleonApp {
	@Override
	protected void initDocTypes() {
		super.initDocTypes();
		
		ServerCommand.Category = "vanpda";
		DbObject.regNewDataType(Org.class, OrgEx2.class);
		DbObject.regNewDataType(Sales.class, SalesEx.class);
		
		NPrinter.setPrintStrategy(NPrinter.TEXT);
		
		DocType.addType(SalesDoc.instance());
		DocType.addType(ReturnDoc.instance(ReturnImplEx.class));
		
		DocType.setCurDoc(SalesDoc.instance());
		
		SalesDetail.activity = SalesDetailEx.class;
		CreateSales.activity = CreateSalesEx.class;
		
		Setting.addTabs.remove(TextPrinterSetting.class);
		Setting.addTabs.add(WiFiPrinterSettings.class);
		
		SalesDetail.SalesPrintType = SalesPrintEx.class;
		PriceCount.activity = PriceCountEx.class;
		Documents.activity = DocumentsEx.class;
		DocList.activity = DocListEx.class;
		VanRestReport.activity = VanRestReportEx.class;
		
		Napoleon.docMenuPrepared.add( new MenuPrepareHitching() {
			
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(getString(R.string.wsorder_title), new Runnable() {
					@Override public void run() { WSOrderList.open(activity); }
				}));
			}
		});
		
		Features.CANT_CHANGE_SEND_FLAG = true;
		
		DocHelper.makeDocNumberStrategy = new DocNumberStrategy();
		DocFilterOnClickListener.HiddenTypes.add(WSOrderDoc.instance());
	}
}
