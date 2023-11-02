package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DaysGoods;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PlanQtyData;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.ReturnRequest;
import com.grsoft.dataobjects.impl.AgentPlanNewImpl;
import com.grsoft.dataobjects.impl.DaysGoodsImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgMatrixImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.dataobjects.impl.ReturnRequestImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.util.AssortmentMatrixAdapterEx;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.InputNumber;
import com.grsoft.util.MatrixItemsAdapterEx;
import com.grsoft.util.PriceNodeComparer;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {
	
	private static final String DAYS_GOODS_MATRIX = "<Товар дня>";

	public static HashSet<String> planItems = null; 

	Date prevVisit = null;
	HashMap<String, Integer> prevRest = new HashMap<String, Integer>();
	HashMap<String, Integer> prevDelivery = new HashMap<String, Integer>();
	HashMap<String, Integer> dailyOrder = new HashMap<String, Integer>();
	public static HashMap<String, Integer> autoOrder = new HashMap<String, Integer>();
	static long autoOrderRowID = ExtrasConst.INVALID_ROWID;
	public static HashSet<String> orgMtxItems = null;
	
	static boolean showDaysGoods = false;
	
	@Override protected int getLayoutId() { return R.layout.warehouse_ex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		FoldersAdapter.resetCache();
		super.onCreate(savedInstanceState);

		int ids[] = new int[] { R.id.tvSortName, R.id.tvSortPack, R.id.tvSortTherm };
		for(int id : ids) {
			View v = (View)findViewById(id);
			if( v != null )
//				v.setVisibility(inItemSelectMode ? View.GONE : View.VISIBLE);
				v.setOnClickListener(setSorting);
		}
		
		updateSortHeader();
		
		findViewById(R.id.ibNextPrice).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showDaysGoods = false;
				resetMatrix();
			}
		});
	}
	
	protected void onResume() {
		super.onResume();
		refreshCurrentData();
		
		TextView tv = (TextView)findViewById(R.id.tvPrevDate);
		String text = "";
		
		if( prevVisit != null ) {
			text = Util.simpleDateFormat.format(prevVisit);
		} else
			text = "не определена";
		tv.setText(text);
	}
	
	void loadPrevRest(Date curDay) {
		String where = "created < " + Long.toString(curDay.getTime());
		DocList dl = RemnantsDoc.instance().docList(document.getId(), "created desc", where);
		for(Document<?> d : dl) {
			Remnants rdoc = (Remnants) d.getData();
			prevVisit = Util.getDayStart(rdoc.created);
			for(RemnantItem ri : rdoc.items)
				prevRest.put(ri.id, ri.qty);
			break;
		}
		dl.close();
	}
	
	void loadPrevDelivery(Date prevDay, Date curDay, String firmCode) {
		if(prevDay == null)
			return;
		
		String where = "firm = '" + firmCode + "' and date >= " + Long.toString(Util.getDayStart(prevDay).getTime()) + 
				" and date < " + Long.toString(Util.getDayStart(curDay).getTime()) + " and id='" + document.getId() + "'";
		DocList dl = DeliveryDoc.instance().docList(document.getId(), null, where);
		for(Document<?> d : dl) {
			for(DeliveryItem di : ((Delivery)d.getData()).items) {
				Integer qty = prevDelivery.get(di.id);
				if( qty == null )
					qty = 0;
				qty += di.qty;
				prevDelivery.put(di.id, qty);
			}
		}
		dl.close();
	}
	
	private void loadDailyOrder(Date curDay, String firmCode) {
		String where = "firmCode = '" + firmCode + "' and created >= " + Long.toString(Util.getDayStart(curDay).getTime()) + 
				" and created <= " + Long.toString(Util.getDayEnd(curDay).getTime());
		DocList dl = OrderDoc.instance().docList(null, null, where);
		for(Document<?> d : dl) {
			for(OrderItem oi : ((Order)d.getData()).items) {
				Integer qty = dailyOrder.get(oi.id);
				if( qty == null )
					qty = 0;
				qty += oi.qty;
				dailyOrder.put(oi.id, qty);
			}
		}
		dl.close();
	}
	
	void countAutoOrder() {
		if(document.getRowid() == autoOrderRowID)
			return;
		
		autoOrder.clear();
		HashMap<String, Integer> curRest = new HashMap<String, Integer>();
		if( remnantsDoc != null )
			for(RemnantItem ri : remnantsDoc.getData().items)
				curRest.put(ri.id, ri.qty);
		
		HashMap<String, Integer> prevData = new HashMap<String, Integer>();
		for(Entry<String, Integer> kv : prevRest.entrySet())
			prevData.put(kv.getKey(), kv.getValue());
		for(Entry<String, Integer> kv : prevDelivery.entrySet()) {
			Integer rest = prevData.get(kv.getKey());
			if( rest == null)
				rest = 0;
			prevData.put(kv.getKey(), kv.getValue() + rest);
		}
		
		for(Entry<String, Integer> kv : prevData.entrySet()) {
			int value = kv.getValue();
			
			Integer cv = curRest.get(kv.getKey());
			if( cv == null)
				cv = 0;
			value -= cv;
			value *= 1.5;
			value -= cv;
			if( value < 0 )
				value = 0;
			autoOrder.put(kv.getKey(), value);
		}
		autoOrderRowID = document.getRowid();
	}
	
	private void refreshCurrentData() {
		prevRest.clear();
		prevDelivery.clear();		
		dailyOrder.clear();
		prevVisit = null;
		
		if( !(document instanceof OrderImplEx) || document.getRowid() == ExtrasConst.INVALID_ROWID) {
			autoOrder.clear();
			autoOrderRowID = ExtrasConst.INVALID_ROWID;
			showDaysGoods = false;
			return;
		}		
		
		String firmCode = ((OrderEx)document.getData()).firmCode;
		Date curDay = Util.getDayStart(((Order)document.getData()).created);
		
		loadPrevRest(curDay);
		loadDailyOrder(curDay, firmCode);
		loadPrevDelivery(prevVisit, curDay, firmCode);
		countAutoOrder();
		
		updateTotals();
	}
	
	private void updateTotals() {
		TextView tv;
		
		tv = (TextView)findViewById(R.id.tvPrevRest);
		tv.setText(Util.IntToScaleStr(CountTotals(prevRest), 0));
		
		tv = (TextView)findViewById(R.id.tvPrevSales);
		tv.setText(Util.IntToScaleStr(CountTotals(prevDelivery), 0));
		
		tv = (TextView)findViewById(R.id.tvCurRest);
		tv.setText(Util.IntToScaleStr(remnantsDoc == null ? 0 : remnantsDoc.qty(), 0));

		tv = (TextView)findViewById(R.id.tvAutoOrder);
		tv.setText(Util.IntToScaleStr(CountTotals(autoOrder), 0));

		tv = (TextView)findViewById(R.id.tvCurOrder);
		tv.setText(Util.IntToScaleStr(document instanceof OrderImplEx ? ((OrderImplEx)document).qty() : 0, 0));

		int value = 0;
		if(document instanceof OrderImplEx) {
			Map<String, PlanQtyData> pd = ((OrderImplEx)document).getPlanData();
			for(Entry<String, PlanQtyData> kv : pd.entrySet()) {
				PlanQtyData pq = kv.getValue();
				value += (int)((long)pq.qty * pq.inPack / Consts.QTY_SCALE); 
			}
			value /= Consts.QTY_SCALE;
		}
		tv = (TextView)findViewById(R.id.tvPlan);
		tv.setText(Util.IntToScaleStr(value, 0));
		
		tv = (TextView)findViewById(R.id.tvFact);
		tv.setText(Util.IntToScaleStr(CountTotals(dailyOrder), 0));
	}

	private int CountTotals(HashMap<String, Integer> data) {
		int ret = 0;
		for(Entry<String, Integer> kv : data.entrySet())
			ret += kv.getValue();
		return (ret + Consts.QTY_SCALE / 2) / Consts.QTY_SCALE;
	}

	OnClickListener setSorting = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if( adapter instanceof Adapter ) {
				PriceNodeComparer pnc = (PriceNodeComparer)FoldersAdapter.TreeNodeComparator;
				pnc.setCompareMethod(arg0.getId(), pnc.getCompareMethod() == arg0.getId() ? !pnc.isReverse() : false);
				((Adapter)adapter).sortNodes();
				updateSortHeader();
			}
		}
	};
	
	@Override
	protected int getItemLayoutId() { return R.layout.priceitemrow_ex; }
	
	@Override
	public void sortingPriceList(ArrayList<TreeNode> price) {
		Collections.sort(price, FoldersAdapter.TreeNodeComparator);
	}
	
	Date docDate() {
		if( document == null || document.getRowid() == ExtrasConst.INVALID_ROWID )
			return null;
		
		return Util.getDayEnd(document.getDate());
	}
	
	String getFirm() {
		if( document == null || document.getRowid() == ExtrasConst.INVALID_ROWID )
			return null;
		return (document instanceof OrderImplEx) ? ((OrderEx)document.getData()).firmCode : "";
	}
	
	OrgEx getOrg(){
		if( document == null || document.getRowid() == ExtrasConst.INVALID_ROWID )
			return null;
		
		OrgImpl org = new OrgImpl();
		org.read("id", document.getId());
		
		return (OrgEx) org.getData();
	}
	
	@Override
	protected ArrayList<String> prepareMatrixList(ArrayList<String> items) {
		items.add(1, DAYS_GOODS_MATRIX);
		return items;
	}
	
	@Override
	protected boolean inheritedApplayMatrix(String matrixName) {
		if(matrixName.equals(DAYS_GOODS_MATRIX)) {
			WarehouseAdapter adapter = (WarehouseAdapter) getDaysGoodsMatrix(getOrg(), getFirm());
				if(adapter != null) {
				applayAdapter(adapter);
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		planItems = AgentPlanNewImpl.getPlanItems(docDate(), getFirm());
		orgMtxItems = OrgMatrixImpl.getItems(getOrg(), getFirm());
		return (AssortmentMatrixAdapter)new AssortmentMatrixAdapterEx(this, document.getId());
	}
	
	BaseAdapter getDaysGoodsMatrix(OrgEx org, String firm) {
		DaysGoodsImpl dgi = new DaysGoodsImpl();
		DaysGoods dg = dgi.getData();
		dg.firm = firm;
		dg.id = org.id;

		if( !dgi.read())  {
			dg.id = org.ido;
			if( !dgi.read() )
				dg.id = "";
			dgi.read();
		}
		dgi.close();
		
		return dg.items.size() > 0 ? new MatrixItemsAdapterEx(this, dg.items) : null;
	}
	
	@Override protected BaseAdapter createListAdapter() {
		View nextBtn = findViewById(R.id.ibNextPrice);

		if(document instanceof ReturnRequestImpl ) {
			nextBtn.setVisibility(View.GONE);
			return new ReturnRequestAdapter(this, (ReturnRequest) document.getData());
		}
		
		OrgEx o = getOrg();
		String firm = getFirm();
		planItems = AgentPlanNewImpl.getPlanItems(docDate(), firm);
		orgMtxItems = OrgMatrixImpl.getItems(o, firm);
		
		if(document instanceof OrderImplEx && autoOrderRowID != document.getRowid())
			showDaysGoods = true;
		
		if(showDaysGoods) {
			BaseAdapter ret = getDaysGoodsMatrix(o, firm);			
			if(ret != null) {
				nextBtn.setVisibility(View.VISIBLE);
				return ret;
			}
			showDaysGoods = false;
		}
		
		nextBtn.setVisibility(View.GONE);
		return new Adapter(this); 
	}

	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View v = super.getPriceView(node, convertView);
		
		v.findViewById(R.id.llQuant).setVisibility(View.GONE);
		
		PriceEx pe = (PriceEx)price.getData();
		TextView tv = (TextView)v.findViewById(R.id.tvThermal);
		tv.setText(pe.thermalState);
//		tv.setVisibility(inItemSelectMode ? View.GONE : View.VISIBLE);
		
		tv = (TextView)v.findViewById(R.id.tvPackName);
		tv.setText(pe.packName);
//		tv.setVisibility(inItemSelectMode ? View.GONE : View.VISIBLE);
		
		int inPack =  pe.qtyInPack;
		if(inPack == 0)
			inPack = Consts.QTY_SCALE;
		
		String text = "";
		Integer val;
		val = prevRest.get(pe.id);
		if(val != null && val != 0)
			text = Util.IntToScaleStr((val + Consts.QTY_SCALE / 2)/ Consts.QTY_SCALE, 0);
		else
			text = "";
		((TextView)v.findViewById(R.id.tvPrevRestQty)).setText(text);

		val = prevDelivery.get(pe.id);
		if(val != null && val != 0)
			text = Util.IntToScaleStr((val + Consts.QTY_SCALE / 2)/ Consts.QTY_SCALE, 0);
		else
			text = "";
		((TextView)v.findViewById(R.id.tvPrevOrdQty)).setText(text);

		val = 0;
		if( remnantsDoc != null ) {
			RemnantItem ri = (RemnantItem) remnantsDoc.findItem(pe.id);
			if( ri != null )
				val = ri.qty;
		} else if(document instanceof RemnantsImpl) {
			RemnantItem ri = (RemnantItem) ((RemnantsImpl)document).findItem(pe.id);
			if( ri != null )
				val = ri.qty;
		}
		if(val != null && val != 0)
			text = Util.IntToScaleStr((val + Consts.QTY_SCALE / 2)/ Consts.QTY_SCALE, 0);
		else
			text = "";
		((TextView)v.findViewById(R.id.tvRestQty)).setText(text);
		
		val = autoOrder.get(pe.id);
		if(val != null && val != 0)
			text = Util.IntToScaleStr((val + Consts.QTY_SCALE / 2)/ Consts.QTY_SCALE, 0);
		else
			text = "";
		tv = ((TextView)v.findViewById(R.id.tvAutoOrdQty));
		tv.setTag(pe.id);
		tv.setText(text);
		tv.setOnClickListener(new AutoOrderClick(pe.id));
		
		text = "";
		if(document instanceof OrderImplEx) {
			OrderItem oi = (OrderItem)((OrderImplEx)document).findItem(pe.id);
			if( oi != null )
				text = Util.IntToScaleStr((oi.qty + Consts.QTY_SCALE / 2)/ Consts.QTY_SCALE, 0);
		}
		((TextView)v.findViewById(R.id.tvOrdQty)).setText(text);
		
		val = 0;
		if(document instanceof OrderImplEx) {
			PlanQtyData pq = ((OrderImplEx)document).getPlanQty(pe.id);
			val = (int)((long)pq.qty * pq.inPack / Consts.QTY_SCALE);
		}
		if(val != null && val != 0)
			text = Util.IntToScaleStr((val + Consts.QTY_SCALE / 2)/ Consts.QTY_SCALE, 0);
		else
			text = "";
		((TextView)v.findViewById(R.id.tvPlanQty)).setText(text);

		val = dailyOrder.get(pe.id);
		if(val != null && val != 0)
			text = Util.IntToScaleStr((val + Consts.QTY_SCALE / 2)/ Consts.QTY_SCALE, 0);
		else
			text = "";
		((TextView)v.findViewById(R.id.tvFactQty)).setText(text);
		
//		tv = (TextView)v.findViewById(R.id.tvOrdQty);
//		if (document != null) {
//			int value = ((Itemsable)document).getItemQty(pe);
//			if( pe.qtyInPack != 0 ) {
//				value = (int)((long)value * Consts.QTY_SCALE / pe.qtyInPack);
//			}
//			tv.setText( value == 0 ? "" : Util.IntToScaleStr(value, Consts.QTY_SCALE));
//		}
//		tv.setVisibility(inItemSelectMode ? View.GONE : View.VISIBLE);
		
//		int backResource = pe.thermalState.contains("Охл") == false ? R.drawable.list_selector : R.drawable.even_row_selector;
//		v.setBackgroundResource(backResource);
		
		return v;
	}
	
	@Override
	protected String getItemName(Price p) {
		PriceEx pe = (PriceEx)p;
		return p.name + " " + pe.thermalState + "/" + pe.packName;
	}
		
	void updateSortHeader() {
		int sortMethod = ((PriceNodeComparer)FoldersAdapter.TreeNodeComparator).getCompareMethod();
		int ids[] = new int[] { R.id.tvSortName, R.id.tvSortPack, R.id.tvSortTherm };
		for(int id : ids) {
			TextView v = (TextView)findViewById(id);
			if( v != null ) {
				int color = (id == sortMethod) ? Color.RED : Color.BLACK;
				v.setTextColor(color);
			}
		}		
	}
	
	class AutoOrderClick implements View.OnClickListener {
		
		String id;
		public AutoOrderClick(String id) {
			this.id = id;
		}
		
		@Override
		public void onClick(View v) {
			InputNumberDlg.open(v.getContext(), new InputNumber() {
				
				@Override
				public int getValue() {
					Integer val = autoOrder.get(id);
					if( val == null )
						val = 0;
					return ((val + Consts.QTY_SCALE / 2) / Consts.QTY_SCALE )* Consts.QTY_SCALE;
//					return ( val == null ) ? 0 : val;
				}
				
				@Override
				public void applayInput(int value, Object... params) {
					autoOrder.put(id,  value);
					notifyDataSetChanged();
					((TextView)findViewById(R.id.tvAutoOrder)).setText(Util.IntToScaleStr(CountTotals(autoOrder), 0));
				}
			}, Consts.QTY_SCALE, true, "Введите количество");
		}
	};
	
	@Override protected Filter createZeroPositionFilter() { return new ZeroPlanFilter(); }
	
	class ZeroPlanFilter extends ZeroPositionFilter {
		public ZeroPlanFilter() {
			super();
			where = "";
		}
		
		@Override
		public boolean inset(long priceRowID, String id) {
			if( document instanceof OrderImplEx )
				return (((OrderImplEx)document).getRestPlanQty(id) > 0);
			return super.inset(priceRowID, id);
		}
	}
	
	class Adapter extends FoldersAdapter {
		public Adapter(WarehouseManager warehouse) {
			super(warehouse);
		}
		
		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			View v = super.getView(arg0, convertView, arg2);
			if(document != null && ((Itemsable)document).getItemValue(price.getData()) < 0 ) {
				v.setBackgroundResource(R.drawable.price_below_zero);
			}
			return v;
		}
		
		public void sortNodes() {
			Collections.sort(folderTop.getChilds(), FoldersAdapter.TreeNodeComparator);
			notifyDataSetChanged();
		}
		
		@Override
		public void buldProcess(AsyncTask<?, ?, ?> task) {
			super.buldProcess(task);
			if( document instanceof OrderImplEx )
				((OrderImplEx)document).loadQtyData();
		}
		
		@Override
		public boolean inset(long rowid, String id) {
			if (WarehouseEx.planItems.contains(id) && (WarehouseEx.orgMtxItems == null || WarehouseEx.orgMtxItems.contains(id)))
				return super.inset(rowid, id);
			return false;
		}
	}
}

class ReturnRequestAdapter extends FoldersAdapter {
	HashSet<String> ids = new HashSet<String>();
	
	public ReturnRequestAdapter(WarehouseManager warehouse, ReturnRequest document) {
		super(warehouse);
		
		FoldersAdapter.resetCache();
		
		String orgId = document.id;
		
		Date expDate = Util.getDayStart(document.getExpiredDate());
		DocList dl = DeliveryDoc.instance().docList(orgId, "", "firm='" + document.firmCode + "'");
		for(Document<?> d : dl) {
			Delivery ddoc = (Delivery)d.getData(); 
			for(DeliveryItem item : ddoc.items) {
				if( ((DeliveryItemEx)item).expired.compareTo(expDate) >= 0)
					ids.add(item.id);
			}
		}
		dl.close();
	}
	
	@Override
	public boolean inset(long rowid, String id, int folder) {
		return ids.contains(id);
	}
}