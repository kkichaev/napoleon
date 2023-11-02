package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.napoleon.RemnantHelper.RemItemVal;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.FoldersAdapterEx;

public class WarehouseEx extends WarehouseNew {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		View v = findViewById(R.id.tvRemnantsItemTtl);
		
		if(v != null)
			v.setVisibility(DocType.getCurDoc() == RemnantsDoc.instance() ? View.VISIBLE  : View.GONE);
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = new FoldersAdapterEx(this, orgid);
		
		if( Features.SHOW_ZERO_FILTER )
			ret.putFilter(createZeroPositionFilter());
		
		return ret;
	}
	
	@Override
	protected int getLayoutId() { return R.layout.warehousex; }
	
	@Override
	protected int getDefaultColor(Price p) {
		if(adapter instanceof FoldersAdapterEx){
			if((((FoldersAdapterEx)adapter).idsAsrtMtx).contains(p.id))
				return getResources().getColor(R.color.red);
			else
				return super.getDefaultColor(p); 
		}
		else
			return super.getDefaultColor(p);
	}
	
	@Override
	protected int getItemLayoutId() { return R.layout.priceitemrowex; }
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View result = super.getPriceView(node, convertView);
		View v = result.findViewById(R.id.llQuant);
		
		if( v != null)
			v.setVisibility(View.GONE);
		
		RemnantHelper.adjustView(result, document, node.getId(), setStock, setShelf);
		return result;
	}

	OnCheckedChangeListener setStock = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			RemnantHelper.updateRemQty(document, buttonView, isChecked , new RemItemVal(){ @Override public void setItemVal(RemnantItemEx i, int val) { i.qty = val; }}, adapter);
		}
	};
	
	OnCheckedChangeListener setShelf = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			RemnantHelper.updateRemQty(document, buttonView, isChecked , new RemItemVal(){ @Override public void setItemVal(RemnantItemEx i, int val) { i.shelf = val; }}, adapter);
		}
	};
}
