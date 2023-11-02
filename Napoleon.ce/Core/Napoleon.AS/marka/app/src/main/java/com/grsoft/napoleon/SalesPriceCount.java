package com.grsoft.napoleon;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SalesPriceCount extends PriceCount {
//	List<PriceSalesQty> qty = new ArrayList<PriceSalesQty>();
//	Adapter adapter;
	
	public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
		Intent i = new Intent(context, SalesPriceCount.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);		
	}

	int prevQty = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override protected int getContentViewId() { return R.layout.sales_price_count; }
	
//	@Override
//	protected void refreshData() {		
//		super.refreshData();
//		
//		ListView lv = (ListView)findViewById(R.id.lvParts);
//		PricePrintEx pe = (PricePrintEx) price.getData();
//		SalesItemEx sie = (SalesItemEx) ((SalesImplEx)document).findItem(pe.id);
//		if(sie != null) {
//			prevQty = sie.qty;
//			qty = sie.party;
//			sie.party = new ArrayList<PriceSalesQty>();
//			pe.add(qty);
//		} else {
//			qty = pe.distrubuteFIFO(prevQty); // init new
//		}
//		
//		adapter = new Adapter(pe.party);
//		lv.setAdapter(adapter);
//		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				if(document.isEditable() == false)
//					return true;
//				final PriceSalesQty item = (PriceSalesQty) adapter.getItem(arg2);
//				final PriceSalesQty dest = findParty(item);
//				
//				int tq = freeQty() + ((dest == null) ?  0 : dest.qty);;
//				if(tq > item.qty)
//					tq = item.qty;
//				
//				final int maxQty = tq;
//				InputNumberDlg.open(SalesPriceCount.this, new InputNumber() {
//					
//					@Override public int getValue() { return maxQty; }
//					
//					@Override
//					public void applayInput(int value, Object... params) {
//						if(value == 0) {
//							if(dest != null)
//								qty.remove(dest);
//						} else {
//							if(value > maxQty)
//								value = maxQty;
//							if(dest == null) {
//								PriceSalesQty psq = new PriceSalesQty();
//								psq.id = item.id;
//								psq.name = item.name;
//								psq.qty = value;
//								qty.add(psq);
//							} else {
//								dest.qty = value;
//							}
//						}
//						int fq = freeQty();
//						if(fq > 0) {
//							makeFreeQtyAlert(fq);
//						}
//						adapter.notifyDataSetChanged();
//					}
//				});
//				
//				return true;
//			}
//		});
//		
//		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				if(document.isEditable() == false)
//					return;
//				
//				PriceSalesQty src = (PriceSalesQty) adapter.getItem(arg2);
//				PriceSalesQty dest = findParty(src);
//				int fq = freeQty();
//				if(dest != null) {
//					qty.remove(dest);
//				} else {
//					if(fq == 0) { // нет свободных - распределяем все заново
//						qty.clear();
//						fq = prevQty;
//					}
//					dest = new PriceSalesQty();
//					dest.id = src.id;
//					dest.name = src.name;
//					dest.qty = (fq < src.qty) ? fq : src.qty;
//					qty.add(dest);
//				}
//
//				fq = freeQty();
//				if(fq > 0) {
//					makeFreeQtyAlert(fq);
//				}
//				adapter.notifyDataSetChanged();
//			}
//		});
//		
//		updateSumTextView();
//	}
		
	int getQty() {
		int count = getCountValue();
		if(cbPackets.isChecked())
			count = (int)((long)count * qtyInPack / Consts.QTY_SCALE);
		return count;
	}
	
//	@Override
//	protected void updateSumTextView() {
//		super.updateSumTextView();
//
//		int newQty = getQty();
//		if(document.isEditable() && newQty != prevQty) {
//			prevQty = newQty;
//			if(adapter != null) {
//				PricePrintEx pe = (PricePrintEx) price.getData();
//				qty = pe.distrubuteFIFO(newQty);
//				adapter.notifyDataSetChanged();
//			}
//		}
//		
//	}
	
//	PriceSalesQty findParty(PriceSalesQty src) {
//		for(PriceSalesQty psq : qty)
//			if(psq.id.equals(src.id))
//				return psq;
//		
//		return null;
//	}
//
//	int freeQty() {
//		int free = prevQty;
//		for(PriceSalesQty psq : qty) {
//			free -= psq.qty;
//		}
//		
//		return free;
//	}
//	
//
//	void makeFreeQtyAlert(int fq) {
//		Toast.makeText(SalesPriceCount.this, "Не распределено по сериям " + Integer.toString(fq / Consts.QTY_SCALE) + " шт.", 
//				Toast.LENGTH_SHORT).show();
//	}
//	
//	@Override
//	protected boolean isInputValid(Runnable r) {
//		int fq = freeQty();
//		if(fq > 0) {
//			makeFreeQtyAlert(fq);
//			return false;
//		}
//		return true; 
//	}
//	
//	@Override
//	protected boolean updateOrder() {
//		SalesImplEx seDoc = (SalesImplEx)document; 
//		seDoc.updateItem(price, qty, priceVal, cbPackets.isChecked());
//		return false;
//	}
//	
//	class Adapter extends BaseAdapter {
//
//		public List<PriceSalesQty> data;
//		
//		public Adapter(List<PriceSalesQty> src) { data = src; }
//		
//		@Override public int getCount() { return data.size(); }
//		@Override public Object getItem(int arg0) { return data.get(arg0); }
//		@Override public long getItemId(int arg0) { return arg0; }
//
//		@Override
//		public View getView(int arg0, View view, ViewGroup arg2) {
//			if(view == null)
//				view = View.inflate(SalesPriceCount.this, R.layout.sales_party_row, null);
//			
//			PriceSalesQty item = (PriceSalesQty) getItem(arg0);
//			TextView tv;
//			String text;
//			
//			tv = (TextView)view.findViewById(R.id.tvParty);
//			text = item.name;
//			tv.setText(text);
//			
//			int qip = price.getData().qtyInPack;
//			if(qip == 0)
//				qip = Consts.QTY_SCALE;
//			int qty = item.qty; 
//			text = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + "<br/>" + 
//				Util.IntToScaleStr((int)((long)qty * Consts.QTY_SCALE / qip), Consts.QTY_SCALE) + " уп.";
//			tv = (TextView)view.findViewById(R.id.tvPartQty);
//			tv.setText(Html.fromHtml(text));
//			
//			PriceSalesQty dest = findParty(item);
//			text = "";
//			int bk = R.drawable.list_selector;
//			if(dest != null) {
//				bk = R.drawable.even_row_selector;
//				qty = dest.qty; 
//				text = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + "<br/>" + 
//					Util.IntToScaleStr((int)((long)qty * Consts.QTY_SCALE / qip), Consts.QTY_SCALE) + " уп.";
//			}
//			tv = (TextView)view.findViewById(R.id.tvInQty);
//			tv.setText(Html.fromHtml(text));
//			
//			view.setBackgroundResource(bk);
//			view.setTag(item);
//			
//			return view;
//		}
//		
//	}
}
