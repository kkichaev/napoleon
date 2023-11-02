package com.grsoft.napoleon;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.FoldersAdapterEx;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class WarehouseEx extends WarehouseNew implements OnClickListener {
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = new FoldersAdapterEx(this, orgid);
		
		if( Features.SHOW_ZERO_FILTER )
			ret.putFilter(createZeroPositionFilter());
		
		return ret;
	}
	
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
	public View getPriceView(PriceTreeNode node, View convertView) {
		
		if (DocType.getCurDoc() == RemnantsDoc.instance()) {
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
			
			ImageView iv = (ImageView) view.findViewById(R.id.ivNal);
			iv.setOnClickListener(this);
			iv.setTag(p.id);
			
			int src = R.drawable.btn_check_off;
			
			if (((Itemsable)document).getItemQty(p) > 0)
				src = R.drawable.btn_check_on;
			
			iv.setImageDrawable(getResources().getDrawable(src));
			
			return view;
		} else 
			return super.getPriceView(node, convertView);
	}
	
	@Override
	protected int getItemLayoutId() {
		return R.layout.priceitemrowex;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ivNal) {
			((RemnantsImplEx)document).reverseQty((String)v.getTag(), this);
		}
	}
}
