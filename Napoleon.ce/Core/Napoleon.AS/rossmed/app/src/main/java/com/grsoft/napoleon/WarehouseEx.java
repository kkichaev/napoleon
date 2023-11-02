package com.grsoft.napoleon;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WarehouseEx extends Warehouse {
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	
	@Override
	protected boolean hasPresentation() {
		String path = ((CfgNpl)ConfigManager.getConfig()).presentpath;
		File f = new File(path);
		return f.isDirectory();
	}
	
	@Override
	protected int getItemLayoutId() {
		return R.layout.priceitemrowex;
	}
	
	
	public View getPriceView(PriceTreeNode node, View convertView) {
		readPriceNode(node.getRowid());
		Price p = price.getData();

		View view;
		int id = getItemLayoutId();
		if (convertView != null && convertView.getTag(id) != null)
			view = convertView;
		else {
			view = View.inflate(this, id, null);
			view.setTag(id, true);
		}

		setName(view, p, 1, node);

		TextView tv = (TextView) view.findViewById(R.id.tvClmn1);
		setTextColumnValue(tv, COLUMN_QTY_WH, p);
		
		tv = (TextView) view.findViewById(R.id.tvClmn2);
		setTextColumnValue(tv, COLUMN_COST, p);
		
		tv = (TextView) view.findViewById(R.id.tvClmn3);
		tv.setText(((PriceEx)p).comeQty);

		tv = (TextView) view.findViewById(R.id.tvClmn4);
		
		String s = "";
		Date d = ((PriceEx)p).comeDate;
		
		if(d != null && d.getYear() > 70)
			s = sdf.format(d);
		
		tv.setText(s);
		
		updateChildPriceView(view, p);
		
		return view;
	}
	
}
