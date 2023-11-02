package com.grsoft.napoleon.utl;

import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.PriceCountEx;
import com.senter.support.openapi.StBarcodeScanner;

import android.content.Context;

public class ScannerHelper {
	
	public static boolean doScan(Context context, DbObject<? extends DataObject> document) {		
		String barcode = null;
		try {
			StBarcodeScanner scanner=StBarcodeScanner.getInstance();
			if (scanner!=null)
				barcode = scanner.scan();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if( barcode == null )
			return false;
		
		String table = DataObjectInfo.getInstance().getTableName(Price.class);
		List<Long> ids = DbReader.readIds(table, "barcode='" + barcode + "'", null);
		if(ids.size() > 0) {
			PriceCountEx.openEx(context, ids.get(0), document, false);
		} else {
			ids = DbReader.readIds(table, "packBarcode='" + barcode + "'", null);
			if( ids.size() > 0 )
				PriceCountEx.openEx(context, ids.get(0), document, true);
		}
		return ids.size() > 0;
	}
}
