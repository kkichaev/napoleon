package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class PriceDialog {
	public static Dialog create(Context context, final ItemClicked ic) {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle("Выберите товар");

		final List<Price> priceItems = loadPrice();

		int idx = 0;
		String[] items = new String [priceItems.size()];
		for(Price p : priceItems)
			items[idx++] = p.name;
		
		b.setItems(items, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ic.clicked(priceItems.get(which));
			}
			
		});
		return b.create();
	}
	
	public interface ItemClicked {
		public void clicked(Price item);
	}

	static List<Price> loadPrice() {
		List<Price> ret = new ArrayList<Price>();
		Price data = new Price();
		data.id = "";
		data.name = "<нет>";
		ret.add(data);
		
		data = new Price();
		String table = DataObjectInfo.getInstance().getTableName(data.getClass());
		DbReader r = new DbReader();
		boolean bdo = r.select(data, table, "", "name");
		while( bdo ) {
			ret.add(data);
			
			data = new Price();
			bdo = r.selectNext(data);
		}
		r.close();
		return ret;
	}
}
