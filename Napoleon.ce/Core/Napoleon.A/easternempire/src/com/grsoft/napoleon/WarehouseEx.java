package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.ActCost;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgAssortimentMatrix;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.MatrixBaseAdapter;
import com.grsoft.util.Util;

public class WarehouseEx extends WarehouseNew {
	
	static final String ACTION_MATRIX = "<Акционный>";
	static final String ORG_ASSORTIMENT_MATRIX = "<Обязательный>";
	
	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View res = super.getPriceView(node, convertView);
		ActCost ac = ((CostStrategyEx)CostStrategy.defaultInstance).getActCost(price.getData(), document);
		ImageView iv = (ImageView)res.findViewById(R.id.ivAction);
		if(ac != null)
			iv.setImageResource(R.drawable.promo);
		else
			iv.setImageDrawable(null);

		if( document instanceof RemnantsImplEx ) {
			RemnantItemEx re =  (RemnantItemEx)((Itemsable)document).findItem(price.getData().id);
			TextView tv = (TextView) res.findViewById(R.id.tvRestBoard);
			tv.setText(re == null ? "" : Util.IntToScaleStr(re.qtyBoard, Consts.QTY_SCALE));
			tv.setVisibility(View.VISIBLE);
			
			tv = (TextView) res.findViewById(R.id.tvRestWh);
			tv.setText(re == null ? "" : Util.IntToScaleStr(re.qtyWh, Consts.QTY_SCALE));
			tv.setVisibility(View.VISIBLE);
		}
		return res;
	}
	
	@Override
	protected void applayMatrix(String matrixName) {
		this.matrixName = matrixName;
		if( matrixName.equals(ACTION_MATRIX) ) {
			applayAdapter(new ActionAdapter(this, document == null ? "" : document.getId()));
		} else if(matrixName.equals(ORG_ASSORTIMENT_MATRIX)) {
			applayAdapter(new OrgAsmAdapter(this, document == null ? "" : document.getId()));
		} else
			super.applayMatrix(matrixName);
	}

	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		items.add(ACTION_MATRIX);
		items.add(ORG_ASSORTIMENT_MATRIX);
		return super.prepareMatrixList(items);
	}
	
	class OrgAsmAdapter extends MatrixBaseAdapter {

		OrgAssortimentMatrix matrix = new OrgAssortimentMatrix();
		
		public OrgAsmAdapter(WarehouseNew warehouse, String id) {
			super(warehouse);
			resetCache();
			
			if( id != null && id.length() > 0 ) {
				String table = DataObjectInfo.getInstance().getTableName(matrix.getClass());
				DbReader r = new DbReader();
				boolean bdo = r.select(matrix, table, "id='" + id + "'");
				if( bdo == false ) {
					OrgImpl oi = new OrgImpl();
					OrgEx oe = (OrgEx)oi.getData();
					oe.id = id;
					oi.read();
					oi.close();
					
					r.select(matrix, table, "category='" + oe.category + "'");
				}
				r.close();
			}
		}

		@Override
		protected List<? extends MatrixItem> getMatrixItems() { return matrix.items; }
		
	}
	
	class ActionAdapter extends MatrixBaseAdapter {

		List<MatrixItem> ret = new ArrayList<MatrixItem>();
		
		public ActionAdapter(WarehouseNew warehouse, String id) {
			super(warehouse);
			resetCache();
			
			final HashSet<String> used = new HashSet<String>();
			if( id != null && id.length() > 0 ) {
				DataTraveler.travel(ActCost.class, new DataTraveler.Travel<ActCost>() {
	
					@Override
					public boolean travel(DataTraveler<ActCost> item) {
						String id = item.data.id;
						if( !used.contains(id) ) {
							used.add(id);
							MatrixItem mi = new MatrixItem(id);
							ret.add(mi);
						}
						return true;
					}
				}, "idOrg='" + id + "'");
			}
		
			DataTraveler.travel(ActCost.class, new DataTraveler.Travel<ActCost>() {
				
				@Override
				public boolean travel(DataTraveler<ActCost> item) {
					String id = item.data.id;
					if( !used.contains(id) ) {
						used.add(id);
						MatrixItem mi = new MatrixItem(id);
						ret.add(mi);
					}
					return true;
				}
			}, "idOrg=''");
		}

		@Override
		protected List<? extends MatrixItem> getMatrixItems() { return ret; }
		
	}
}
	