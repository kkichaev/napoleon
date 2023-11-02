package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceSizeCost;
import com.grsoft.dataobjects.PriceSizeQty;
import com.grsoft.dataobjects.StyleColor;
import com.grsoft.dataobjects.StyleSizes;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.PriceSizeQtyImpl;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.KeypadHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderQtyEdit extends BaseActivity {
	
	OrderImplEx doc;
	PriceEx price;
	PriceImpl prcImpl = new PriceImpl();
	
	DataRow current;
	Adapter adapter;
	
	int totQty;
	Map<String, View> sizes = new HashMap<String, View>();
	Map<String, Map<String, Integer>> qty = new HashMap<String, Map<String,Integer>>();
	Set<String> colors = new HashSet<String>();
	
	public static void openDoc(Context context, long rid, OrderImplEx doc) {
		Intent i = new Intent(context, OrderQtyEdit.class);

		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, rid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pricecountex);
		
		doc = new OrderImplEx();
		Bundle b = getIntent().getExtras();
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		doc.read(rid);
		
		rid = b.getLong(ExtrasConst.PRICE_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		prcImpl.read(rid);
		price = (PriceEx)prcImpl.getData();
		
		findViewById(R.id.btnMinus).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { removeOne(); }
		});

		findViewById(R.id.btnPlus).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { addOne(); }
		});
		
		View ok = findViewById(R.id.btnOK);
		ok.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { saveData(); finish(); }
		});
		
		findViewById(R.id.btnRight).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				((HorizontalScrollView)findViewById(R.id.hsv)).arrowScroll(View.FOCUS_RIGHT);
			}
		});

		findViewById(R.id.btnLeft).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				((HorizontalScrollView)findViewById(R.id.hsv)).arrowScroll(View.FOCUS_LEFT);
			}
		});
		
		if(doc.isEditable() == false)
			ok.setVisibility(View.INVISIBLE);

		refreshData();
	}
	
	protected void saveData() {
		OrderEx o = (OrderEx) doc.getData();
		
		List<OrderItem> rmv = new ArrayList<OrderItem>();
		for(OrderItem oi : o.items)
			if(oi.id.equals(price.id))
				rmv.add(oi);
		
		o.items.removeAll(rmv);
		
		List<DataRow> rows = adapter.data();
		for(Entry<String, View> kv : sizes.entrySet()) {
			PriceSizeCost pcs = (PriceSizeCost) kv.getValue().getTag();
			
			for(DataRow dr : rows) {
				Integer q = dr.qty.get(kv.getKey());
				if(q != null && q > 0) {
					OrderItemEx oie = new OrderItemEx();
					oie.id = price.id;
					oie.color = dr.color.id;
					oie.cost = pcs.cost;
					oie.qty = q;
					oie.size = kv.getKey();
					o.items.add(oie);
					
					Map<String, Integer> sq = qty.get(oie.color);
					if(sq != null) {
						Integer wq = sq.get(kv.getKey());
						if(wq != null)
							wq -= q;
					}
				}
			}
		}
		
		int totQty = 0;
		int whi = o.whIndex;
		PriceSizeQtyImpl psi = new PriceSizeQtyImpl();
		for(Entry<String, Map<String, Integer>> kkv : qty.entrySet()) {
			for(Entry<String, Integer> kv : kkv.getValue().entrySet()) {
				Integer val = kv.getValue();
				if(val != null) {
					PriceSizeQty psd = psi.getData();
					psd.id = price.id;
					psd.color = kkv.getKey();
					psd.size = kv.getKey();
					if( psi.read() ) {
						psd.qty.get(whi).qty = val;
						totQty += val;
						psi.write();
					}
				}
			}
		}
		
		if(whi == 0)
			price.qty = totQty;
		else if(whi <= price.whQty.size())
			price.whQty.get(whi-1).qty = totQty;
		
		prcImpl.write();
		doc.write();
		psi.close();
		prcImpl.close();
	}

	protected void addOne() {
		if(current == null)
			return;
		
		Map<String, Integer> sizeData = qty.get(current.color.id);
		if(sizeData == null)
			return;
		
		for(Entry<String, Integer> kv : sizeData.entrySet()) {
			Integer v = current.qty.get(kv.getKey()); 
			Integer rq = kv.getValue();
			if(v == null)
				v = 0;
			if(v + Consts.QTY_SCALE <= rq) {
				v += Consts.QTY_SCALE;
				rq -= Consts.QTY_SCALE;
				sizeData.put(kv.getKey(), rq);
				current.qty.put(kv.getKey(), v);
			}
		}
		refreshSizes();
		adapter.notifyDataSetChanged();
	}

	protected void removeOne() {
		if(current == null)
			return;
		
		Map<String, Integer> sizeData = qty.get(current.color.id);
		
		for(Entry<String, Integer> kv : current.qty.entrySet()) {
			Integer v = kv.getValue(); 
			if(v > 0) {
				v -= Consts.QTY_SCALE;
				if(sizeData != null) {
					Integer rq = sizeData.get(kv.getKey());
					if(rq == null)
						rq = 0;
					rq += Consts.QTY_SCALE;
					sizeData.put(kv.getKey(), rq);
				}
				current.qty.put(kv.getKey(), v);
			}
		}
		
		refreshSizes();
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}
	
	void updateData(OrderItemEx item) {
//		Map<String, Integer> sizeData = qty.get(item.color);
//		if(sizeData == null) {
//			sizeData = new HashMap<String, Integer>();
//			qty.put(item.color, sizeData);
//		}
//		
//		Integer q = sizeData.get(item.size);
//		if(q == null)
//			q = 0;
//		q += item.qty;
//		sizeData.put(item.size, q);
		
		adapter.update(item);
	}
	
	protected void setItemImage(final String fileName) {
		try{
			final int PICSZ = 200;
			((ImageView) findViewById(R.id.ivPresent)).setImageDrawable(BitmapUtils.createBitmap(fileName, PICSZ));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void showItemImage() {
		android.database.Cursor cursor = null;
		
		try{
			final String CLMN_NAME = "photoPath";
			DbWriter.checkDBTable(DbObject.getDataType(Present.class));
			cursor = DataBaseManager.getDataBase().query(
					DataObjectInfo.getInstance().getTableName(Present.class), new String[]{CLMN_NAME}, 
					"id=?", new String[]{price.id}, null, null, null);
			
			if(cursor.moveToFirst()){
				final String path = cursor.getString(cursor.getColumnIndex(CLMN_NAME));
				View v = findViewById(R.id.ivPresent);
				v.setVisibility(View.VISIBLE);
				v.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { PricePresentation.open(v.getContext(), path, prcImpl.getRowid());	} });
				setItemImage(path);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (cursor != null)
				cursor.close();
		}
	}
	
	protected void refreshData() {
		final int whi = ((OrderEx)doc.getData()).whIndex;
		totQty = 0;
		
		showItemImage();
		
		DataTraveler.travel(PriceSizeQty.class, new DataTraveler.Travel<PriceSizeQty>() {

			@Override
			public boolean travel(DataTraveler<PriceSizeQty> item) {
				if(item.data.qty.size() > whi ) {
					Map<String, Integer> sizeData = qty.get(item.data.color);
					if(sizeData == null) {
						sizeData = new HashMap<String, Integer>();
						qty.put(item.data.color, sizeData);
					}
					colors.add(item.data.color);
					int cqty = item.data.qty.get(whi).qty;
					totQty += cqty;
					sizeData.put(item.data.size, cqty);
				}				
				return true;
			}
		}, "id='" + price.id + "'");
		
		((TextView) findViewById(R.id.tvPriceName)).setText(price.name);
		
		adapter = new Adapter();
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				DataRow dr = (DataRow) arg0.getItemAtPosition(arg2);
				adapter.setSelection(arg2);
				current = dr;
				refreshSizes();
			}
		});
		
		for(OrderItem oi : doc.getData().items) {
			if(oi.id.equals(price.id) == false)
				continue;
			updateData((OrderItemEx)oi);
		}
		
		Map<String, StyleSizes> ss = StyleSizes.get();
		LinearLayout ll = (LinearLayout)findViewById(R.id.llSize);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); 
		for(PriceSizeCost psc : price.sizes) {
			
			StyleSizes cs = ss.get(psc.size);
			if(cs == null)
				continue;
			
			View v = View.inflate(this, R.layout.size_edit, null);
			TextView tv = (TextView)v.findViewById(R.id.tvName);
			tv.setText(cs.name);
			
			sizes.put(psc.size, v);
			v.setTag(psc);
			v.setOnClickListener(sizeQtyHandler);
			ll.addView(v, lp);
		}
		TextView tv;
		tv = (TextView) findViewById(R.id.tvItemCost);
		tv.setText("остаток: " + Util.IntToScaleStr(totQty, Consts.QTY_SCALE));

		refreshSizes();
	}
	
	protected void refreshSizes() {
		Map<String, Integer> sizeData = null;
		if(current != null)
			sizeData = qty.get(current.color.id);
		
		for(Entry<String, View> kv : sizes.entrySet()) {
			Integer free = (sizeData != null) ? sizeData.get(kv.getKey()) : null;
			Integer qty = (current != null) ? current.qty.get(kv.getKey()) : null;
			
			PriceSizeCost psc = (PriceSizeCost) kv.getValue().getTag();
			
			String text;
			text = Util.IntToScaleStr(psc.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "\n";
			if(free != null && free != 0)
				text += Util.IntToScaleStr(free, Consts.QTY_SCALE);
					
			TextView tv;
			tv = (TextView)kv.getValue().findViewById(R.id.tvFree);
			tv.setText(text);

			tv = (TextView)kv.getValue().findViewById(R.id.tvQty);
			tv.setText(qty == null || qty == 0 ? "" : Util.IntToScaleStr(qty, Consts.QTY_SCALE));
		}
		
		TextView tv = (TextView) findViewById(R.id.tvItemQty);
		tv.setText("в заказе: " + Util.IntToScaleStr(adapter.totalQty(), Consts.QTY_SCALE));
	}

	View.OnClickListener sizeQtyHandler = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			if(current == null)
				return;
			final Map<String, Integer> sizeData = qty.get(current.color.id);
			if(sizeData == null)
				return;
			
			final PriceSizeCost psc = (PriceSizeCost) arg0.getTag();
			
			InputNumberDlg.Decorator dec = new InputNumberDlg.Decorator() {
				
				@Override public int getContentView() { return R.layout.input_num_cost; }
				
				@Override
				public void adjustView(AlertDialog dialog, View view, KeypadHelper nh) {
					TextView tv = (TextView)view.findViewById(R.id.tvCost);
					tv.setText("Цена: " + Util.IntToScaleStr(psc.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
				}
			};
			
			InputNumberDlg.open(arg0.getContext(), new InputNumber() {
				
				@Override
				public int getValue() {
					Integer cq = current.qty.get(psc.size);
					return cq == null ? 0 : cq;
				}
				
				@Override
				public void applayInput(int value, Object... params) {
					Integer cq = sizeData.get(psc.size);
					if(cq == null)
						cq = 0;
					if(value > cq) {
						Toast.makeText(OrderQtyEdit.this, "Количество больше остатка", Toast.LENGTH_SHORT).show();
						return;
					}
					current.qty.put(psc.size, value);
					cq -= value;
					sizeData.put(psc.size, cq);
					refreshSizes();
					adapter.notifyDataSetChanged();
				}
			}, Consts.QTY_SCALE, true, "Введите количество", false, dec);
		}
	};
	
	class Adapter extends BaseAdapter {

		int selIdx = -1;
		
		List<DataRow> items = new ArrayList<DataRow>();
		
		public Adapter() {
			Map<String, StyleColor> allColors = StyleColor.get();
			for(String  pc : colors) {
				StyleColor sc = allColors.get(pc);
				if(sc == null)
					continue;
				DataRow dr = new DataRow();
				dr.color = sc;
				items.add(dr);
			}
		}
		
		public void setSelection(int pos) {
			selIdx = pos;
			notifyDataSetChanged();
		}
		
		public List<DataRow> data() { return items; }
		
		public int totalQty() {
			int qty = 0;
			for(DataRow dr : items)
				qty += dr.count();
			return qty;
		}

		public void update(OrderItemEx item) {
			for(DataRow dr : items) {
				if(dr.color.id.equals(item.color)) {
					dr.qty.put(item.size, item.qty);
					break;
				}
			}
		}

		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null) {
				view = View.inflate(OrderQtyEdit.this, R.layout.color_row, null);
			}
			DataRow rd = (DataRow)getItem(arg0);
			
			TextView tv;
			String text = rd.color.name;
			text += " (" + Util.IntToScaleStr(rd.count(), Consts.QTY_SCALE) + ")";
			
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(text);
			tv.setTypeface(null, selIdx == arg0 ? Typeface.BOLD_ITALIC: Typeface.NORMAL);

			return view;
		}
		
	}
}

class DataRow {
	public StyleColor color;
	public Map<String, Integer> qty = new HashMap<String, Integer>();
	
	public int count() {
		int res = 0;
		for(Integer i : qty.values())
			res += i;
		return res;
	}
}