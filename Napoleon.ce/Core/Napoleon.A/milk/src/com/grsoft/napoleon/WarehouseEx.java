package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.OffTakeHistory;
import com.grsoft.util.ExtrasConst;

public class WarehouseEx extends Warehouse {

	public static final boolean OFF_TAKE_FROM_ORDERS = true;
	boolean haveDocument = false;
	OffTakeHistory history = null;
	ArrayList<TextView> labels = new ArrayList<TextView>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		haveDocument = (getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID) != ExtrasConst.INVALID_ID);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		if( haveDocument ) {
			boolean firstTime = (history == null);
			
			history = new OffTakeHistory(document.getId(), OFF_TAKE_FROM_ORDERS);
			ArrayList<Date> dates = history.getLabels();
			
			if( firstTime ) {
				SimpleDateFormat sf = new SimpleDateFormat("dd.MM");
				LinearLayout p = (LinearLayout)findViewById(R.id.llLables);
				for(Date d : dates){
					TextView tv = new TextView(this);
					tv.setText(sf.format(d));
					tv.setTextColor(Color.BLACK);
					tv.setPadding(0, 0, 3, 0);
	
					p.addView(tv, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);
	
					labels.add(tv);
				}
			}
		}
		
		super.onResume();
	}
	
	@Override
	protected void setTitleText(FolderTreeNode folder) {
//		int visible = /*(folder == null) ? View.GONE : */View.VISIBLE;
//		for( TextView tv : labels )
//			tv.setVisibility(visible);

		super.setTitleText(folder);
	}
	@Override
	protected ItemSelectAdapter createItemAdapter() { return new ItemExAdapter();	}
	
	class ItemExAdapter extends ItemSelectAdapter {
		
		@Override
		protected View setPriceView(PriceTreeNode priceTreeNode, Price p, int linesCount) {
			linesCount = 3;
			
			View view = View.inflate(WarehouseEx.this, R.layout.pricerowex, null);

			setName(priceTreeNode, view, p, linesCount);
			
			if( history != null ) {
				ArrayList<OffTakeHistory.Item> hist = history.getHistory(p.id);
				ArrayList<TextView> views = createViews(view);
				
				int ctr = 0;
				for( ; ctr < views.size() && ctr < hist.size(); ctr++ ) {
					TextView tv = views.get(ctr);
					OffTakeHistory.Item hi = hist.get(ctr);
					
//					String text = "";
//					text += Util.IntToScaleStr(hi.rest, Consts.QTY_SCALE, Util.DEC_DELIM, true);
//					text += "\n";
//					text += Util.IntToScaleStr(hi.offTake, Consts.QTY_SCALE, Util.DEC_DELIM, true);
//					text += "\n";
//					text += Util.IntToScaleStr(hi.qty, Consts.QTY_SCALE, Util.DEC_DELIM, true);
					
					tv.setText(Html.fromHtml(hi.makeText(false)));
				}
			}
			return view;
		}

		private ArrayList<TextView> createViews(View view) {
			ArrayList<TextView> views = new ArrayList<TextView>();
						
			ViewGroup p = (ViewGroup)view.findViewById(R.id.llPriceRow);
			int ctr = 0;
			for( ; ctr < labels.size(); ctr++) {
				
				TextView src = labels.get(ctr);
				int viewWidth = src.getMeasuredWidth();
				TextView tv = new TextView(WarehouseEx.this);
				tv.setGravity(Gravity.RIGHT);
				tv.setPadding(0, 0, 3, 0);
				tv.setTextColor(Color.BLACK);
				p.addView(tv, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);	
				tv.setWidth((viewWidth>0) ? viewWidth : 50);
				views.add(tv);
			}
			return views;
		}
		
	}
}
