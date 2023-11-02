package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryKey;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnCause;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.ReturnItemDlv;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ReturnPriceCount extends PriceCount {
	private Spinner spReturnCause;
	DeliveryImpl delivery = new DeliveryImpl();
	DlvAdapter adapter = new DlvAdapter();
	HashMap<DlvKeySum, Integer> dlvQtys = new HashMap<DlvKeySum, Integer>();

	@Override protected int getContentViewId() { return R.layout.returncount; }
	
	public static void open(Context context, long priceRoid,
			DbObject<? extends DataObject> doc) {
		Intent i = new Intent(context, ReturnPriceCount.class);

		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		spReturnCause = (Spinner) findViewById(R.id.spCause);
		
		final List<String> items = new ArrayList<String>();
		
		DataTraveler.travel(ReturnCause.class, new DataTraveler.Travel<ReturnCause>() {
			@Override
			public boolean travel(DataTraveler<ReturnCause> item) {
				items.add(item.data.text);
				return false;
			}}, null);
		
		Collections.sort(items);
		
		spReturnCause.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, items));
		
		ReturnImpl ri = ((ReturnImpl)document); 
		ri.setUpdateQtyHandler(updater);
		
		ReturnItem it = (ReturnItem) ri.findItem(price.getData().id);
		
		if(it != null){
			for (int i = 0; i < spReturnCause.getCount(); i++)
				if(spReturnCause.getItemAtPosition(i).toString().equals(it.cause)){
					spReturnCause.setSelection(i, true);
					break;
				}
		}
		
		ListView lv = (ListView)findViewById(R.id.lvDocs);
		lv.setAdapter(adapter);

	}
	
	private UpdateQtyHandler updater = new UpdateQtyHandler() {

		@Override
		public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
			Object sel = spReturnCause.getSelectedItem();
			
			if(sel != null)
				((ReturnItem)item).cause = sel.toString();
			
			Spinner sp = (Spinner)findViewById(R.id.spCause);
			String value = (String) sp.getSelectedItem();
			ReturnItem rie = (ReturnItem)item;
			
			if( value != null)
				rie.cause = value;
			
			long sum = 0;

			if( isNewItem )
				rie.uid = UUID.randomUUID().toString().replace("-", "");
			
			rie.items.clear();
			for(Entry<DlvKeySum, Integer> kv : dlvQtys.entrySet()) {
				if( kv.getValue() == 0 )
					continue;
				
				ReturnItemDlv dlv = new ReturnItemDlv();
				dlv.date = kv.getKey().date;
				dlv.number = kv.getKey().number;
				dlv.cost = kv.getKey().cost;
				dlv.qty = kv.getValue();
				rie.items.add(dlv);

				sum += (long)dlv.cost * dlv.qty / Consts.QTY_SCALE;
			}
			
			rie.cost = (int)((sum * Consts.QTY_SCALE)/ qtyItems);

			
		}};

	@Override
	protected void refreshData() {
		super.refreshData();
		
		dlvQtys.clear();
		
		String itemId = price.getData().id;
		ReturnItem re = (ReturnItem) ((ReturnImplEx)document).findItem(itemId);
				
		if( re != null ) {
			for(ReturnItemDlv rid : re.items)
				dlvQtys.put(new DlvKeySum(rid), rid.qty);
		}
		
		adapter.refresh(document.getId(), itemId);
		updateQty();
	}
	
	void updateQty() {
		int qty = 0;
		for(Entry<DlvKeySum, Integer> kv : dlvQtys.entrySet())
			qty += kv.getValue();
		
		qtyItems = qty;
		((TextView)findViewById(R.id.tvTotalQty)).setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
	}

	@Override
	protected void makeSaleHistory(Price p) {
	}

	@Override
	protected boolean isComplexSalesHistory() {
		return false;
	}

//	@Override
//	protected boolean isInputValid(Runnable r) {
//
//		int qty = qtyItems;
//		qty = fixOrderQty(cbPackets.isChecked(), qty, price.getData());
//		if (dlv.items != null)
//			for (DeliveryItem item : dlv.items) {
//				if (item.id.equals(price.getData().id)) {
//					if (qty > item.qty) {
//						Toast.makeText(
//								this,
//								"¬веденное количество больше количества в накладной",
//								Toast.LENGTH_SHORT).show();
//						return false;
//					}
//				}
//			}
//
//		return true;
//	}

//	@Override
//	protected int getInputCost(Price p) {
//		if (document instanceof ReturnImplEx) {
//			int result = 0;
//
//			if (delivery.getRowid() != ExtrasConst.INVALID_ID) {
//				for (DeliveryItem item : dlv.items) {
//					if (item.id.equals(p.id)) {
//						result = ((DeliveryItemEx) item).cost;
//						break;
//					}
//				}
//			}
//
//			return result;
//		} else
//			return super.getInputCost(p);
//	}
	
	class DlvAdapter extends BaseAdapter {
		ReturnItem item;
		List<DlvData> docs = new ArrayList<DlvData>();
		
		public void refresh(String orgId, String itemId) {
			 item = (ReturnItem) ((ReturnImplEx)document).findItem(itemId);
			 if( item == null)
				 item = new ReturnItem();
			 
			 docs.clear();
			 com.grsoft.napoleon.documents.DocList dl = DeliveryDoc.instance().docList(orgId, "date", "");
			 for(Document<?> d : dl) {
				 for(DeliveryItem di : ((DeliveryImpl)d).getData().items)
					 if(di.id.equals(itemId))
						 docs.add(new DlvData((Delivery)d.getData(), di));
			 }
			 dl.close();
			 
			 notifyDataSetChanged();
		}

		@Override public int getCount() { return docs.size(); }
		@Override public Object getItem(int arg0) { return docs.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(ReturnPriceCount.this, R.layout.return_doc_row, null);
			
			DlvData dd = (DlvData)getItem(pos);
			TextView tv;
			String text;
			
			tv = (TextView)view.findViewById(R.id.tvDoc);
			text = dd.doc.number + " " + Util.simpleDateFormat.format(dd.doc.date);
			tv.setText(text);
			
			tv = (TextView)view.findViewById(R.id.tvCost);
			tv.setText(Util.IntToScaleStr(dd.doc.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

			tv = (TextView)view.findViewById(R.id.tvMaxQty);
			tv.setText(Util.IntToScaleStr(dd.item.qty, Consts.QTY_SCALE));
			
			Integer val = dlvQtys.get(dd.doc);
			text = val == null ? "" : Util.IntToScaleStr(val, Consts.QTY_SCALE);
			tv = (TextView)view.findViewById(R.id.tvQty);
			tv.setText(text);
			if(document.isEditable())
				tv.setOnClickListener(new SetQty(dd.doc));
			return view;
		}
		
	}
	
	class SetQty implements View.OnClickListener {
		DlvKeySum doc;
		
		public SetQty(DlvKeySum doc) {
			this.doc = doc;
		}

		@Override
		public void onClick(View v) {
			InputNumberDlg.open(v.getContext(), new InputNumber() {
				
				@Override
				public int getValue() {
					Integer value = dlvQtys.get(doc);
					return value == null ? 0 : value;
				}
				
				@Override
				public void applayInput(int value, Object... params) {
					if( value > doc.qty) {
						Toast.makeText(ReturnPriceCount.this, "¬веденное количество превышает количество в накладной", Toast.LENGTH_SHORT).show();
						return;
					}
					if( value == 0 )
						dlvQtys.remove(doc);
					else
						dlvQtys.put(doc, value);
					adapter.notifyDataSetChanged();
					updateQty();
				}
				
			}, Consts.QTY_SCALE, true, "¬ведите количество");
		}
		
	}
}

class DlvKeySum extends DeliveryKey {
	public int qty;
	public int cost;
	
	public DlvKeySum(Delivery doc, int qty, int cost) {
		super(doc);
		
		this.qty = qty;
		this.cost = cost;
	}

	public DlvKeySum(ReturnItemDlv rid) {
		super(rid.date, rid.number);
		
		this.qty = rid.qty;
		this.cost = rid.cost;
	}
}

class DlvData {
	public DlvKeySum doc;
	public DeliveryItem item;
	
	public DlvData(Delivery doc, DeliveryItem item) {
		int sum = item.qty != 0 ? (int)((long)item.sum * Consts.QTY_SCALE / item.qty) : 0;
		this.doc = new DlvKeySum(doc, item.qty, sum);
		this.item = item;
	}
}
