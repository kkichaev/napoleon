package com.grsoft.napoleon;

import android.database.Cursor;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Order;

public class OrderDeliveryDetailEx extends OrderDeliveryDetail {
	@Override
	protected void init() {
		Order ordData = doc.getData();

		try {
			String table = DataObjectInfo.getInstance().getTableName(Delivery.class);
			String where = "number='" + ordData.number + "' and id='" + ordData.id + "'";
			String sql = "Select rowid from " + table + " where " + where;
			Cursor c = DataBaseManager.getDataBase().rawQuery(sql, null);
			if( c.moveToNext() )
				delivery.read(c.getLong(0));
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
