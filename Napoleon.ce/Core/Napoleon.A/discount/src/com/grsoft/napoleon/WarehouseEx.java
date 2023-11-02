package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.MntrGoods;
import com.grsoft.dataobjects.MntrMatrix;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Plan;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.SkladItem;
import com.grsoft.dataobjects.impl.MntrGoodsImpl;
import com.grsoft.dataobjects.impl.MntrMatrixImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DiscountMonitoringDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;
import android.widget.TextView;


public class WarehouseEx extends WarehouseNew {
	public static Map<String, Integer> planCash = null;
	public static Map<String, Integer> selCash = new HashMap<String, Integer>(); 
	
	MntrGoodsImpl monitorGood = new MntrGoodsImpl();

	static String whCode = "";
	
	@Override
	protected void adapterInit() {
		if (DocType.getCurDoc() == OrderDoc.instance() && planCash == null)
			initPlan(document);
		
		super.adapterInit();
	}
	
	@Override
	protected void onDestroy() {
		monitorGood.close();
		super.onDestroy();
	}

	public static  void initPlan(Document<?> doc) {
		planCash = new HashMap<String, Integer>();
		
		if(doc != null){
			String id = doc.getId();
			
			if(id.trim().length() > 0){
				StringBuilder where = new StringBuilder();
				where.append("id='").append(id).append("'");
				DataTraveler.travel(Plan.class, new DataTraveler.Travel<Plan>() {

					@Override
					public boolean travel(DataTraveler<Plan> item) {
						
						if(!planCash.containsKey(item.data.pid))
							planCash.put(item.data.pid, item.data.qty);
						
						item.data = new Plan();
						return true;
					}}, where.toString());
			}
		}
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View result = super.getPriceView(node, convertView);
		TextView tv = (TextView) result.findViewById(R.id.tvPlan);
		
		if(tv != null){
			if(DocType.getCurDoc() == OrderDoc.instance()){
				tv.setText("");
				
				if(planCash.containsKey(node.getId())){
					int plan = planCash.get(node.getId());
					
					int sel = 0;
					
					if(selCash.containsKey(node.getId()))
						sel = selCash.get(node.getId());
					
					int pcn = 0;
					
					if(plan > 0)
						pcn = (int)((double)sel / plan * 100);
					
					StringBuilder sb = new StringBuilder();
					sb.append(Util.IntToScaleStr(plan, Consts.QTY_SCALE));
					sb.append("<br>");
					sb.append(Util.IntToScaleStr(sel, Consts.QTY_SCALE));
					sb.append("<br>");
					sb.append(pcn).append("%");
		
					tv.setText(Html.fromHtml(sb.toString()));
				}
			}else
				tv.setVisibility(View.GONE);
		}
		
		return result;
	}
	
	@Override
	void readPriceNode(long rowid) {
		if(adapter instanceof MonitoringAdapter) {
			monitorGood.read(rowid);
			MntrGoods mg = monitorGood.getData();
			Price p = price.getData();
			p.id = mg.id;
			p.name = mg.name;
			p.cost.clear();
		} else
			super.readPriceNode(rowid);
	}
	
	@Override
	protected int getItemLayoutId() { return R.layout.priceitemrowex; }

	@Override
	protected void onResume() {
		super.onResume();
		
		if(DocType.getCurDoc() == OrderDoc.instance()){
			updaterTask = new UpdaterTask();
			updaterTask.execute((Void[])null);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(updaterTask != null)
			updaterTask.cancel(true);
	}
	
	class UpdaterTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			updateSelCash(document);
			return null;
		}
		
		protected void onPostExecute(Void result) {
			if(adapter != null)
				adapter.notifyDataSetChanged();
		};
	};
	
	private AsyncTask<Void, Void, Void> updaterTask = null;

	public static void updateSelCash(Document<?> doc) {
		selCash.clear();
		
		if(doc != null){
			String id = doc.getId();
			if(id.trim().length() > 0){
				Calendar calendar = Calendar.getInstance();
				Date end = calendar.getTime();
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				Date begin = calendar.getTime();
				DatePeriod dp = new DatePeriod(begin, end);
				dp.periodType = DatePeriod.CREATED;
				DocList dl = OrderDoc.instance().docList(id, null, dp);
		
				for (int i = 0; i < dl.getCount(); i++) {
					Document<?> d = dl.get(i);
					if (d instanceof OrderImpl) {
						Order o = ((OrderImpl) d).getData();
		
						if (o.items != null && o.items.size() > 0)
							for (OrderItem oi : o.items){
								if(!selCash.containsKey(oi.id))
									selCash.put(oi.id, oi.qty);
								else
									selCash.put(oi.id, selCash.get(oi.id) + oi.qty);
							}
					}
				}
			}
		}
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		if (DocType.getCurDoc() == OrderDoc.instance()){
			return new ZeroF(((OrderEx) document.getData()).whCode);
		}
		return super.createZeroPositionFilter();
	}
	
	class ZeroF extends Filter {
		HashMap<String, SkladItem> data = null;
		
		public ZeroF(String whId) {
			super(ZeroPositionFilter.NAME);
			data = CostStrategyEx.getSkaldData(whId);			
		}
		
		@Override
		public boolean inset(long priceRowID, String id) {
			if(data.containsKey(id))
				return data.get(id).qty > 0;
			return false;
		}
	}
	
	@Override
	protected FoldersAdapter createAdapterInstance() {
		if (DocType.getCurDoc() == OrderDoc.instance()){
			OrderEx o = (OrderEx) document.getData();
			if(whCode.equals(o.whCode) == false) {
				whCode = o.whCode;
				FoldersAdapter.resetCache();
			}
			final HashMap<String, SkladItem> si = CostStrategyEx.getSkaldData(o.whCode);

			return new FoldersAdapter(this){
				@Override public boolean inset(long rowid, String id) { return si.containsKey(id); }
			};
		}
		
		if(DocType.getCurDoc() == DiscountMonitoringDoc.instance()) {
			return new MonitoringAdapter(this, document == null ? "" : document.getId());
		}
		return super.createAdapterInstance();
	}
	
	class MonitoringAdapter extends FoldersAdapter {
		
		HashSet<String> items = new HashSet<String>();

		public MonitoringAdapter(WarehouseManager warehouse, String orgId) {
			super(warehouse);

			MntrMatrixImpl mmi = new MntrMatrixImpl();
			MntrMatrix mm = mmi.getData();
			
			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx)oi.getData();
			oe.id = orgId;
			oi.read();
			oi.close();
			mm.name = oe.orgType;
			mmi.read();
			mmi.close();
			
			for(MatrixItem i : mm.items)
				items.add(i.id);
			
			String code = "Monitoring" + oe.orgType;
			if(whCode.equals(code) == false) {
				whCode = code;
				FoldersAdapter.resetCache();
			}
		}
		
		@Override
		protected void fillPriceIds(SQLiteDatabase database) {
			try{
				fprice.clear();
				String stmt = "select f.id, p.id, p.name, p.rowid from MonitoringGoods p, MonitoringFolders f where p.folder = f.fid";
				Cursor c = DataBaseManager.getDataBase().rawQuery(stmt, null);
				while(c.moveToNext()) {
					String id = c.getString(1);
					if(items.contains(id)) {
						long rowid = c.getLong(3);					
						int folder = c.getInt(0);
						String name = c.getString(2);
						
						if(!fprice.containsKey(folder))
							fprice.put(folder, new ArrayList<PriceInfo>());
						
						PriceInfo pi = new PriceInfo(rowid, name, id);
						fprice.get(folder).add(pi);
					}
				}
				c.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		@Override
		protected void fillTree(SQLiteDatabase database) {
			try {
				String stmt = "select name, level, id from MonitoringFolders order by id";
				Cursor c = DataBaseManager.getDataBase().rawQuery(stmt, null);
				if (c.moveToFirst()){
					root.getChilds().clear();
					makeTree(c,root);
					moveChilds(root);
					deleteEmptyNodes(root);
					sortFullTree(root);
				}
				c.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
