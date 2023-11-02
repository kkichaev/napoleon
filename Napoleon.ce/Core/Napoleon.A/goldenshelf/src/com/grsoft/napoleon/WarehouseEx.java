package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.CMonitoring;
import com.grsoft.dataobjects.Contract;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.CMonitoringImpl;
import com.grsoft.dataobjects.impl.ContractImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;


public class WarehouseEx extends WarehouseNew {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
		FoldersAdapter.resetCache();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		if(document instanceof ContractImpl || document instanceof CMonitoringImpl){
			String defid = ""; 
			
			if (document instanceof ContractImpl){
				Contract c = (Contract) document.getData();
				defid = c.def;
			}else if (document instanceof CMonitoringImpl){
				CMonitoring cm = (CMonitoring) document.getData();
				defid = cm.def;
			}
				
			return new ContractAdapter(this, defid);
		}else if (document instanceof ReturnImpl){
			return new ReturnAdapter(this);
		}else
			return super.createListAdapter();
	}

	@Override
	protected void initZeroFilter() {}
	
	@Override
	public boolean isPriceExpand() { return false; }
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		int id = document instanceof CMonitoringImpl ? R.id.tvClmn2 : R.id.tvClmn1; 
		
		View result = super.getPriceView(node, convertView);
		TextView tv = (TextView) result.findViewById(id);
		tv.setVisibility(View.GONE);
		return result;
	}
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if (document instanceof CMonitoringImpl)
			textView.setText(Util.IntToScaleStr(((CMonitoringImpl) document).getItemValue(price), Consts.SUM_SCALE, Util.DEC_DELIM, false));
		else
			super.setTextColumnValue(textView, type, price);
	}
}
