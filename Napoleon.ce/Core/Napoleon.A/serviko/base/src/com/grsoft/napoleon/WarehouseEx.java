package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.ActionResult;
import com.grsoft.dataobjects.ActionResultItem;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.ItemActionData;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.WhData;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {
	static final String ACTION_MATRIX = "<Акции>"; 
	
	String discountText = "";
	
//	private List<String> userMtx = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		FoldersAdapter.TreeNodeComparator = new NodeComparer();
//		DbWriter.checkDBTable(UserAssortMtx.class);
//		UserAssortMtx data  = new UserAssortMtx();
//		DbReader reader = new DbReader();
//		boolean bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(data.getClass()), null, null);
//		
//		if(bdo){
//			MatrixImpl mtx = new MatrixImpl();
//			mtx.read("name", data.matrix);
//			
//			for(MatrixItem i : mtx.getData().items)
//				userMtx.add(i.id);
//		}
	}
	
	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		items.add(ACTION_MATRIX);
		return items;
	}
	
	@Override
	protected void applayMatrix(String matrixName) {
		if(matrixName.equals(ACTION_MATRIX)) {
			applayAdapter(new ActionAdapter(this, document));
			return;
		}
		super.applayMatrix(matrixName);
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = (FoldersAdapter)super.createListAdapter();
		if(document instanceof OrderImplEx) {
			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx)oi.getData();
			oe.id = document.getId();
			oi.read();
			oi.close();
			if(oe.mark == 1) {
				FirmImpl fi = new FirmImpl();
				FirmEx f = (FirmEx)fi.getData();
				f.id = ((OrderEx)document.getData()).firmCode;
				fi.read();
				fi.close();
				if(f.mark > 0)
					ret.putFilter(new MarkFilter());
			}
		}
		return ret;
	}
	
	protected android.app.Dialog onCreateDialog(int id) {
		if(id == R.id.discount_desc_dlg) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Акции");
			b.setMessage(Html.fromHtml(discountText));
			b.setNeutralButton(android.R.string.ok, null);
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected void onPrepareDialog(int id, android.app.Dialog dialog) {
		if(id == R.id.discount_desc_dlg) {
			((AlertDialog)dialog).setMessage(Html.fromHtml(discountText));
		} else {	
			super.onPrepareDialog(id, dialog);
		}
	}
	
	View.OnClickListener discountShow = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			ActionResult res = (ActionResult) arg0.getTag();
			discountText = "";
			for(ActionResultItem ari : res.conditions) {
				discountText += Util.IntToScaleStr(ari.value, Consts.SUM_SCALE, Util.DEC_DELIM, false) + 
						" р. " + ari.action.id + " " + ari.action.condition + "<br/>";
			}
			showDialog(R.id.discount_desc_dlg);
		}
	};
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		boolean done = false;
		if(document instanceof OrderImpl && type == COLUMN_COST) {
			ActionResult res = ((CostStrategyEx)CostStrategy.defaultInstance).getOrderItemCost(price, (OrderImpl)document);
			if(res != null) {
				done = true;
				String text = Util.IntToScaleStr(res.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
				if(res.conditions.size() > 0) {
					text = "<font color='red'><b>!</b></font> " + text;
					textView.setTag(res);
					textView.setOnClickListener(discountShow);
				}

				textView.setText(Html.fromHtml(text));
			}
		} 
		if(!done)
			super.setTextColumnValue(textView, type, price);
	}
	
	@Override
	public void afterBuildSet() {
		super.afterBuildSet();
		if(document instanceof OrderImpl) {
			OrderEx order = (OrderEx) document.getData();
			CostStrategyEx.loadActions(order.id, order.whCode);
		}
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		String whCode = "";
		int whIndex = 0;
		if( document instanceof OrderImplEx ) {
			OrderImplEx oie = (OrderImplEx)document;
			whCode = oie.getWhId();
			whIndex = oie.getWhIndex();
		}
		return new ZeroFilter(whIndex, whCode);
	}

	class MarkFilter extends Filter {

		public MarkFilter() {
			super("MarkFilter");
			where = "mark=1";
		}
		
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		public ZeroFilter(int whIndex, String whCode) {
			super();
			
			if( whIndex != 0) {
				WhData wh = new WhData();
				where = "id in (select id from [" + wh.getTableName() + "] where whCode='" + whCode + "' )";
				
				if(prevWhere != where) {
					FoldersAdapter.resetCache();
					prevWhere = where;
				}
			}
		}
	}
	
	@Override
	protected void setName(View view, Price p, int linesCount, PriceTreeNode node) {
		super.setName(view, p, linesCount, node);
		
		ImageView iv = (ImageView)view.findViewById(R.id.iAction);
		if( iv != null ) {
			ItemActionData iad = ((CostStrategyEx)CostStrategy.defaultInstance).getActionData(p, document);
			iv.setImageResource( (iad != null && iad.actions.size() > 0) ? R.drawable.action : R.drawable.empty );
		}
		
//		TextView tv = (TextView) view.findViewById(R.id.tvPriceItemName);
//		if(userMtx.contains(p.id))
//			tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.action), null, null, null);
//		else
//			tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
	}
	
//	class NodeComparer extends TreeNodeCmp {
//		@Override
//		public int compare(TreeNode object1, TreeNode object2) {
//			if (object1 instanceof PriceTreeNode && object2 instanceof PriceTreeNode) {
//				PriceTreeNode lhs = (PriceTreeNode) object1;
//				PriceTreeNode rhs = (PriceTreeNode) object2;
//
//				if (userMtx.contains(lhs.getId()) && !userMtx.contains(rhs.getId()))
//					return -1;
//				else if (!userMtx.contains(lhs.getId())	&& userMtx.contains(rhs.getId()))
//					return 1;
//				else
//					super.compare(object1, object2);
//			}
//			
//			return super.compare(object1, object2);
//		}
//	}
}

class ActionAdapter extends FoldersAdapter {

	List<String> items;
	
	public ActionAdapter(WarehouseManager warehouse, Document<?> doc) {
		super(warehouse);
		
		FoldersAdapter.resetCache();
		items = ((CostStrategyEx)CostStrategy.defaultInstance).getActionItems(doc);
	}
	
	@Override
	public boolean inset(long rowid, String id, int folder) {
		return items.contains(id);
	}
}
