package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class WarehouseEx extends Warehouse {
	
	@Override
	protected int getLayoutId() {
		return R.layout.warehouseex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if ((docRowId==ExtrasConst.INVALID_ID)) {
			List<KeyValue> priceTypes = new ArrayList<KeyValue>();
			((LinearLayout) findViewById(R.id.llChooseCostType)).setVisibility(View.VISIBLE);
			Spinner spPrices = (Spinner) findViewById(R.id.spChooseCostType);
			
			DialogHelper.loadSpinnerWithKey(new ConfigImpl(), "�������", priceTypes, spPrices, null);
			spPrices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> spAdapter, View arg1,
						int position, long arg3) {
					if(document instanceof OrderImpl) {
						OrderEx oe = (OrderEx) document.getData();
						oe.sumType = position;
						oe.payType = ((KeyValue) spAdapter.getSelectedItem()).key.toString();
						adapter.notifyDataSetChanged();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
			});	
			
		}
		
		
		
	}
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View v = super.getPriceView(node, convertView);
		TextView tv = (TextView) v.findViewById(R.id.tvItemID);
		if (tv != null) {
			CfgNpl config = (CfgNpl) ConfigManager.getConfig();
			if (config.idInPriceList) {
				tv.setVisibility(View.VISIBLE);
				tv.setText(((PriceEx)price.getData()).article);
			}
		}
		return v;
	}
}
