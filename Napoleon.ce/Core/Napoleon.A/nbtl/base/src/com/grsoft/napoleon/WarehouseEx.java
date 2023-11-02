package com.grsoft.napoleon;

import java.util.Comparator;
import java.util.HashSet;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.CMonitoring;
import com.grsoft.dataobjects.Contract;
import com.grsoft.dataobjects.ContractItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.BtlPlanImpl;
import com.grsoft.dataobjects.impl.CMonitoringImpl;
import com.grsoft.dataobjects.impl.ContractImpl;
import com.grsoft.dataobjects.impl.DistribAdapter;
import com.grsoft.dataobjects.impl.DistribImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.dataobjects.impl.ReturnImplBase;
import com.grsoft.dataobjects.impl.SlsnetImpl;
import com.grsoft.napoleon.documents.DistribDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class WarehouseEx extends WarehouseNew {
	private  TextView tvPartShelf;
	private  TextView tvSlsPlan;
	private  TextView tvRestPlan;
	private  TextView tvBtlPlan;
	private  TextView tvRestBtlPlan;
	
	private int plan = 0;
	private int btlPlan = 0;
	
	static Comparator<TreeNode> svTreeNodeComparator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(svTreeNodeComparator == null)
			svTreeNodeComparator = FoldersAdapter.TreeNodeComparator;
		if(FoldersAdapter.TreeNodeComparator != svTreeNodeComparator)
			FoldersAdapter.TreeNodeComparator = svTreeNodeComparator;
		
		super.onCreate(savedInstanceState);
		findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
		tvPartShelf = (TextView) findViewById(R.id.tvPartShelf);
		tvSlsPlan = (TextView) findViewById(R.id.tvSlsPlan);
		tvRestPlan = (TextView) findViewById(R.id.tvRestPlan);
		tvRestPlan.setVisibility(View.GONE);
		
		tvBtlPlan = (TextView) findViewById(R.id.tvBtlPlan);
		tvRestBtlPlan = (TextView) findViewById(R.id.tvRestBtlPlan);
		tvRestBtlPlan.setVisibility(View.GONE);
		
		FoldersAdapter.resetCache();
		
		if (document instanceof ContractImpl)
		{
			final Contract c = (Contract) document.getData();
			
			findViewById(R.id.btnPlan).setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					if (document instanceof ContractImpl){
						PlanView.open(v.getContext(), c.def, c.id);
					}
				}
			});
			
			tvPartShelf.setVisibility(document instanceof ContractImpl ? View.VISIBLE : View.GONE);
			
			OrgImpl org = new OrgImpl();
			org.read("id", document.getId());
			
			SlsnetImpl sls = new SlsnetImpl();
			sls.read("id", ((OrgEx)org.getData()).sid);
			
			plan = sls.getData().plan;
			tvSlsPlan.setText(getString(R.string.slsnet_plan, plan));
			
			BtlPlanImpl btl = new BtlPlanImpl();
			btl.getData().id = c.id;
			btl.getData().cid = c.def;
			
			btl.read();
			btl.close();
			
			btlPlan = btl.getData().face;
			tvBtlPlan.setText(getString(R.string.btl_plan, Util.IntToScaleStr(btlPlan, Consts.QTY_SCALE)));
		}else {
			tvPartShelf.setVisibility(View.GONE);
			tvSlsPlan.setVisibility(View.GONE);
			tvBtlPlan.setVisibility(View.GONE);
			tvRestPlan.setVisibility(View.GONE);
			tvRestBtlPlan.setVisibility(View.GONE);
		}
	}
	
	@Override protected int getLayoutId() { return R.layout.warehouseex; }
	
	@Override protected int getOptionsMenuId() { return R.menu.wh_menu; }
	
	@Override
	protected BaseAdapter createListAdapter() {
		if(document instanceof CMonitoringImpl || document instanceof ReturnImplBase || document instanceof RemnantsImpl)
			return new GoodsAdapter(this, document, !(document instanceof CMonitoringImpl));
		if(document instanceof DistribImpl)
			return new DistribAdapter(this, document);
		else if(document instanceof ContractImpl){
			String defid = ""; 
			
			if (document instanceof ContractImpl){
				Contract c = (Contract) document.getData();
				defid = c.def;
			}else if (document instanceof CMonitoringImpl){
				CMonitoring cm = (CMonitoring) document.getData();
				defid = cm.def;
			}
				
			return new ContractAdapter(this, defid, document.getId());
		}else
			return super.createListAdapter();
	}

	@Override
	protected void initZeroFilter() {}
	
//	@Override
//	public boolean isPriceExpand() { return false; }
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		int id = document instanceof CMonitoringImpl ? R.id.tvClmn2 : R.id.tvClmn1; 
		
		if(document instanceof DistribImpl)
			id = R.id.llQuant;
			
		View result = super.getPriceView(node, convertView);
		View v = result.findViewById(id);
		v.setVisibility(View.GONE);

		return result;
	}
	
	@Override
	protected void postAdapterChange() {
		updateTotalSum();
//		super.postAdapterChange();
	}
	
	@Override
	protected void updateTotalSum() {
		if (adapter != null && document instanceof ContractImpl) {
			if(adapter.isTop())
				updateTotalSum(document.qty(), 0);
			else {
				HashSet<String> nodes = new HashSet<String>();
				FolderTreeNode f = adapter.getFolderTop();
				for(TreeNode tn : f.getChilds()) {
					if(tn instanceof PriceTreeNode)
						nodes.add(((PriceTreeNode)tn).getId());
				}
				updateTotalSum(((ContractImpl)document).countQty(nodes), 0);
			}
		} else
			super.updateTotalSum();
	}
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if (document instanceof CMonitoringImpl)
			textView.setText(Util.IntToScaleStr(((CMonitoringImpl) document).getItemValue(price), Consts.SUM_SCALE, Util.DEC_DELIM, false));
		else
			super.setTextColumnValue(textView, type, price);
	}
	
	@Override
	protected int getItemLayoutId() {
		if(document instanceof DistribImpl)
			return R.layout.ditrsibitemrow;
		else
			return super.getItemLayoutId();
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		
		if(document instanceof ContractImpl){
			PriceImpl p = new PriceImpl();
			Contract c = (Contract) document.getData();
			
			long my = 0;
			long all = 0;
			long cnc = 0;
			
			for(ContractItem i : c.items){
				p.read("id",i.id);
				if(((PriceEx)p.getData()).my != 0)
					my += i.qty;
				else
					cnc += i.qty;
				
				all += i.qty;
			}
			
			double d = 0;
			
			if (all != 0)
				d = ((double)my / all) * 100;
			
			tvPartShelf.setText(getString(R.string.fact_part_shelf, d));
			
			double pv = (plan * (cnc / Consts.QTY_SCALE)) / (double)(100 - plan);
			
			if (pv % 1 != 0)
				pv += 1;
			
			long ipv = (long)pv;
			long rest = ipv - my / Consts.QTY_SCALE;
			
			tvRestPlan.setVisibility(View.VISIBLE);
			tvRestPlan.setText(getString(R.string.rest_plan, rest < 0 ? 0 : rest));
			
			rest = (btlPlan - my) / Consts.QTY_SCALE;
			tvRestBtlPlan.setVisibility(View.VISIBLE);
			tvRestBtlPlan.setText(getString(R.string.rest_plan, rest < 0 ? 0 : rest));
		}
	}
	
	public void onBackPressed() {
		if (DocType.getCurDoc() == DistribDoc.instance()){
			if(((DistribImpl)document).isEmpty()){
				document.delete();
				document.close();
			}
		}
		
		super.onBackPressed();
	};
}
