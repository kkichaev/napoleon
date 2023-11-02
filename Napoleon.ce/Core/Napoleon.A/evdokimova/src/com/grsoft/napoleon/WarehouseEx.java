package com.grsoft.napoleon;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class WarehouseEx extends WarehouseNew {

	PriceImpl pi = new PriceImpl();
	
	@Override
	protected int getItemLayoutId() {
		if(document instanceof RemnantsImplEx)
			return R.layout.priceitemrowex;
		return super.getItemLayoutId();
	}
	
	@Override
	protected void onStop() {
		pi.close();
		super.onStop();
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View ret = super.getPriceView(node, convertView);
		CheckBox cb = (CheckBox)ret.findViewById(R.id.cbExists);
		if(cb != null) {
			final RemnantsImplEx rdoc = (RemnantsImplEx)document; 
			DataObject obj = rdoc.findItem(node.getId());
			cb.setTag(node.getId());
			cb.setChecked(obj != null);
			cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					rdoc.updateItem((String)arg0.getTag(), arg1);
					notifyDataSetChanged();
				}
			});
		}
		return ret;
	}
}
