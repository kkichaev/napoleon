package com.grsoft.ads;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.grsoft.ads.database.OrderItem;
import com.grsoft.ads.database.OrderItemEx;
import com.grsoft.ads.database.Return;
import com.grsoft.ads.dataobjects.impl.OrderImpl;
import com.grsoft.ads.dataobjects.impl.OrderImplEx;
import com.grsoft.ads.dataobjects.impl.ReturnImpl;
import com.grsoft.database.DbWriter;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;

public class OrderReturn extends BaseActivity {
	public static final String TAB_NAME = "order_items";
	public static final String TAB_CAPTION = "Возврат";
	private ItemsAdapter adapter;
	private ListView lvReturn;
	private ReturnImpl returnImpl;
	private long rowid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_items);
		
		rowid = getIntent()
				.getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		lvReturn = ((ListView)findViewById(R.id.list)); 
		lvReturn.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		adapter = new ItemsAdapter(this, rowid, lvReturn); 
		lvReturn.setAdapter(adapter);
		
		returnImpl = new ReturnImpl();
		
//		lvReturn.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				if(returnImpl.getData().items.size() > 0)
//					returnImpl.getData().items.clear();
//			}
//		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		int count = adapter.getCount();
		boolean hasReturn = false;
		
		for(int i = 0; i < count; i++){
			if (lvReturn.isItemChecked(i)){
				if (!hasReturn)
					hasReturn = true;
				returnImpl.getData().items.add((OrderItemEx)adapter.getItem(i));
			}
		}
		
		OrderImpl orderImpl = new OrderImpl();
		
		if (orderImpl.read(rowid))
		{
			DbWriter.checkDBTable(Return.class);
			
			if (hasReturn)
				returnImpl.init(orderImpl.getData());
			else {
				returnImpl.getData().created = orderImpl.getData().created;
				if(returnImpl.read())
					returnImpl.delete();
				returnImpl.close();
			}
		}
		
		orderImpl.close();
	}
	
	class ItemsAdapter extends BaseAdapter{

		private OrderImplEx orderImpl = new OrderImplEx();
		private Context context;
		private ReturnImpl returnImpl = new ReturnImpl();
		private ListView listView;
		
		public ItemsAdapter(Context context, long rowid, ListView listView){
			this.context = context;
			this.listView = listView;
			
			orderImpl.read(rowid);
			orderImpl.close();
			
			returnImpl.getData().created = orderImpl.getData().created;
			returnImpl.read();
			returnImpl.close();
		}
		
		@Override
		public int getCount() {
			return orderImpl.getData().items.size();
		}

		@Override
		public Object getItem(int position) {
			return orderImpl.getData().items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return -1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = View.inflate(context, android.R.layout.simple_list_item_multiple_choice , null);
			
			OrderItemEx oi = (OrderItemEx)getItem(position);
			
			if(oi != null){
				CheckedTextView ctv = (CheckedTextView) convertView.findViewById
						(android.R.id.text1);
				ctv.setText(oi.name);
				
				for(OrderItem oii : returnImpl.getData().items)
					if (oii.priceid.equals(oi.priceid)){
						listView.setItemChecked(position, true);
						returnImpl.getData().items.remove(oii);
						break;
					}
			}
			
			return convertView;
		}
		
	}
}
