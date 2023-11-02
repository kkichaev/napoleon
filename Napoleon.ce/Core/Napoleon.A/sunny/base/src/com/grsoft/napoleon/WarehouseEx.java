package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.MatrixEx;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.AssortmentMatrixAdapterEx;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;



public class WarehouseEx extends WarehouseNew {
	private static final int MAX_ITEMS = 5;
	private static final int MONTH = 3;
	
	public Map<String, List<PrevData>> cache = null; 
	public RemnantsImpl remnants  = new RemnantsImpl();
	public AssortmentMatrixAdapterEx assortmentMatrixAdapter;
	public Map<String, Integer> mtxColors = new HashMap<String, Integer>(); 
	public int curFolder = -1;
	
	int nameWidth = 100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(isValidOrder()){
			btnLines.setVisibility(View.GONE);
			
			Display display = getWindowManager().getDefaultDisplay();
			nameWidth = display.getWidth() / 3;
		}
	}
	
	@Override protected int getLayoutId() {
		Intent i = getIntent();
		long r = ExtrasConst.INVALID_ID;
		
		if (i != null){
			Bundle b = i.getExtras();
			if (b != null)
				r = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		}
		
		if(DocType.getCurDoc() == OrderDoc.instance() && r != ExtrasConst.INVALID_ID)
			return R.layout.warehouseex; 
		else
			return super.getLayoutId();
	}
	
	public View getPriceView(PriceTreeNode node, View convertView) {
		updateSalesTotal();
		if(isValidOrder())
			return createOrderView(node, convertView);
		else return super.getPriceView(node, convertView);
	}
	
	private void updateSalesTotal() {
		int id = adapter.isTop() ? 0 : adapter.getFolderTop().id;
		if(curFolder < 0 || curFolder != id) {
			curFolder = id;
			List<Integer> wdata = assortmentMatrixAdapter.getFolderSellData(curFolder);
			int[] ids = new int[] {R.id.tvSoldW1,R.id.tvSoldW2,R.id.tvSoldW3,R.id.tvSoldW4,R.id.tvSoldW5,}; 
			
			int idx = 0;
			for(Integer val : wdata) {
				if(idx >= ids.length)
					break;
				TextView tv = (TextView)findViewById(ids[idx++]);
				tv.setText(Util.IntToScaleStr(val, Consts.QTY_SCALE, Util.DEC_DELIM, true));
			}
		}
	}
	
	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		updateSalesTotal();
		return super.getFolderView(node, convertView);
	}

	protected boolean isValidOrder() {
		return DocType.getCurDoc() == OrderDoc.instance() && document != null && document.getRowid() != ExtrasConst.INVALID_ROWID;
	}

	protected View createOrderView(PriceTreeNode node, View convertView) {
		View view = null;
		if (convertView != null && convertView.getTag(R.layout.priceitemrowex) != null)
			view = convertView;
		else {
			view = View.inflate(this, R.layout.priceitemrowex, null);
			view.setTag(R.layout.priceitemrowex, true);
		}
		
		price.read(node.getRowid(), false);
		Price p = price.getData();
		
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(p.name);
		tv.setWidth(nameWidth);
		setColor(tv, p);
		
		List<PrevData> prevData = null;
		
		if(cache.containsKey(p.id)) {
			prevData = cache.get(p.id);
		}
		if(prevData != null)
			Collections.sort(prevData);
		
		int[] prevR = new int[] {R.id.tvPrevRem1, R.id.tvPrevRem2, R.id.tvPrevRem3, R.id.tvPrevRem4, R.id.tvPrevRem5};
		int[] prevO = new int[] {R.id.tvPrevOrd1, R.id.tvPrevOrd2, R.id.tvPrevOrd3, R.id.tvPrevOrd4, R.id.tvPrevOrd5};
		int[] prevD = new int[] {R.id.tvDate1, R.id.tvDate2, R.id.tvDate3, R.id.tvDate4, R.id.tvDate5};
		
		for(int i=0; i<MAX_ITEMS; i++) {
			PrevData v = (prevData != null && i < prevData.size()) ? prevData.get(i) : null;
			
			tv = (TextView) view.findViewById(prevR[i]);
			if(v != null && v.remn != null)
				tv.setText(Util.IntToScaleStr(v.remn, Consts.QTY_SCALE));
			else
				tv.setText("");
			
			tv = (TextView) view.findViewById(prevO[i]);
			if(v != null && v.ord != null)
				tv.setText(Util.IntToScaleStr(v.ord, Consts.QTY_SCALE));
			else
				tv.setText("");
			
			tv = (TextView) view.findViewById(prevD[i]);
			if(v != null && v.date != null)
				tv.setText(Util.simpleDateFormat.format(v.date));
			else
				tv.setText("");
		}
				
		
		tv = (TextView) view.findViewById(R.id.tvRemn);
		tv.setTag(price.getRowid());
		tv.setOnClickListener(remnantsClick());
		RemnantItem ri = (RemnantItem) remnants.findItem(p.id);
		if(ri != null)
			tv.setText(Util.IntToScaleStr(ri.qty, Consts.QTY_SCALE));
		else tv.setText("");
		
		tv = (TextView) view.findViewById(R.id.tvOrd);
		OrderItem oi = (OrderItem) ((Itemsable)document).findItem(p.id);
		if(oi != null)
			tv.setText(Util.IntToScaleStr(oi.qty,  Consts.QTY_SCALE));
		else
			tv.setText("");
		
		tv = (TextView) view.findViewById(R.id.tvCost);
		tv.setText(Util.IntToScaleStr(getCost(p), Consts.SUM_SCALE));
		
		tv = (TextView) view.findViewById(R.id.tvWhQty);
		tv.setText(Util.IntToScaleStr(getWhQty((Itemsable) document, p), Consts.QTY_SCALE));
		
		return view;
	};
	
	private OnClickListener remnantsClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				long rowid = (Long) v.getTag();
				remnants.editItem(rowid, v.getContext());
				adapter.notifyDataSetChanged();
			}
		};
	}

	private class PrevData implements Comparable<PrevData>{
		public Integer remn = null;
		public Integer ord = null;
		public Date date = null;
		
		@Override
		public int compareTo(PrevData o) {
			return date.compareTo(o.date);
		}
	}
	
	@Override
	public void afterBuildSet() {
		super.afterBuildSet();
		createAssortementMatrixAdapter();
		
		if(isValidOrder() &&  cache == null)
			initCashe();
		
		initMatrixColors();
	}

	private void initCashe() {
		cache = new HashMap<String, List<WarehouseEx.PrevData>>();
		DatePeriod dp = createDatePeriod();
		
		com.grsoft.napoleon.documents.DocList remn = RemnantsDoc.instance().docList(document.getId(), "created DESC", dp);
		com.grsoft.napoleon.documents.DocList orders = OrderDoc.instance().docList(document.getId(), "created DESC", dp);
		
		collectRemnants(remn);
		collectOrders(orders);
	}

	private void initMatrixColors() {
		DataTraveler.travel(Matrix.class, new DataTraveler.Travel<Matrix>(){

			@Override
			public boolean travel(DataTraveler<Matrix> item) {
				int color = ((MatrixEx)item.data).color;
				for(MatrixItem i : item.data.items)
					if(!mtxColors.containsKey(i.id))
						mtxColors.put(i.id, color);
					
				return true;
			}}, null);
		
	}

	protected DatePeriod createDatePeriod() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		Date end = calendar.getTime();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.MONTH, -MONTH);
		calendar.add(Calendar.DAY_OF_MONTH, 0);
		Date begin = calendar.getTime();
		DatePeriod dp = new DatePeriod(begin, end);
		return dp;
	}

	protected void collectOrders(com.grsoft.napoleon.documents.DocList orders) {
		Iterator<Document<?>> iter = orders.iterator();
		while (iter.hasNext()){
			OrderImpl o = (OrderImpl) iter.next();
			
			if(o.getRowid() == document.getRowid())
				continue;
			
			if (o != null && o.getData().items != null)
				for(OrderItem i : o.getData().items){
					if (!cache.containsKey(i.id))
						cache.put(i.id, new ArrayList<PrevData>());
					
					List<PrevData> v = cache.get(i.id);
					boolean find = false;
					Date d = Util.getDayStart(o.getDate());
					for(PrevData pd : v) {
						if(pd.date.compareTo(d) == 0) {
							find = true;
							pd.ord = i.qty;
							break;
						}
					}
					
					if(!find){
						for(PrevData pd : v) {
							if(pd.ord == null) {
								find = true;
								pd.ord = i.qty;
								pd.date = d;
								break;
							}
						}
						if(!find && v.size() < MAX_ITEMS) {
							PrevData pd = new PrevData();
							v.add(pd);
							pd.ord = i.qty;
							pd.date = o.getDate();
							find = true;
						}
					}
				}
		}
	}

	protected void collectRemnants(com.grsoft.napoleon.documents.DocList remn) {
		long tr = RemnantsImpl.find(document.getId(), new Date());
		Iterator<Document<?>> iter = remn.iterator();
		
		while (iter.hasNext()){
			RemnantsImpl r = (RemnantsImpl) iter.next();
			
			if(r.getRowid() == tr)
				continue;
			
			if (r != null && r.getData().items != null)
				for(RemnantItem i : r.getData().items){
					if (!cache.containsKey(i.id))
						cache.put(i.id, new ArrayList<PrevData>());
					
					List<PrevData> v = cache.get(i.id);
					if(v.size() < MAX_ITEMS) {
						PrevData pd = new PrevData();
						pd.date = Util.getDayStart(r.getData().created);
						pd.remn = i.qty;
						v.add(pd);
					}
				}
		}
	}
	
	@Override
	protected void postDocInited() {
		super.postDocInited();
		
		if(isValidOrder()){
			long rowid = RemnantsImpl.find(document.getId(), ((OrderImpl)document).getData().created);
			
			if(rowid != ExtrasConst.INVALID_ROWID){
				remnants.read(rowid);
				remnants.close();
			}else
				remnants.init(document);
		}
	}
	
	void setOrderQty(long rowid) {
		final PriceImpl priced = new PriceImpl();
		priced.read(rowid);
		priced.close();
		final Price prc = priced.getData();
		final OrderImpl oi = (OrderImpl)document;
		final int qty = oi.getItemQty(prc);
		OrderItem oitem = (OrderItem) oi.findItem(prc.id);
		final boolean isInPack = oitem != null ? oitem.inPack() : false;
		
		InputNumberDlg.open(WarehouseEx.this, new InputNumber() {
			
			@Override public int getValue() { return isInPack ? (int)((long)qty *Consts.QTY_SCALE / prc.qtyInPack) : qty; }			
			@Override public boolean isInpack() { return isInPack; }
			
			@Override
			public void applayInput(int value, Object... params) {
				boolean inPack = (Boolean)params[0];
				CostStrategy cs = CostStrategy.getInstance(oi.getClass());
				
				int cost = cs.getCostInt(prc, oi, oi.getSumType());

				if( inPack )
					value = (int)((long)value * prc.qtyInPack / Consts.QTY_SCALE);
				oi.updateQty(priced, value, cost, inPack);
				
				notifyDataSetChanged();
				
				curFolder = -1;
				assortmentMatrixAdapter.refreshSellData();
			}
		}, Consts.QTY_SCALE, true, "Заказ", true);
	}		

	@Override
	public void editItem(long rowid) {
		if(isValidOrder()){
			setOrderQty(rowid);
		}else
			super.editItem(rowid);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuItem it = menu.findItem(R.id.itColumns);
		
		if(it != null && isValidOrder())
			it.setVisible(false);
		
		return true;
	}
	
	@Override
	protected void setDefaultBackground(TextView textView) {
		if(isValidOrder()) {
			int id = R.drawable.list_selector;
			if(assortmentMatrixAdapter != null) {
				String pid = price.getData().id;
				if( assortmentMatrixAdapter.isIdInMatrix(pid) && 
						((Itemsable)document).findItem(pid) == null && !lastBuyingItems.contains(pid))
					id = R.drawable.lt_blue;
			}
			textView.setBackgroundResource(id);
		}
		else
			super.setDefaultBackground(textView);
	}
	
//	@Override
//	public void setColor(TextView textView, Price price) {
//		if(assortmentMatrixAdapter != null && assortmentMatrixAdapter.isIdInMatrix(price.id) &&
//			((Itemsable)document).findItem(price.id) == null && !lastBuyingItems.contains(price.id)){
//			textView.setTextColor(getResources().getColor(R.color.blue));
//		} else
//			super.setColor(textView, price);
//	};

	@Override
	protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
		if (assortmentMatrixAdapter == null)
			assortmentMatrixAdapter =  new AssortmentMatrixAdapterEx(this, document.getId());
		
		return assortmentMatrixAdapter;
	}
	
	@Override
	protected int getDefaultColor(Price p) {
		int result = super.getDefaultColor(p);
		
		if(p.color == 0 && mtxColors.containsKey(p.id))
			result = Util.GrServerColorToSystem(mtxColors.get(p.id));
		return  result ;
	}
}
