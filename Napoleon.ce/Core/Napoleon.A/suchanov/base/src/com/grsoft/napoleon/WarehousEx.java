package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.graphics.Color;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.MatrixBaseAdapter;

public class WarehousEx extends WarehouseNew {
	
	static String PLUS_MATRIX = "<Выделенные товары>";
	
	@Override
	protected int getDefaultColor(Price p) {
		if( (((PriceEx)p).flags & PriceEx.PLUS_FLAG) != 0)
			return Color.rgb(0, 192, 192);
		
		return super.getDefaultColor(p);
	}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		items.add(1, PLUS_MATRIX);
		return items;
	}
	
	@Override
	protected void applayMatrix(String matrixName) {
		if(matrixName.equals(PLUS_MATRIX)) {
			this.matrixName = matrixName;
			applayAdapter(new PlusMatrixAdapter(this));
		} else
			super.applayMatrix(matrixName);
	}
}

class PlusMatrixAdapter extends MatrixBaseAdapter {

	public PlusMatrixAdapter(WarehouseNew warehouse) {
		super(warehouse);
	}

	@Override
	public String getName() {
		return "Plus Matrix Adapter";
	}
	
	@Override
	protected List<? extends MatrixItem> getMatrixItems() {
		List<MatrixItem> ret = new ArrayList<MatrixItem>();
		String priceTable = DataObjectInfo.getInstance().getTableName(Price.class);
		String sql = "select id from " + priceTable + " where ((flags & " + Integer.toString(PriceEx.PLUS_FLAG) + ") <> 0)";
		Cursor c = null;
		try {
			c = DataBaseManager.getDataBase().rawQuery(sql, null);
			int ord = 0;
			while(c.moveToNext()) {
				MatrixItem mi = new MatrixItem();
				mi.id = c.getString(0);
				mi.order = ord++;
				ret.add(mi);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if( c != null )
				c.close();
		}
		return ret;
	}
	
}
