package com.grsoft.napoleon.modules.print;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.FoldersTree;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.SalesRestore;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.Documents;
import com.grsoft.napoleon.DocumentsPrint;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.Main;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.UpdateDB;
import com.grsoft.napoleon.VanRestReport;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;
import com.grsoft.util.ViewInitializer;
import com.grsoft.util.ZeroPositionFilter;

public class Print {
	public static void init() {
		init(true);
	}
	
	public static void init(boolean changeDocMenu) {
		DebtDoc.init();
		
		ServerCommand.Category = "vanpda";

		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(Sales.class, "items", SalesItem.class);

		
		// need check activiti assign in NapoleonAppBase
		// move actions to Print.init();
//		UpdateDB.activity = UpdateDBPrint.class;
		Documents.activity = DocumentsPrint.class;
		
		Features.RECIEVE_REMNANTS_IN_MAIN_MENU = true;
		Features.PRINT_MODULE = true;
		
		FoldersTree.ZeroFilterStr = new ZeroFilter(FoldersTree.ZeroFilterStr);
		ZeroPositionFilter.SalesDoc = SalesDoc.instance();
		
		if( changeDocMenu )
			Main.docMenuPrepared.add(new DocMenuHitching());

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new RcvNewHitching(DbObject.getDataType(Firm.class), "Firm"),
						new RcvNewHitching(AgentPrefix.class, "AgentPrefix"),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.GEN_DATA_HITCHING);

		UpdateDB.addHitchingCtor(new HitchingCtor(){
			@Override
			public List<Hitching> createList() {
				Hitching[] h = new Hitching[] {
						new SalesRestore(),
						new DocumentRestore(PkoDoc.instance()),
				};
				return Arrays.asList(h);
			}
		}, UpdateDB.RESTORE_DATA_HITCHING);

		UpdateDB.initUI = new ViewInitializer(){
			@Override
			public void init(Activity activity) {
				CheckBox cbRemains = (CheckBox) activity.findViewById(R.id.cbRemains);
				cbRemains.setChecked(false);
				cbRemains.setVisibility(View.INVISIBLE);
			}
		};
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

