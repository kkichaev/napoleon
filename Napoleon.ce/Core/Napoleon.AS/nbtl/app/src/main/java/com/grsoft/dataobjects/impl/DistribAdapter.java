package com.grsoft.dataobjects.impl;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DistribItem;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.napoleon.GoodsAdapter;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MatrixOrderComparer;
import com.grsoft.util.WarehouseManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


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
			TextView tvStatus = view.findViewById(R.id.tvStatus);
			tvStatus.setTag(item);
			tvStatus.setTag(R.id.id_key, ptn.getId());
			tvStatus.setText("");

			if(item != null){
				ImageView im = (ImageView) view.findViewById(R.id.ivStatus);
				im.setImageDrawable(view.getContext().getResources().getDrawable(item.exist == 0 ? R.drawable.btn_check_off : R.drawable.btn_check_on));
				if (item.itemStatus >= 0)
					tvStatus.setText(Integer.toString(item.itemStatus));
			}

			if (((DistribImpl) document).isEditable())
				tvStatus.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						final DistribItem distribItem = (DistribItem) v.getTag();
						final String id = (String) v.getTag(R.id.id_key);
						InputNumberDlg.open(v.getContext(), new InputNumber() {

							@Override public boolean useComma() { return false; }
							@Override public boolean replaceCommaToPlus() { return false; }

							@Override
							public void applayInput(int value, Object... params) {
								if (distribItem == null){
									DistribItem item  = new DistribItem();
									item.id = id;
									item.itemStatus = item.exist == 1 ? value > 0 ? 1 : 0 : 0 ;

									((DistribImpl)document).getData().items.add(item);
								}
								else
									distribItem.itemStatus = item.exist == 1 ? value > 0 ? 1 : 0 : 0 ;
								document.write();
								document.close();
								DistribAdapter.this.notifyDataSetChanged();
							}

							@Override
							public long getValue() {
								return distribItem != null ? distribItem.itemStatus < 0 ? 0 : distribItem.itemStatus : 0;
							}
						}, 1, true, "Статус товара");
					}
				});
		}
	}
}
