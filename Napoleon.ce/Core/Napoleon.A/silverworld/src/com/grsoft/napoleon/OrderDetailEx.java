package com.grsoft.napoleon;

import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.Consts;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {
	SQLiteStatement statement;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		statement = DataBaseManager.getDataBase().compileStatement(
				"select photoPath from presentation where id=?");
		setPrefValue(LinesOnClickListener.PREF_NAME, -1);
		btnLines.setVisibility(View.GONE);
	}

	@Override
	protected void onDestroy() {
		if(statement != null)
			statement.close();
		super.onDestroy();
	}

	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderItemsAdapter() {
			@Override
			protected void drawInternal(View view, String name, int color,
					OrderItem item) {
				super.drawInternal(view, name, color, item);
				try {
					statement.bindString(1, item.id);
					String path = statement.simpleQueryForString();
					TextView tvName = (TextView) view.findViewById(R.id.tvName);
					
					tvName.setPadding(10, 0, 0, 0);
					tvName.setCompoundDrawablesWithIntrinsicBounds(
							BitmapUtils.createBitmap(path, 200), null, null,
							null);
					tvName.setCompoundDrawablePadding(10);

					StringBuilder sb = new StringBuilder();
					price.getData().id = item.id;
					price.read();
					price.close();

					PriceEx pe = (PriceEx) price.getData();
					sb.append(name)
							.append("<br>")
							.append(Util.IntToScaleStr(pe.avgw,
									Consts.WEIGHT_SCALE)).append("<br>")
							.append(pe.art);
					tvName.setText(Html.fromHtml(sb.toString()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
