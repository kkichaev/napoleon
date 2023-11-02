package com.grsoft.napoleon;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.FolderEx;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.CfgNplEx;

public class SVIBase {
	public static void init() {
		DbObject.regNewDataType(Folder.class, FolderEx.class);
		DbObject.regNewDataType(Incass.class, IncassEx.class);
		DbObject.regNewDataType(Return.class, ReturnEx.class);
		
		DataObjectInfo doi = DataObjectInfo.getInstance();
		doi.replaceListType(ReturnEx.class, "items", ReturnItem.class);
		
		Warehouse.activity = WarehouseEx.class;
		IncassEdit.activity = IncassEditEx.class;
		OrderDetail.activity = OrderDetailEx.class;
		Documents.activity = DocumentsEx.class;
		PriceCount.activity = PriceCountEx.class;
		ReturnDetail.activity = ReturnDetailEx.class;
		CreateReturn.activity = CreateReturnEx.class;
		Setting.BehaviorSettingActivity = BehaviorSettingEx.class;
		
		Features.HAVE_PRICE_MOVER = true;
		Features.QUESTION = true;
		Features.FOCUSED_GROUP = true;
		Features.SCRIPT_DOC = true;
		
		CostStrategy.defaultInstance = new CostStrategyEx();
	}
	
	public static void begin(){
		ConfigManager.initConfig(new CfgNplEx());
	}
}
