package com.grsoft.dataobjects.impl;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DistribItem;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.napoleon.GoodsAdapter;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixOrderComparer;
import com.grsoft.util.WarehouseManager;
import android.view.View;
import android.widget.ImageView;


public class DistribAdapter extends GoodsAdapter {

	public DistribAdapter(WarehouseManager warehouse, Document<?> doc) {
		super(warehouse, doc, true);
	}

	@Override
	protected void fillFilterArray(String orgid) {
		if(document instanceof DistribImpl){
			DistribImpl dimpl = (DistribImpl)document;
			
			Matrix mtx = new Matrix();
			mtx.name = "Distrib";

			int order = 1;
			for(DistribItem di : dimpl.getData().items) {
				MatrixItem mi= new MatrixItem();
				mi.id = di.id;
				mi.order = order++;
				mtx.items.add(mi);
				priceFilter.add(di.id);
			}
			if(mtx.items.size() > 0)
				FoldersAdapter.TreeNodeComparator = new MatrixOrderComparer(mtx);
		}
	}
	
	@Override
	protected void postUpdateView(View view, TreeNode node) {
		if(node instanceof PriceTreeNode){
			PriceTreeNode ptn = (PriceTreeNode)node;
			DistribItem item = (DistribItem) ((DistribImpl)document).findItem(ptn.getId());
			
			if(item != null){
				ImageView im = (ImageView) view.findViewById(R.id.ivStatus);
				im.setImageDrawable(view.getContext().getResources().getDrawable(item.exist == 0 ? R.drawable.btn_check_off : R.drawable.btn_check_on));
			}
		}
	}
}
