package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.util.DeliveryList;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ReturnCount extends PriceCount implements UpdateQtyHandler {
	
	DeliveryList dlist;
	int dlvQty;
	int checkedDoc;
	ListView listView;
	
	public static void open(Context context, long itemRowid, ReturnImplEx doc) {
		Intent i = new Intent(context, ReturnCount.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, itemRowid);
		context.startActivity(i);
	}
	
	@Override protected int getContentViewId() { return R.layout.returncount; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		((ReturnImplEx)document).setUpdateQtyHandler(this);
	}
	
	@Override
	protected boolean isComplexSalesHistory() { return false; }
	
	@Override
	protected boolean canChangeCost() { return false; }
	
	void updateQtyCost(DeliveryEx d) {
		DeliveryItem item = d.findItem(price.getData().id);
		int cost = (int)((long)item.sum * Consts.QTY_SCALE / item.qty);
		onChangeCost(cost);

		dlvQty = item.qty;
		TextView tvQty = (TextView) findViewById(R.id.tvQty);
		tvQty.setText(Util.IntToScaleStr(dlvQty, Consts.QTY_SCALE));
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		if(dlist == null)
			dlist = DeliveryList.open(document.getId());
		
		List<DeliveryEx> docs = dlist.getDocuments(price.getData().id);
		ArrayAdapter<DeliveryEx> aa = new ArrayAdapter<DeliveryEx>(this, R.layout.dlv_doc_list, docs){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null)
					convertView = View.inflate(getContext(), R.layout.dlv_doc_list, null);
				
				TextView textView = (TextView)convertView.findViewById(R.id.tvItem);
				DeliveryEx item = getItem(position);
				if( item != null ){
					String text = item.toString();
					if(listView.isItemChecked(position))
						convertView.setBackgroundResource(R.drawable.item_selected);
					else
						convertView.setBackgroundResource(R.drawable.list_selector);
					DeliveryItem di = item.findItem(price.getData().id);
					if( di != null ) {
						int cost =  di.qty != 0 ? (int)((long)di.sum * Consts.QTY_SCALE / di.qty) : 0;
						text += " <i>(" + Util.IntToScaleStr(cost, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р., " + Util.IntToScaleStr(di.qty, Consts.QTY_SCALE) + ")</i>";
					}
					textView.setText(Html.fromHtml(text));
				}
				return convertView;
			}
		};
		
		if( listView == null ) {
			listView = ((ListView)findViewById(R.id.lvDocs)); 
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					checkedDoc = arg2;
					DeliveryEx d = (DeliveryEx)arg0.getAdapter().getItem(arg2);
					updateQtyCost(d);

					listView.setItemChecked(arg2, true);
					((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
				}
			});
		}
		
		listView.setAdapter(aa);
		
		checkedDoc = 0;
		ReturnItem ri = (ReturnItem) ((ReturnImplEx)document).findItem(price.getData().id);
		if( ri != null ) {
			for(DeliveryEx d : docs) {
				if(d.date.equals(ri.date) && d.number.equals(ri.number))
					break;
				checkedDoc++;
			}
		}
		if( checkedDoc < docs.size() ) {
			updateQtyCost(docs.get(checkedDoc));
			listView.setItemChecked(checkedDoc, true);
			((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
		}		
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		String text = edCount.getText().toString();
		int count = text.length() == 0 
			? 0 
			: Util.StrToScale(text, Consts.QTY_SCALE);
		if( count > dlvQty ) {
			Toast.makeText(this, "Введенное количество больше доступного в накладной", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		DeliveryEx de = null;
		if( listView.getAdapter().getCount() > 0 )
			de = (DeliveryEx) listView.getAdapter().getItem(checkedDoc);
		
		if( de != null ) {
			ReturnItem ri = (ReturnItem)item;
			ri.number = de.number;
			ri.date = de.date;
		}			
	}
}
