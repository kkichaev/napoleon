package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgDistribItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DistribImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.FoldersAdapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class WarehouseEx extends WarehouseNew implements OnClickListener {
	protected FoldersAdapter createAdapterInstance() {
		return new FoldersAdapterEx(this);
	}
	
	@Override
	protected void updateChildPriceView(View view, Price p) {
		super.updateChildPriceView(view, p);
		
		view.findViewById(R.id.tvClmn1).setVisibility(View.GONE);
		view.findViewById(R.id.tvClmn2).setVisibility(View.GONE);
		
		CheckBox cb = (CheckBox) view.findViewById(R.id.cbVal);
		cb.setChecked(((Itemsable) document).getItemQty(p) != 0);
		cb.setTag(p.id);
		cb.setOnClickListener(this);
	}
	
	@Override
	protected int getItemLayoutId() {
		return R.layout.priceitemrowex;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.cbVal) {
			String id = v.getTag().toString();
			CheckBox cb = (CheckBox)v;
			price.read("id", id);
			
			boolean ch = ((Itemsable) document).getItemQty(price.getData()) == 0;
			cb.setChecked(ch);
			int cost = 0;
			DistribImpl d = ((DistribImpl)document);
			OrgDistribItem i = (OrgDistribItem) d.findItem(id);
			
			if (i != null)
				cost = i.cost;
			
			d.updateQty(price, ch ? 1 * Consts.QTY_SCALE : 0, cost, false);
		}
	}
}
