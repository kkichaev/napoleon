package com.grsoft.napoleon.documents;

import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Bonus;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.R;

public class OrderDocEx extends OrderDoc {
	public static void initialize() {
		if (instance != null)
			throw new RuntimeException("OrderDoc уже создан!");
		instance = new OrderDocEx();
	}

	OrderDocEx() {
		super("Заявки", "Order", OrderImplEx.class);
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);
		
		TextView tv = (TextView)view.findViewById(R.id.tvOther);
		
		if (doc instanceof OrderImpl)
			if (hasBonus((Order) doc.getData()))
				tv.setBackgroundResource(R.drawable.list_grey_selector);
			else
				tv.setBackgroundResource(R.drawable.list_selector);
	}

	public boolean hasBonus(Order order) {
		boolean result = false;

		SQLiteDatabase db = DataBaseManager.getDataBase();
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from ")
				.append(DataObjectInfo.getInstance().getTableName(Bonus.class))
				.append(" where [order]=?");
		android.database.Cursor c = null;

		try {
			c = db.rawQuery(sql.toString(),
					new String[] { Long.toString(order.created.getTime()) });
			
			if(c.moveToNext())
				result = c.getInt(0) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null)
				c.close();
		}

		return result;
	}
}
