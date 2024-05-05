package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrderAction;
import com.grsoft.dataobjects.OrderActionItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.modules.CostManager;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixBaseAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;


public class WarehouseEx extends Warehouse {
	final String MATRIX_NAME = "<Матрица контрагента>";
	CostManager.CostType[] costTypes;
	List<MatrixItem> orgMatrix = null;
	private boolean matrixInited = false;
	Set<String> actions = new HashSet<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnLines.setVisibility(View.GONE);
		costTypes = Features.COST_MANAGER.getCostTypes();

		if(document instanceof SalesImplEx || document instanceof OrderImplEx) {
			actions = OrderAction.orgActionItems(document.getId());
		}
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View result = super.getPriceView(node, convertView);
		
		LinearLayout llQuant = (LinearLayout) result.findViewById(R.id.llQuant);
		llQuant.setOrientation(LinearLayout.HORIZONTAL);
		
		return result;
	}
	
	@Override
	protected void loadLastBuyingItems(String orgId) {
		DocType dt = DocType.getCurDoc();
		if( dt == OrderDoc.instance() ) {			
			HashSet<String> idPrice = new HashSet<String>();
			DocList dl = DeliveryDoc.instance().docList(orgId, "date desc");
			int count = 0;
			for(Document<?> doc : dl) {
				if( count++ >= 4 )
					break;
				
				for(DeliveryItem di : ((DeliveryImpl)doc).getData().items)
					idPrice.add(di.id);
			}
			lastBuyingItems.clear();
			lastBuyingItems.addAll(idPrice);
			dl.close();
		} else
			super.loadLastBuyingItems(orgId);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if(type == COLUMN_COST){
			StringBuilder value = new StringBuilder();
			PriceEx pe = (PriceEx)price;
			
			int[] arr = new int[]{(int)CostStrategy.getInstance(
					(Class<? extends Document<?>>) document.getClass())
					.getItemCost(price, (Document<?>) document), pe.akc1, pe.akc2};
			
			for(int cost: arr){		
				if(cost != 0)
					value.append(Util.IntToScaleStr(cost, Consts.SUM_SCALE));
				else
					value.append("&nbsp");
					
				value.append("<br>");
			}
				
			textView.setText(Html.fromHtml(value.toString()));
		}else
			super.setTextColumnValue(textView, type, price);
	}
	
	@Override
	protected FoldersAdapter createAdapterInstance() {
		FoldersAdapter.resetCache();
		return new FoldersAdapter(this){
			@Override
			protected void postUpdateView(View view, TreeNode node) {
				if(!node.isFolderNode()){
					price.read(node.getRowid(), false);
					PriceEx p = (PriceEx) price.getData();
					
					if(p.newitem == 1)
						view.setBackgroundResource(R.drawable.new_item_back);
					else if(p.akc1 != 0 || p.akc2 != 0 )
						view.setBackgroundResource(R.drawable.lgray_row);
				}
					
			}
		};
	}

	@Override
	protected void setName(View view, Price p, int linesCount, PriceTreeNode node) {
		super.setName(view, p, linesCount, node);

		ImageView iv = (ImageView)view.findViewById(R.id.iAction);
		if( iv != null ) {
			iv.setImageResource( (actions.contains(p.id)) ? R.drawable.action : R.drawable.empty );
		}

	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.priceitemrowex;
	}

	@Override protected Filter createZeroPositionFilter() { return new ZeroFilter(); }

	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			if( !(document instanceof Itemsable) )
				return super.inset(priceRowID, id);
			
			boolean result = false; 			
			if(price.read(priceRowID))
				result = (((Itemsable)document).getItemValue(price.getData()) > 0);			
			return result;
		}
	}

	@Override
	protected void adapterInit() {
		if (document != null && !matrixInited) {
			OrgImpl oi = new OrgImpl();
			Org org = oi.getData();
			org.id = document.getId();
			oi.read();
			oi.close();

			OrgEx oe = (OrgEx) org;

			if (oe.matrix.size() > 0)
				orgMatrix = oe.matrix;

			matrixInited = true;

			if (orgMatrix != null){
				applyAdapter(new OrgMatrixAdapter(this, orgMatrix), true);
				matrixName = MATRIX_NAME;
			}else{
				if(folderID != -1)
					adapter.buildSet(folderID);
				else
					adapter.buildSet();
			}
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		if(orgMatrix != null && orgMatrix.size() > 0){
			MenuItem i = menu.findItem(R.id.itMatrix);
			
			if(i != null)
				i.setVisible(false);
		}
		
		return true;
	}

	static class OrgMatrixAdapter extends MatrixBaseAdapter {
		List<MatrixItem> matrix;

		public OrgMatrixAdapter(Warehouse warehouse, List<MatrixItem> matrix) {
			super(warehouse);
			this.matrix = matrix;
		}

		public String getName() {
			return "OrgMatrixAdapter";
		}

		@Override
		protected List<MatrixItem> getMatrixItems() {
			return matrix;
		}
	}
}