package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.BonusDefImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.AssortmentMatrixAdapterEx;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {
	private HashMap<String, BonusDef> actionItems = new HashMap<String, BonusDef>();
	public AssortmentMatrixAdapterEx assortmentMatrixAdapter;
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if( document instanceof OrderImpl )
			actionItems = BonusDefImpl.getActiveBonuses(document.getDate()); 
	}

	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View v = super.getPriceView(node, convertView);

		TextView tv = (TextView)v.findViewById(R.id.tvPriceItemName);
		tv.setCompoundDrawablesWithIntrinsicBounds(actionItems.containsKey(price.getData().id) ? R.drawable.bonus : 0, 0, 0, 0);

		return v;
	}
	
	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		if (assortmentMatrixAdapter == null){
			Date now = Calendar.getInstance().getTime();
			Date start = Util.getMonthStart(now);
			AssortmentMatrixAdapter.PERIOD_IN_DAY = (int)DatePeriod.daysDiff(start, now);
			AssortmentMatrixAdapter.PERIOD_IN_MONTH = 0;
			
			if(AssortmentMatrixAdapter.PERIOD_IN_DAY > 0)
				AssortmentMatrixAdapter.PERIOD_IN_DAY -= 1;
			
			assortmentMatrixAdapter =  new AssortmentMatrixAdapterEx(this, document.getId());
		}
		
		return assortmentMatrixAdapter;
	}
	
	@Override
	public void setColor(TextView textView, Price price) {
		if(assortmentMatrixAdapter != null && assortmentMatrixAdapter.isIdInMatrix(price.id) &&
			((Itemsable)document).findItem(price.id) == null && !lastBuyingItems.contains(price.id)){
			textView.setTextColor(getResources().getColor(R.color.blue));
		} else
			super.setColor(textView, price);
	};
	
	@Override
	protected BaseAdapter createListAdapter() {
		createAssortementMatrixAdapter();
		return super.createListAdapter();
	}
}
