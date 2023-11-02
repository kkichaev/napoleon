package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class PriceCountEx extends PriceCount {
	
	List<TextView> costViews = new ArrayList<TextView>();
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = "¬ид÷ены";
		ci.read();
		ci.close();
		
		List<CharSequence> values = new ArrayList<CharSequence>();		
		DialogHelper.makeList(c.value, values);
		
		TableRow tr = (TableRow)findViewById(R.id.trCost);
		TableLayout tl = (TableLayout)tr.getParent();
		int index = tl.indexOfChild(tr);
		
		for(int i = 0; i<values.size(); i++) {
			TableRow addRow = new TableRow(this);
			TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
			
			TextView addV = new TextView(this);
			addV.setText(values.get(i).toString());
			addV.setTextColor(Color.BLACK);
			addRow.addView(addV, lp);
			
			addV = new TextView(this);
			addV.setTextColor(Color.BLACK);
			addV.setGravity(Gravity.RIGHT);
			addRow.addView(addV, lp);
			
			costViews.add(addV);
			
			tl.addView(addRow, index++, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
		}
		
		tr.setVisibility(View.GONE);
		refreshCost();
	}
	
	
	
	@Override
	protected void refreshData() {
		super.refreshData();
		refreshCost();
	}

	private void refreshCost() {
		Price p = price.getData();
		int cs = document == null ? -1 :  document.getSumType();
		for(int i=0; i<costViews.size(); i++) {
			int cost = i < p.cost.size() ? p.cost.get(i).cost : 0;
			
			TextView tv = costViews.get(i);
			String text = Util.IntToScaleStr(cost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			if(i == cs)
				text = "<b>" + text + "</b>";
			tv.setText(Html.fromHtml(text));
		}
	}
}
