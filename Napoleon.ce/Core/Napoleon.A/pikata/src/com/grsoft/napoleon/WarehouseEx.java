package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	static String idStore = ""; 
	PriceImpl pi = new PriceImpl();
	
	public static void resetCache() { idStore = ""; }

	@Override
	protected Filter createZeroPositionFilter() {
		if(document instanceof OrderImplEx) {
			OrderEx o = (OrderEx) document.getData();
			if(idStore.equals(o.whCode) == false ) {
				FoldersAdapter.resetCache();
				idStore = o.whCode;
			}
			return new ZeroFilter();
		}
		return super.createZeroPositionFilter();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pi.close();
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			boolean result = false; 
			
			Price p = pi.getData();
			p.id = id;
			pi.read();
			result = (((OrderImplEx)document).getItemValue(p) > 0);			
			return result;
		}
	}
}
